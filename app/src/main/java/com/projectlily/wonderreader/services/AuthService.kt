package com.projectlily.wonderreader.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Should abstract over Firebase
class AuthService {
    companion object {
        var auth: FirebaseAuth = Firebase.auth;

        fun login(
            email: String,
            password: String,
            onSuccess: (AuthResult) -> Unit,
            onFailure: (Exception) -> Unit = { Log.i("yabe", "Login fail: " + it.message) }
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure)
        }

        fun register(
            email: String,
            password: String,
            onSuccess: (AuthResult) -> Unit,
            onFailure: (Exception) -> Unit = { Log.i("yabe", "Login fail: " + it.message) }
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure)
        }
    }
}

fun toastErrorHandler(context: Context): (Exception) -> Unit {
    return { e: Exception ->
        Toast.makeText(
            context,
            "${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}
