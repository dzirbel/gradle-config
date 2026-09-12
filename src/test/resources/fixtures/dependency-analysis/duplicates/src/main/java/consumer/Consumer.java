package consumer;
import leaf.Leaf;
import extra.Extra;
import shared.Duplicate;
public class Consumer {
    public static int value() { return Leaf.value() + Extra.value() + Duplicate.value(); }
}
