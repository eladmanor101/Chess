import javax.swing.JFrame;
import java.awt.Dimension;

public class Window
{
    public Window(int width, int height, String title, Game game)
    {
        JFrame frame = new JFrame(title);

        game.setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        game.createBufferStrategy(3);
    }
}