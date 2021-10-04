import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class PieceController
{
    private int TILE_WIDTH, TILE_HEIGHT;

    private Input input;
    private Board board;

    boolean isDragging = false;
    Piece draggedPiece;
    Point draggedPiecePos;
    Point draggedPieceBoardPos;

    public PieceController(Input input, Board board)
    {
        this.input = input;
        this.board = board;

        this.TILE_WIDTH = Game.TILE_WIDTH;
        this.TILE_HEIGHT = Game.TILE_HEIGHT;
    }

    public void update()
    {
        Point targetPosition = new Point(input.getMousePosition().x / TILE_WIDTH, input.getMousePosition().y / TILE_HEIGHT);

        if (input.isButtonPressed(MouseEvent.BUTTON1))
        {
            if (board.grid[targetPosition.y * 8 + targetPosition.x] != null)
            {
                draggedPieceBoardPos = targetPosition;
                draggedPiecePos = new Point(input.getMousePosition().x - TILE_WIDTH / 2, input.getMousePosition().y - TILE_HEIGHT / 2);
                draggedPiece = new Piece(board.grid[draggedPieceBoardPos.y * 8 + draggedPieceBoardPos.x]);

                isDragging = true;

                board.highlightMoves();
            }
        }
        else if (input.isButtonReleased(MouseEvent.BUTTON1))
        {
            if (isDragging && board.isMoveLegal(draggedPieceBoardPos.y * 8 + draggedPieceBoardPos.x, targetPosition.y * 8 + targetPosition.x))
            {
                if (board.gameMode == "Sandbox")
                {
                    board.makeMove(new Move(draggedPieceBoardPos.y * 8 + draggedPieceBoardPos.x, targetPosition.y * 8 + targetPosition.x));
                }
                else if (board.grid[draggedPieceBoardPos.y * 8 + draggedPieceBoardPos.x].color == board.turn)
                {
                    board.makeMove(new Move(draggedPieceBoardPos.y * 8 + draggedPieceBoardPos.x, targetPosition.y * 8 + targetPosition.x));
                }
            }

            board.unhighlightMoves();

            isDragging = false;
            draggedPieceBoardPos = null;
            draggedPiece = null;
        }
        else if (isDragging)
        {
            draggedPiecePos.x = input.getMousePosition().x - TILE_WIDTH / 2;
            draggedPiecePos.y = input.getMousePosition().y - TILE_HEIGHT / 2;
        }
    }

    public void draw(Graphics g)
    {
        if (isDragging)
        {
            try
            {
                g.drawImage(Utilities.resizeImage(Game.pieceImages.get(draggedPiece.color + " " + draggedPiece.type), TILE_WIDTH, TILE_HEIGHT), draggedPiecePos.x, draggedPiecePos.y, null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
