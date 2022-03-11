@file:Suppress("unused")

package durak.app.utils

actual typealias Lock = platform.Foundation.NSRecursiveLock

@Suppress("NOTHING_TO_INLINE")
actual inline fun Lock.close() {
}
