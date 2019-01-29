package game;

public class PiecePrinter {
    public static Integer value = 5;

    private static Character vertical = 'R';
    private static Character left = 'G';
    private static Character right = 'B';

    public static void main(String[] args) {
        // This is an example of how to use the function below.
        System.out.println(printPiece("RGB3"));
    }
    
    // TODO add method for WWW1 input
    
    public static String printPiece(String s){
        int val = Integer.parseInt(String.valueOf(s.charAt(3)));
        return printPiece(val, s.charAt(0), s.charAt(1), s.charAt(2));
    }

    public static String printPiece(Integer value, Character verticalIn, Character leftIn, Character rightIn) {
        String right = rightIn.toString();
        String left = leftIn.toString();
        String vertical = verticalIn.toString();


        // All lists should have exactly 36 items.
        String template =
            "     ^\n" +
                "    / \\\n" +
                "   /   \\\n" +
                "  /{f0} {fv} {f1}\\\n" +
                " /   {f2}   \\\n" +
                "/---------\\\n";

        // Fill in value


        // Fill in left colors
        template = template.replace("{f0}", left);
        template = template.replace("{f1}", right);
        template = template.replace("{f2}", vertical);
        template = template.replace("{fv}", value.toString());

        return template;
    }
}


