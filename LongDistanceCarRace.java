import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.sound.sampled.*;
import java.io.File;

public class LongDistanceCarRace extends JPanel implements ActionListener, KeyListener {

    javax.swing.Timer timer;
    Random rand = new Random();

    // Players
    int p1X = 100, p1Y = 380;
    int p2X = 240, p2Y = 380;

    // Enemy cars
    int[] enemyX = {40, 120, 200, 280};
    int[] enemyY = {-300, -700, -1100, -1500};

    // Road lines
    int[] roadLineY = {0, 100, 200, 300, 400};

    // Race variables
    int distance = 0;
    final int FINISH_DISTANCE = 5000;

    int speed = 3;
    int level = 1;

    boolean gameOver = false;
    String winner = "";

    JButton restartBtn;

    public LongDistanceCarRace() {
        setFocusable(true);
        addKeyListener(this);
        setLayout(null);

        restartBtn = new JButton("⟳");
        restartBtn.setBounds(180, 280, 40, 40);
        restartBtn.setFont(new Font("Arial", Font.BOLD, 18));
        restartBtn.setVisible(false);
        restartBtn.addActionListener(e -> restartGame());
        add(restartBtn);

        timer = new javax.swing.Timer(20, this);
        timer.start();
    }

    // 🔊 Crash sound
    void playCrashSound() {
        try {
            AudioInputStream audio =
                    AudioSystem.getAudioInputStream(new File("crash.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound error");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Road
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, 400, 500);

        // 🛣️ White road lines
        g2.setColor(Color.WHITE);
        for (int y : roadLineY) {
            g2.fillRect(195, y, 10, 60);
        }

        // Distance bar
        g2.drawRect(10, 10, 200, 10);
        g2.setColor(Color.GREEN);
        g2.fillRect(10, 10,
                (int)((distance / (double)FINISH_DISTANCE) * 200), 10);

        g2.setColor(Color.WHITE);
        g2.drawString("Distance: " + distance + " / " + FINISH_DISTANCE, 10, 35);
        g2.drawString("Level: " + level, 250, 35);

        // PLAYER LABELS
        g2.setColor(Color.WHITE);
        g2.drawString("PLAYER 1", p1X + 2, p1Y - 8);
        g2.drawString("PLAYER 2", p2X + 2, p2Y - 8);

        // Players
        drawCar(g2, p1X, p1Y, Color.BLUE);
        drawCar(g2, p2X, p2Y, Color.GREEN);

        // Enemies
        for (int i = 0; i < enemyX.length; i++) {
            drawCar(g2, enemyX[i], enemyY[i], Color.RED);
        }

        // Finish line near end
        if (distance >= FINISH_DISTANCE - 300) {
            for (int i = 0; i < 400; i += 40)
                g2.fillRect(i, 50, 20, 20);
        }

        // Game Over
        if (gameOver) {
    g2.setColor(Color.YELLOW);
    g2.setFont(new Font("Arial", Font.BOLD, 28));
    g2.drawString(winner, 80, 230);

    // Restart hint text (drawn AFTER button, lower position)
    g2.setFont(new Font("Arial", Font.BOLD, 16));
    g2.drawString("Press R to Restart", 115, 380);

    restartBtn.setVisible(true);
}

    }

    void drawCar(Graphics2D g2, int x, int y, Color c) {
        g2.setColor(c);
        g2.fillRoundRect(x, y, 40, 80, 10, 10);

        g2.setColor(Color.BLACK);
        g2.fillOval(x - 5, y + 10, 10, 15);
        g2.fillOval(x + 35, y + 10, 10, 15);
        g2.fillOval(x - 5, y + 55, 10, 15);
        g2.fillOval(x + 35, y + 55, 10, 15);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!gameOver) {
            distance += speed;

            // 🔼 LEVEL SYSTEM
            if (distance % 1000 == 0) {
                level++;
                speed++;
            }

            // Move road lines
            for (int i = 0; i < roadLineY.length; i++) {
                roadLineY[i] += speed + 2;
                if (roadLineY[i] > 500) roadLineY[i] = -60;
            }

            // Move enemies
            for (int i = 0; i < enemyY.length; i++) {
                enemyY[i] += speed + level + 2;

                if (enemyY[i] > 500) {
                    enemyY[i] = -rand.nextInt(1200);
                    enemyX[i] = rand.nextInt(360);
                }
            }

            Rectangle p1 = new Rectangle(p1X, p1Y, 40, 80);
            Rectangle p2 = new Rectangle(p2X, p2Y, 40, 80);

            if (distance >= FINISH_DISTANCE) {
                winner = "RACE COMPLETED!";
                gameOver = true;
            }

            for (int i = 0; i < enemyX.length; i++) {
                Rectangle enemy = new Rectangle(enemyX[i], enemyY[i], 40, 80);

                if (p1.intersects(enemy)) {
                    winner = "PLAYER 2 WINS!";
                    playCrashSound();
                    gameOver = true;
                }
                if (p2.intersects(enemy)) {
                    winner = "PLAYER 1 WINS!";
                    playCrashSound();
                    gameOver = true;
                }
            }
        }

        repaint();
    }

    void restartGame() {
        p1X = 100;
        p2X = 240;
        distance = 0;
        speed = 3;
        level = 1;
        gameOver = false;
        winner = "";

        for (int i = 0; i < enemyY.length; i++)
            enemyY[i] = -rand.nextInt(1200);

        restartBtn.setVisible(false);
        timer.start();
        requestFocusInWindow();
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT && p1X > 0)
                p1X -= 20;
            if (e.getKeyCode() == KeyEvent.VK_RIGHT && p1X < 360)
                p1X += 20;

            if (e.getKeyCode() == KeyEvent.VK_A && p2X > 0)
                p2X -= 20;
            if (e.getKeyCode() == KeyEvent.VK_D && p2X < 360)
                p2X += 20;
        }

        // 🔁 Restart shortcut
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Long Distance Car Race");
        LongDistanceCarRace game = new LongDistanceCarRace();

        frame.add(game);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
