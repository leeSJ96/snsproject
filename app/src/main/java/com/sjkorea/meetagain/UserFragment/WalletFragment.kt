package com.sjkorea.meetagain.WalletFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.user.UserApiClient
import com.sjkorea.meetagain.CustomBottomDialog
import com.sjkorea.meetagain.intro.LoginActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.intro.IntroActivity
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.viewpager_wallet_item.*

class WalletFragment : Fragment() {

    var chattingview: View? = null
     var firebaseAuth: FirebaseAuth? = null
    private val customDialog = CustomBottomDialog()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        chattingview = LayoutInflater.from(inflater.context).inflate(
            R.layout.viewpager_wallet_item,
            container,
            false
        )


        return chattingview
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //로그아웃 스위치
        logout_Switch.setOnClickListener {


                btn_logout()

        }

        revokeAccess_btn.setOnClickListener {
            revokeAccessAD()

            }

    }

    //회원탈퇴
    fun revokeAccessAD() {
        AlertDialog.Builder(context)
            .setTitle("회원탈퇴").setMessage(" 정말 회원탈퇴하시겠습니까? \n [ 기록된 정보는 사라집니다 ]  ")
            .setPositiveButton("회원탈퇴", DialogInterface.OnClickListener { dialog, whichButton ->
                SharedPreferenceFactory.clearAllValue()

                deleteId()
                //인트로화면으로 이동
                val i = Intent(context, IntroActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
            })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, whichButton -> })
            .show()
    }

    //로그아웃구문
    fun btn_logout() {
        AlertDialog.Builder(context)
            .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("로그아웃", DialogInterface.OnClickListener { dialog, whichButton ->
                SharedPreferenceFactory.clearAllValue()
                firebaseAuth?.signOut()

                val i = Intent(context, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                Snackbar.make(frag_layout, "로그아웃 되었습니다.", Snackbar.LENGTH_LONG).show();


            })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, whichButton -> })
            .show()


    }

    fun deleteId(){
        FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Snackbar.make(frag_layout, "아이디 삭제가 완료되었습니다", Snackbar.LENGTH_LONG).show()



            }else{
                Snackbar.make(frag_layout, task.exception.toString(), Snackbar.LENGTH_LONG).show()

            }
        }
    }






}