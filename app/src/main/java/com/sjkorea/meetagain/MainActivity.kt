package com.sjkorea.meetagain


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationSet
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.sjkorea.meetagain.utils.Constants
import com.squareup.okhttp.internal.Internal.instance
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import java.lang.Exception


private var mAuth: FirebaseAuth? = null


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemReselectedListener {


    private var backKeyPressedTime: Long = 0
    var PICK_PROFILE_FROM_ALBUM = 10
    private var photoUri: Uri? = null
    private var photoUse: Boolean = false

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        action_add.setOnClickListener {

            startActivity(Intent(this, AddActivity::class.java))

        }



        when (item.itemId) {
            R.id.action_home -> {
                var homeFragment = MainHomeFragment()

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.main_content, homeFragment).commit()

                return true
            }

            R.id.action_Search -> {
                var searchFragment = FollowFragment()


                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.main_content, searchFragment)
                    .commit()
                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_right)
                return true
            }


            R.id.action_Notice -> {
                var alertFragment = AlarmFragment()
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.main_content, alertFragment)
                    .commit()

                return true
            }

            R.id.action_User -> {
                var userFragment = UserFragment()
                var bundle = Bundle()
                var uid = FirebaseAuth.getInstance().currentUser?.uid

                bundle.putString("destinationUid", uid)
                userFragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.main_content, userFragment)
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
        //프로그레스바 색상
        activityMainBinding?.loadingProgress?.indeterminateDrawable?.setColorFilter(
            Color.rgb(
                255,
                255,
                255
            ), android.graphics.PorterDuff.Mode.MULTIPLY
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        // 앨범에서 Profile Image 사진 선택시 호출 되는 부분
//        if (requestCode == PICK_PROFILE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
//
//            hideAndShowUi(true)
//            var imageUri = data?.data
//            var uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
//            var storageRef =
//                FirebaseStorage.getInstance().reference.child("userProfileImages").child(
//                    uid!!
//                )
//
//            //사진을 업로드 하는 부분  userProfileImages 폴더에 uid에 파일을 업로드함
//            storageRef.putFile(imageUri!!)
//                .continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
//                    return@continueWithTask storageRef.downloadUrl
//                }.addOnSuccessListener { uri ->
//
//                    var map = HashMap<String, Any>()
//                    map["image"] = uri.toString()
//                    FirebaseFirestore.getInstance().collection("profileImages").document(uid)
//                        .set(map).addOnCompleteListener {
//
//                            hideAndShowUi(false)
//                        }.addOnFailureListener {
//
//                            uploadErrorMessage(it)
//                        }
//
//                }

//        }


        when (requestCode) {
            PICK_PROFILE_FROM_ALBUM -> {
                data?.data?.let { uri ->
                    cropImage(uri) //이미지를 선택하면 여기가 실행됨
                }
                photoUri = data?.data
                photoUse = true

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {

                var uid = FirebaseAuth.getInstance().currentUser!!.uid //파일 업로드
                var storageRef =
                    FirebaseStorage.getInstance().reference.child("userProfileImages").child(
                        uid!!
                    )
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    result.uri?.let {
                        Log.d(Constants.TAG, "secces : 1 ")
                        //사진을 업로드 하는 부분  userProfileImages 폴더에 uid에 파일을 업로드함
                        storageRef.putFile(photoUri!!)
                            .continueWithTask { task: com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> ->
                                return@continueWithTask storageRef.downloadUrl
                            }.addOnSuccessListener { it ->


                                var map = HashMap<String, Any>()
                                map["image"] = it.toString()
                                FirebaseFirestore.getInstance().collection("profileImages")
                                    .document(uid)
                                    .set(map).addOnCompleteListener {

                                        hideAndShowUi(false)
                                    }.addOnFailureListener {

                                        uploadErrorMessage(it)
                                    }

                            }


                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error = result.error
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                    Log.d(Constants.TAG, "error:2 ")
                }
            }
            else -> {
                Log.d(Constants.TAG, "finish:3 ")
            }

        }

    }

    private fun cropImage(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            //사각형 모양으로 자른다
            .start(this)
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
            Toast.makeText(this, "한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
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

    private fun uploadErrorMessage(errorMessage: Exception) {

        hideAndShowUi(false)
        Log.d("로그", "error $errorMessage")
        Toast.makeText(this, "업로드 실패/ 서버 에러", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun hideAndShowUi(hideCheck: Boolean) {

        when (hideCheck) {

            true -> {

                activityMainBinding?.loadingProgress?.visibility = View.VISIBLE

//                btn_select_image.isEnabled = false
//                addphoto_btn_upload.isEnabled = false
//                addphoto_edit_mamo.isEnabled = false
//                imageView6.isEnabled = false
//                Image_p1.isEnabled = false
//                add_back_btn.isEnabled = false

            }

            false -> {

                activityMainBinding?.loadingProgress?.visibility = View.INVISIBLE

//                btn_select_image.isEnabled = true
//                addphoto_btn_upload.isEnabled = true
//                addphoto_edit_mamo.isEnabled = true
//                imageView6.isEnabled = true
//                Image_p1.isEnabled = true
//                add_back_btn.isEnabled = true

            }

        }

    }


}


