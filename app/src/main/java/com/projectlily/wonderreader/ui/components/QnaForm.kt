package com.projectlily.wonderreader.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

class QnaState() {
    var question: String by mutableStateOf("")
    var answer: String by mutableStateOf("")
}

@Composable
fun QnaForm() {
    val formState = remember { QnaState() }
    val context = LocalContext.current

    Form(
        name = "Question",
        placeholder = "Insert Question",
        value = formState.question,
        onChange = { formState.question = it })
    Form(
        name = "Answer",
        placeholder = "Insert Answer",
        value = formState.answer,
        onChange = { formState.answer = it })
    Spacer(Modifier.height(24.dp))
    SendFormButton(
        onValidate = {
            Toast.makeText(
                context,
                "Question: ${formState.question}\nAnswer: ${formState.answer}",
                Toast.LENGTH_SHORT
            ).show()
        })
}

@Preview(showBackground = true)
@Composable
fun QnaPreview(modifier: Modifier = Modifier) {
    WonderReaderTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(bottom = 16.dp),
        ) {
            QnaForm()
        }
    }
}