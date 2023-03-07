import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GamePlay extends JPanel implements ActionListener {

    private boolean playing = false;
    private int score = 0;


    private Timer timer;
    private int delay = 8;
    private int playerX = 310;
    private int playerY = 550;
    private int ballPosX = playerX + 40;
    private int ballPosY = 500;
    private double ballXDir = 0;
    private double ballYDir = 6;

    private boolean[] activeKeys = new boolean[2];

    private int brickNumberRows;
    private int brickNumberCols;

    private int totalBricks;

    private MapGenerator map;

    private JComponent component;

    public GamePlay() {
        brickNumberRows = (int)(Math.random()*10)+1;
        brickNumberCols = (int)(Math.random()*30)+1;
        totalBricks = brickNumberRows*brickNumberCols;
        map = new MapGenerator(brickNumberRows, brickNumberCols);
        setFocusable(true);
        setDoubleBuffered(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(0, this);
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
        graphics.fillRect(playerX, playerY, 100, 8);

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
            playerX = 310;
            ballPosX = playerX + 40;
            ballPosY = 500;
            ballXDir = 0;
            ballYDir = 6;
            brickNumberRows = (int)(Math.random()*10)+1;
            brickNumberCols = (int)(Math.random()*30)+1;
            totalBricks = brickNumberCols*brickNumberRows;
            map = new MapGenerator(brickNumberRows, brickNumberCols);
            playing = true;
            repaint();
        }

        graphics.dispose();
    }



    public void keyBindings() {
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left-pressed");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "left-released");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right-pressed");
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "right-released");

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enter-pressed");


        this.getActionMap().put("enter-pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!playing) {
                    playerX = 310;
                    ballPosX = playerX + 40;
                    ballPosY = 500;
                    ballXDir = 0;
                    ballYDir = 6;
                    brickNumberRows = (int)(Math.random()*10)+1;
                    brickNumberCols = (int)(Math.random()*30)+1;
                    totalBricks = brickNumberCols*brickNumberRows;
                    map = new MapGenerator(brickNumberRows, brickNumberCols);
                    playing = true;
                    repaint();
                }
            }
        });

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

    private int playerMovementLeft = -5;
    private int playerMovementRight = 5;

    @Override
    public void actionPerformed(ActionEvent e) {

        if (playing) {

            if (activeKeys[0] && playerX >=10){
                playerX += playerMovementLeft;
            }
            if (activeKeys[1] && playerX <= 585){
                playerX += playerMovementRight;
            }




            if (new Rectangle(ballPosX, ballPosY, 20, 20).intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballYDir = -ballYDir;
                if (activeKeys[0]) ballXDir = -6;
                if (activeKeys[1]) ballXDir = 6;

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


                        if (ballRect.intersects(rect)) {
                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 1;
                            if (ballPosX + 19 <= rect.x || ballPosX + 2 >= rect.x + bricksWidth) {
                                ballXDir *= -1;
                            } else {
                                ballYDir *= -1;
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


}
