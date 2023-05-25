package com.projectlily.wonderreader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.projectlily.wonderreader.ui.screens.AddQnAScreen
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.screens.SendToDeviceScreen

class QnAChosen {
    var chosenItemIndex: Int by mutableStateOf(-1)
    var chosenItemCategory: String by mutableStateOf("")
}
fun NavGraphBuilder.qnaNavGraph(navController: NavController) {
    val chosenQna = QnAChosen() // TODO

    navigation(startDestination = Screen.QnA.route, route="QnA_Root") {
        composable(Screen.AddQnA.route) {
            ScaffoldScreen(navController) {
                AddQnAScreen()
            }
        }
        composable(Screen.QnA.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna)
            }
        }
        composable(Screen.SendToDevice.route) { SendToDeviceScreen(chosenQna) }
    }
}