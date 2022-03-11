package durak.app.client

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.bottomSheet
import durak.app.ui.RulesEditor

fun NavGraphBuilder.clientGraph(base: String, navController: NavHostController) = navigation("$base/index", base) {
    composable("$base/index") { entry -> ClientIndex(entry, { navController.navigate("$base/$it") }, navController::popBackStack) }
    bottomSheet("$base/rules") { RulesEditor(hiltViewModel<ClientViewModel>(remember { navController.getBackStackEntry("$base/index") }).rules, {}, false, onBack = navController::popBackStack) }
}
