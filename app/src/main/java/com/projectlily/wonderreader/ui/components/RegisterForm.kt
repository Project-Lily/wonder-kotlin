package com.projectlily.wonderreader.ui.components

import android.util.Log
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.toastErrorHandler
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

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
            AuthService.register(formState.email, formState.password,
                onSuccess = {
                    navController.navigate("home_root")
                    Log.e("yabe", "lmao")
                },
                onFailure = toastErrorHandler(context)
        )
    })
}