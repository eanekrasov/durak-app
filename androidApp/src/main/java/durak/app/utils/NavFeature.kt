package durak.app.utils

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController

/**
 * One should follow contract for builder to define single composable in a given NavGraphBuilder
 */
data class NavFeature(
    val route: String,
    val title: Int,
    val index: Int = 0,
    val enabled: Boolean = true,
    val builder: NavGraphBuilder.(NavHostController) -> Unit
)
