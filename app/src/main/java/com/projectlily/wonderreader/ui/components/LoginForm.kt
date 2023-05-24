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
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.QnaService
import com.projectlily.wonderreader.services.toastErrorHandler
import com.projectlily.wonderreader.ui.theme.WonderReaderTheme

class LoginState() {
    var email: String by mutableStateOf("")
    var password: String by mutableStateOf("")
}

@Composable
fun LoginForm() {
    val formState = remember { LoginState() }
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
        text = "Login",
        onValidate = {
            AuthService.login(
                formState.email, formState.password,
                onSuccess = {
//                  TODO: Validation here
                    Log.e("yabe", "${AuthService.auth.currentUser?.email}")
//                  TODO: Navigation here
                },
                onFailure = toastErrorHandler(context)
            )
        })
    SendFormButton(
        text = "Register",
        onValidate = {
            AuthService.register(
                formState.email, formState.password,
                onSuccess = {
//                  TODO: Validation here
                    Log.e("yabe", "${AuthService.auth.currentUser?.email}")
//                  TODO: Navigate to other page here
                },
                onFailure = toastErrorHandler(context)
            )
        })
    SendFormButton(
        text = "Qna Add",
        onValidate = {
            QnaService.addQuestionAndAnswer(formState.email, formState.password, "Math")
        })
    SendFormButton(
        text = "Qna Get",
        onValidate = {
            QnaService.getAllQnaFromFolder("Math", { Log.e("yabe", it.toString()) })
        })
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(modifier: Modifier = Modifier) {
    WonderReaderTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.padding(bottom = 16.dp),
        ) {
            LoginForm()
        }
    }
}