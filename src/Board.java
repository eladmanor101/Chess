import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Math;

public class Board
{
    private int TILE_WIDTH, TILE_HEIGHT;
    private int CIRCLE_WIDTH;

    Piece[] grid = new Piece[64];
    char turn = 'w';
    String gameMode = "Sandbox";

    PieceController pieceController;
    PieceLocations pieceLocations = new PieceLocations();

    ArrayList<Move> moves;
    ArrayList<MoveLog> moveLog;
    ArrayList<Move> highlightedMoves;
    boolean movesGenerated = false;

    boolean waitingForPromotion = false;
    int promotionSquare;
    char promotionColor;

    private int[][] squaresToEdge = new int[64][8];
    int[] directions = {8, -8, -1, 1, 7, -7, 9, -9};
    Point[] knightDirections = {new Point(2, 1), new Point(1, 2), new Point(-1, 2), new Point(-2, 1), new Point(-2, -1), new Point(-1, -2), new Point(1, -2), new Point(2, -1)};

    public Board(Input input)
    {
        this.TILE_WIDTH = Game.TILE_WIDTH;
        this.TILE_HEIGHT = Game.TILE_HEIGHT;
        this.CIRCLE_WIDTH = TILE_WIDTH / 4;

        pieceController = new PieceController(input, this);

        highlightedMoves = new ArrayList<Move>();
        moveLog = new ArrayList<MoveLog>();

        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                int numSouth = 7 - i;
                int numNorth = i;
                int numWest = j;
                int numEast = 7 - j;

                squaresToEdge[i * 8 + j] = new int[]{
                        numSouth,
                        numNorth,
                        numWest,
                        numEast,
                        Math.min(numSouth, numWest),
                        Math.min(numNorth, numEast),
                        Math.min(numSouth, numEast),
                        Math.min(numNorth, numWest)
                };
            }
        }
    }

    public void draw(Graphics g)
    {
        // Draw Board
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if ((i + j) % 2 == 0)
                {
                    g.setColor(new Color(240, 217, 181));
                }
                else
                {
                    g.setColor(new Color(184, 140, 100));
                }

                g.fillRect(j * TILE_WIDTH, i * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
            }
        }

        // Draw Highlighted Moves
        if (highlightedMoves != null)
        {
            for (int k = 0; k < highlightedMoves.size(); k++)
            {
                int i = highlightedMoves.get(k).targetSquare / 8, j = highlightedMoves.get(k).targetSquare % 8;

                if (grid[highlightedMoves.get(k).targetSquare] == null)
                {
                    g.setColor(new Color(136, 148, 108));
                    g.fillOval(j * TILE_WIDTH + TILE_WIDTH / 2 - CIRCLE_WIDTH / 2, i * TILE_HEIGHT + TILE_HEIGHT / 2 - CIRCLE_WIDTH / 2, CIRCLE_WIDTH, CIRCLE_WIDTH);
                }
                else
                {
                    g.setColor(new Color(136, 148, 108));
                    g.fillRect(j * TILE_WIDTH, i * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                    g.setColor(((i + j) % 2 == 0) ? new Color(240, 217, 181) : new Color(184, 140, 100));
                    g.fillOval(j * TILE_WIDTH, i * TILE_HEIGHT, TILE_WIDTH, TILE_HEIGHT);
                }
            }
        }

        // Draw Pieces
        for (int i = 0; i < 8; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                if (grid[i * 8 + j] != null)
                {
                    try
                    {
                        BufferedImage pieceImage = Game.pieceImages.get(grid[i * 8 + j].color + " " + grid[i * 8 + j].type);
                        pieceImage = Utilities.resizeImage(pieceImage, TILE_WIDTH, TILE_HEIGHT);

                        // Check if image is being held
                        if (pieceController.isDragging && pieceController.draggedPieceBoardPos.x == j && pieceController.draggedPieceBoardPos.y == i)
                        {
                            // Draw the image at half the transparency
                            Graphics2D g2d = (Graphics2D) g;
                            Composite composite = g2d.getComposite();
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
                            g2d.drawImage(pieceImage, j * TILE_WIDTH, i * TILE_HEIGHT, null);
                            g2d.setComposite(composite);
                        }
                        else
                        {
                            g.drawImage(pieceImage, j * TILE_WIDTH, i * TILE_HEIGHT, null);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Draw Promotion Request
        if (waitingForPromotion)
        {
            g.setColor(Color.WHITE);
            g.fillRect(promotionSquare % 8, promotionSquare / 8, TILE_WIDTH, 4 * TILE_HEIGHT);

            char[] promotionChoices = {'q', 'r', 'b', 'n'};
            for (int i = 0; i < 4; i++)
            {
                try
                {
                    BufferedImage pieceImage = Game.pieceImages.get(promotionColor + " " + promotionChoices[i]);
                    pieceImage = Utilities.resizeImage(pieceImage, TILE_WIDTH, TILE_HEIGHT);

                    g.drawImage(pieceImage, promotionSquare % 8, promotionSquare / 8 + i * TILE_HEIGHT, null);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadBoardFromFen(String fen)
    {
        int i = 0, j = 0;
        for (char symbol : fen.toCharArray())
        {
            if (symbol == '/')
            {
                i++;
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
                    Piece piece = new Piece(pieceType, pieceColor);
                    grid[i * 8 + j] = piece;
                    pieceLocations.addPiece(piece, i * 8 + j);

                    System.out.println(pieceColor + " " + pieceType + " " + (i * 8 + j));

                    j++;
                }
            }
        }


    }

    private void makeMove(Move move)
    {
        // Sound
        SoundClip sound;
        if (grid[move.targetSquare] == null)
        {
            sound = new SoundClip("/audio/Move.wav");
        }
        else
        {
            sound = new SoundClip("/audio/Capture.wav");
        }
        sound.play();

        Piece takenPiece = null;
        if (grid[move.targetSquare] != null)
        {
            pieceLocations.removePiece(grid[move.targetSquare], move.targetSquare);
            takenPiece = grid[move.targetSquare];
        }
        pieceLocations.updatePiece(grid[move.startSquare], move.startSquare, move.targetSquare);

        grid[move.targetSquare] = new Piece(grid[move.startSquare]);
        grid[move.targetSquare].hasMoved = true;
        grid[move.startSquare] = null;

        if (move.isCastling)
        {
            int direction = (move.targetSquare - move.startSquare) / Math.abs(move.targetSquare - move.startSquare);
            int rookPosition = move.startSquare + direction * squaresToEdge[move.startSquare][(direction == 1) ? 3 : 2];

            grid[move.startSquare + direction] = grid[rookPosition];
            grid[rookPosition] = null;

            pieceLocations.updatePiece(grid[move.startSquare + direction], rookPosition, move.startSquare + direction);
        }
        else if (move.isPromotion)
        {
            waitingForPromotion = true;
            promotionSquare = move.targetSquare;
            promotionColor = grid[move.targetSquare].color;
        }

        moveLog.add(new MoveLog(move.startSquare, move.targetSquare, takenPiece));

        unhighlightMoves();
        switchTurns();
    }

    public void tryMakeMove(int startSquare, int targetSquare)
    {
        for (int i = 0; i < moves.size(); i++)
        {
            if (moves.get(i).startSquare == startSquare && moves.get(i).targetSquare == targetSquare)
            {
                makeMove(moves.get(i));
            }
        }
    }

    public void revertMove()
    {
        if (moveLog.size() == 0) { return; }
        MoveLog move = moveLog.remove(moveLog.size() - 1);

        if (grid[move.targetSquare] != null)
        {
            pieceLocations.removePiece(grid[move.targetSquare], move.targetSquare);
        }
        pieceLocations.updatePiece(grid[move.targetSquare], move.targetSquare, move.startSquare);

        grid[move.startSquare] = new Piece(grid[move.targetSquare]);
        grid[move.targetSquare] = move.takenPiece;

        if (move.isCastling)
        {
            int direction = (move.targetSquare - move.startSquare) / Math.abs(move.targetSquare - move.startSquare);
            int rookPosition = move.startSquare + direction * squaresToEdge[move.startSquare][(direction == 1) ? 3 : 2];

            grid[rookPosition] = grid[move.startSquare + direction];
            grid[move.startSquare + direction] = null;

            pieceLocations.updatePiece(grid[rookPosition], move.startSquare + direction, rookPosition);
        }
        else if (move.isPromotion)
        {
            waitingForPromotion = false;
        }

        switchTurns();
    }

    public boolean isMoveLegal(int startSquare, int targetSquare)
    {
        for (int i = 0; i < moves.size(); i++)
        {
            if (moves.get(i).startSquare == startSquare && moves.get(i).targetSquare == targetSquare)
            {
                return true;
            }
        }

        return false;
    }

    public void switchTurns()
    {
        turn = (turn == 'w') ? 'b' : 'w';
        generateMoves();
    }

    public void generateMoves()
    {
        moves = new ArrayList<Move>();
        movesGenerated = false;

        for (int i = 0; i < 64; i++)
        {
            if (grid[i] != null && grid[i].color == turn)
            {
                Piece piece = grid[i];

                moves.addAll(getPseudoLegalMoves(grid, piece, i));
            }
        }

        for (int i = 0; i < moves.size(); i++)
        {
            Piece[] tempGrid = grid.clone();
            Move move = moves.get(i);
            int test = moves.size();

            // Make move on the temporary grid
            tempGrid[move.targetSquare] = new Piece(tempGrid[move.startSquare]);
            tempGrid[move.startSquare] = null;
            System.out.print("");
            // Get all of the opponent's possible responses
            for (int j = 0; j < 64; j++)
            {
                boolean isIllegal = false;

                if (tempGrid[j] != null && tempGrid[j].color != turn)
                {
                    Piece tempPiece = tempGrid[j];
                    ArrayList<Move> tempOpponentMoves = getPseudoLegalMoves(tempGrid, tempPiece, j);

                    // Check if the opponent can capture our king. If so, remove our move from the list
                    for (int k = 0; k < tempOpponentMoves.size(); k++)
                    {
                        int kingLocation = pieceLocations.getKingLocation(turn);
                        if (tempOpponentMoves.get(k).targetSquare == (move.startSquare == kingLocation ? move.targetSquare : kingLocation))
                        {
                            // Move is illegal
                            moves.remove(i);
                            i--;
                            isIllegal = true;
                            break;
                        }
                    }
                }

                if (isIllegal == true)
                {
                    break;
                }
            }
        }

        movesGenerated = true;
    }

    public ArrayList<Move> getPseudoLegalMoves(Piece[] grid, Piece piece, int position)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        if (piece.type == 'k')
        {
            moves.addAll(getKingMoves(grid, piece, position));
        }
        else if (piece.type == 'q')
        {
            moves.addAll(getQueenMoves(grid, piece, position));
        }
        else if (piece.type == 'r')
        {
            moves.addAll(getRookMoves(grid, piece, position));
        }
        else if (piece.type == 'b')
        {
            moves.addAll(getBishopMoves(grid, piece, position));
        }
        else if (piece.type == 'n')
        {
            moves.addAll(getKnightMoves(grid, piece, position));
        }
        else if (piece.type == 'p')
        {
            moves.addAll(getPawnMoves(grid, piece, position));
        }

        return moves;
    }

    public void highlightMoves()
    {
        highlightedMoves = new ArrayList<Move>();

        for (int i = 0; i < moves.size(); i++)
        {
            if (moves.get(i).startSquare == pieceController.draggedPieceBoardPos.y * 8 + pieceController.draggedPieceBoardPos.x)
            {
                highlightedMoves.add(moves.get(i));
            }
        }
    }

    public void unhighlightMoves()
    {
        highlightedMoves.clear();
    }

    private ArrayList<Move> filterIllegalMoves(ArrayList<Move> moves)
    {


        return moves;
    }

    private ArrayList<Move> getKingMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        int startX = startSquare % 8, startY = startSquare / 8;

        for (int i = startY - 1; i <= startY + 1; i++)
        {
            for (int j = startX - 1; j <= startX + 1; j++)
            {
                if (j < 0 || j > 7 || i < 0 || i > 7)
                {
                    continue;
                }

                int targetSquare = i * 8 + j;
                Piece targetPiece = grid[targetSquare];

                if (targetPiece != null)
                {
                    if (targetPiece.color == piece.color)
                    {
                        continue;
                    }

                    moves.add(new Move(startSquare, targetSquare));
                }
                else
                {
                    moves.add(new Move(startSquare, targetSquare));
                }
            }
        }

        // Castling Moves
        Piece king = grid[startSquare];
        if (!king.hasMoved)
        {
            for (int dir = -1; dir <= 1; dir += 2)
            {
                int castleLength = squaresToEdge[startSquare][(dir == 1) ? 3 : 2];
                boolean canCastle = true;

                for (int i = 1; i < castleLength; i++)
                {
                    if (grid[startSquare + i * dir] != null)
                    {
                        canCastle = false;
                    }
                }

                if (grid[startSquare + castleLength * dir] == null || grid[startSquare + castleLength * dir].type != 'r' || grid[startSquare + castleLength * dir].color != king.color)
                {
                    canCastle = false;
                }

                if (canCastle)
                {
                    Move castlingMove = new Move(startSquare, startSquare + 2 * dir);
                    castlingMove.isCastling = true;
                    moves.add(castlingMove);
                }
            }
        }

        return moves;
    }

    private ArrayList<Move> getQueenMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
        {
            for (int n = 0; n < squaresToEdge[startSquare][i]; n++)
            {
                int targetSquare = startSquare + directions[i] * (n + 1);
                Piece targetPiece = grid[targetSquare];

                if (targetPiece != null)
                {
                    if (targetPiece.color == piece.color)
                    {
                        break;
                    }

                    moves.add(new Move(startSquare, targetSquare));

                    if (targetPiece.color != piece.color)
                    {
                        break;
                    }
                }
                else
                {
                    moves.add(new Move(startSquare, targetSquare));
                }
            }
        }

        return moves;
    }

    private ArrayList<Move> getRookMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = 0; i < 4; i++)
        {
            for (int n = 0; n < squaresToEdge[startSquare][i]; n++)
            {
                int targetSquare = startSquare + directions[i] * (n + 1);
                Piece targetPiece = grid[targetSquare];

                if (targetPiece != null)
                {
                    if (targetPiece.color == piece.color)
                    {
                        break;
                    }

                    moves.add(new Move(startSquare, targetSquare));

                    if (targetPiece.color != piece.color)
                    {
                        break;
                    }
                }
                else
                {
                    moves.add(new Move(startSquare, targetSquare));
                }
            }
        }

        return moves;
    }

    private ArrayList<Move> getBishopMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = 4; i < 8; i++)
        {
            for (int n = 0; n < squaresToEdge[startSquare][i]; n++)
            {
                int targetSquare = startSquare + directions[i] * (n + 1);
                Piece targetPiece = grid[targetSquare];

                if (targetPiece != null)
                {
                    if (targetPiece.color == piece.color)
                    {
                        break;
                    }

                    moves.add(new Move(startSquare, targetSquare));

                    if (targetPiece.color != piece.color)
                    {
                        break;
                    }
                }
                else
                {
                    moves.add(new Move(startSquare, targetSquare));
                }
            }
        }

        return moves;
    }

    private ArrayList<Move> getKnightMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        for (int i = 0; i < 8; i++)
        {
            int targetX = startSquare % 8 + knightDirections[i].x;
            int targetY = startSquare / 8 + knightDirections[i].y;

            if (targetX >= 0 && targetX <= 7 && targetY >= 0 && targetY <= 7)
            {
                int targetSquare = targetY * 8 + targetX;
                Piece targetPiece = grid[targetSquare];

                if (targetPiece != null)
                {
                    if (targetPiece.color == piece.color)
                    {
                        continue;
                    }
                }
                moves.add(new Move(startSquare, targetSquare));
            }
        }

        return moves;
    }

    private ArrayList<Move> getPawnMoves(Piece[] grid, Piece piece, int startSquare)
    {
        ArrayList<Move> moves = new ArrayList<Move>();

        int directionOffset = (piece.color == 'w') ? -8 : 8;

        int targetSquare = startSquare + directionOffset;
        if (targetSquare >= 0 && targetSquare <= 63)
        {
            Piece targetPiece = grid[targetSquare];

            if (targetPiece == null)
            {
                moves.add(new Move(startSquare, targetSquare));

                // Double Pawn Movement
                targetSquare = startSquare + 2 * directionOffset;
                if (startSquare / 8 == ((piece.color == 'w') ? 6 : 1))
                {
                    targetPiece = grid[targetSquare];

                    if (targetPiece == null)
                    {
                        Move move = new Move(startSquare, targetSquare);
                        if (targetSquare / 8 == (piece.color == 'w' ? 0 : 7))
                        {
                            move.isPromotion = true;
                            moves.add(move);
                        }
                        else
                        {
                            moves.add(move);
                        }
                    }
                }
            }
        }

        targetSquare = startSquare + directionOffset - 1;
        if (targetSquare >= 0 && targetSquare <= 63)
        {
            Piece targetPiece = grid[targetSquare];

            if (targetPiece != null && targetPiece.color != piece.color)
            {
                Move move = new Move(startSquare, targetSquare);
                if (targetSquare / 8 == (piece.color == 'w' ? 0 : 7))
                {
                    move.isPromotion = true;
                    moves.add(move);
                }
                else
                {
                    moves.add(move);
                }
            }
        }

        targetSquare = startSquare + directionOffset + 1;
        if (targetSquare >= 0 && targetSquare <= 63)
        {
            Piece targetPiece = grid[targetSquare];

            if (targetPiece != null && targetPiece.color != piece.color)
            {
                Move move = new Move(startSquare, targetSquare);
                if (targetSquare / 8 == (piece.color == 'w' ? 0 : 7))
                {
                    move.isPromotion = true;
                    moves.add(move);
                }
                else
                {
                    moves.add(move);
                }
            }
        }

        return moves;
    }

}