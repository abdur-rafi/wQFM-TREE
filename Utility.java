package wqfm.dsGT;

public class Utility {
    public static void addIntArrToFirst(int[] a, int[] b) {
        int n = a.length;

        for (int i = 0; i < n; ++i) {
            a[i] += b[i];
        }
    }

}
