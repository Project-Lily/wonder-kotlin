package com.projectlily.wonderreader.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Form(name: String, placeholder: String, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }

    Column(modifier.padding(top = 32.dp)) {
        Text(
            text = name, style = MaterialTheme.typography.headlineMedium, modifier = Modifier
                .paddingFromBaseline(bottom = 16.dp)
                .padding(horizontal = 16.dp)
        )
        TextField(
            value = text, onValueChange = { text = it }, placeholder = { Text(placeholder) },
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 16.dp),
        )
    }
}