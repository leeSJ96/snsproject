package com.sjkorea.meetagain

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sjkorea.meetagain.databinding.ActivityUpdateBinding
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AddUpdateActivity : AppCompatActivity() {

    val PICTURE_REQUEST_CODE = 100

    private var selectedUriList: List<Uri>? = null

    var auth: FirebaseAuth? = null
    var photoUri: Uri? = null
    var storage: FirebaseStorage? = null
    var firestore: FirebaseFirestore? = null
    var btnSelectImage: Button? = null
    var uid: String? = null
    var view: View? = null
    private var contentDTO: ContentDTO? = ContentDTO()
    private var photoUse: Boolean = false

    private var PICK_IMAGE_FROM_ALBUM = 13

    var explain: String? = null
    var title: String? = null
    var imageUrl: String? = null

    private var activityUpdateBinding: ActivityUpdateBinding? = null
    private var fireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




        fireStore = FirebaseFirestore.getInstance()
        //인텐트 데이터 받아오기
//        title = intent.getStringExtra("title")
//        explain = intent.getStringExtra("explain")
//        imageUrl = intent.getStringExtra("imageUrl")


        var binding: ActivityUpdateBinding = ActivityUpdateBinding.inflate(layoutInflater)
        activityUpdateBinding = binding



        contentDTO = intent.getParcelableExtra<ContentDTO>("contentDTO")

        contentDTO!!.let {

            activityUpdateBinding?.addphotoEditMamo?.setText(it.title)
            activityUpdateBinding?.addphotoEditExplain?.setText(it.explain)


            if (it.imageUrl != "NullPhotoLink") {

                photoUse = true

                Glide.with(this)
                    .load(it.imageUrl)
                    .placeholder(R.drawable.ic_arrow_back)
                    .into(activityUpdateBinding!!.ImageP1)
            } else {
                photoUse = false
                activityUpdateBinding?.ImageP1?.visibility = View.INVISIBLE

            }
        }


        setContentView(activityUpdateBinding!!.root)


        //프로그레스바 색상
        activityUpdateBinding?.loadingProgress?.indeterminateDrawable?.setColorFilter(Color.rgb(255 ,255 ,255), android.graphics.PorterDuff.Mode.MULTIPLY)

        //제목누르면 키보드 상단에 맞춤
        activityUpdateBinding?.addphotoEditMamo?.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        //내용누르면 키보드 상단에 맞춤
        activityUpdateBinding?.addphotoEditExplain?.setOnClickListener {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }

        //뒤로가기 버튼
        activityUpdateBinding?.addBackBtn?.setOnClickListener {
            // 뒤로가기 경고창(다이로그)
            warningWindow()
        }

        // 업로드 이벤트 처리
        activityUpdateBinding?.addphotoBtnUpload?.setOnClickListener {

            when {
                //게시글 카운트 5이상
                activityUpdateBinding?.addphotoEditMamo?.text?.count()!! < 5 -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    Snackbar.make(add_back, "제목을 5자이상 적어주세요", Snackbar.LENGTH_SHORT).show()
                }
                //게시글 카운트 5이상
                activityUpdateBinding?.addphotoEditExplain?.text?.count()!! < 8 -> {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    Snackbar.make(add_back, "내용을 8자이상 적어주세요", Snackbar.LENGTH_SHORT).show()
                }
                else -> {
                    hideAndShowUi(true)
                    if (photoUse) {
                        //사진이 있을경우
                        contentUpload()

                    } else {
                        //사진이 없을경우
                        contentUploadNoPhoto()
                    }
                }

            }


        }


        // 파이어베이스 초기화
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()
        btnSelectImage = findViewById(R.id.btn_select_image)
        uid = auth?.currentUser?.email

        activityUpdateBinding?.btnSelectImage?.setOnClickListener {



            val intent = Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            )

            //사진을 여러개 선택할수 있도록 한다
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            intent.putExtra("crop", true) //기존 코드에 이 줄 추가!
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_FROM_ALBUM

            )

