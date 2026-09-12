import org.junit.BeforeClass;
import org.junit.Test;

public class SetupOutputTest {
    @BeforeClass
    public static void setup() {
        System.err.println("test-setup-marker");
    }

    @Test
    public void passing() {}
}
