package com.sjkorea.meetagain.homeFragment

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.Comment.CommentFragment
import com.sjkorea.meetagain.databinding.ItemFregmentHomepostBinding
import com.sjkorea.meetagain.databinding.ViewpagerPostItemBinding
import com.sjkorea.meetagain.utils.Constants.TAG
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.item_fregment_homepost.*
import kotlinx.android.synthetic.main.item_fregment_homepost.view.*


class HomePostFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var user: FirebaseUser? = null
    var uid: String? = null
    var homepostview: View? = null
    var uidsize : Int? = null
    var userId: String? = null
    var title: String? = null
    var explain: String? = null
    var imageUrl: String? = null
    var favoriteCount: Int? = null
    var meaningCount: Int? = null
    var favorites: HashMap<String, Boolean> = HashMap()
    var favoritess: String? = null
    var meaning: HashMap<String, Boolean> = HashMap()
    var contentUidListposition: String? = null
    var fcmPush: FcmPush? = null
    val contentUidList: ArrayList<String> = java.util.ArrayList()

    var imageprofileListenerRegistration: ListenerRegistration? = null
    var homepostListenerRegistration: ListenerRegistration? = null

    private var viewpagerPostItemBinding : ItemFregmentHomepostBinding ? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding  = ItemFregmentHomepostBinding.inflate(inflater, container,false)
        viewpagerPostItemBinding = binding


//        var homeview =
//            LayoutInflater.from(activity).inflate(R.layout.fregment_home, container, false)
        firestore = FirebaseFirestore.getInstance()

        userId = requireArguments().getString("userId")
        Log.d(this.userId, "홈 포스트 로그 userId 받기 ")
        uid = arguments?.getString("destinationUid")
        Log.d(this.uid, "홈 포스트 로그 uid 받기 ")
        uidsize = arguments?.getInt("uidsize")
        Log.d(this.uid, "홈 포스트 로그 uid 받기 ")
        title =  requireArguments().getString("title")
        Log.d(this.title, "홈 포스트 로그 title 받기 ")

        explain =  requireArguments().getString("explain")
        Log.d(this.explain, "홈 포스트 로그 explain 받기 ")

        imageUrl =  requireArguments().getString("imageUrl")
        Log.d(this.imageUrl, "홈 포스트 로그 imageUrl 받기 ")

        favoriteCount =  requireArguments().getInt("favoriteCount")
        Log.d(this.favoriteCount.toString(), "홈 포스트 로그 favoriteCount 받기 ")

        meaningCount =  requireArguments().getInt("meaningCount")
        Log.d(this.meaningCount.toString(), "홈 포스트 로그 meaningCount 받기 ")

        favorites  = requireArguments().getSerializable("favoriteshashmap") as HashMap<String, Boolean>
        Log.d(this.favorites.toString(), "홈 포스트 로그 favorites 받기 ")

        meaning  = requireArguments().getSerializable("meaninghashmap") as HashMap<String, Boolean>
        Log.d(this.meaning.toString(), "홈 포스트 로그 favorites 받기 ")

        contentUidListposition = requireArguments().getString("userIdposition")
        Log.d(contentUidListposition.toString(), " 홈 포스트 로그 contentUidListposition 받기")

        val contentDTOs: ArrayList<ContentDTO>
        val contentUidList: ArrayList<String>
        contentDTOs = java.util.ArrayList()
        contentUidList = java.util.ArrayList()

        //댓글창
        viewpagerPostItemBinding?.bottomviewitemCommentImageview?.setOnClickListener {

            val customcommentDialog = CommentFragment()
            var bundle = Bundle()

            bundle.putString("contentUid", contentUidListposition)
            Log.d(contentUidListposition.toString(), "홈포스트contentUid1 로그 ")

            bundle.putString("destinationUid",uid)
            Log.d(uid.toString(), "홈포스트uid2 로그 ")


            customcommentDialog
            customcommentDialog.arguments = bundle
            customcommentDialog.show(activity?.supportFragmentManager!!, "")
        }

        //프로필창
        viewpagerPostItemBinding?.homePostProfileImage?.setOnClickListener {


            val bundle = Bundle()

            val bottomSheetDialogFragment = CustomBottomDialog()

            bundle.putString("destinationUid", uid)
            bundle.putString("userId", userId)
            bundle.putString("userIdposition",contentUidListposition)

            bottomSheetDialogFragment.arguments = bundle

            bottomSheetDialogFragment.show(childFragmentManager, bottomSheetDialogFragment.tag)
        }



        //좋아요 버튼 설정
        if (favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

            viewpagerPostItemBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_redc)

        } else {

            viewpagerPostItemBinding?.homePostFavoriteImageview?.setImageResource(R.drawable.heart_red)
        }

        //슬퍼요 버튼 설정
        if (meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

            viewpagerPostItemBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_bluec)

        } else {

            viewpagerPostItemBinding?.homePostMaImageview?.setImageResource(R.drawable.heart_blue)
        }

        // 좋아요
        viewpagerPostItemBinding?.homePostLike?.text =
            "좋아요" + favoriteCount+ "개"
        viewpagerPostItemBinding?.homePostFavoriteImageview?.setOnClickListener {
            favoriteEvent()

        }

        // 슬퍼요
        viewpagerPostItemBinding?.homePostLike?.text =
            "슬퍼요" + favoriteCount + "개"
        viewpagerPostItemBinding?.homePostMaImageview?.setOnClickListener {
            Log.d(TAG, "확인:싫어요 ")
            meaningEvent()
        }



