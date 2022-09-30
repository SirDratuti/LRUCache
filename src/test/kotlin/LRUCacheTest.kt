import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class LRUCacheTest {

    @Test
    fun `should initialize correct with positive capacity`() {
        assertDoesNotThrow {
            lru { CORRECT_CAPACITY }
        }
    }

    @Test
    fun `should not initialize correct with positive capacity`() {
        assertThrows<IllegalArgumentException> {
            lru { INCORRECT_CAPACITY }
        }
    }

    @Test
    fun `should correctly add one element`() {
        assertDoesNotThrow {
            lru { CORRECT_CAPACITY }.apply {
                put("1", 1)
            }
        }
    }

    @Test
    fun `should correctly add size-amount of elements`() {
        assertDoesNotThrow {
            lru { CORRECT_CAPACITY }.apply {
                repeat(CORRECT_CAPACITY) {
                    put(it.toString(), it)
                }
            }
        }
    }

    @Test
    fun `should correctly add more than size amount of elements`() {
        assertDoesNotThrow {
            lru { CORRECT_CAPACITY }.apply {
                repeat(CORRECT_CAPACITY * 2) {
                    put(it.toString(), it)
                }
            }
        }
    }

    @Test
    fun `should fail if try to read non-existing value`() {
        assertThrows<IllegalArgumentException> {
            lru { INCORRECT_CAPACITY }.apply {
                get(KEY)
            }
        }
    }

    @Test
    fun `should set minimum priority after read`() {
        assertDoesNotThrow {
            lru { CORRECT_CAPACITY }.apply {
                put(KEY, 1)
                put(ANOTHER_KEY, 2)
                get(KEY)
                assert(queue.first() == KEY)
            }
        }
    }

    private companion object {
        private const val CORRECT_CAPACITY = 2
        private const val INCORRECT_CAPACITY = -2

        private const val KEY = "KEY"
        private const val ANOTHER_KEY = "ANOTHER_KEY"
    }
}

private fun lru(capacity: () -> Int) = LRUCacheFactory(capacity.invoke()).create()

private class LRUCacheFactory(
    private val capacity: Int
) {
    fun create() = LRUCacheImpl<String, Int>(capacity = capacity)
}