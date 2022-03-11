@file:Suppress("BlockingMethodInNonBlockingContext")
@file:SuppressLint("MissingPermission")

package durak.app.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException

actual class Socket(private val socket: BluetoothSocket) {
    actual val name get() = socket.remoteDevice.name ?: "unknown"
    actual val address get() = socket.remoteDevice.address!!

    actual suspend fun readNullTerminated(io: CoroutineDispatcher, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit) {
        var buffer = ByteArray(8192)
        var i = 0
        while (true) {
            val it = withContext(io) {
                try {
                    var data: String? = null
                    while (data == null) {
                        when (val c = socket.inputStream.read()) {
                            -1 -> break
                            0 -> {
                                data = buffer.copyOf(i).toString(Charsets.UTF_8)
                                i = 0
                            }
                            else -> buffer[i++] = c.toByte()
                        }
                        if (i == buffer.size) buffer = buffer.copyOf(buffer.size + 8192)
                    }
                    data
                } catch (e: IOException) {
                    e
                } catch (e: CancellationException) {
                    null
                }
            }
            when (it) {
                is String -> onSuccess(it)
                is IOException -> {
                    onFailure(it)
                    break
                }
                else -> break
            }
        }
    }

    actual suspend fun writeNullTerminated(io: CoroutineDispatcher, json: String, onFailure: (IOException) -> Unit) {
        withContext(io) {
            try {
                socket.outputStream.write("$json\u0000".toByteArray(Charsets.UTF_8))
                null
            } catch (e: IOException) {
                e
            } catch (e: CancellationException) {
                null
            }
        }?.let { onFailure(it) }
    }

    actual fun close() = socket.close()

    companion object {
        operator fun invoke(socket: BluetoothSocket?) = if (socket != null) Socket(socket) else null
    }
}
