import org.junit.Ignore;
import org.junit.Test;

public class QuietTest {
    @Test
    public void passing() {}

    @Ignore("intentionally skipped")
    @Test
    public void skipped() {}
}
