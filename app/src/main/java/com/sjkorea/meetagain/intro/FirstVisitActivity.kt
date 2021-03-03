package com.sjkorea.meetagain.intro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.model.AuthModel
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.sjkorea.meetagain.utils.onMyTextChanged
import kotlinx.android.synthetic.main.activity_firstvisit.*
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.fragment_user.*
import java.util.*
import kotlin.collections.HashMap

class FirstVisitActivity : AppCompatActivity() {


    var firestore: FirebaseFirestore? = null
    var auth: FirebaseAuth? = null
    private val firebaseAuth = FirebaseAuth.getInstance()

    //    var uid = FirebaseAuth.getInstance().currentUser!!.uid
    var uid = FirebaseAuth.getInstance().currentUser?.email

    val userId: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firstvisit)
        firestore = FirebaseFirestore.getInstance()

        //키보드위스낵바
        val inputMethodManager =
            this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            this.getWindow().getDecorView().getRootView().getWindowToken(), 0
        )

        val uid = SharedPreferenceFactory.getStrValue("userToken", "")
        val email = SharedPreferenceFactory.getStrValue("userEmail", "")

//        val uid = pref.getString("userToken", "")
        Log.d("로그", "First email : $email")
        Log.d("로그", "First uid : $uid ")


//
//        Log.d("로그", "email $email")
//        Log.d("로그", "uid $uid ")
        //닉네임 제한
        name_edit.onMyTextChanged {

            if (it.toString().count() > 0) {
                start_btn.visibility = View.VISIBLE

                scrollView.scrollTo(0, 200)
            } else {
                start_btn.visibility = View.INVISIBLE
            }



        }
        //확인 버튼
        start_btn.setOnClickListener {
            Log.d(Constants.TAG, "start_btn 확인: ")

            when {
                name_edit.length() < 1 -> {
                    Snackbar.make(scrollView, "닉네임 값이 비었습니다.", Snackbar.LENGTH_SHORT).show()
                }
                name_edit.length() > 8 -> {
                    Snackbar.make(scrollView, "닉네임은 최대 8자까지 작성가능합니다.", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else-> {
                    updateData()
                }

            }
        }




    }


    //
    private fun updateData() {

        val userName = SharedPreferenceFactory.getStrValue("userName", "")//유저 닉네임
        val id  =     SharedPreferenceFactory.getStrValue("userEmail", "") // 유저 이메일
        SharedPreferenceFactory.getStrValue("userToken", "")  // 유저 uid
        val path = SharedPreferenceFactory.getStrValue("userPath", "")  // 유저 디비 위치)
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var contentDTO = ContentDTO()
        var map = HashMap<String, Any>()
        val nameing = name_edit.text.toString()
        var email = FirebaseAuth.getInstance().currentUser?.email
        Log.d(Constants.TAG, "uid = $uid")
        map["name"] = nameing



//        val pathData = "${id}_${System.currentTimeMillis()}"
        val authModel = AuthModel()

        if (path != null) {
            firestore?.collection("user_auth")?.document(path)?.update(map)
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {

                        SharedPreferenceFactory.putStrValue("userName", nameing)   // 유저 닉네임
                        Toast.makeText(this, "닉네임이 변경 되었습니다", Toast.LENGTH_SHORT).show()
                        finish()


                    }
                }
        }
        if (path != null) {
        FirebaseFirestore.getInstance().collection("profileName").document(uid).set(map)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {

                    SharedPreferenceFactory.putStrValue("userMainName", nameing)   // 유저 닉네임
                    finish()


                }
            }
        }


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
