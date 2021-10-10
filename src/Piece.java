public class Piece
{
    char type;
    char color;

    boolean hasMoved = false;

    public Piece(char type, char color)
    {
        this.type = type;
        this.color = color;
    }

    public Piece(Piece piece)
    {
        this.type = piece.type;
        this.color = piece.color;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Piece))
        {
            return false;
        }
        Piece piece = (Piece) obj;
        return piece.type == type && piece.color == color;
    }

    @Override
    public int hashCode()
    {
        int result = 1;
        int prime = 31;
        result = result * prime + Character.valueOf(type).hashCode();
        result = result * prime + Character.valueOf(color).hashCode();

        return result;
    }
}