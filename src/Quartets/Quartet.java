package src.Quartets;

import java.util.Arrays;

public class Quartet {
    
    // ab|cd
    public int a, b, c, d;

    public Quartet(int a, int b, int c, int d) {
        int[] arr1 = {a, b};
        int[] arr2 = {c, d};
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        int[][] arr = {arr1, arr2};
        Arrays.sort(arr, (x, y) -> {
            if(x[0] != y[0]) return x[0] - y[0];
            return x[1] - y[1];
        });

        this.a = arr[0][0];
        this.b = arr[0][1];
        this.c = arr[1][0];
        this.d = arr[1][1];
        
    }

    @Override
    public String toString() {
        return "(" + a + "," + b + "|" + c + "," + d + ")";
    }
}
