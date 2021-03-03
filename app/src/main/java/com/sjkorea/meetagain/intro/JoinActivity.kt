package com.sjkorea.meetagain.intro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.model.AuthModel
import com.sjkorea.meetagain.utils.Constants.AuthOverLap
import com.sjkorea.meetagain.utils.Constants.EmailFormError
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_firstvisit.*
import kotlinx.android.synthetic.main.activity_join.*
import java.util.*
import java.util.regex.Pattern

class JoinActivity : AppCompatActivity(), InputFilter {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val authStore = FirebaseFirestore.getInstance().collection("user_auth")

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

                email_input.length() > 20 -> {
                    Snackbar.make(join_layout, "아이디는 최대 20자까지 작성가능합니다.", Snackbar.LENGTH_SHORT).show()
                }

                name_input.length() < 1 -> {
                    Snackbar.make(join_layout, "닉네임 값이 비었습니다.", Snackbar.LENGTH_SHORT).show()
                }
                name_input.length() > 8 -> {
                    Snackbar.make(join_layout, "닉네임은 최대 8자까지 작성가능합니다.", Snackbar.LENGTH_SHORT).show()
                }
                //패스워드
                password_input.length() < 6 -> {
                    Snackbar.make(join_layout, "패스워드는 최소 6자 이상으로 작성해야 합니다.", Snackbar.LENGTH_SHORT).show()
                }

                password_input.length() > 12 -> {
                    Snackbar.make(join_layout, "패스워드는 최대 12자까지 작성가능합니다.", Snackbar.LENGTH_SHORT).show()
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
        val nameValue = name_input.text.toString()




        Log.d("로그", "nameValue $nameValue")

        authStore.whereEqualTo("name", nameValue).get().addOnSuccessListener { nameData ->


            when {

                nameData == null || nameData.size() == 0 -> {




                    firebaseAuth.createUserWithEmailAndPassword(emailValue, pwValue).addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            val userUid = firebaseAuth.uid.toString()
                            Log.d("로그", "uid $userUid")
                            authDatabaseAdd(emailValue, pwValue, nameValue, userUid)

                        }

                    }.addOnFailureListener {
                        input_btn.isEnabled = true
                        input_btn.text = "가입하기"

                        if (AuthOverLap in it.toString()) {
                            Snackbar.make(join_layout, "이미 가입된 이메일 계정입니다.", Snackbar.LENGTH_SHORT).show()
                        } else if (EmailFormError in it.toString()) {
                            Snackbar.make(join_layout, "아이디는 이메일 형식으로 작성해주세요.", Snackbar.LENGTH_SHORT).show()

                        }

                    }



                }
                else -> {

                    Log.d("로그","nameData 크기 ${nameData.size()}")

                    for (i in nameData) {
                        Log.d("로그", "중복 ${i.data["name"]}")
                    }

                    input_btn.isEnabled = true
                    input_btn.text = "가입하기"

                    Snackbar.make(join_layout, "이미 존재하는 닉네임입니다.", Snackbar.LENGTH_SHORT).show()

                }

            }

        }.addOnFailureListener {

            Log.d("로그", "조회 에러 $it")
            Snackbar.make(join_layout, "계정 생성 실패, 다시 시도해 주세요.", Snackbar.LENGTH_SHORT).show()
            input_btn.isEnabled = true
            input_btn.text = "가입하기"

        }


    }


    private fun authDatabaseAdd(id: String, pw: String, userName: String, userUid: String) {


        val pathData = "${id}_${System.currentTimeMillis()}"


        val authModel = AuthModel()

        authModel.apply {
            email = id
            uid = userUid
            name = userName
            path = pathData
            timeStamp = Date().toString()
        }

        var map = HashMap<String, Any>()
        map["name"] = userName.toString()


        var uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
        FirebaseFirestore.getInstance().collection("profileName").document(uid).set(map)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {

                    SharedPreferenceFactory.putStrValue("userMainName", userName )   // 유저 닉네임
                    finish()


                }
            }


        authStore.document(pathData).set(authModel).addOnSuccessListener {

            Snackbar.make(join_layout, "계정 생성 완료", Snackbar.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id", id)
            intent.putExtra("pw", pw)
            startActivity(intent)
            overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)


        }.addOnFailureListener {
            Snackbar.make(join_layout, "계정 생성 실패, 다시 시도해 주세요.", Snackbar.LENGTH_SHORT).show()
            Log.d("로그", "error $it")
        }


    }


}