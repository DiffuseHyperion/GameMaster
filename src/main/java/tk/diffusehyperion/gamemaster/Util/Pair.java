package tk.diffusehyperion.gamemaster.Util;

public class Pair<X, Y> {
    private final X value0;
    private final Y value1;

    public Pair(X value0, Y value1) {
        this.value0 = value0;
        this.value1 = value1;
    }

    public X getValue0() {
        return value0;
    }

    public Y getValue1() {
        return value1;
    }
}
