package com.projectlily.wonderreader

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.projectlily.wonderreader.ui.components.LoginForm
import com.projectlily.wonderreader.ui.components.RegisterForm

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    navigation(startDestination = "login", route="auth") {
        composable(AuthScreen.Login.route) { AuthScreen {
            LoginForm()
        }}
        composable(AuthScreen.Register.route) { AuthScreen {
            RegisterForm()
        }}
    }
}

sealed class AuthScreen(val route: String) {
    object Login: AuthScreen(route="login")
    object Register: AuthScreen(route="register")
}

@Composable
fun AuthScreen(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier
            .fillMaxSize()
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
            content()
        }
    }
}