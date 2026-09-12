package consumer;
import bridge.Bridge;
import leaf.Leaf;
public class Consumer {
    public static int value() { return Bridge.value() + Leaf.value(); }
}
