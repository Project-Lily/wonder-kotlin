package com.projectlily.wonderreader.services

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.projectlily.wonderreader.types.QnA
import org.json.JSONArray

class QnaService {
    companion object {
        private var db: FirebaseFirestore = Firebase.firestore;
        private var auth: FirebaseAuth = Firebase.auth;

        private fun getQnaRef(): DocumentReference {
            var uid: String? = auth.uid
            if (uid == null) {
                Log.e("yabe", "User not logged in")
                throw NotFoundException("User id not found")
            }
            return db.collection("users").document(uid);
        }

        fun addQuestionAndAnswer(question: String, answer: String, categoryName: String) {
            val ref = getQnaRef();
            val qnaObject = QnA(question, answer)

            val value = hashMapOf(
                categoryName to FieldValue.arrayUnion(qnaObject)
            )

            Log.i("yabe", value.toString())

            ref.set(value, SetOptions.merge()).addOnFailureListener { exception ->
                Log.e("yabe", "Failed with ", exception)
            }
        }

        fun getAllQnaFromFolder(
            categoryName: String,
            onSuccessListener: (MutableList<QnA>) -> Unit,
            onFailureListener: (Exception) -> Unit = { exception ->
                Log.d("yabe", "get failed with ", exception)
            }
        ) {
            val ref = getQnaRef();

            val output = mutableListOf<QnA>();
            ref.get().addOnSuccessListener {
                if (it != null) {
                    var questionList = JSONArray(it.data?.get(categoryName).toString())
                    Log.e("yabe", questionList.toString())
                    for (i in 0 until questionList.length()) {
                        val qna = questionList.getJSONObject(i);
                        val question = qna.get("question") as String
                        val answer = qna.get("answer") as String

                        output.add(QnA(question, answer))
                    }
                    onSuccessListener(output)
                }
            }.addOnFailureListener(onFailureListener)
        }
    }
}