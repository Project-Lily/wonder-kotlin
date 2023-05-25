package com.projectlily.wonderreader.ui.components

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

class QnAState {
    val options = listOf("Math", "Language", "Science", "Social Science")
    
    var category: String by mutableStateOf(options[0])
    var question: String by mutableStateOf("")
    var answer: String by mutableStateOf("")
}

@Composable
fun QnAForm() {
    val formState = remember { QnAState() }
    val context = LocalContext.current

    Dropdown(
        options = formState.options,
        selectedText = formState.category,
        onChange = { formState.category = it }
    )
    Form(
        name = "Question",
        placeholder = "Insert Question",
        value = formState.question,
        onChange = { formState.question = it }
    )
    Form(
        name = "Answer",
        placeholder = "Insert Answer",
        value = formState.answer,
        onChange = { formState.answer = it }
    )
    Spacer(Modifier.height(24.dp))
    SendFormButton(
        onValidate = {
            Toast.makeText(
                context,
                "Category: ${formState.category}\nQuestion: ${formState.question} | Answer: ${formState.answer}",
                Toast.LENGTH_SHORT
            ).show()
        })
}

@Preview(showBackground = true)
@Composable
fun QnAPreview(modifier: Modifier = Modifier) {
    WonderReaderTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(bottom = 16.dp),
        ) {
            QnAForm()
        }
    }
}