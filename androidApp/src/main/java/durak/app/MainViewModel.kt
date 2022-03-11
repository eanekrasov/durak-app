package durak.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO: Required to avoid datastore`s loading gap causing awkward recomposition
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val splashState = MutableStateFlow(true)
    val splash = splashState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000L)
            splashState.value = false
        }
    }
}
