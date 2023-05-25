package com.projectlily.wonderreader.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.QnAChosen
import com.projectlily.wonderreader.SampleData
import com.projectlily.wonderreader.types.QnA

@Composable
fun SendToDeviceScreen(qnAChosen: QnAChosen, data: MutableList<QnA>) {
    val data = data[qnAChosen.chosenItemIndex]

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Question: ${data.question}\nAnswer: ${data.answer}\n")
        }
    }
}
