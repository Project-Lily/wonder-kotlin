package com.projectlily.wonderreader.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.SampleData
import com.projectlily.wonderreader.ui.components.QnAList

@Composable
fun QnAScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 12.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        QnAList(data = SampleData.QnASample)
    }
}

@Preview(showBackground = true)
@Composable
fun QnAPreview() {
    QnAScreen()
}