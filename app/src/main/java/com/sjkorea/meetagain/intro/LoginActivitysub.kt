package com.sjkorea.meetagain.intro

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.model.IdDTO
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.login_layout
import kotlinx.android.synthetic.main.activity_loginsub.*
import kotlinx.coroutines.*

class LoginActivitysub : AppCompatActivity() {

    // 지금 로그인 실패 시 이벤트가 구현이 안 되서. 아마 그래서 계속 조회 중이 떴을 거예요.
    // 지금 회원 가입부터 다시 해봐요.


    private var firebaseAuth = FirebaseAuth.getInstance()
    private var firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginsub)

        val idValue = intent.getStringExtra("id")
        val pwValue = intent.getStringExtra("pw")
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        if (idValue != null) {
            login_email_input.setText(idValue)
            login_password_input.setText(pwValue)
        }


        login_btn.setOnClickListener {

            imm.hideSoftInputFromWindow(login_password_input.windowToken, 0);

            when {
                login_email_input.text.isNullOrEmpty() -> {
                    Snackbar.make(login_layout, "아이디 값이 비었습니다", Snackbar.LENGTH_SHORT).show()
                }
                login_password_input.text.isNullOrEmpty() -> {
                    Snackbar.make(login_layout, "패스워드 값이 비었습니다", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    login_btn.isEnabled = false
                    login_btn.text = "조회 중입니다."
                    loginCheck()
                }
            }

        }

        create_auth_btn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
        }

        missing_auth_btn.setOnClickListener {
            // 아이디 찾기 액티비티로 이동 시킬 예정
            Snackbar.make(login_layout, "아직 구현 중입니다", Snackbar.LENGTH_SHORT).show()

        }

    }



    private fun loginCheck() {

        val id = login_email_input.text.toString()
        val pw = login_password_input.text.toString()

//        val pref: SharedPreferences = getSharedPreferences("ref", Context.MODE_PRIVATE)
//        val editor = pref.edit()

        val uidStore = FirebaseFirestore.getInstance().collection("user_uid")
        val nameStore = FirebaseFirestore.getInstance().collection("user_name")

//        val name = pref.getString("userName"," ")
        val name = SharedPreferenceFactory.getStrValue("userName","")
        Log.d(Constants.TAG, "로그인 name: $name ")


        var authFirstCheck = true
        var email = ""
        var uid = ""

        firebaseAuth.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {


                    var idEmail = FirebaseAuth.getInstance().currentUser?.email
//                    //닉네임이 있다면 가져오기
//                    firestore.collection("user_id").document(idEmail!!).get()
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                var idDTO = task.result?.toObject(IdDTO::class.java)
//                                Log.d(Constants.TAG, "idDTO : ${idDTO.toString()} ")
//                            }
//                        }


                    var intent = Intent(applicationContext, FirstVisitActivity::class.java)
                    uid = firebaseAuth.uid.toString()


//                    editor.putString("userToken", uid)
//                    editor.putString("userEmail", id)
//                    editor.apply()
                        SharedPreferenceFactory.putStrValue("userToken", uid)
                        SharedPreferenceFactory.putStrValue("userEmail", id)


                    Log.d("log","getValue ${SharedPreferenceFactory.getStrValue("userToken",null)}")

                    uidStore.whereEqualTo(id,uid).addSnapshotListener { value, _ ->

                        authFirstCheck = value != null

                        if(!authFirstCheck) {
                            nameStore.whereEqualTo(id,name).addSnapshotListener { value, _ ->

                                if(value != null ) {

                                    SharedPreferenceFactory.putStrValue("userName", name)
//
//                                    editor.putString("userName",name)
//                                    editor.apply()

                                    intent = Intent(this@LoginActivitysub, MainActivity::class.java)

                                }
                                startActivity(intent)
                                overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                            }

                        } else {
                            intent = Intent(this@LoginActivitysub, FirstVisitActivity::class.java)
                            startActivity(intent)
                            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)
                        }

                    }


                }

            }
    }
}