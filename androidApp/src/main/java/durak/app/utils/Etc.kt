package durak.app.utils

import kotlin.contracts.contract

inline fun <T> List<T>.fastForEachIndexed(action: (Int, T) -> Unit) {
    contract { callsInPlace(action) }
    for (index in indices) {
        val item = get(index)
        action(index, item)
    }
}

inline fun <T> Collection<T>.fastForEach(action: (T) -> Unit) {
    contract { callsInPlace(action) }
    for (item in this) {
        action(item)
    }
}

inline fun <T> List<T>.fastFirstOrNull(predicate: (T) -> Boolean): T? {
    contract { callsInPlace(predicate) }
    fastForEach { if (predicate(it)) return it }
    return null
}

inline fun <T> List<T>.fastAny(predicate: (T) -> Boolean): Boolean {
    contract { callsInPlace(predicate) }
    fastForEach { if (predicate(it)) return true }
    return false
}

inline fun <T> List<T>.fastAll(predicate: (T) -> Boolean): Boolean {
    contract { callsInPlace(predicate) }
    fastForEach { if (!predicate(it)) return false }
    return true
}

inline fun <T> Map<String, T>.fastForEach(action: (String, T) -> Unit) {
    contract { callsInPlace(action) }
    for (item in entries) {
        action(item.key, item.value)
    }
}

fun lerp(start: Float, stop: Float, fraction: Float): Float = (1 - fraction) * start + fraction * stop
