package durak.app.config

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.navigation.animation.composable

fun NavGraphBuilder.configGraph(base: String, navController: NavHostController) = navigation("$base/index", base) {
    composable("$base/index") { ConfigIndex(base, navController::navigate, navController::popBackStack) }
    composable("$base/settings") { ConfigSettings(navController::popBackStack) }
    composable("$base/slot") { ConfigSlot(navController::popBackStack) }
    composable("$base/layout") { ConfigLayout(navController::popBackStack) }
    composable("$base/rules") { ConfigRules(navController::popBackStack) }
}
