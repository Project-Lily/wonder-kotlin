package com.projectlily.wonderreader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.QnaService
import com.projectlily.wonderreader.types.QnA
import com.projectlily.wonderreader.ui.screens.AddQnAScreen
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.screens.SendToDeviceScreen

class QnAChosen {
    var chosenItemIndex: Int by mutableStateOf(-1)
    var chosenItemCategory: String by mutableStateOf("")
}
fun NavGraphBuilder.qnaNavGraph(navController: NavController) {
    val chosenQna = QnAChosen() // TODO
    var data = mutableListOf<QnA>()

    if (AuthService.auth.currentUser != null) {
        QnaService.getAllQnaFromFolder("Math", onSuccessListener = {
            data = it
        })
    }
    navigation(startDestination = Screen.QnA.route, route="QnA_Root") {
        composable(Screen.AddQnA.route) {
            ScaffoldScreen(navController) {
                AddQnAScreen(it)
            }
        }
        composable(Screen.QnA.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna, data, it)
            }
        }
        composable(Screen.SendToDevice.route) {
            SendToDeviceScreen(chosenQna, data)
        }
    }
}