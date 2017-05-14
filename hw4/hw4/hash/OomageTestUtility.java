package hw4.hash;

import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        int[] buckets = new int[M];
        int lowerBound = oomages.size()/50;
        int upperBound = (int) (oomages.size()/2.5);
        for (Oomage o : oomages) {
            int bucketNum = (o.hashCode() & 0x7FFFFFFF) % M;
            buckets[bucketNum] += 1;
        }
        for (int bucketSize : buckets) {
            if (bucketSize < lowerBound || bucketSize > upperBound) {
                return false;
            }
        }
        return true;
    }
}
