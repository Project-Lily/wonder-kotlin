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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projectlily.wonderreader.ui.components.ActionButton
import com.projectlily.wonderreader.ui.components.BottomNavBar
import com.projectlily.wonderreader.ui.components.TopBar
import com.projectlily.wonderreader.ui.screens.AddQnAScreen
import com.projectlily.wonderreader.ui.screens.HomeScreen
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
    val icon: ImageVector = Icons.Filled.Home,
    val action: String = "",
    val actionButton: ImageVector = Icons.Filled.Send,
    val actionButtonDestination: String = ""
) {
    object Home :
        Screen(
            "Home", R.string.home, Icons.Default.Home,
            "Add QnA", Icons.Filled.Add, "Add QnA"
        )

    object QnA :
        Screen(
            "QnA", R.string.qna, Icons.Default.QuestionAnswer,
            "Send", Icons.Filled.Send, "Send To Device"
        )

    object Debug : Screen("Debug", R.string.debug, Icons.Default.Build)

    //    This shouldn't be on bottom bar, just here for testing
    object Auth : Screen("Auth", R.string.auth, Icons.Default.AccountBox)

    object AddQnA : Screen("Add QnA", R.string.add_qna)
    object SendToDevice : Screen("Send To Device", R.string.send_to_device)
    object FolderMath : Screen("Math", R.string.math)
}

val screenItems = listOf(
    Screen.Home,
    Screen.QnA,
    Screen.Debug,
    Screen.Auth,
    Screen.AddQnA,
    Screen.SendToDevice
)

val navBarItems = listOf(
    Screen.Home,
    Screen.QnA,
    Screen.Debug,
    Screen.Auth
)

class QnAChosen {
    var chosenItemIndex: Int by mutableStateOf(-1)
    var chosenItemCategory: String by mutableStateOf("")
}

@Composable
fun MainApp() {
    WonderReaderTheme {
        val navController = rememberNavController()
        val qnaState = remember { QnAChosen() }

        Scaffold(
            topBar = { TopBar(navController, screenItems, navBarItems) },
            floatingActionButton = { ActionButton(navController, screenItems) },
            bottomBar = { BottomNavBar(navController, screenItems, navBarItems) }) { padding ->
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Debug.route) { DebugScreen() }
                composable(Screen.Auth.route) { AuthScreen() }
                composable(Screen.AddQnA.route) { AddQnAScreen() }
                qnaNavGraph(navController, qnaState)
            }
        }
    }
}