//            val intent = Intent(
//                Intent.ACTION_GET_CONTENT,
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            )
//            //사진을 여러개 선택할수 있도록 한다
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//            intent.type = "image/*"
//            startActivityForResult(
//                Intent.createChooser(intent, "Select Picture"),
//                PICTURE_REQUEST_CODE
//            )
        }


    }

    private fun cropImage(uri: Uri?){
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            //사각형 모양으로 자른다
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode){
            PICK_IMAGE_FROM_ALBUM -> {
                data?.data?.let { uri ->
                    cropImage(uri) //이미지를 선택하면 여기가 실행됨
                }
                photoUri = data?.data
                photoUse = true
                activityUpdateBinding?.ImageP1?.setImageURI(photoUri)
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                //그후, 이곳으로 들어와 RESULT_OK 상태라면 이미지 Uri를 결과 Uri로 저장!
                val result = CropImage.getActivityResult(data)
                if(resultCode == Activity.RESULT_OK){
                    result.uri?.let {
                        activityUpdateBinding?.ImageP1?.setImageBitmap(result.bitmap)
                        activityUpdateBinding?.ImageP1?.setImageURI(result.uri)
                        photoUri = result.uri

                    }
                }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    val error = result.error
                    Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                }
            }
            else ->{finish()}

        }



//        if (requestCode == PICTURE_REQUEST_CODE) {
//
//            if(resultCode == Activity.RESULT_OK){
//                // This is path to the seleted image
//                photoUri = data?.data
//                activityUpdateBinding?.ImageP1?.setImageURI(photoUri)
//                photoUse = true
//
//            }else{
//                // Exit the addPhotoActivity if you leave the album without selecting it
//                finish()
//            }

//            if (resultCode == RESULT_OK) {
//
//                    //기존 이미지 지우기
//                    activityUpdateBinding?.ImageP1?.setImageResource(0)
//                    activityUpdateBinding?.ImageP2?.setImageResource(0)
//                    activityUpdateBinding?.ImageP3?.setImageResource(0)
//
//                    //ClipData 또는 Uri를 가져온다
//                    photoUri = data!!.data
//                    photoUse = true
//                    val clipData = data.clipData
//
//                    //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
//                    if (clipData != null) {
//                        for (i in 0..2) {
//                            if (i < clipData.itemCount) {
//                                val urione = clipData.getItemAt(i).uri
//                                when (i) {
//                                    0 -> activityUpdateBinding?.ImageP1?.setImageURI(urione)
//                                    1 -> activityUpdateBinding?.ImageP2?.setImageURI(urione)
//                                    2 -> activityUpdateBinding?.ImageP3?.setImageURI(urione)
//                                }
//                            }
//                        }
//                    } else if (photoUri != null) {
//                        Image_p1.setImageURI(photoUri)
//                    }
//            }

    }


    //사진이 있을시 업로드 데이터
    fun contentUpload() {
        // Make filename
        val now = Date()
        val timestamp = now.time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val createdAt = sdf.format(timestamp)
        var imageFileName = "IMAGE_" + createdAt + "_.png"


        // Callback method
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        //사진이 변경 구분
        when (photoUri) {
            //사진 변경을 안 했을 시
            null -> {

                val name = SharedPreferenceFactory.getStrValue("userName", null)
                Log.d(Constants.TAG, "네임확인 : $name ")
                contentDTO?.name = name
//                        // Insert downloadUrl of image
//                        contentDTO.imageUrl = uri.toString()
                // Insert uid of user

                contentDTO!!.let { it ->

                    it.uid = auth?.currentUser?.uid
                    // Insert userId
                    it.userId = auth?.currentUser?.email
                    it.explain = addphoto_edit_explain.text.toString()
                    it.title = addphoto_edit_mamo.text.toString()

                }
                Log.d("로그", "링크 ${contentDTO!!.pathData}")

                val mamoText = addphoto_edit_mamo.text.toString()
                val explainText = addphoto_edit_explain.text.toString()
                val update = firestore?.collection("images")?.document(contentDTO!!.pathData.toString())

                val mamoTextmap = HashMap<String, Any>()
                val explainTextmap = HashMap<String, Any>()
                mamoTextmap["title"] = mamoText
                explainTextmap["explain"] = explainText

                update?.set(contentDTO!!)?.addOnSuccessListener {
                    Log.d(Constants.TAG, "제목수정완료: ")
                    setResult(Activity.RESULT_OK)
                    finish()
                }

                update?.set(contentDTO!!)?.addOnSuccessListener {
                    Log.d(Constants.TAG, "내용수정완료: ")
                    setResult(Activity.RESULT_OK)
                    finish()
                }



                setResult(Activity.RESULT_OK)

                finish()

            }
            else -> {  //변경 했을시


                storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG)
                        .show()
                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val name = SharedPreferenceFactory.getStrValue("userName", null)
                        Log.d(Constants.TAG, "네임확인 : $name ")

                        contentDTO?.name = name

                        // Insert downloadUrl of image
                        contentDTO?.imageUrl = uri.toString()

                        contentDTO?.let { it ->
                            // Insert uid of user
                            it.uid = auth?.currentUser?.uid
                            // Insert userId
                            it.userId = auth?.currentUser?.email
                            // Insert explain of content
                            it.explain = addphoto_edit_explain.text.toString()

                            it.title = addphoto_edit_mamo.text.toString()

                        }



                        firestore?.collection("images")?.document(contentDTO?.pathData.toString())
                            ?.set(contentDTO!!)
                            ?.addOnSuccessListener {
                                hideAndShowUi(false)
                                setResult(Activity.RESULT_OK)
                                finish()
                            }?.addOnFailureListener {
                                uploadErrorMessage(it)
                            }
                    }

                }
            }


        }

    }

    //사진이 없을시  업로드 데이터
    fun contentUploadNoPhoto() {


        // Make filename
        val now = Date()
        val timestamp = now.time
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.KOREA)
        val createdAt = sdf.format(timestamp)
        val contentDTO = ContentDTO()
        val email = FirebaseAuth.getInstance().currentUser?.email
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val name = SharedPreferenceFactory.getStrValue("userName", null)
        Log.d(Constants.TAG, "네임확인 : $name ")
        contentDTO.name = name

        contentDTO.let {
            // Insert downloadUrl of image

            it.imageUrl = "NullPhotoLink"
            // Insert uid of user
            it.uid = auth?.currentUser?.uid

            // Insert userId
            it.userId = auth?.currentUser?.email

            // Insert explain of content
            it.explain = addphoto_edit_explain.text.toString()

            it.title = addphoto_edit_mamo.text.toString()



            it.imageUrl =
                "https://img.khan.co.kr/news/2020/06/11/l_2020061201001441700115431.jpg"

        }



