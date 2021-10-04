import java.util.HashMap;
import java.util.ArrayList;

public class PieceLocations
{
    private HashMap<Piece, ArrayList<Integer>> pieceLocations = new HashMap<>();

    public ArrayList<Integer> getPieceLocation(Piece piece)
    {
        return pieceLocations.get(piece);
    }

    public void addPiece(Piece piece, int position)
    {
        if (!pieceLocations.containsValue(piece))
        {
            pieceLocations.put(piece, new ArrayList<Integer>());
        }
        pieceLocations.get(piece).add(position);
        System.out.println(pieceLocations.get(piece).get(0));
    }

    public void updatePiece(Piece piece, int fromSquare, int toSquare)
    {
        ArrayList<Integer> locations = pieceLocations.get(piece);
        for (int i = 0; i < locations.size(); i++)
        {
            if (fromSquare == locations.get(i))
            {
                locations.set(i, toSquare);
                return;
            }
        }
    }

    public void removePiece(Piece piece, int position)
    {
        ArrayList<Integer> locations = pieceLocations.get(piece);
        for (int i = 0; i < locations.size(); i++)
        {
            if (position == locations.get(i))
            {
                locations.remove(i);
                return;
            }
        }
    }

    public int getKingLocation(char color)
    {
        //System.out.println(pieceLocations.get(new Piece('k', color)));
        Piece piece = new Piece('k', color);
        return pieceLocations.get(piece).get(0);
    }
}