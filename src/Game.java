import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Game extends Canvas implements Runnable
{
    public static final int WIDTH = 800, HEIGHT = 800;
    public static final int TILE_WIDTH = WIDTH / 8, TILE_HEIGHT = HEIGHT / 8;

    private final int MAX_FRAMES_PER_SECOND = 60;
    private final double fOPTIOMAL_TIME = 1000000000 / MAX_FRAMES_PER_SECOND;

    private Thread thread;
    private boolean running = false;

    private Input input;

    Board board;

    static HashMap<String, BufferedImage> pieceImages = new HashMap(12);

    public Game()
    {
        new Window(WIDTH, HEIGHT, "Chess", this);
        start();
    }

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop()
    {
        try
        {
            thread.join();
            running = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        running = true;
        this.requestFocus();

        input = new Input(this);
        board = new Board(input);

        loadPieceImages();
        board.loadBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        board.generateMoves();

        double deltaTime = 0;
        int frames = 0;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        while (running)
        {
            long currentTime = System.nanoTime();
            deltaTime += (currentTime - lastTime);
            lastTime = currentTime;

            if (deltaTime >= fOPTIOMAL_TIME)
            {
                update(deltaTime);
                draw();

                frames++;
                deltaTime -= fOPTIOMAL_TIME;
            }

            if (System.currentTimeMillis() - timer >= 1000)
            {
                System.out.println("FPS: " + frames);

                frames = 0;
                timer += 1000;
            }
        }
    }

    public void update(double deltaTime)
    {
        board.pieceController.update();
        input.update();
    }

    public void draw()
    {
        BufferStrategy buffer = this.getBufferStrategy();
        Graphics g = buffer.getDrawGraphics();

        // Clear Screen
        g.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw
        board.draw(g);
        board.pieceController.draw(g);

        g.dispose();
        buffer.show();
    }

    private void loadPieceImages()
    {
        String[] pieceNames = {"b b", "b k", "b n", "b p", "b q", "b r", "w b", "w k", "w n", "w p", "w q", "w r"};
        for (int i = 0; i < pieceNames.length; i++)
        {
            try
            {
                pieceImages.put(pieceNames[i], ImageIO.read(getClass().getResource("pieces/" + pieceNames[i] + ".png")));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
