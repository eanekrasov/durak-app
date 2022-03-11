@file:JvmName("LockAndroid")
@file:Suppress("unused")

package durak.app.utils

actual typealias Lock = java.util.concurrent.locks.ReentrantLock

@Suppress("NOTHING_TO_INLINE")
actual inline fun Lock.close() {
}
