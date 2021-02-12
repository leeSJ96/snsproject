package com.sjkorea.meetagain.intro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.utils.Constants.EmailFormError
import com.sjkorea.meetagain.utils.Constants.IdFail
import com.sjkorea.meetagain.utils.Constants.PasswordFail
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.login_layout
import kotlinx.android.synthetic.main.activity_loginsub.*
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {


    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val idValue = intent.getStringExtra("id")
        val pwValue = intent.getStringExtra("pw")
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        if (idValue != null) {
            login_email_input.setText(idValue)
            login_password_input.setText(pwValue)
        }


        login_btn.setOnClickListener {

            imm.hideSoftInputFromWindow(login_password_input.windowToken, 0)

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


        firebaseAuth.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    val userUid = firebaseAuth.uid.toString()
                    saveLoginData(userUid)


                }

            }.addOnFailureListener {

                login_btn.isEnabled = true
                login_btn.text = "로그인하기"

                when {
                    IdFail in it.toString() -> {
                        Snackbar.make(login_layout, "아이디가 없습니다", Snackbar.LENGTH_SHORT).show()
                    }
                    PasswordFail in it.toString() -> {
                        Snackbar.make(login_layout, "비밀번호가 일치하지 않습니다", Snackbar.LENGTH_SHORT).show()
                    }
                    EmailFormError in it.toString() -> {
                        Snackbar.make(login_layout, "아이디는 이메일 형식으로 작성해주세요", Snackbar.LENGTH_SHORT).show()
                    }
                }

            }
    }




    // join에서 서버에서 저장
    // login에서 서버 조회 후 디바이스에 저장
    private fun saveLoginData(userUid : String) {

        val authStore = FirebaseFirestore.getInstance().collection("user_auth")

        authStore.whereEqualTo("uid",userUid).get().addOnSuccessListener { querySnapshot ->

            for(i in querySnapshot) {
                SharedPreferenceFactory.putStrValue("userName", i.data["name"].toString())   // 유저 닉네임
                SharedPreferenceFactory.putStrValue("userEmail", i.data["email"].toString()) // 유저 이메일
                SharedPreferenceFactory.putStrValue("userToken", i.data["uid"].toString())  // 유저 uid
                SharedPreferenceFactory.putStrValue("userPath", i.data["path"].toString())  // 유저 디비 위치
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)


            login_btn.isEnabled = true
            login_btn.text = "로그인하기"

        }.addOnFailureListener {

            Log.d("로그","error $it")
            Toast.makeText(this,"로그인 실패 / Server error", Toast.LENGTH_SHORT).show()

            login_btn.isEnabled = true
            login_btn.text = "로그인하기"

        }

    }

}