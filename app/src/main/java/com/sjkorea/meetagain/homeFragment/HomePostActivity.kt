package com.sjkorea.meetagain.homeFragment

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.Comment.CommentFragment
import com.sjkorea.meetagain.databinding.ActivityHomePostBinding
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.TAG
import com.sjkorea.meetagain.DeleteDialog
import com.sjkorea.meetagain.utils.Constants.MORESPINNER
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_home_post.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HomePostActivity : AppCompatActivity() {
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var uid: String? = null
    var userId: String? = null
    var title: String? = null
    var explain: String? = null
    var imageUrl: String? = null
    var favoriteCount: Int? = null
    var meaningCount: Int? = null
    var favorites: HashMap<String, Boolean> = HashMap()
    var meaning: HashMap<String, Boolean> = HashMap()
    var contentUidListposition: String? = null
    var fcmPush: FcmPush? = null
    private var contentDTO: ContentDTO? = null
    var imageprofileListenerRegistration: ListenerRegistration? = null
    var homepostListenerRegistration: ListenerRegistration? = null
    private var deleteDialog: DeleteDialog? = null
    private var homePostActivityBinding : ActivityHomePostBinding ? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var binding: ActivityHomePostBinding = ActivityHomePostBinding.inflate(layoutInflater)
        homePostActivityBinding = binding
        setContentView(homePostActivityBinding!!.root)

        firestore = FirebaseFirestore.getInstance()
        contentDTO = intent.getParcelableExtra<ContentDTO>("contentDTO")
        userId = intent.getStringExtra("userId")
        uid = intent.getStringExtra("destinationUid")
        contentUidListposition = intent.getStringExtra("userIdposition")
        Log.d(contentUidListposition.toString(), " 홈 포스트 로그 contentUidListposition 받기")

//
//        favorites  = intent.getSerializableExtra("favoriteshashmap") as HashMap<String, Boolean>
//        Log.d(this.favorites.toString(), "홈 포스트 로그 favorites 받기 ")
//
//        meaning  =  intent.getSerializableExtra("meaninghashmap") as HashMap<String, Boolean>
//        Log.d(this.meaning.toString(), "홈 포스트 로그 favorites 받기 ")
//



        //싱글톤사용 동적 스피너 체인지
        moreSpinnerChange()


        //메뉴 스피너버튼

        //어댑터의 아이템은 안드로이드 스튜디오에서 제공해 주는 기본인
        //android.R.layout.simple_spinner_dropdown_item 을 사용했습니다.



        contentDTO?.apply {

            // 제목 텍스트
            homePostActivityBinding?.homePostTitleTextview?.text = title.toString()
            // 내용 텍스트
            homePostActivityBinding?.homePostExplainTextview?.text = explain.toString()
            // 이미지 사진
            homePostActivityBinding?.homePostImageviewContent?.let {

                Glide.with(applicationContext).load(imageUrl)
                    .into(it)
            }
            //좋아요개수
            homePostActivityBinding?.homePostLike?.text =
                "좋아요" + contentDTO?.favoriteCount.toString() + "개"
            //싫어요개수
            homePostActivityBinding?.homePostMa?.text =
                "싫어요" + contentDTO?.meaningCount.toString() + "개"

            //좋아요 버튼 설정
            if (favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                homePostActivityBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_redc)

            } else {

                homePostActivityBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_red)
            }

            //슬퍼요 버튼 설정
            if (meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                homePostActivityBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_bluec)

            } else {

                homePostActivityBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_blue)
            }


            // 좋아요
            homePostActivityBinding?.homePostLike?.text =
                "" + favoriteCount+ "개"
            homePostActivityBinding?.homePostFavoriteImageview?.setOnClickListener {
                favoriteEvent()
            }

            // 슬퍼요
            homePostActivityBinding?.homePostMa?.text =
                "" + meaningCount + "개"
            homePostActivityBinding?.homePostMaImageview?.setOnClickListener {
                Log.d(TAG, "확인:싫어요 ")
                meaningEvent()
            }



        }

        //닉네임 따로 받아오기
        //프로필 닉네임
        homePostActivityBinding?.homePostProfileTextview?.text = userId.toString()
        //프로필사진
        getProfileImage()




        //댓글창
        homePostActivityBinding?.bottomviewitemCommentImageview?.setOnClickListener {

            val customcommentDialog = CommentFragment()
            var bundle = Bundle()
            bundle.putString("contentUid", contentUidListposition)
            Log.d(contentUidListposition.toString(), "홈포스트contentUid1 로그 ")
            bundle.putString("destinationUid", uid)
            bundle.putString("contentDTO", contentDTO?.pathData)
            Log.d(uid.toString(), "홈포스트uid2 로그 ")

            customcommentDialog.arguments = bundle
            customcommentDialog.show(this.supportFragmentManager, "")
        }

        //프로필창
        homePostActivityBinding?.homePostProfileImage?.setOnClickListener {
            Constants.POSTSHOW = "homePostView"
            val bundle = Bundle()
            bundle.putString("contentDTO", contentDTO?.pathData)
            val bottomSheetDialogFragment = CustomBottomDialog()
            bundle.putString("destinationUid", uid)
            bundle.putString("userId", userId)
            bundle.putString("userIdposition", contentUidListposition)

            bottomSheetDialogFragment.arguments = bundle

            bottomSheetDialogFragment.show(this.supportFragmentManager, bottomSheetDialogFragment.tag)
        }






