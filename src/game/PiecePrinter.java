package game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class PiecePrinter {
    private static List<Integer> bonuses =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
    private static List<Integer> values =        Arrays.asList(5,   null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> vertical =    Arrays.asList('R', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> left =        Arrays.asList('G', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> right =       Arrays.asList('B', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    public static void main(String[] args) {
        // This is an example of how to use the function below.
        System.out.println(getBoardString(values, vertical, left, right));
    }

    /**
     * Method to print a board of Spectrangle, given the properties of the pieces that reside on it.
     *
     * The arguments to this method together represent the current state of the board. You will need to generate
     * these arguments yourself. Each argument needs to be a list of exactly 36 items, representing the 36 fields
     * on your board. The index in the list corresponds with the index of the field on the board, as they were
     * explained in the slides of the Project Design session in week 6.
     *
     * An example of how you would generate the 'values' list is as follows, assuming your board has a List of fields,
     * which have an attribute called "value" to represent the value of the piece that is placed in that field, or
     * null if there is no piece in that field:
     *
     * <pre>{@code
     * List<Integer> values = new ArrayList<>();
     * for(int i = 0; i < this.getFields().size(); i++) {
     *     values.add(this.getField(i).value);
     * }
     * }</pre>
     *
     * The SpectrangleBoardPrinter class has a main method which prints out a board where only
     * the first field (index 0) is filled with a piece with value 5 and the colors red on the bottom,
     * green on the left and blue on the right. It uses the ArrayLists defined on the top of the file.
     *
     * @param values The values of all fields on the board. This should be a List of exactly 36 items.
     *               If the field has no piece on it, the value is 'null', and if it does have a piece
     *               on it, it is the integer value of the piece.
     * @param vertical The letters of the vertical colors of all fields on the board. This should be a List of exactly
     *                 36 items. If the field has no piece on it, the value is 'null', and if it does have a piece
     *                 on it, the value is a character representing the color of the top or bottom side of the piece.
     * @param left The letters of the vertical colors of all fields on the board. This should be a List of exactly
     *             36 items. If the field has no piece on it, the value is 'null', and if it does have a piece
     *             on it, the value is a character representing the color of the left side of the piece.
     * @param right The letters of the vertical colors of all fields on the board. This should be a List of exactly
     *              36 items. If the field has no piece on it, the value is 'null', and if it does have a piece
     *              on it, the value is a character representing the color of the right side of the piece.
     * @return A string representing the state of the board as given.
     */
    public static String getBoardString(List<Integer> values, List<Character> vertical, List<Character> left, List<Character> right){
        // All lists should have exactly 36 items.
        if(!Stream.of(values, vertical, left, right).parallel().map(List::size).allMatch(n -> n == 36)){
            throw new IllegalArgumentException("Input lists should all have 36 items, one for each field on the board.");
        }
        String template =
            "     ^\n" +
            "    / \\\n" +
            "   / {f0b} \\\n" +
            "  /{f00}{f0v} {f01}\\\n" +
            " /   {f02}   \\\n" +
            "/---------\\\n";

        // Fill in bonus values
        template = listToMap(bonuses).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "b}", elem.getValue() != 1 ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in values
        template = listToMap(values).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "v}", elem.getValue() != null ? String.format("%2d", elem.getValue()) : String.format("%2d", elem.getKey())), (s, s2) -> s);

        // Fill in left colors
        template = listToMap(left).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "0}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in right colors
        template = listToMap(right).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "1}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in vertical colors
        template = listToMap(vertical).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "2}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        return template;
    }

    private static <K> Map<Integer, K> listToMap(List<K> inputList){
        Map<Integer, K> indexed_values = new HashMap<>();
        for(int i = 0; i < values.size(); i++){ indexed_values.put(i, inputList.get(i)); }
        return indexed_values;
    }
}
