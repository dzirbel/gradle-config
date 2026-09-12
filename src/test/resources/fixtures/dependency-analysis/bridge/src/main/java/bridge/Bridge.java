package bridge;
import leaf.Leaf;
public class Bridge {
    public static Leaf leaf() { return new Leaf(); }
    public static int value() { return Leaf.value(); }
}
