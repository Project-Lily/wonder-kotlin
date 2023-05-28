package com.projectlily.wonderreader.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.QnAChosen
import com.projectlily.wonderreader.types.QnA
import com.projectlily.wonderreader.ui.components.QnAList

@Composable
fun QnAScreen(qnaState: QnAChosen, data: MutableList<QnA>, category: String, modifier: Modifier = Modifier) {
    qnaState.chosenItemCategory = category
    qnaState.chosenItemIndex = -1

    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        QnAList(
            data = data,
            isChoosing = true,
            chosenIndex = qnaState.chosenItemIndex,
            onClick = { index, question, answer ->
                qnaState.chosenItemIndex = index
                qnaState.chosenItemQuestion = question
                qnaState.chosenItemAnswer = answer
                Log.d("Debuging", qnaState.chosenItemIndex.toString())
                Log.d("Debuging", qnaState.chosenItemQuestion)
                Log.d("Debuging", qnaState.chosenItemAnswer)
            }
        )
    }
}
