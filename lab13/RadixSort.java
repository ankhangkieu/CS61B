import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Arrays;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra
 * @version 1.4 - April 14, 2016
 *
 **/
public class RadixSort
{

    /**
     * Does Radix sort on the passed in array with the following restrictions:
     *  The array can only have ASCII Strings (sequence of 1 byte characters)
     *  The sorting is stable and non-destructive
     *  The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     **/
    public static String[] sort(String[] asciis)
    {
        if ( asciis == null || asciis.length <= 1) {
            return asciis;
        }
        String[] copy = new String[asciis.length];
        System.arraycopy(asciis, 0, copy, 0, asciis.length);
        sortHelper(copy, 0, copy.length-1, 0);
        return copy;
    }

    /**
     * Radix sort helper function that recursively calls itself to achieve the sorted array
     *  destructive method that changes the passed in array, asciis
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelper(String[] asciis, int start, int end, int index)
    {
        if (end - start <= 0 || asciis == null || asciis.length <= 1) {
            return;
        }
        int[] buckets = new int[257];
        for (int i = 0; i < asciis.length; i++) {
            if (asciis[i].length() <= index) {
                buckets[0] += 1;
            } else {
                int pos = asciis[i].charAt(index)+1;
                buckets[pos] += 1;
            }
        }
        String[][] sorted = new String[257][];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = new String[buckets[i]];
        }

        int[] increment = new int[257];
        for (int i = 0; i < asciis.length; i++) {
            if (index >= asciis[i].length()) {
                sorted[0][increment[0]] = asciis[i];
                increment[0] += 1;
            }else {
                int pos = asciis[i].charAt(index)+1;
                sorted[pos][increment[pos]] = asciis[i];
                increment[pos] += 1;
            }
        }
        for (int i = 0; i < sorted.length; i++) {
            if (sorted[i].length >= 2) {
                sortHelper(sorted[i], 0, sorted[i].length-1, index+1);
            }
        }
        int k = 0;
        for (int i = 0; i < sorted.length; i++) {
            for (int j = 0; j < sorted[i].length; j++, k++) {
                asciis[k] = sorted[i][j];
            }
        }
    }

    @Test
    public void radixTest() {
        String[] arr1 = new String[] {"&^%*(^&*(", "as$#F!", "+=s", "@#%#@$%F","#$%#@%@%$#$%", "@#$%", "%^&" };


        String expected[] = RadixSort.sort(arr1);
        Arrays.sort(arr1);

        assertArrayEquals(expected, arr1);
    }
}
