package com.sjkorea.meetagain.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.intro.LoginActivity
import com.sjkorea.meetagain.model.IdDTO
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.IDDTO
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.coroutines.*
import java.lang.Runnable

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        // 사용자가 로그인을 전에 했을 경우 자동 로그인을 하기 위해 디바이스에 저장된 데이터값을 조회한다
        val uid = SharedPreferenceFactory.getStrValue("userToken", null)
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        val email = SharedPreferenceFactory.getStrValue("userEmail", null)

        Log.d("로그", "uid $uid")
        Log.d("로그", "name $name")
        Log.d("로그", "email $email")


        if (uid != null && name != null && email != null) {

            moveNext(uid, email, name)

        } else {

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                finish()
            }, 1500)


        }


    }

    private fun moveNext(uid: String, email: String?, name: String?) {

        val authStore = FirebaseFirestore.getInstance().collection("user_auth")


        Handler(Looper.getMainLooper()).postDelayed({

            authStore.whereEqualTo("uid",uid).get().addOnSuccessListener { querySnapshot ->

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                finish()

            }.addOnFailureListener { error ->
                Log.d("로그","error $error")
            }

        }, 1500)
    }




}