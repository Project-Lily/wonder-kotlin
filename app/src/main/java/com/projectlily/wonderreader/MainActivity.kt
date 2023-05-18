package com.projectlily.wonderreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.projectlily.wonderreader.ui.components.BottomNavBar
import com.projectlily.wonderreader.ui.components.SendForm
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("Home", R.string.home, Icons.Default.Home)
    object Debug : Screen("Debug", R.string.debug, Icons.Default.Build)
}

val items = listOf(
    Screen.Home,
    Screen.Debug,
)

@Composable
fun MainApp() {
    WonderReaderTheme {
        val navController = rememberNavController()
        Scaffold(bottomBar = { BottomNavBar(navController, items) }) { padding ->
            HomeScreen(Modifier.padding(padding))
            NavHost(
                navController,
                startDestination = Screen.Home.route,
                Modifier.padding(padding)
            ) {
                composable(Screen.Home.route) { HomeScreen() }
                composable(Screen.Debug.route) { DebugScreen() }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .border(BorderStroke(4.dp, MaterialTheme.colorScheme.primary))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.wonder_logo),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Text(
                text = "Wonder Reader",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(56.dp))
            SendForm()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    MainApp()
}