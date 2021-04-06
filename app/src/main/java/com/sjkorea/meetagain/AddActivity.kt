package com.sjkorea.meetagain

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjkorea.meetagain.databinding.ActivityAddBinding
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*


class AddActivity : AppCompatActivity() {

    val PICTURE_REQUEST_CODE = 100
    private var commentTime: Long = 0
    private var selectedUriList: List<Uri>? = null
    private var photoUri: Uri? = null
    var auth: FirebaseAuth? = null
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    var btnSelectImage: Button? = null
    var uid: String? = null
    var view : View? = null
    private var photoUse : Boolean = false

    private var activityAddBinding: ActivityAddBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var binding: ActivityAddBinding = ActivityAddBinding.inflate(layoutInflater)
        activityAddBinding = binding


        setContentView(activityAddBinding!!.root)

        //제목누르면 키보드 상단에 맞춤
        activityAddBinding?.addphotoEditMamo?.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) }

        //내용누르면 키보드 상단에 맞춤
        activityAddBinding?.addphotoEditExplain?.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        //뒤로가기 버튼
        activityAddBinding?.addBackBtn?.setOnClickListener {
            // 뒤로가기 경고창(다이로그)
            warningWindow()
        }

        // 업로드 이벤트 처리
        activityAddBinding?.addphotoBtnUpload?.setOnClickListener {



            if (System.currentTimeMillis() >= commentTime + 5000) {
                commentTime = System.currentTimeMillis()

                when{
                    //게시글 카운트 5이상
                    addphoto_edit_mamo.text.count() < 5 -> {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        Snackbar.make(add_back, "제목을 5자이상 적어주세요", Snackbar.LENGTH_SHORT).show()
                    }
                    //게시글 카운트 5이상
                    addphoto_edit_explain.text.count() < 8 -> {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                        Snackbar.make(add_back, "내용을 8자이상 적어주세요", Snackbar.LENGTH_SHORT).show()
                    }
                    else -> {

                        if (photoUse) {
                            //사진이 있을경우
                            contentUpload()

                        }else {
                            //사진이 없을경우
                            contentUploadNoPhoto()
                        }
                    }

                }

            }else{   Snackbar.make(add_back, "천천히 눌러주세요", Snackbar.LENGTH_SHORT).show()




            }


        }


        // 파이어베이스 초기화
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btnSelectImage = findViewById(R.id.btn_select_image)
        uid = auth?.currentUser?.uid

        activityAddBinding?.btnSelectImage?.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            )
            //사진을 여러개 선택할수 있도록 한다
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICTURE_REQUEST_CODE
            )
        }



    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICTURE_REQUEST_CODE) {

            if(resultCode == Activity.RESULT_OK){
                // This is path to the seleted image
                photoUri = data?.data
                activityAddBinding?.ImageP1?.setImageURI(photoUri)
                photoUse = true

            }else{
                // Exit the addPhotoActivity if you leave the album without selecting it
                finish()
            }


//            if (resultCode == RESULT_OK) {
//
//                //기존 이미지 지우기
//                activityAddBinding?.ImageP1?.setImageResource(0)
//                activityAddBinding?.ImageP2?.setImageResource(0)
//                activityAddBinding?.ImageP3?.setImageResource(0)
//
//                //ClipData 또는 Uri를 가져온다
//                photoUri = data!!.data
//                photoUse = true
//                val clipData = data.clipData
//
//                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
//                if (clipData != null) {
//                    for (i in 0..2) {
//                        if (i < clipData.itemCount) {
//                            val urione = clipData.getItemAt(i).uri
//                            when (i) {
//                                0 -> activityAddBinding?.ImageP1?.setImageURI(urione)
//                                1 -> activityAddBinding?.ImageP2?.setImageURI(urione)
//                                2 -> activityAddBinding?.ImageP3?.setImageURI(urione)
//                            }
//                        }
//                    }
//                } else if (photoUri != null) {
//                    Image_p1.setImageURI(photoUri)
//                }
//            }
        }
    }





    //사진이 있을시 업로드 데이터
    fun contentUpload() {
        // Make filename
        val now = Date()
        val timestamp = now.time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val createdAt = sdf.format(timestamp)
        var imageFileName = "IMAGE_" + createdAt + "_.png"
        val contentDTO = ContentDTO()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val path = "${uid}_${System.currentTimeMillis()}"



        // 데이터 저장
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG)
                .show()
            storageRef.downloadUrl.addOnSuccessListener { uri ->

                val name = SharedPreferenceFactory.getStrValue("userName", null)
                Log.d(Constants.TAG, "네임확인 : $name ")

                contentDTO.name = name
                // Insert downloadUrl of image
                contentDTO.imageUrl = uri.toString()

                contentDTO.pathData = path
                // Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                // Insert userId
                contentDTO.userId = auth?.currentUser?.email

                // Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                contentDTO.title = addphoto_edit_mamo.text.toString()

                // Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()

                firestore?.collection("images")?.document(path)?.set(contentDTO)


                setResult(Activity.RESULT_OK)

                finish()
            }
        }

    }

    //사진이 없을시  업로드 데이터
    fun contentUploadNoPhoto() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val path = "${uid}_${System.currentTimeMillis()}"
        val contentDTO = ContentDTO()
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        Log.d(Constants.TAG, "네임확인 : $name ")
        //contentDTO 모델 리스트
                contentDTO.name = name
                // Insert downloadUrl of image
                contentDTO.imageUrl = "NullPhotoLink"
                // Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                contentDTO.pathData = path

                // Insert userId
                contentDTO.userId = auth?.currentUser?.email

                // Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                contentDTO.title = addphoto_edit_mamo.text.toString()

                // Insert timestamp
                contentDTO.timestamp = System.currentTimeMillis()

                contentDTO.imageUrl ="https://img.khan.co.kr/news/2020/06/11/l_2020061201001441700115431.jpg"

                firestore?.collection("images")?.document(path)?.set(contentDTO)


                setResult(Activity.RESULT_OK)

                finish()



    }

    //뒤로가기
    override fun onBackPressed() {
//        super.onBackPressed();

        //뒤로가기 경고창(다이로그)
        warningWindow()
    }

    // 뒤로가기 경고창(다이로그)
    fun warningWindow(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_close)
        dialog.show()
        val button = dialog.findViewById(R.id.btnSample) as Button
        button.setOnClickListener {
            dialog.dismiss() }
        val button1 = dialog.findViewById(R.id.btn_close) as Button
        button1.setOnClickListener {
            finish() }
    }



}







