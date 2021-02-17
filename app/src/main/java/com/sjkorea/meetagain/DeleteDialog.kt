package com.sjkorea.meetagain

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.delete_dialog.*

class DeleteDialog(context: Context) : Dialog(context) {

    lateinit var completeResult: (deleteSet: Boolean) -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_dialog)

    }

    fun deleteSetting(pathData: String, completeResult: (deleteSet: Boolean) -> Unit) {

        this.completeResult = completeResult


        yes_btn.setOnClickListener {

            Log.d("로그", "yesClick")

            val fireStore = FirebaseFirestore.getInstance().collection("images").document(pathData)

            fireStore.delete().addOnSuccessListener {
                Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show()
                this.completeResult.invoke(true)
            }.addOnFailureListener {
                Toast.makeText(context, "삭제 에러", Toast.LENGTH_SHORT).show()
                this.completeResult.invoke(false)
            }
            dismiss()
        }

        no_btn.setOnClickListener {
            Log.d("로그", "noClick")
            this.completeResult.invoke(false)
            dismiss()
        }


    }

    override fun dismiss() {
        super.dismiss()
    }
}