package com.projectlily.wonderreader

import android.util.Log
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
import com.projectlily.wonderreader.ui.screens.FolderScreen
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.screens.SendToDeviceScreen

class QnAChosen {
    var chosenItemIndex: Int by mutableStateOf(-1)
    var chosenItemCategory: String by mutableStateOf("")
    var chosenItemQuestion: String by mutableStateOf("")
    var chosenItemAnswer: String by mutableStateOf("")
}

fun NavGraphBuilder.qnaNavGraph(navController: NavController) {
    val chosenQna = QnAChosen() // TODO
    var dataMath = mutableListOf<QnA>()
    var dataLanguage = mutableListOf<QnA>()
    var dataScience = mutableListOf<QnA>()
    var dataSocialScience = mutableListOf<QnA>()

    if (AuthService.auth.currentUser != null) {
        QnaService.getAllQnaFromFolder("Math", onSuccessListener = {
            dataMath = it
        })
        QnaService.getAllQnaFromFolder("Language", onSuccessListener = {
            dataLanguage = it
        })
        QnaService.getAllQnaFromFolder("Science", onSuccessListener = {
            dataScience = it
        })
        QnaService.getAllQnaFromFolder("Social Science", onSuccessListener = {
            dataSocialScience = it
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
                FolderScreen(navController, it)
            }
        }
        composable(Screen.FolderMath.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna, dataMath,"Math", it)
            }
        }
        composable(Screen.FolderLanguage.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna, dataLanguage, "Language", it)
            }
        }
        composable(Screen.FolderScience.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna, dataScience, "Science", it)
            }
        }
        composable(Screen.FolderSocialScience.route) {
            ScaffoldScreen(navController) {
                QnAScreen(chosenQna, dataSocialScience, "Social Science", it)
            }
        }
        composable(Screen.SendToDevice.route) {
            ScaffoldScreen(navController) {
                Log.d("Debuging", chosenQna.chosenItemIndex.toString())
                Log.d("Debuging", chosenQna.chosenItemQuestion)
                Log.d("Debuging", chosenQna.chosenItemAnswer)
                SendToDeviceScreen(chosenQna.chosenItemQuestion, chosenQna.chosenItemAnswer, it)
            }
        }
    }
}