package src;

public class Utility {
    public static void addIntArrToFirst(int[] a, int[] b) {
        int n = a.length;

        for (int i = 0; i < n; ++i) {
            a[i] += b[i];
        }
    }


    public static void subIntArrToFirst(int[] a, int[] b) {
        int n = a.length;

        for (int i = 0; i < n; ++i) {
            a[i] -= b[i];
        }
    }
    
    

    public static int nc2(int n){
        return (n * (n - 1)) / 2;
    }

}
