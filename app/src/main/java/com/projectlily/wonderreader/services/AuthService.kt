package com.projectlily.wonderreader.services

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Should abstract over Firebase
class AuthService {
    companion object {
        private var auth: FirebaseAuth = Firebase.auth;

        fun login(
            email: String,
            password: String,
            onSuccess: (AuthResult) -> Unit = { Log.i("yabe", "Login success") },
            onFailureListener: (Exception) -> Unit = { Log.i("yabe", "Login fail: " + it.message) }
        ) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailureListener)
        }

        fun register(
            email: String,
            password: String,
            onSuccess: (AuthResult) -> Unit = { Log.i("yabe", "Login success") },
            onFailureListener: (Exception) -> Unit = { Log.i("yabe", "Login fail: " + it.message) }
        ) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailureListener)


        }

        fun errorHandler() {

        }
    }
}