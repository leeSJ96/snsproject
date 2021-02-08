package com.sjkorea.meetagain.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.utils.Constants.AuthOverLap
import com.sjkorea.meetagain.utils.Constants.EmailFormError
import kotlinx.android.synthetic.main.activity_join.*
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity(), InputFilter {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            Spanned: Spanned?,
            dstart: Int,
            dend: Int): CharSequence {

        val ps: Pattern = Pattern.compile("^[-.@0-9a-zA-Z]*\$")

        if (source!! == "" || ps.matcher(source).matches()) {

            return source
        }

        return ""


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)


        email_input.filters = arrayOf<InputFilter>(JoinActivity())
        email_input.privateImeOptions = "defaultInputmode=english;"
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

        back_btn.setOnClickListener {
            onBackPressed()
        }

        input_btn.setOnClickListener {

            imm.hideSoftInputFromWindow(password_input.windowToken, 0);

            when {

                email_input.length() < 1 -> {
                    Snackbar.make(join_layout, "아이디 값이 비었습니다.", Snackbar.LENGTH_SHORT).show()
                }

                password_input.length() < 6 -> {
                    Snackbar.make(join_layout, "패스워드는 최소 6자 이상으로 작성해야 합니다.", Snackbar.LENGTH_SHORT).show()
                }

                password_input.text.toString() != password2_input.text.toString() -> {
                    Snackbar.make(join_layout, "패스워드가 틀립니다.", Snackbar.LENGTH_SHORT).show()
                }

                else -> {
                    input_btn.isEnabled = false
                    input_btn.text = "잠시만 기다려주세요..."
                    authCheck()
                }


            }

        }
    }


    private fun authCheck() {

        val emailValue = email_input.text.toString()
        val pwValue = password_input.text.toString()


        firebaseAuth.createUserWithEmailAndPassword(emailValue, pwValue)
                .addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) {
                        Snackbar.make(join_layout, "계정 생성 완료", Snackbar.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivitysub::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        intent.putExtra("id", emailValue)
                        intent.putExtra("pw", pwValue)
                        startActivity(intent)
                        overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)

                    }

                }

                .addOnFailureListener {
                    input_btn.isEnabled = true
                    input_btn.text = "가입하기"

                    if (AuthOverLap in it.toString()) {
                        Snackbar.make(join_layout, "이미 가입된 이메일 계정입니다.", Snackbar.LENGTH_SHORT).show()
                    } else if (EmailFormError in it.toString()) {
                        Snackbar.make(join_layout, "아이디는 이메일 형식으로 작성해주세요.", Snackbar.LENGTH_SHORT)
                                .show()

                    }

                }
    }


}