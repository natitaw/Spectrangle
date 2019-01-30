package game;

import java.util.*;
import java.util.stream.Stream;

public class SpectrangleBoardPrinter {
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
     * Method to toPrinterString a board of Spectrangle, given the properties of the pieces that reside on it.
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
        String template = "\n" +
            "                               ^\n" +
            "                              / \\\n" +
            "                             / {f0b} \\\n" +
            "                            /{f00}{f0v} {f01}\\\n" +
            "                           /   {f02}   \\\n" +
            "                          /---------\\\n" +
            "                         / \\   {f22}   / \\\n" +
            "                        / {f1b} \\{f20}{f2v} {f21}/ {f3b} \\\n" +
            "                       /{f10}{f1v} {f11}\\ {f2b} /{f30}{f3v} {f31}\\\n" +
            "                      /   {f12}   \\ /   {f32}   \\\n" +
            "                     /---------X---------\\\n" +
            "                    / \\   {f52}   / \\   {f72}   / \\\n" +
            "                   / {f4b} \\{f50}{f5v} {f51}/ {f6b} \\{f70}{f7v} {f71}/ {f8b} \\\n" +
            "                  /{f40}{f4v} {f41}\\ {f5b} /{f60}{f6v} {f61}\\ {f7b} /{f80}{f8v} {f81}\\\n" +
            "                 /   {f42}   \\ /   {f62}   \\ /   {f82}   \\\n" +
            "                /---------X---------X---------\\\n" +
            "               / \\   {f102}   / \\   {f122}   / \\   {f142}   / \\\n" +
            "              / {f9b} \\{f100}{f10v} {f101}/ {f11b} \\{f120}{f12v} {f121}/ {f13b} \\{f140}{f14v} {f141}/ {f15b} \\\n" +
            "             /{f90}{f9v} {f91}\\ {f10b} /{f110}{f11v} {f111}\\ {f12b} /{f130}{f13v} {f131}\\ {f14b} /{f150}{f15v} {f151}\\\n" +
            "            /   {f92}   \\ /   {f112}   \\ /   {f132}   \\ /   {f152}   \\\n" +
            "           /---------X---------X---------X---------\\\n" +
            "          / \\   {f172}   / \\   {f192}   / \\   {f212}   / \\   {f232}   / \\\n" +
            "         / {f16b} \\{f170}{f17v} {f171}/ {f18b} \\{f190}{f19v} {f191}/ {f20b} \\{f210}{f21v} {f211}/ {f22b} \\{f230}{f23v} {f231}/ {f24b} \\\n" +
            "        /{f160}{f16v} {f161}\\ {f17b} /{f180}{f18v} {f181}\\ {f19b} /{f200}{f20v} {f201}\\ {f21b} /{f220}{f22v} {f221}\\ {f23b} /{f240}{f24v} {f241}\\\n" +
            "       /   {f162}   \\ /   {f182}   \\ /   {f202}   \\ /   {f222}   \\ /   {f242}   \\\n" +
            "      /---------X---------X---------X---------X---------\\\n" +
            "     / \\   {f262}   / \\   {f282}   / \\   {f302}   / \\   {f322}   / \\   {f342}   / \\\n" +
            "    / {f25b} \\{f260}{f26v} {f261}/ {f27b} \\{f280}{f28v} {f281}/ {f29b} \\{f300}{f30v} {f301}/ {f31b} \\{f320}{f32v} {f321}/ {f33b} \\{f340}{f34v} {f341}/ {f35b} \\\n" +
            "   /{f250}{f25v} {f251}\\ {f26b} /{f270}{f27v} {f271}\\ {f28b} /{f290}{f29v} {f291}\\ {f30b} /{f310}{f31v} {f311}\\ {f32b} /{f330}{f33v} {f331}\\ {f34b} /{f350}{f35v} {f351}\\\n" +
            "  /   {f252}   \\ /   {f272}   \\ /   {f292}   \\ /   {f312}   \\ /   {f332}   \\ /   {f352}   \\\n" +
            " /-----------------------------------------------------------\\\n";

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
