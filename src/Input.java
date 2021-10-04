import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseMotionListener
{
    private final int NUM_KEYS = 256;
    private boolean[] keysPressed = new boolean[NUM_KEYS];
    private boolean[] lastKeysPressed = new boolean[NUM_KEYS];

    private final int NUM_BUTTONS = 5;
    private boolean[] buttonsPressed = new boolean[NUM_BUTTONS];
    private boolean[] lastButtonsPressed = new boolean[NUM_BUTTONS];

    private Point mousePosition;
    private int scroll;

    public Input(Game game)
    {
        mousePosition = new Point(0, 0);

        game.addKeyListener(this);
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
    }

    public boolean isKeyPressed(int keyCode)
    {
        return keysPressed[keyCode] && !lastKeysPressed[keyCode];
    }

    public boolean isKeyReleased(int keyCode)
    {
        return !keysPressed[keyCode] && lastKeysPressed[keyCode];
    }

    public boolean isKeyDown(int keyCode)
    {
        return keysPressed[keyCode];
    }

    public boolean isButtonPressed(int button)
    {
        return buttonsPressed[button] && !lastButtonsPressed[button];
    }

    public boolean isButtonReleased(int button)
    {
        return !buttonsPressed[button] && lastButtonsPressed[button];
    }

    public boolean isButtonDown(int button)
    {
        return buttonsPressed[button];
    }

    public Point getMousePosition()
    {
        return new Point(mousePosition);
    }

    public void update()
    {
        for (int i = 0; i < NUM_KEYS; i++)
        {
            lastKeysPressed[i] = keysPressed[i];
        }

        for (int i = 0; i < NUM_BUTTONS; i++)
        {
            lastButtonsPressed[i] = buttonsPressed[i];
        }
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        keysPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        keysPressed[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        buttonsPressed[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        buttonsPressed[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {

    }

    @Override
    public void mouseExited(MouseEvent e)
    {

    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        mousePosition.x = e.getX();
        mousePosition.y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mousePosition.x = e.getX();
        mousePosition.y = e.getY();
    }
}