package com.sjkorea.meetagain


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.kakao.sdk.common.util.Utility
import com.sjkorea.meetagain.AlertFragment.AlarmFragment
import com.sjkorea.meetagain.App.Companion.instance
import com.sjkorea.meetagain.FollowFragment.FollowFragment
import com.sjkorea.meetagain.UserFragment.*
import com.sjkorea.meetagain.databinding.ActivityMainBinding
import com.sjkorea.meetagain.homeFragment.HomeFragment
import com.sjkorea.meetagain.homeFragment.MainHomeFragment
import com.squareup.okhttp.internal.Internal.instance
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*


private var mAuth: FirebaseAuth? = null



class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemReselectedListener {


    private var backKeyPressedTime: Long = 0
    var PICK_PROFILE_FROM_ALBUM = 10



    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        action_add.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }



        when (item.itemId) {
            R.id.action_home -> {
                var homeFragment = MainHomeFragment()

                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, homeFragment).commit()

                return true
            }

            R.id.action_Search -> {
                var searchFragment = FollowFragment()


                supportFragmentManager.beginTransaction().replace(R.id.main_content, searchFragment)
                    .commit()

                return true
            }


            R.id.action_Notice -> {
                var alertFragment = AlarmFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content, alertFragment)
                    .commit()

                return true
            }

            R.id.action_User -> {
                var userFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid

                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment)
                    .commit()
                return true
            }


        }
        return false

    }

    override fun onNavigationItemReselected(item: MenuItem) {
        return
    }

    private var activityMainBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //뷰 바인딩과 연결
        var binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        activityMainBinding = binding

        setContentView(activityMainBinding!!.root)

        // 레이아웃과 연결


        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.action_home

        bottomNavigationView.setOnNavigationItemReselectedListener(this)


        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)

        registerPushToken()

        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            1
        )

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 앨범에서 Profile Image 사진 선택시 호출 되는 부분
        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {

            var imageUri = data?.data
            var uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
            var storageRef =
                FirebaseStorage.getInstance().reference.child("userProfileImages").child(
                    uid!!
                )

            //사진을 업로드 하는 부분  userProfileImages 폴더에 uid에 파일을 업로드함
            storageRef.putFile(imageUri!!)
                .continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                    return@continueWithTask storageRef.downloadUrl
                }.addOnSuccessListener { uri ->
                    var map = HashMap<String, Any>()
                    map["image"] = uri.toString()
                    FirebaseFirestore.getInstance().collection("profileImages").document(uid)
                        .set(map)
                }

        }

    }

    fun registerPushToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            val token = task.result?.token
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val map = mutableMapOf<String, Any>()
            map["pushToken"] = token!!

            FirebaseFirestore.getInstance().collection("pushtokens").document(uid!!).set(map)
        }
    }




//
//
//
//    bottomNavigationView.background = null
//    bottomNavigationView.menu.getItem(2).isEnabled = false
//
//
//
//    mAuth = FirebaseAuth.getInstance()
//    sign_out_and_disconnect2.text = getString(R.string.logout)
//    sign_out_and_disconnect2.setOnClickListener {
//
//        startActivity(Intent(this, LoginActivity::class.java))
//        signOut()
//
//    }
    //뒤로가기 2초
    override fun onBackPressed() {
    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
        backKeyPressedTime = System.currentTimeMillis();
        Toast.makeText(this,"한번 더 누르시면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show()
        return;
    }
    // 현재 표시된 Toast 취소
    if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
        finish();
    }

    }
    //로그아웃
    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    //회원탈퇴
    private fun revokeAccess() {
        mAuth!!.currentUser!!.delete()
    }


     }


