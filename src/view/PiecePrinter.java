package view;

import controller.client.Client;
import controller.client.ClientCommands;
import model.Piece;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PiecePrinter {

    public static void main(String[] args) {
        // This is an example of how to use the function below.
        System.out.println(printPiece("RGB3"));
    }

    private static String printPiece(String s) {
        int val = Integer.parseInt(String.valueOf(s.charAt(3)));
        return printPiece(val, s.charAt(0), s.charAt(1), s.charAt(2));
    }

    public static String printPiece(Integer value, Character verticalIn, Character leftIn, Character rightIn) {
        String right = rightIn.toString();
        String left = leftIn.toString();
        String vertical = verticalIn.toString();


        // All lists should have exactly 36 items.
        String template =
            "     ^     \n" +
                "    / \\    \n" +
                "   /   \\   \n" +
                "  /{f0} {fv} {f1}\\  \n" +
                " /   {f2}   \\ \n" +
                "/---------\\\n";

        // Fill in value


        // Fill in left colors
        template = template.replace("{f0}", left);
        template = template.replace("{f1}", right);
        template = template.replace("{f2}", vertical);
        template = template.replace("{fv}", value.toString());

        return template;
    }

    public static String printEmptyPiece() {
        return "           \n" +
            "           \n" +
            "           \n" +
            "           \n" +
            "           \n" +
            "           \n";
    }

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


