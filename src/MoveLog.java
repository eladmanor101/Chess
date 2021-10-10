public class MoveLog
{
    int startSquare, targetSquare;
    Piece takenPiece;
    boolean isCastling;
    boolean isPromotion;

    public MoveLog(int startSquare, int targetSquare, Piece takenPiece)
    {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;

        this.takenPiece = takenPiece;
    }
}