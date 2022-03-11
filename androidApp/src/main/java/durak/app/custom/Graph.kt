package durak.app.custom

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.material.bottomSheet
import durak.app.ui.RulesEditor
import durak.app.utils.rememberComponentContext

fun NavGraphBuilder.customGraph(base: String, navController: NavHostController) = navigation("$base/index", base) {
    composable("$base/index") { entry -> CustomIndex(hiltViewModel(entry), entry.rememberComponentContext(), { navController.navigate("$base/$it") }, navController::popBackStack) }
    bottomSheet("$base/rules") {
        val vm = hiltViewModel<CustomViewModel>(remember { navController.getBackStackEntry("$base/index") })
        RulesEditor(vm.rules, { vm.rules = it }, true, onBack = navController::popBackStack)
    }
    bottomSheet("$base/talon") {
        val vm = hiltViewModel<CustomViewModel>(remember { navController.getBackStackEntry("$base/index") })
        CustomTalon(vm, navController::popBackStack)
    }
    bottomSheet("$base/hands/{plr}", listOf(navArgument("plr") { type = NavType.IntType })) { entry ->
        val vm = hiltViewModel<CustomViewModel>(remember { navController.getBackStackEntry("$base/index") })
        CustomHand(vm, entry.arguments!!.getInt("plr"), navController::popBackStack)
    }
}
