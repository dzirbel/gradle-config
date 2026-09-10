import kotlin.test.Test
import kotlin.test.assertEquals

class FeaturesTest {
    @Test
    fun experimentsWork() {
        assertEquals("Hello Ada", greetAda())
        assertEquals(listOf(1, 2, 3), numbers())
        assertEquals("Ada", destructure())
    }
}