//        // meaning
//        homepostview.homeviewitem_meaningcounter_textview.text =
//            "슬퍼요" + contentDTOs!![position].meaningCount + "개"
//        homepostview.homeviewitem_meaning_imageview.setOnClickListener {
//            meaningEvent(position)
//        }
//        //슬퍼요 버튼 설정
//        if (contentDTOs[position].meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {
//
//            homepostview.homeviewitem_meaning_imageview.setImageResource(R.drawable.m2)
//
//        } else {
//
//            homepostview.homeviewitem_meaning_imageview.setImageResource(R.drawable.m1)
//        }



//        homepostview.home_post_favorite_imageview.setOnClickListener {
//            favoriteEvent()
//            Log.d(favoriteEvent().toString(), " 로그")
//        }

        return viewpagerPostItemBinding!!.root
    }

    override fun onResume() {
        super.onResume()
        gethomeposttitle()
        getProfileImage()

    }

    override fun onDestroy() {
        super.onDestroy()
        imageprofileListenerRegistration?.remove()
        homepostListenerRegistration?.remove()
    }

    override fun onDestroyView() {
        viewpagerPostItemBinding = null
        super.onDestroyView()
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // .setOnClickListener {
        //     customDialog.show(childFragmentManager, "")
        // }





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
        alarmDTO.userId = user?.email
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 0
        alarmDTO.timestamp = System.currentTimeMillis()


        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = alarmDTO.name + "님이 좋아요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }


    fun meaningAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()




        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = user?.email
        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)
        alarmDTO.uid = user?.uid
        alarmDTO.kind = 3
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = user?.email + "님이 힘내요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }

//
//    //좋아요 이벤트 기능
//    private fun favoriteEvent(position: Int) {
//
//        val contentUidList: ArrayList<String>
//        contentUidList = java.util.ArrayList()
//
//        var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
//        firestore?.runTransaction { transaction ->
//
//            val uid = FirebaseAuth.getInstance().currentUser!!.uid
//            val contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
//
//            if (contentDTO!!.favorites.containsKey(uid)) {
//                // Unstar the post and remove self from stars
//                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! - 1
//                contentDTO?.favorites.remove(uid)
//
//            } else {
//                // Star the post and add self to stars
//                contentDTO?.favoriteCount = contentDTO?.favoriteCount!! + 1
//                contentDTO?.favorites[uid] = true
//            }
//            transaction.set(tsDoc, contentDTO)
//        }
//    }
//
//    //싫어요
//    fun meaningEvent(position: Int) {
//        var tsDoc = firestore?.collection("images")?.document(contentUidList[position])
//
//
//        firestore?.runTransaction { transaction ->
//
//            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
//            var uid = FirebaseAuth.getInstance().currentUser!!.uid
//
//            if (contentDTO!!.meaning.containsKey(uid)) {
//                // When the button is clicked
//                contentDTO.meaningCount = contentDTO?.meaningCount - 1
//                contentDTO.meaning.remove(uid)
//            } else {
//                // When the button is not clicked
//                contentDTO.meaningCount = contentDTO?.meaningCount + 1
//                contentDTO.meaning[uid!!] = true
//            }
//            transaction.set(tsDoc, contentDTO)
//        }
//    }
    //프로필사진
    fun getProfileImage() {
    imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    val url = documentSnapshot.data!!["image"]
                    Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                        .into(this.home_post_profile_image)



                }
            }
    }
    //포스트
    fun gethomeposttitle() {
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
                    home_post_profile_textview.text = userId.toString()
                    // 제목 텍스트
                    home_post_title_textview.text = title.toString()
                    // 내용 텍스트
                    home_post_explain_textview.text = explain.toString()
                    // 이미지 사진
                    Glide.with(this).load(imageUrl)
                        .into(home_post_imageview_content)
                    //좋아요개수
                    home_post_like.text =
                        "좋아요" + favoriteCount.toString() + "개"
                    //싫어요개수
                    home_post_ma.text =
                        "싫어요" + meaningCount.toString() + "개"

                    //좋아요 버튼 설정
                    if (favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                        homepostview?.home_post_favorite_imageview?.setImageResource(R.drawable.heart_redc)

                    } else {

                        homepostview?.home_post_favorite_imageview?.setImageResource(R.drawable.heart_red)
                    }

                    //슬퍼요 버튼 설정
                    if (meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                        homepostview?.home_post_ma_imageview?.setImageResource(R.drawable.heart_bluec)

                    } else {

                        homepostview?.home_post_ma_imageview?.setImageResource(R.drawable.heart_blue)
                    }







                    Log.d(contentDTO.size.toString(), "bottomsize테스트")
                }

            }

    }

//    fun favoriteEvent(){
//        var tsDoc = firestore?.collection("images")?.document(contentUidListposition!!)
//
//        firestore?.runTransaction { transaction ->
//
//            var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java)
//
//            if(contentDTO!!.favorites.containsKey(uid)){
//                // When the button is clicked
//                contentDTO.favoriteCount = contentDTO?.favoriteCount - 1
//                contentDTO.favorites.remove(uid)
//            }else{
//                // When the button is not clicked
//                contentDTO.favoriteCount = contentDTO?.favoriteCount + 1
//                contentDTO.favorites[uid!!] = true
//            }
//            transaction.set(tsDoc,contentDTO)
//        }
//    }



}








