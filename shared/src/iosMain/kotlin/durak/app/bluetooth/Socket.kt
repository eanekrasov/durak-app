package durak.app.bluetooth

import kotlinx.coroutines.CoroutineDispatcher
import java.io.IOException

actual class Socket(/*private val socket: android.bluetooth.BluetoothSocket*/) {
    actual suspend fun readNullTerminated(io: CoroutineDispatcher, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) = Unit
    actual suspend fun writeNullTerminated(io: CoroutineDispatcher, json: String, onFailure: (IOException) -> Unit) = Unit
    actual fun close() = Unit
    actual val name get() = "unknown"
    actual val address get() = "address"
}
