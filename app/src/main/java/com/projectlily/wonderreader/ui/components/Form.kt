package com.projectlily.wonderreader.ui.components

import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class TextFieldState() {
    var question: String by mutableStateOf("")
    var answer: String by mutableStateOf("")
}

@Composable
fun SendForm() {
    val formState = remember { TextFieldState() }
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
    SendFormButton(onValidate = {
        Toast.makeText(
            context,
            "Question: ${formState.question}\nAnswer: ${formState.answer}",
            Toast.LENGTH_SHORT
        ).show()
    })
}

@Composable
fun Form(
    name: String,
    placeholder: String,
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(top = 32.dp)) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .paddingFromBaseline(bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        TextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text(placeholder) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp)
                .focusable(),
        )
    }
}

@Composable
private fun SendFormButton(onValidate: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onValidate,
        modifier = modifier
            .padding(top = 32.dp)
            .size(width = 150.dp, height = 60.dp)
    ) {
        Text(
            text = "Submit",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}