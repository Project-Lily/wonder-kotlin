package com.projectlily.wonderreader.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.toastErrorHandler

class RegisterState() {
    var email: String by mutableStateOf("")
    var password: String by mutableStateOf("")
}

@Composable
fun RegisterForm(navController: NavController) {
    val formState = remember { RegisterState() }
    val context = LocalContext.current

    Form(
        name = "Email",
        placeholder = "Email",
        value = formState.email,
        onChange = { formState.email = it })
    Form(
        name = "Password",
        placeholder = "Password",
        keyboardType = KeyboardType.Password,
        value = formState.password,
        onChange = { formState.password = it })
    Spacer(Modifier.height(24.dp))
    SendFormButton(
        text = "Register",
        onValidate = {
            AuthService.register(
                formState.email, formState.password,
                onSuccess = {
                    navController.navigate("home_root")
                },
                onFailure = toastErrorHandler(context)
            )
        })
}