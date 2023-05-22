package com.projectlily.wonderreader.ui.components

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

@Composable
fun Form(
    modifier: Modifier = Modifier,
    name: String,
    placeholder: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
fun SendFormButton(
    modifier: Modifier = Modifier,
            onValidate: () -> Unit,
    text: String = "Submit",
) {
    Button(
        onClick = onValidate,
        modifier = modifier
            .padding(top = 32.dp)
            .size(width = 150.dp, height = 60.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormPreview(modifier: Modifier = Modifier) {
    WonderReaderTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(bottom = 16.dp),
        ) {
            QnaForm()
        }
    }
}