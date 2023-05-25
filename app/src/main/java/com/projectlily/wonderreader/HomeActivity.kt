package com.projectlily.wonderreader

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.projectlily.wonderreader.ui.components.ActionButton
import com.projectlily.wonderreader.ui.components.BottomNavBar
import com.projectlily.wonderreader.ui.components.TopBar
import com.projectlily.wonderreader.ui.screens.AddQnAScreen
import com.projectlily.wonderreader.ui.screens.HomeScreen

fun NavGraphBuilder.homeNavGraph(navController: NavController) {
//    Not very dry but then again NavHost can't be nested so here we are
    navigation(startDestination=Screen.Home.route, route="home_root") {
        composable(Screen.Home.route) {
            ScaffoldScreen(navController) {
                HomeScreen(it)
            }
        }
        composable(Screen.Debug.route) {
            ScaffoldScreen(navController) {
                DebugScreen(it)
            }
        }

        qnaNavGraph(navController)
    }
}

@Composable
fun ScaffoldScreen(
        navController: NavController,
        content: @Composable (Modifier) -> Unit
) {
    Scaffold(
            topBar = { TopBar(navController, screenItems, navBarItems) },
            floatingActionButton = { ActionButton(navController, screenItems) },
            bottomBar = { BottomNavBar(navController, screenItems, navBarItems) }) { padding ->
        content(Modifier.padding(padding))
    }
}