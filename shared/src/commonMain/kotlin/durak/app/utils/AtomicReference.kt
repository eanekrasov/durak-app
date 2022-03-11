package durak.app.utils

expect class AtomicReference<V>(initialValue: V) {
    fun get(): V
    fun set(value_: V)

    /** Compare current value with expected and set to new if they're the same. Note, 'compare' is checking the actual object id, not 'equals'. */
    fun compareAndSet(expected: V, new: V): Boolean
}

var <T> AtomicReference<T>.value: T
    get() = get()
    set(value) = set(value)
