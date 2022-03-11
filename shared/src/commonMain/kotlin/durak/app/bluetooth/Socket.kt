package durak.app.bluetooth

import kotlinx.coroutines.CoroutineDispatcher
import java.io.IOException

expect class Socket {
    suspend fun readNullTerminated(io: CoroutineDispatcher, onSuccess: (String) -> Unit, onFailure: (IOException) -> Unit)
    suspend fun writeNullTerminated(io: CoroutineDispatcher, json: String, onFailure: (IOException) -> Unit = {})
    fun close()
    val name: String
    val address: String
}
