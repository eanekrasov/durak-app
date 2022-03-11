package durak.app.server

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.bottomSheet
import durak.app.ui.RulesEditor
import durak.app.utils.rememberComponentContext

fun NavGraphBuilder.serverGraph(base: String, navController: NavHostController) = navigation("$base/index", base) {
    composable("$base/index") { entry ->
        val vm = hiltViewModel<ServerViewModel>(entry)
        val ctx = entry.rememberComponentContext()
        ServerIndex(vm, ctx, { navController.navigate("$base/$it") }, navController::popBackStack)
    }
    bottomSheet("$base/rules") {
        val entry = remember { navController.getBackStackEntry("$base/index") }
        val vm = hiltViewModel<ServerViewModel>(entry)
        RulesEditor(vm.rules, vm::updateRules, true, onBack = navController::popBackStack)
    }
}
