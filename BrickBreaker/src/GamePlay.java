import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Queue;
import java.util.LinkedList;

public class GamePlay extends JPanel implements ActionListener {

    private boolean playing;
    private int score = 0;

    public Queue<Rectangle> trail = new LinkedList<>();

    private int playerX = 310;
    private int ballPosX = playerX + 40;
    private int ballPosY = 500;
    private double ballXDir = 0;
    private double ballYDir = 6;

    private final boolean[] activeKeys = new boolean[2];

    private int brickNumberRows;
    private int brickNumberCols;

    private int totalBricks;



    private final int targetTps = 1000/60;
    private int lastTime = (int) System.currentTimeMillis();
    private int targetTime = lastTime + targetTps;






    private MapGenerator map;


    public GamePlay() {
        brickNumberRows = (int)(Math.random()*10)+1;
        brickNumberCols = (int)(Math.random()*30)+1;
        totalBricks = brickNumberRows*brickNumberCols;
        map = new MapGenerator( brickNumberRows, brickNumberCols);
        setFocusable(true);
        setDoubleBuffered(true);
        setFocusTraversalKeysEnabled(false);
        Timer timer = new Timer(0, this);
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
        int playerY = 550;
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
            paintImmediately(1, 1, 692, 592);
        }


        Iterator iter = trail.iterator();
        double portion = 0.2;
        while(iter.hasNext()){
            graphics.setColor(Color.green);
            Rectangle temp = (Rectangle) iter.next();
            System.out.println(temp.x);
            graphics.fillOval(temp.x + ((temp.width - (int)(temp.width*portion))/2), temp.y + ((temp.height - (int)(temp.height*portion))/2) , (int)(temp.width*portion), (int)(temp.height*portion));
            portion += 0.2;
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
                    score = 0;
                    brickNumberRows = (int)(Math.random()*10)+1;
                    brickNumberCols = (int)(Math.random()*30)+1;
                    totalBricks = brickNumberCols*brickNumberRows;
                    map = new MapGenerator(brickNumberRows, brickNumberCols);
                    playing = true;
                    paintImmediately(1, 1, 692, 592);
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



    @Override
    public void actionPerformed(ActionEvent e) {

        if (playing) {

            int current = (int) System.currentTimeMillis();
            if (current < targetTime) return;
            lastTime = current;
            targetTime = (current+targetTps) - (current - targetTime);







            if (activeKeys[0] && playerX >=10){
                int playerMovementLeft = -5;
                playerX += playerMovementLeft;
            }
            if (activeKeys[1] && playerX <= 585){
                int playerMovementRight = 5;
                playerX += playerMovementRight;
            }

            int ballProjectedPositionX = (int) (ballPosX + ballXDir);
            int ballProjectedPositionY = (int) (ballPosY + ballYDir);

            System.out.println("Ball X: " + ballPosX);
            System.out.println("Ball Y: " + ballPosY);
            System.out.println("Ball Projected X: " + ballProjectedPositionX);
            System.out.println("Ball Projected Y: " + ballProjectedPositionY);


            System.out.println();

            Rectangle ballRect = new Rectangle(ballPosX, ballPosY, 20, 20);
            Rectangle ballProjectedRect = new Rectangle(ballProjectedPositionX, ballProjectedPositionY, 20, 20);



            if (ballProjectedRect.intersects(new Rectangle(playerX, 550, 100, 8))) {
                ballPosY = 530;
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



                        if (ballProjectedRect.intersects(rect)) {
                            System.out.println("Rectangle X: " + rect.x);
                            System.out.println("Rectangle Y: " + rect.y);
                            System.out.println();

                            map.setBrickValue(0, i, j);
                            totalBricks--;
                            score += 1;

                            if(ballPosX + 14 <= rect.x){
                                ballXDir *= -1;
                                ballPosX = rect.x - 14;
                            }else if(ballPosX + 7 >= rect.x + bricksWidth){
                                ballXDir *= -1;
                                ballPosX = rect.x + bricksWidth - 7;
                            }else if(ballPosY + 14 <= rect.y){
                                ballYDir *= -1;
                                ballPosY = rect.y - 14;
                            }else if(ballPosY + 6 >= rect.y + bricksHeight){
                                ballYDir *= -1;
                                ballPosY = rect.y + bricksHeight - 6;
                            }
                            break A;
                        }
                    }
                }
            }

            ballPosX += ballXDir;
            ballPosY += ballYDir;

            if(trail.size() == 4){
                trail.remove();
            }
            trail.add(ballRect);





            if (ballPosX < 3 ){
                ballPosX = 3;
                ballXDir = -ballXDir;
            } else if(ballPosX > 670) {
                ballPosX = 670;
                ballXDir = -ballXDir;
            }

            if (ballPosY < 3) {
                ballPosY = 3;
                ballYDir = -ballYDir;
            }
        }
        paintImmediately(1, 1, 692, 592);
    }


}
