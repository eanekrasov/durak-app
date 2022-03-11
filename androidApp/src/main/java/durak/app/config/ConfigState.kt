@file:Suppress("unused")

package durak.app.config

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.drop

internal class ConfigState(initial: Config = Config.defaultValue) : MutableState<Config> {
    var readValue by mutableStateOf(initial)
    var writeValue by mutableStateOf(initial)
    override fun component1() = value
    override fun component2() = ::value::set
    override var value: Config
        get() = ::readValue.get()
        set(value) = ::writeValue.set(value)
}

@Composable
fun DataStore<Config>.collectAsMutableState(): MutableState<Config> = remember { ConfigState() }.apply {
    LaunchedEffect(this) { data.collect { config -> readValue = config } }
    LaunchedEffect(this) { snapshotFlow { writeValue }.drop(1).collect { config -> updateData { config } } }
}
