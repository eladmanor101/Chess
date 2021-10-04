import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main
{
    public static void main(String[] args)
    {
        /*
        Settings.init();

        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.add(new Game());
            f.setSize(Settings.screenWidth, Settings.screenHeight);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });*/
        Game game = new Game();
    }
}
/*
class MyPanel extends JPanel
{
    Piece[] board = new Piece[64];
    Piece ghostPiece;
    Point ghostImagePos;
    Point heldPiecePos;

    //Timer timer;

    public MyPanel()
    {
        MouseHandler mouseHandler  = new MouseHandler();
        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.setFocusable(true);

        loadBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");

        //timer = new Timer(10, e -> update());
        //timer.start();
    }

    public void update(float deltaTime)
    {


        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if ((i + j) % 2 == 0)
                {
                    g.setColor(new Color(240, 217, 181));
                    g.fillRect(j * Settings.tileWidth, i * Settings.tileHeight, Settings.tileWidth, Settings.tileHeight);
                }
                else
                {
                    g.setColor(new Color(184, 140, 100));
                    g.fillRect(j * Settings.tileWidth, i * Settings.tileHeight, Settings.tileWidth, Settings.tileHeight);
                }

                if (board[i * 8 + j] != null)
                {
                    try {
                        // Initialize Image
                        BufferedImage pieceImage;
                        pieceImage = ImageIO.read(getClass().getResource(board[i * 8 + j].color + " " + board[i * 8 + j].type + ".png"));
                        pieceImage = resizeImage(pieceImage, Settings.tileWidth, Settings.tileHeight);

                        // Check if image is being held
                        if (MouseHandler.isDragging && heldPiecePos.x == j && heldPiecePos.y == i)
                        {
                            BufferedImage tempImage = new BufferedImage(pieceImage.getWidth(), pieceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2d = (Graphics2D) tempImage.getGraphics();
                            g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
                            g2d.drawImage(pieceImage, 0, 0, null);
                        }
                        else
                        {
                            g.drawImage(pieceImage, j * Settings.tileWidth, i * Settings.tileHeight, null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (MouseHandler.isDragging)
        {
            try {
                BufferedImage ghostImage = ImageIO.read(getClass().getResource(ghostPiece.color + " " + ghostPiece.type + ".png"));
                ghostImage = resizeImage(ghostImage, Settings.tileWidth, Settings.tileHeight);
                g.drawImage(ghostImage, ghostImagePos.x, ghostImagePos.y, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadBoardFromFen(String fen)
    {
        int i = 7, j = 0;
        for (char symbol : fen.toCharArray())
        {
            if (symbol == '/')
            {
                i--;
                j = 0;
            }
            else
            {
                if (Character.isDigit(symbol))
                {
                    j += Character.getNumericValue(symbol);
                }
                else
                {
                    char pieceColor = (Character.isUpperCase(symbol)) ? 'w' : 'b';
                    char pieceType = Character.toLowerCase(symbol);
                    board[i * 8 + j] = new Piece(pieceType, pieceColor);
                    j++;
                }
            }
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException
    {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    public class MouseHandler extends MouseAdapter
    {

        private Point offset;
        public static boolean isDragging = false;

        @Override
        public void mousePressed(MouseEvent e)
        {
            Point tilePos = new Point(e.getPoint().x / Settings.tileWidth, e.getPoint().y / Settings.tileHeight);
            if (tilePos.x >= 0 && tilePos.x <= 7 && tilePos.y >= 0 && tilePos.y <= 7 && board[tilePos.y * 8 + tilePos.x] != null)
            {
                heldPiecePos = tilePos;
                offset = new Point(e.getPoint().x - heldPiecePos.x * Settings.tileWidth, e.getPoint().y - heldPiecePos.y * Settings.tileHeight);

                ghostImagePos = new Point(e.getPoint().x - offset.x, e.getPoint().y - offset.y);
                ghostPiece = new Piece(board[heldPiecePos.y * 8 + heldPiecePos.x]);

                isDragging = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            if (isDragging)
            {
                board[(e.getPoint().y / Settings.tileHeight) * 8 + (e.getPoint().x / Settings.tileWidth)] = new Piece(ghostPiece);
                isDragging = false;
            }

            offset = null;
            heldPiecePos = null;
            ghostPiece = null;
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            if (isDragging)
            {
                ghostImagePos.x = e.getPoint().x - offset.x;
                ghostImagePos.y = e.getPoint().y - offset.y;
            }
        }
    }
}*/