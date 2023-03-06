import javax.swing.*;

public class Main {
    public static void main(String[] args){
        JFrame frame = new JFrame();
        GamePlay gamePlay = new GamePlay();


        frame.setBounds(10, 10, 710, 600);
        frame.setTitle("Brick Breaker");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gamePlay);
    }
}
