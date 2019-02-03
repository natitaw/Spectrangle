package view;

import controller.client.Client;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class adapted from SpectrangleBoardPrinter as provided on Canvas.
 * Most methods take pieces (or strings such as RRR6) as input,
 * and output nicely formatted ASCII art.
 */
public class PiecePrinter {

    /**
     * Alternate method for printPiece. Allows using a short string representation as
     * specified in the protocol (RRR6 for example) to represent a piece.
     * @param s String representation of piece
     * @return ASCII art of piece.
     */
    private static String printPiece(String s) {
        int val = Integer.parseInt(String.valueOf(s.charAt(3)));
        return printPiece(val, s.charAt(0), s.charAt(1), s.charAt(2));
    }

    /**
     * Method that returns ASCII art of piece.
     * @param value value of piece
     * @param verticalIn vertical color (Character) of piece
     * @param leftIn left color (Character) of piece
     * @param rightIn right color (Character) of piece
     * @return ASCII art of piece
     */
    public static String printPiece(Integer value, Character verticalIn, Character leftIn, Character rightIn) {
        String right = rightIn.toString();
        String left = leftIn.toString();
        String vertical = verticalIn.toString();

        String template =
            "     ^     \n" +
                "    / \\    \n" +
                "   /   \\   \n" +
                "  /{f0} {fv} {f1}\\  \n" +
                " /   {f2}   \\ \n" +
                "/---------\\\n";


        // Fill template
        template = template.replace("{f0}", left);
        template = template.replace("{f1}", right);
        template = template.replace("{f2}", vertical);
        template = template.replace("{fv}", value.toString());

        return template;
    }

    /**
     * Returns a string for printing an "empty" piece in the printTiles method
     * @return a strng of 6 lines with the same amount of spaces.
     */
    public static String printEmptyPiece() {
        return "           \n" +
            "           \n" +
            "           \n" +
            "           \n" +
            "           \n" +
            "           \n";
    }

    /**
     * Prints 4 ASCII art representations of pieces next to each other
     * And adds numbers [1] [2] [3] [4] below it
     * For use in choosing a piece in TerminalInputHandler
     * @param clientObject client that this is called from
     */
    public static void printTiles(Client clientObject) {
        clientObject.getPrinter().println("You have tiles:");

        List<String[]> pieceLineList = new ArrayList<>();
        String[] resultArray;
        for (String t : clientObject.getClientTiles()) {
            Piece tempPiece = new Piece(t);
            String pieceString = tempPiece.toPrinterString();
            String[] pieceLines = pieceString.split(Pattern.quote("\n"));
            pieceLineList.add(pieceLines);
        }
        resultArray = new String[] {"", "", "", "", "", ""};

        for (int lineNr = 0; lineNr < resultArray.length; lineNr++) {
            for (String[] tempPieceLines : pieceLineList) {
                resultArray[lineNr] = resultArray[lineNr].concat(" ").concat(tempPieceLines[lineNr]);
            }
        }
        String tilesPrinted = String.join("\n", resultArray);
        clientObject.getPrinter().println(tilesPrinted);
        clientObject.getPrinter().println("");
        clientObject.getPrinter().println("     [1]    " + " " + "    [2]    " + " " + "    [3]    " + " " + "    [4]    ");
        clientObject.getPrinter().println("");
    }
}


