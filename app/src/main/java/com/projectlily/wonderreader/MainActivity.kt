package com.projectlily.wonderreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.projectlily.wonderreader.ui.components.BottomNavBar
import com.projectlily.wonderreader.ui.components.Form
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WonderReaderApp()
        }
    }
}

@Composable
fun WonderReaderApp() {
    WonderReaderTheme {
        Scaffold(bottomBar = { BottomNavBar() }) { padding ->
            Main(Modifier.padding(padding))
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary)),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wonder_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Text("Wonder Reader", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(56.dp))
            Form(name = "Question", placeholder = "Insert Question")
            Form(name = "Answer", placeholder = "Insert Answer")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    WonderReaderApp()
}