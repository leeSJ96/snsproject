package com.sjkorea.meetagain.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.UserFragment.HistoryFragment
import com.sjkorea.meetagain.UserFragment.UserFragment
import com.sjkorea.meetagain.homeFragment.HomePostFragment
import com.sjkorea.meetagain.model.IdDTO
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.TAG
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.activity_firstvisit.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.util.*
import java.util.Calendar.getInstance
import kotlin.collections.ArrayList

class HomeViewRecyclerViewAdapter(
    fragmentManager: FragmentManager,
    homeRecyclerviewInterface: HomeRecyclerviewInterface,
    var contentDTOs: ArrayList<ContentDTO>,
    var idDTO: ArrayList<IdDTO>,
    var comments: ArrayList<ContentDTO.Comment>,
    var firestore: FirebaseFirestore? = null,
    var fcmPush: FcmPush? = null,
    var rvTransaction: FragmentTransaction,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val contentUidList: ArrayList<String>
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var user: FirebaseUser? = null
    var uid: String? = null
    var name: String? = null

    private var homeRecyclerviewInterface: HomeRecyclerviewInterface? = null
    private var mFragmentManager: FragmentManager

    init {
        this.homeRecyclerviewInterface = homeRecyclerviewInterface
        mFragmentManager = fragmentManager

        fcmPush = FcmPush()

        //아이디
        user = FirebaseAuth.getInstance().currentUser
        contentDTOs = java.util.ArrayList()
        contentUidList = java.util.ArrayList()
        idDTO = java.util.ArrayList()
        comments = ArrayList()
        fcmPush = FcmPush()
        okHttpClient = OkHttpClient()


        imagesSnapshot = firestore?.collection("images")?.orderBy("timestamp")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()
                idDTO.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(ContentDTO::class.java)
                    contentDTOs.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                notifyDataSetChanged()
            }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false),
            this.homeRecyclerviewInterface!!
        )
    }


    inner class CustomViewHolder(
        itemView: View,
        recyclerviewInterface: HomeRecyclerviewInterface
    ) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var homeRecyclerviewInterface: HomeRecyclerviewInterface? = null

        init {
            itemView.setOnClickListener(this)
            this.homeRecyclerviewInterface = recyclerviewInterface
        }


        fun bind(
            contentDTOs: ArrayList<ContentDTO>,
            idDTO: ArrayList<IdDTO>,
            comments: ArrayList<ContentDTO.Comment>,
            fragmentManager: FragmentManager
        ) {
            //  Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs[position].uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]
                        Glide.with(itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop())
                            .into(itemView.homeviewitem_profile_image)

                    }
                }

            //닉네임
            itemView.homeviewitem_profile_name.text = contentDTOs[position].name


            //제목
            itemView.homeviewitem_profile_textview.text = contentDTOs[position].title

//            itemView.homeviewitem_profile_name.text = contentDTOs[position].name

            // 메인사진
            Glide.with(itemView.context).load(contentDTOs[position].imageUrl)
                .into(itemView.homeviewitem_imageview_content)

            // 내용
            itemView.homeviewitem_explain_textview.text = contentDTOs[position].explain


//
//            // 유저 아이디
//            itemView.homeviewitem_profile_name.text = contentDTOs[position].name

//            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val formatter = SimpleDateFormat("yyy년\nMM월 dd일")
//
//            val outputDateString = formatter.format(parser.parse(contentDTOs[position].timestamp.toString()))


            // 시간
            val curTime = System.currentTimeMillis()
            var diffTime = (curTime - contentDTOs[position].timestamp!!) / 1000
            var msg: String? = null
            if (diffTime < TimeValue.SEC.value)
                msg = "초 전"
            else {
                for (i in TimeValue.values()) {
                    diffTime /= i.value
                    if (diffTime < i.maximum) {
                        msg = i.msg
                        break
                    }
                }
            }


            itemView.date.text = diffTime.toString() + msg.toString()
//
//            itemView.date.text = contentDTOs[po]
//            itemView.date.text =  contentDTOs[position].timestamp.toString()

//            itemView.date.text = contentDTOs[position].timestamp.toString()


            // 좋아요
            itemView.homeviewitem_favoritecounter_textview.text =
                "좋아요" + contentDTOs!![position].favoriteCount + "개"
            itemView.homeviewitem_fovorite_imageview.setOnClickListener {
                favoriteEvent(position)
            }
            // 게시물 삭제 버튼
            itemView.btn_delete.setOnClickListener {
                deleteData(position)

            }

            //좋아요 버튼 설정
            if (contentDTOs[position].favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                itemView.homeviewitem_fovorite_imageview.setImageResource(R.drawable.heart_redc)
            } else {
                itemView.homeviewitem_fovorite_imageview.setImageResource(R.drawable.heart_red)
            }

            // 슬퍼요
            itemView.homeviewitem_meaningcounter_textview.text =
                "슬퍼요" + contentDTOs!![position].meaningCount + "개"
            itemView.homeviewitem_meaning_imageview.setOnClickListener {
                meaningEvent(position)
            }
            //슬퍼요 버튼 설정
            if (contentDTOs[position].meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                itemView.homeviewitem_meaning_imageview.setImageResource(R.drawable.heart_bluec)

            } else {

                itemView.homeviewitem_meaning_imageview.setImageResource(R.drawable.heart_blue)
            }
