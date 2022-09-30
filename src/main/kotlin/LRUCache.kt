import java.util.LinkedList

class LRUCacheImpl<K, V>(
    private val capacity: Int,
) : LRUCache<K, V> {

    init {
        require(capacity > 0) { "Capacity must be greater then zero" }
    }

    private val size: Int get() = _cache.size

    private val _queue = LinkedList<K>()
    val queue get() = _queue.toList()

    private val _cache = mutableMapOf<K, V>()
    val cache get() = _cache.toMap()

    override fun get(key: K): V = withSizeEquality {
        require(key != null) { "Key must not be null" }
        check(_cache.containsKey(key)) { "No such key in cache" }

        val value = _cache[key]!! // assume that key is exists because of check
        _queue.moveToFront(key)
        value
    }

    override fun put(key: K, value: V) {
        require(key != null) { "Key must not be null" }
        require(value != null) { "Value must not be null" }

        when {
            _cache.containsKey(key) -> withSizeEquality {
                _cache[key] = value
                _queue.moveToFront(key)
            }

            else -> withSizeChanged {
                _cache[key] = value
                _queue.push(key)
                normalize()
            }
        }

        check(size <= capacity) { "Size is bigger than capacity" }
    }

    private fun withSizeChanged(changedBy: Int = 1, body: () -> Unit) {
        val preSize = size
        body()
        val diff = kotlin.math.abs(size - preSize)
        checkEquality()
        assert(diff == changedBy || preSize == capacity) {
            "Size changed by $diff, but have to change by $changedBy $size $preSize"
        }
    }

    private fun <T> withSizeEquality(body: () -> T): T {
        val preSize = size
        return body().also {
            checkEquality()
            assert(preSize == size) { "Size has changed, but should not" }
        }
    }

    private fun checkEquality() {
        check(_cache.size == _queue.size) { "Size of hash map and linked list are not equal" }
    }

    private fun normalize() = when {
        size <= capacity -> Unit
        else -> {
            _queue.removeLast().also {
                _cache.remove(it)
            }; Unit
        }
    }

    private fun LinkedList<K>.moveToFront(key: K) {
        remove(key)
        push(key)
    }
}

interface LRUCache<K, V> {
    fun get(key: K): V
    fun put(key: K, value: V)
}