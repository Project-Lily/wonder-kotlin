package com.projectlily.wonderreader.ui.components

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projectlily.wonderreader.services.AuthService
import com.projectlily.wonderreader.services.toastErrorHandler

@Composable
fun LoginForm(navController: NavController) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var isError by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it
        },
        placeholder = { Text(text = "Email") },
        label = { Text(text = "Email") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = isError,
    )
    Spacer(Modifier.height(20.dp))
    TextField(
        value = password,
        onValueChange = {
            password = it
        },
        placeholder = { Text(text = "Password") },
        label = { Text(text = "Password") },
        singleLine = true,
        visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisibility)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisibility) "Hide password" else "Show password"
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, description)
            }
        }
    )
    Spacer(Modifier.height(24.dp))
    SendFormButton(
        text = "Login",
        onValidate = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                AuthService.login(
                    email, password,
                    onSuccess = {
                        navController.navigate("home_root")
                        Log.e("yabe", "${AuthService.auth.currentUser?.email}")
                        //                  TODO: Navigation here
                    },
                    onFailure = toastErrorHandler(context)
                )
            } else {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }

        })
    SendFormButton(
        text = "Register",
        onValidate = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                AuthService.register(
                    email, password,
                    onSuccess = {
                        navController.navigate("home_root")
                        Log.e("yabe", "${AuthService.auth.currentUser?.email}")
                    },
                    onFailure = toastErrorHandler(context)
                )
            } else {
                Toast.makeText(context, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        })
}