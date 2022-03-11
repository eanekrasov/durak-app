package durak.app.utils

import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.savedstate.SavedStateRegistryOwner
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.backpressed.BackPressedHandler
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.lifecycle.asEssentyLifecycle
import com.arkivanov.essenty.statekeeper.StateKeeper

@Composable
fun rememberComponentContext(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    savedStateRegistryOwner: SavedStateRegistryOwner = LocalSavedStateRegistryOwner.current,
    viewModelStoreOwner: ViewModelStoreOwner? = LocalViewModelStoreOwner.current,
    onBackPressedDispatcherOwner: OnBackPressedDispatcherOwner? = LocalOnBackPressedDispatcherOwner.current
) = remember {
    DefaultComponentContext(
        lifecycleOwner.lifecycle.asEssentyLifecycle(),
        savedStateRegistryOwner.savedStateRegistry.let(::StateKeeper),
        viewModelStoreOwner?.viewModelStore?.let(::InstanceKeeper),
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.let(::BackPressedHandler)
    )
}

@Composable
fun NavBackStackEntry.rememberComponentContext(): ComponentContext = rememberComponentContext(this, this, this, LocalOnBackPressedDispatcherOwner.current)

@Composable
fun <T> NavBackStackEntry.rememberComponent(componentContext: ComponentContext = rememberComponentContext(), block: (ComponentContext) -> T): T = remember { block(componentContext) }
