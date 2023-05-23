package com.projectlily.wonderreader.services

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Should abstract over Firebase
class AuthService {
    private var auth: FirebaseAuth = Firebase.auth;

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    fun login(email: String, password: String, onComplete: OnCompleteListener<AuthResult>) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(onComplete)
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    fun register(email: String, password: String, onComplete: OnCompleteListener<AuthResult>) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(onComplete)
    }
}