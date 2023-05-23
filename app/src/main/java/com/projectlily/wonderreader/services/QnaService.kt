package com.projectlily.wonderreader.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QnaService {
    private var db: FirebaseFirestore = Firebase.firestore;

    fun addQuestionAndAnswer(question: String, answer: String, categoryName: String) {
        return
    }

    fun getAllQnaFromFolder(categoryName: String) {
        return
    }
}