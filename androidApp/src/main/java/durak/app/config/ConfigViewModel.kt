package durak.app.config

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import durak.app.di.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardsConfig @Inject constructor() {
    var isAnimated = mutableStateOf(true)
}

@HiltViewModel
class ConfigViewModel @Inject constructor(
    val configStore: DataStore<Config>,
    @IODispatcher val io: CoroutineDispatcher,
    val cardsConfig: CardsConfig
) : ViewModel(), MutableState<Config> {
    val config = configStore.data.stateIn(viewModelScope, SharingStarted.Eagerly, Config.defaultValue)
    var readConfig by mutableStateOf(config.value)
    var writeConfig by mutableStateOf(config.value)
    fun updateConfig(block: Config.() -> Config) = run { value = value.block() }
    override fun component1() = value
    override fun component2() = ::value::set
    override var value: Config
        get() = ::readConfig.get()
        set(value) = ::writeConfig.set(value)

    init {
        viewModelScope.launch {
            configStore.data.collect { config -> readConfig = config }
        }
        viewModelScope.launch {
            snapshotFlow { writeConfig }.drop(1).collect { config -> configStore.updateData { config } }
        }
    }
}
