package com.projectlily.wonderreader

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.screens.SendToDeviceScreen


fun NavGraphBuilder.qnaNavGraph(navController: NavController, qnaState: QnAChosen) {
    val folderMath = "Math" // TODO

    navigation(startDestination = Screen.QnA.route, route="QnA_Root") {
        composable(Screen.QnA.route) { QnAScreen(qnaState) }
        composable(Screen.SendToDevice.route) { SendToDeviceScreen(qnaState) }
    }
}