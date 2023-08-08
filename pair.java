package wqfm.dsGT;

public class pair {
    int f, s;

    pair(int a, int b) {
        f = a;
        s = b;
    }

    pair sub(pair b) {
        return new pair(f - b.f, s - b.s);
    }

    pair add(pair b) {
        return new pair(f + b.f, s + b.s);
    }

    @Override
    public String toString() {
        return "" + f + " " + s;
    }
}