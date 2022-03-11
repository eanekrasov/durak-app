package durak.app.config

import androidx.datastore.core.CorruptionException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
object ConfigSerializer : androidx.datastore.core.Serializer<Config> {
    override val defaultValue = Config.defaultValue
    override suspend fun readFrom(input: InputStream) = try {
        Json.decodeFromStream<Config>(input)
    } catch (e: SerializationException) {
        throw CorruptionException("Unable to deserialize Config", e)
    }

    override suspend fun writeTo(t: Config, output: OutputStream) = try {
        Json.encodeToStream(t, output)
    } catch (e: SerializationException) {
        throw CorruptionException("Unable to serialize Config", e)
    }
}
