package durak.app

import android.os.Bundle
import android.view.Window.FEATURE_NO_TITLE
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.createGraph
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import durak.app.config.ConfigViewModel
import durak.app.config.LocalCardScale
import durak.app.config.LocalConfig
import durak.app.ui.CircularReveal
import durak.app.ui.DeckColumn
import durak.app.ui.DurakTheme
import durak.app.utils.NavFeature
import durak.app.utils.p8
import durak.app.utils.w
import durak.app.utils.z
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : androidx.activity.ComponentActivity() {
    val mainViewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var navFeatures: @JvmSuppressWildcards Set<NavFeature>

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val config = hiltViewModel<ConfigViewModel>()
            CompositionLocalProvider(LocalConfig provides config, LocalCardScale provides config.value.scale) {
                val bottomSheetNavigator = rememberBottomSheetNavigator()
                val navController = rememberAnimatedNavController(bottomSheetNavigator)
                val graph = rememberNavGraph(navController, navFeatures.sortedBy { it.index })
                CircularReveal(config.value.theme) { theme ->
                    DurakTheme(theme) {
                        ModalBottomSheetLayout(bottomSheetNavigator) {
                            AnimatedNavHost(navController, graph)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberNavGraph(navController: NavHostController, navFeatures: List<NavFeature>) = remember {
    navController.createGraph("index") {
        composable("index") {
            DeckColumn(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(R.string.app_name), Modifier.p8.z, style = MaterialTheme.typography.h1)
                navFeatures.forEach {
                    Button({ navController.navigate(it.route) }, Modifier.w.p8.z) { Text(stringResource(it.title), Modifier.p8) }
                }
            }
        }
        navFeatures.forEach {
            it.builder(this, navController)
        }
    }
}