//
//                contentDTO.createdAt =  timeDiff(contentDTO.timestamp)

        firestore?.collection("images")?.document()?.set(contentDTO.pathData!!)
            ?.addOnSuccessListener {
                hideAndShowUi(false)
                setResult(Activity.RESULT_OK)
                finish()

            }?.addOnFailureListener {
            hideAndShowUi(false)
            uploadErrorMessage(it)
        }


    }

    //뒤로가기
    override fun onBackPressed() {
//        super.onBackPressed();

        //뒤로가기 경고창(다이로그)
        warningWindow()
    }

    // 뒤로가기 경고창(다이로그)
    fun warningWindow() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog_close)
        dialog.show()
        val button = dialog.findViewById(R.id.btnSample) as Button
        button.setOnClickListener {
            dialog.dismiss()
        }
        val button1 = dialog.findViewById(R.id.btn_close) as Button
        button1.setOnClickListener {
            finish()
        }
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

                loading_progress.visibility = View.VISIBLE
                btn_select_image.isEnabled = false
                addphoto_btn_upload.isEnabled = false
                addphoto_edit_mamo.isEnabled = false
                imageView6.isEnabled = false
                Image_p1.isEnabled = false
                add_back_btn.isEnabled = false

            }

            false -> {

                loading_progress.visibility = View.INVISIBLE
                btn_select_image.isEnabled = true
                addphoto_btn_upload.isEnabled = true
                addphoto_edit_mamo.isEnabled = true
                imageView6.isEnabled = true
                Image_p1.isEnabled = true
                add_back_btn.isEnabled = true

            }

        }

    }

}







