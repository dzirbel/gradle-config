import org.junit.Test;

public class OutputTest {
    @Test
    public void stdout() {
        System.out.println("test-stdout-marker");
    }

    @Test
    public void stderr() {
        System.err.println("test-stderr-marker");
    }
}
