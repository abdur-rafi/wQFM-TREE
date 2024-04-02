package src;

public class Utility {
    public static void addArrayToFirst(int[] a, int[] b) {
        int n = a.length;

        for (int i = 0; i < n; ++i) {
            a[i] += b[i];
        }
    }

    public static void addArrayToFirst(double[] a, double[] b){
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

    public static void subArrayToFirst(double[] a, double[] b){
        int n = a.length;

        for (int i = 0; i < n; ++i) {
            a[i] -= b[i];
        }
    }
    
    

    public static int nc2(int n){
        return (n * (n - 1)) / 2;
    }

    public static long nc2(long n){
        return (n * (n - 1)) / 2;
    }

    public static class Pair<F, S> {
        public F first;
        public S second;
    
        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }
    


}