//            //댓글 사이즈
//            itemView.homeviewitem_commentcounter_textview.text = "댓글" + comments[position].commentCount + "개"


            //바텀 프래그먼트 프로필 호출
            itemView.homeviewitem_profile_image.setOnClickListener {

                val bundle = Bundle()

                bundle.putString("destinationUid", contentDTOs[position].uid)
                bundle.putString("userId", contentDTOs[position].name)
                //userIdposition
                bundle.putString("userIdposition", contentUidList[position])

                val userFragment = UserFragment()
                val historyF = HistoryFragment()
                val bottomSheetDialogFragment = CustomBottomDialog()

                userFragment.arguments = bundle
                bottomSheetDialogFragment.arguments = bundle
                historyF.arguments = bundle

                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)
            }

            //누르면 홈포스트 프래그먼트 호출
            itemView.home_post_item.setOnClickListener {
                homePostData(adapterPosition)

            }

            //내용누르면 홈프래그먼트로 이동
            itemView.homeviewitem_explain_textview.setOnClickListener {
                homePostData(adapterPosition)
            }


        }


        override fun onClick(v: View?) {
            Log.d(TAG, "CustomViewHolder -onClick() called ")
            this.homeRecyclerviewInterface?.onItemClicked(adapterPosition)
        }
    }


    override fun getItemCount(): Int {

        return contentDTOs.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var Holder: RecyclerView.ViewHolder = holder as CustomViewHolder
        var data = contentDTOs[position]

//        var viewHolder = (holder as CustomViewHolder).itemView
        holder.bind(contentDTOs, idDTO, comments, mFragmentManager)


    }


    enum class TimeValue(val value: Int, val maximum: Int, val msg: String) {
        SEC(60, 60, "분 전"),
        MIN(60, 24, "시간 전"),
        HOUR(24, 30, "일 전"),
        DAY(30, 12, "달 전"),
        MONTH(12, Int.MAX_VALUE, "년 전")
    }


    //좋아요 이벤트 기능
    private fun favoriteEvent(position: Int) {
        val tsDoc = firestore?.collection("images")?.document(contentUidList[position])
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
                favoriteAlarm(contentDTOs[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }


    //싫어요 이벤트 기능
    private fun meaningEvent(position: Int) {
        val tsDoc = firestore?.collection("images")?.document(contentUidList[position])
        firestore?.runTransaction { transaction ->

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

                meaningAlarm(contentDTOs[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }

    }

    fun favoriteAlarm(destinationUid: String) {
        val alarmDTO = AlarmDTO()

        var contentDTO: ArrayList<ContentDTO>
        contentDTO = ArrayList()



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

    fun homePostData(position: Int) {
        //번들 데이터
        val bundle = Bundle()
        val homePostfrg = HomePostFragment()
        val hstoryFragmentSub = HistoryFragmentSub()
        //uid
        bundle.putString("destinationUid", contentDTOs[position].uid)

        //userid
        bundle.putString("userId", contentDTOs[position].name)

        //title
        bundle.putString("title", contentDTOs[position].title)

        //explain
        bundle.putString("explain", contentDTOs[position].explain)

        //imageUrl
        bundle.putString("imageUrl", contentDTOs[position].imageUrl)

        //favoriteCount
        bundle.putInt("favoriteCount", contentDTOs[position].favoriteCount)

        //userIdposition
        bundle.putString("userIdposition", contentUidList[position])

        //meaningCount
        bundle.putInt("meaningCount", contentDTOs[position].meaningCount)

        //좋아요버튼
        var hashmap = contentDTOs[position].favorites

        bundle.putSerializable("favoriteshashmap", hashmap)
        //싫어요버튼
        var hashmap2 = contentDTOs[position].meaning

        bundle.putSerializable("meaninghashmap", hashmap2)

        bundle.putSerializable("hashmap", contentDTOs[position].favorites)

        contentDTOs[position].favorites =
            bundle.getSerializable("hashmap") as HashMap<String, Boolean>

        hstoryFragmentSub.arguments = bundle
        homePostfrg.arguments = bundle
        rvTransaction.replace(R.id.main_content, homePostfrg).commit()
    }

//    enum class TimeValue(val value: Int,val maximum : Int, val msg : String) {
//        SEC(60,60,"분 전"),
//        MIN(60,24,"시간 전"),
//        HOUR(24,30,"일 전"),
//        DAY(30,12,"달 전"),
//        MONTH(12,Int.MAX_VALUE,"년 전")
//    }
//
//    fun timeDiff(time : Long):String{
//
//
//        val curTime = System.currentTimeMillis()
//        var diffTime = (curTime- timestampz) / 1000
//        var msg: String? = null
//        if(diffTime < TimeValue.SEC.value )
//            msg= "방금 전"
//        else {
//            for (i in TimeValue.values()) {
//                diffTime /= i.value
//                if (diffTime < i.maximum) {
//                    msg=i.msg
//                    break
//                }
//            }
//        }
//    }


    private fun deleteData(position: Int) {
        firestore?.collection("images")?.document(contentDTOs[position].uid.toString())?.delete()
            ?.addOnSuccessListener {
                    Toast.makeText(getApplicationContext(), "삭제성공.", Toast.LENGTH_SHORT).show()

            }
            ?.addOnFailureListener {
                     Toast.makeText(getApplicationContext(), "삭제실패.", Toast.LENGTH_SHORT).show()
            }


    }
}