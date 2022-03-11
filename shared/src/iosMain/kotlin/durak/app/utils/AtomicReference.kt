@file:Suppress("unused")

package durak.app.utils

import kotlin.native.concurrent.freeze

actual class AtomicReference<V> actual constructor(initialValue: V) {
    private val atom = kotlin.native.concurrent.AtomicReference(initialValue.freeze())
    actual fun get() = atom.value

    actual fun set(value_: V) {
        atom.value = value_.freeze()
    }

    /** Compare current value with expected and set to new if they're the same. Note, 'compare' is checking the actual object id, not 'equals'. */
    actual fun compareAndSet(expected: V, new: V) = atom.compareAndSet(expected, new.freeze())
}
