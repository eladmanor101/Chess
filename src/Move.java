public class Move
{
    int startSquare, targetSquare;
    boolean isCastling;
    boolean isPromotion;

    public Move(int startSquare, int targetSquare)
    {
        this.startSquare = startSquare;
        this.targetSquare = targetSquare;
    }
}