//        getProfileImage()
    }

    //친구-본인 상황별웬절
    fun moreSpinnerChange(){

        var myuid = FirebaseAuth.getInstance().currentUser!!.uid


            firestore?.collection("images")?.whereEqualTo("uid","uid")?.get()
                ?.addOnCompleteListener {
                    if(it.isSuccessful){
                        for(dc in it.result!!.documents){
                            var contentDTO =dc.toObject(ContentDTO::class.java)
                            uid = contentDTO?.uid
                        }
                    }
                }

        if (myuid == uid)

            MORESPINNER = "my"

            else{
                MORESPINNER = "frand"

        }
        Log.d(Constants.TAG, "MORESPINNER: $MORESPINNER ")
            when(MORESPINNER){

                "my"->{
                    mymoreSpinner()
                }

                "frand"->{
                    frandmoreSpinner()
                }

            }
    }

    //본인 스피너
    fun mymoreSpinner(){
        val items = resources.getStringArray(R.array.my_array)
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

        homePostActivityBinding?.moreSpinner?.adapter = myAdapter

        homePostActivityBinding?.moreSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                    when (position) {

                        0 -> {
                        }
                        //
                        //업데이트
                        1 -> {
                            contentUpdate()
                        }
                        //삭제
                        else ->{
                            //싱글톤 댓글부분
                            Constants.LIST = 0
                            Log.d(Constants.TAG, "Constants.LIST: ${Constants.LIST}")
                            contentDelete()

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

    }

    //친구 스피너
    fun frandmoreSpinner(){
        val items = resources.getStringArray(R.array.friend_array)
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)

        homePostActivityBinding?.moreSpinner?.adapter = myAdapter

        homePostActivityBinding?.moreSpinner?.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {

                    //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                    when (position) {

                        0 -> {
                        }

                        //신고
                        else ->{

                        }

                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }

    }

    //게시글 삭제
    private fun contentDelete() {

        val path = contentDTO?.pathData

        if (deleteDialog == null) {
            deleteDialog = DeleteDialog(this)
        }

        deleteDialog?.show()
        deleteDialog?.deleteSetting(path!!) { result ->

            if (result) {
                finish()
            }

        }

    }

    //게시글 수정
    private fun contentUpdate() {

        val intent = Intent(this, AddUpdateActivity::class.java)
        intent.putExtra("contentDTO", contentDTO)
        intent.putExtra("title", title)
        intent.putExtra("explain", explain)
        intent.putExtra("imageUrl", imageUrl)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.page_right_in, R.anim.page_left_out)

    }

    //좋아요 이벤트 기능
    private fun favoriteEvent() {
        val tsDoc = firestore?.collection("images")?.document(contentUidListposition.toString())
        firestore?.runTransaction { transaction ->

            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)

            if (contentDTO!!.favorites.containsKey(uid)) {
                // When the button is clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount - 1
                contentDTO.favorites.remove(uid)
            } else {
                // When the button is not clicked
                contentDTO.favoriteCount = contentDTO.favoriteCount + 1
                contentDTO.favorites[uid] = true
                favoriteAlarm(uid)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }

    //싫어요 이벤트 기능
    private fun meaningEvent() {
        val tsDoc = firestore?.collection("images")?.document(contentUidListposition.toString())
        firestore?.runTransaction { transaction ->
            Log.d(TAG, "확인:싫어요2 ")
            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
            val uid = FirebaseAuth.getInstance().currentUser!!.uid

            if (contentDTO!!.meaning.containsKey(uid)) {

                // When the button is clicked
                contentDTO.meaningCount = contentDTO.meaningCount - 1
                contentDTO.meaning.remove(uid)
            } else {

                // When the button is not clicked
                contentDTO.meaningCount = contentDTO.meaningCount + 1
                contentDTO.meaning[uid] = true

                meaningAlarm(uid)
            }
            transaction.set(tsDoc, contentDTO)
        }

    }
    fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()

        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)
        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser!!.email
        alarmDTO.uid = FirebaseAuth.getInstance().uid
        alarmDTO.kind = 0
        alarmDTO.timestamp = System.currentTimeMillis()


        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = alarmDTO.name + "님이 좋아요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }


    fun meaningAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()




        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId =  FirebaseAuth.getInstance().currentUser!!.email
        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser!!.uid
        alarmDTO.kind = 3
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = user?.email + "님이 힘내요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }

    //프로필사진
    fun getProfileImage() {
    imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    val url = documentSnapshot.data!!["image"]

                    homePostActivityBinding?.homePostProfileImage?.let {
                        Glide.with(applicationContext).load(url).apply(RequestOptions().circleCrop())
                            .into(it)

                    }


                }
            }
    }


    //포스트
    fun gethomeposttitle(contentDTO: ContentDTO) {
        var contentDTO: ArrayList<ContentDTO>
        contentDTO = ArrayList()

        homepostListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTO.clear()
                if (querySnapshot == null)
                    return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    Log.d(ContentValues.TAG, "1")
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                    Log.d(ContentValues.TAG, "2")
//                    bottom_tv_post_count.text = contentDTO.size.toString()

                    //프로필 사진
                    homePostActivityBinding?.homePostProfileTextview?.text = userId.toString()
                    // 제목 텍스트
                    homePostActivityBinding?.homePostTitleTextview?.text = title.toString()
                    // 내용 텍스트
                    homePostActivityBinding?.homePostExplainTextview?.text = explain.toString()
                    // 이미지 사진
                    homePostActivityBinding?.homePostImageviewContent?.let {

                        Glide.with(applicationContext).load(imageUrl)
                            .into(it)
                    }
                    //좋아요개수
                    homePostActivityBinding?.homePostLike?.text =
                        "좋아요" + favoriteCount.toString() + "개"
                    //싫어요개수
                    homePostActivityBinding?.homePostMa?.text =
                        "싫어요" + meaningCount.toString() + "개"

                    //좋아요 버튼 설정
                    if (favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                        homePostActivityBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_redc)

                    } else {

                        homePostActivityBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_red)
                    }

                    //슬퍼요 버튼 설정
                    if (meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                        homePostActivityBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_bluec)

                    } else {

                        homePostActivityBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_blue)
                    }







                    Log.d(contentDTO.size.toString(), "bottomsize테스트")
                }

            }

    }





}








