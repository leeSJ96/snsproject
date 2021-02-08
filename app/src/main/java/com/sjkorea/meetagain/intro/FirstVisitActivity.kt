package com.sjkorea.meetagain.intro

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.model.IdDTO
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.IDDTO
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.sjkorea.meetagain.utils.onMyTextChanged
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.activity_firstvisit.*
import kotlinx.android.synthetic.main.fragment_user.*

class FirstVisitActivity : AppCompatActivity() {


    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null

    //    var uid = FirebaseAuth.getInstance().currentUser!!.uid
    var uid = FirebaseAuth.getInstance().currentUser?.email

    val userId: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstvisit)
        firestore = FirebaseFirestore.getInstance()

        val uid = SharedPreferenceFactory.getStrValue("userToken", "")
        val email = SharedPreferenceFactory.getStrValue("userEmail", "")

//        val uid = pref.getString("userToken", "")
        Log.d("로그", "First email : $email")
        Log.d("로그", "First uid : $uid ")


//
//        Log.d("로그", "email $email")
//        Log.d("로그", "uid $uid ")

        name_edit.onMyTextChanged {

            if (it.toString().count() > 0) {
                start_btn.visibility = View.VISIBLE

                scrollView.scrollTo(0, 200)
            } else {
                start_btn.visibility = View.INVISIBLE
            }

            if (it.toString().count() == 15) {
                Snackbar.make(scrollView, "15자까지만 입력 가능합니다", Snackbar.LENGTH_SHORT).show()
            }

        }

        start_btn.setOnClickListener {
//
            updateData()
//            nameSaveDB(email!!, uid!!)
        }


    }


    //
    private fun updateData() {
        var contentDTO = ContentDTO()
        var map = HashMap<String, Any>()
        val nameing = name_edit.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        var email = FirebaseAuth.getInstance().currentUser?.email
        Log.d(Constants.TAG, "uid = $uid")
        map[email!!] = name_edit.text.toString()


        firestore?.collection("user_id")?.document(email)?.set(map)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {



                    val idDTO = IdDTO()
                    idDTO.name = name_edit.text.toString()
                    IDDTO = nameing





                    Log.d(Constants.TAG, "로그 : 업데이트 ")
                    SharedPreferenceFactory.putStrValue("userName", IDDTO)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                    finish()


                }
            }



//        contentDTO.name = nameing
//
//        map["name"] = nameing
//
//        firestore?.collection("images")?.document(uid.toString())?.set(map)
//            ?.addOnCompleteListener {
//                if (it.isSuccessful) {
//
//                    contentDTO.name = name_edit.text.toString()
//
//                    Log.d(Constants.TAG, "로그 : 네임 업데이트 ")
//                }
//            }


//    var tsDoc = firestore?.collection("user_id")!!.document(uid)
//    firestore?.runTransaction { transaction ->
//        Log.d(Constants.TAG, "로그 : 업데이트2 ")
//        val idDTO = transaction.get(tsDoc).toObject(IdDTO::class.java)
//        idDTO?.name = name_edit.text.toString()
//        name_edit.setText("")
//
//        transaction.set(tsDoc, idDTO!!)
//        val intent = Intent(this, MainActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
//        finish()
//
//    }
    }
//}

    // 1. 닉네임 넣기
    // 2. uid 저장하기
    private fun nameSaveDB(userEmail: String, userUid: String) {


        Log.d("로그", "nameSaveDB - call")
        var contentDTO = ContentDTO()
        val nameing = name_edit.text.toString()


        val email: String = userEmail
        val uid: String = userUid
        val nameStore = FirebaseFirestore.getInstance().collection("user_name")
        val uidStore = FirebaseFirestore.getInstance().collection("user_uid")

        val nameMap: MutableMap<String, String> = HashMap()
        val uidMap: MutableMap<String, String> = HashMap()




        nameMap[email] = nameing
        uidMap[email] = uid



        nameStore.add(nameMap).addOnSuccessListener {

            SharedPreferenceFactory.putStrValue("userName", nameMap[email])
            Log.d("로그", "name ${nameMap[email]}")
            Log.d("로그", "이름 저장 성공")
        }

        uidStore.add(uidMap).addOnSuccessListener {

            Log.d("로그", "uid 저장 성공")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
            finish()
        }


    }


}
