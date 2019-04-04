package utils;

import java.io.File;
import java.util.ArrayList;

public abstract class Utilitaire {

    public final static String XML = "xml";
    public final static String LAB = "lab";
    public final static String A2L = "a2l";
    public final static String DCM = "dcm";
    public final static String M = "m";

    /*
     * Get the extension of a file.
     */
    public static final String getExtension(File f) {

        final String s = f.getName();

        final int i = s.lastIndexOf('.');

        return (i > 0 && i < s.length() - 1) ? s.substring(i + 1).toLowerCase() : "";
    }

    public static final String getFileNameWithoutExtension(File f) {

        final String fileNameWithExtension = f.getName();

        final int i = fileNameWithExtension.lastIndexOf('.');

        return (i > 0 && i < fileNameWithExtension.length() - 1) ? fileNameWithExtension.substring(0, i) : "";
    }

    public static String[] split(final String line, final char delimiter) {
        CharSequence[] temp = new CharSequence[(line.length() / 2) + 1];
        int wordCount = 0;
        int i = 0;
        int j = line.indexOf(delimiter, 0); // first substring

        while (j >= 0) {
            temp[wordCount++] = line.substring(i, j);
            i = j + 1;
            j = line.indexOf(delimiter, i); // rest of substrings
        }

        temp[wordCount++] = line.substring(i); // last substring

        String[] result = new String[wordCount];
        System.arraycopy(temp, 0, result, 0, wordCount);

        return result;
    }

    public static String[] fastSplit(String string, char delimiter) {
        /*
         * fastpath of String.split()
         * 
         * [NOTE] it will remove empty token in the end it will not remove in-between empty tokens the same behavior as String.split(String regex)
         * 
         * [EXAMPLE] string = "boo\tboo\tboo\t\t\tboo\t\t\t\t\t"; strings = fastSplit(string, '\t') -> [boo, boo, boo, , , boo]
         */
        int off = 0;
        int next = 0;
        ArrayList<String> list = new ArrayList<>();
        while ((next = string.indexOf(delimiter, off)) != -1) {
            list.add(string.substring(off, next));
            off = next + 1;
        }
        // If no match was found, return this
        if (off == 0)
            return new String[] { string };

        // Add remaining segment
        list.add(string.substring(off, string.length()));

        // Construct result
        int resultSize = list.size();
        while (resultSize > 0 && list.get(resultSize - 1).length() == 0)
            resultSize--;
        String[] result = new String[resultSize];
        return list.subList(0, resultSize).toArray(result);
    }
}
