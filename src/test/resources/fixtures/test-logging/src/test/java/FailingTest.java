import org.junit.Test;

public class FailingTest {
    @Test
    public void failing() {
        System.out.println("test-stdout-marker");
        System.err.println("test-stderr-marker");
        try {
            failWithCause();
        } catch (IllegalArgumentException cause) {
            throw new IllegalStateException("outer-failure-marker", cause);
        }
    }

    private void failWithCause() {
        throw new IllegalArgumentException("inner-failure-marker");
    }
}
