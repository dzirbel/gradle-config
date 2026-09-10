import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FeaturesTest {
    @Test
    fun experimentsAndAssertionsWork() {
        assertEquals(1, implicitApi())
        assertEquals("Hello Ada", greetAda())
        assertEquals(listOf(1, 2, 3), numbers())
        assertEquals("Ada", destructure())
        assertFailsWith<AssertionError> { failAssertion() }
    }
}
