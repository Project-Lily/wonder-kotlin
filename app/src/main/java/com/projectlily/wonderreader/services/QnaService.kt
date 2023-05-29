package com.projectlily.wonderreader.services

import android.content.res.Resources.NotFoundException
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.projectlily.wonderreader.types.QnA
import com.projectlily.wonderreader.types.QnaJsonDTO
import com.projectlily.wonderreader.types.QnaListDTO

class QnaService {
    companion object {
        private var auth: FirebaseAuth = Firebase.auth;
        private val listenerList: ArrayList<ListenerRegistration> = ArrayList()
        private fun getQnaRef(): DocumentReference {
            val db: FirebaseFirestore = Firebase.firestore;
            val uid: String? = auth.uid
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

            ref.set(value, SetOptions.merge()).addOnFailureListener { exception ->
                Log.e("yabe", "Failed with ", exception)
            }
        }

        fun getListenerListLength(): Int {
            return listenerList.size
        }

        fun listenToQna(
            onUpdate: (QnaListDTO?) -> Unit,
        ) {
            val ref = getQnaRef();
            val listener = ref.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.d("yabe", "Listen to qna failed")
                }

                if (snapshot != null && snapshot.exists()) {
                    val questionList = snapshot.toObject<QnaListDTO>()
                    Log.d("yoro", questionList.toString())

                    onUpdate(questionList)
                } else {
                    Log.d("yoro", "QnA is empty")
                }
            }
            listenerList.add(listener);
        }

        fun parseDtoToQna(qnaJsonDTO: List<QnaJsonDTO>?): MutableList<QnA> {
            val output = mutableListOf<QnA>()
            if (qnaJsonDTO == null) return output
            for (i in qnaJsonDTO) {
                if (i.question == null || i.answer == null) continue
                output.add(QnA(i.question, i.answer))
            }
            return output
        }
    }
}