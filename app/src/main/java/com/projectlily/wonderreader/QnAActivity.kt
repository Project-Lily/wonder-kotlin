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
import com.projectlily.wonderreader.ui.screens.FolderScreen
import com.projectlily.wonderreader.ui.screens.QnAScreen
import com.projectlily.wonderreader.ui.screens.SendToDeviceScreen

class QnAChosen {
    var chosenItemIndex: Int by mutableStateOf(-1)
    var chosenItemCategory: String by mutableStateOf("")
}

fun NavGraphBuilder.qnaNavGraph(navController: NavController) {
    val chosenQna = QnAChosen() // TODO
    var dataMath = mutableListOf<QnA>()
    var dataLanguage = mutableListOf<QnA>()
    var dataScience = mutableListOf<QnA>()
    var dataSocialScience = mutableListOf<QnA>()
//    var data = mutableListOf<QnA>()

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

//        QnaService.getAllQnaFromFolder(chosenQna.chosenItemCategory, onSuccessListener = {
//            data = it // TODO: Not in the right order to get data
//        })
    }

    navigation(startDestination = Screen.QnA.route, route="QnA_Root") {
        composable(Screen.AddQnA.route) {
            ScaffoldScreen(navController) {
                AddQnAScreen(it)
            }
        }
        composable(Screen.QnA.route) {
            ScaffoldScreen(navController) {
//                QnAScreen(chosenQna, data, it)
                FolderScreen(chosenQna, navController, it)
            }
        }
        composable(Screen.FolderMath.route) {
            ScaffoldScreen(navController) {
//                FolderScreen(chosenQna, navController, it)
                QnAScreen(chosenQna, dataMath, it)
//                QnAScreen(chosenQna, data, it)
            }
        }
        composable(Screen.FolderLanguage.route) {
            ScaffoldScreen(navController) {
//                FolderScreen(chosenQna, navController, it)
                QnAScreen(chosenQna, dataLanguage, it)
//                QnAScreen(chosenQna, data, it)
            }
        }
        composable(Screen.FolderScience.route) {
            ScaffoldScreen(navController) {
//                FolderScreen(chosenQna, navController, it)
                QnAScreen(chosenQna, dataScience, it)
//                QnAScreen(chosenQna, data, it)
            }
        }
        composable(Screen.FolderSocialScience.route) {
            ScaffoldScreen(navController) {
//                FolderScreen(chosenQna, navController, it)
                QnAScreen(chosenQna, dataSocialScience, it)
//                QnAScreen(chosenQna, data, it)
            }
        }
        composable(Screen.SendToDevice.route) {
            ScaffoldScreen(navController) {
                SendToDeviceScreen(chosenQna, dataMath) // TODO: Change to access appropriate category data
//                SendToDeviceScreen(chosenQna, data)
            }
        }
    }
}