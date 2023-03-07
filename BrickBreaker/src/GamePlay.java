import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class GamePlay extends JPanel implements ActionListener {

    private boolean playing = false;
    private int score = 0;
    private int totalBricks = 21;
    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int ballPosX = 120;
    private int ballPosY = 350;
    private int ballXDir = -1;
    private int ballYDir = -2;

    private boolean[] activeKeys = new boolean[2];

    private MapGenerator map;

    private JComponent component;

    public GamePlay() {
        map = new MapGenerator(3, 7);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(8, this);
        timer.start();
        keyBindings();
        playing = true;
    }

    public void paint(Graphics graphics) {
        graphics.setColor(Color.black);
        graphics.fillRect(1, 1, 692, 592);
        map.draw((Graphics2D) graphics);
        graphics.setColor(Color.blue);
        graphics.fillRect(0, 0, 3, 592);
        graphics.fillRect(0, 0, 692, 3);
        graphics.fillRect(691, 0, 3, 592);

        graphics.setColor(Color.white);
        graphics.setFont(new Font("serif", Font.BOLD, 25));

        graphics.drawString("" + score, 590, 30);

        graphics.setColor(Color.yellow);
        graphics.fillRect(playerX, 550, 100, 8);

        graphics.setColor(Color.green);
        graphics.fillOval(ballPosX, ballPosY, 20, 20);

        if (ballPosY > 570) {
            playing = false;
            ballXDir = 0;
            ballYDir = 0;
            graphics.setColor(Color.white);
            graphics.setFont(new Font("serif", Font.BOLD, 30));
            graphics.drawString("Game Over!", 190, 300);
            graphics.drawString("Score: " + score, 190, 340);

            graphics.drawString("Press Enter to Restart.", 190, 400);
        }
        if (totalBricks == 0) {
            playing = false;
            ballXDir = -1;
            ballYDir = -2;
            graphics.setColor(Color.white);
            graphics.setFont(new Font("serif", Font.BOLD, 30));

            graphics.drawString("You Win!", 190, 300);
            graphics.drawString("Score: " + score, 190, 340);

            graphics.drawString("Press Enter to Restart.", 190, 400);
        }

        graphics.dispose();
    }



    public void keyBindings() {
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left-pressed");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left-released");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right-pressed");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right-released");


        this.getActionMap().put("left-pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeKeys[0] = true;
            }
        });

        this.getActionMap().put("left-released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeKeys[0] = false;
            }
        });


        this.getActionMap().put("right-pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeKeys[1] = true;
            }
        });

        this.getActionMap().put("right-released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeKeys[1] = false;
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (playing) {

            if (activeKeys[0] && playerX >=10){
                playerX += -5;
            }
            if (activeKeys[1] && playerX <= 585){
                playerX += 5;
            }



            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir;
            }
            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int bricksWidth = map.brickWidth;
                        int bricksHeight = map.brickHeight;

                        Rectangle rect = new Rectangle(brickX, brickY, bricksWidth, bricksHeight);
                        Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
                        Rectangle brickRect = rect;

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 5;
                            if (ballPosX + 19 <= brickRect.x || ballPosX + 1 >= brickRect.x + bricksWidth) {
                                ballXDir = -ballXDir;
                            } else {
                                ballYDir = -ballYDir;
                            }
                            break A;
                        }
                    }
                }
            }
            ballPosX += ballXDir;
            ballPosY += ballYDir;

            if (ballPosX < 0 || ballPosX > 670) {
                ballXDir = -ballXDir;
            }
            if (ballPosY < 0) {
                ballYDir = -ballYDir;
            }
        }
        repaint();
    }

    public void keyPressed(KeyEvent e) {


        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!playing) {
                ballPosX = 120;
                ballPosY = 350;
                ballXDir = -1;
                ballYDir = -2;
                score = 0;
                playerX = 310;
                totalBricks = 21;
                map = new MapGenerator(3, 7);
                repaint();
            }
        }
    }

}
