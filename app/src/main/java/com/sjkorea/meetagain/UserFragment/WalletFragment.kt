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

        zzzzzz.setOnClickListener {
                customDialog.show(childFragmentManager, "")

            }

    }



    //로그아웃구문
    fun btn_logout() {
        AlertDialog.Builder(context)
            .setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("로그아웃", DialogInterface.OnClickListener { dialog, whichButton ->
                val i = Intent(context, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(i)
                firebaseAuth?.signOut()
                Snackbar.make(frag_layout, "로그아웃 되었습니다.", Snackbar.LENGTH_LONG).show();

                UserApiClient.instance.logout { error ->
                    if (error != null) {
                        Snackbar.make(frag_layout, "로그아웃 실패하였습니다.", Snackbar.LENGTH_LONG).show();
                    }else {
                        Snackbar.make(frag_layout, "로그아웃 되었습니다.", Snackbar.LENGTH_LONG).show();
                    }
                    val intent = Intent(context, LoginActivity::class.java)
                    startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP))
                }
            })
            .setNegativeButton("취소",
                DialogInterface.OnClickListener { dialog, whichButton -> })
            .show()


    }







}