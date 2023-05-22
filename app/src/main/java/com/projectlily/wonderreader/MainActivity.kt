package com.projectlily.wonderreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projectlily.wonderreader.ui.components.ActionButton
import com.projectlily.wonderreader.ui.components.BottomNavBar
import com.projectlily.wonderreader.ui.components.TopBar
import com.projectlily.wonderreader.ui.screens.HomeScreen
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

sealed class Screen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val action: String = "",
    val actionButton: ImageVector = Icons.Filled.Send
) {
    object Home : Screen("Home", R.string.home, Icons.Default.Home, "Add QnA", Icons.Filled.Add)
    object QnA :
        Screen("QnA", R.string.qna, Icons.Default.QuestionAnswer, "Send", Icons.Filled.Send)

    object Debug : Screen("Debug", R.string.debug, Icons.Default.Build)

    //    This shouldn't be on bottom bar, just here for testing
    object Auth : Screen("Auth", R.string.auth, Icons.Default.AccountBox)
}

val items = listOf(
    Screen.Home,
    Screen.QnA,
    Screen.Debug,
    Screen.Auth
)

@Composable
fun MainApp() {
    WonderReaderTheme {
        val navController = rememberNavController()

        Scaffold(
            topBar = { TopBar(navController) },
            floatingActionButton = { ActionButton(navController, items) },
            bottomBar = { BottomNavBar(navController, items) }) { padding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.QnA.route) { QnAScreen() }
                composable(Screen.Debug.route) { DebugScreen() }
                composable(Screen.Auth.route) { AuthScreen() }
            }
        }
    }
}
