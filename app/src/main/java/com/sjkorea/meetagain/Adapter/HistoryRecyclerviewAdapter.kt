package com.sjkorea.meetagain.Adapter


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.UserFragment.HistoryFragment
import com.sjkorea.meetagain.UserFragment.UserFragment
import com.sjkorea.meetagain.homeFragment.HomeFragment
import com.sjkorea.meetagain.homeFragment.HomePostActivity
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.item_main.view.*
import kotlinx.android.synthetic.main.item_mysub.view.*

class HistoryRecyclerviewAdapter(
    context: HistoryFragment,
    fragmentManager: FragmentManager, homeRecyclerviewInterface: IHomeRecyclerview,
    private var contentArray: ArrayList<ContentDTO>,
    var comments: ArrayList<ContentDTO.Comment>,
    var firestore: FirebaseFirestore? = null,
    var fcmPush: FcmPush? = null,
) : RecyclerView.Adapter<HistoryRecyclerviewAdapter.CustomViewHolder>() {
    private var context: HistoryFragment
    val contentUidList: ArrayList<String>
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var user: FirebaseUser? = null
    var uid: String? = null
    var name: String? = null

    private var homeRecyclerviewInterface: IHomeRecyclerview? = null
    private var mFragmentManager: FragmentManager

    init {
        this.homeRecyclerviewInterface = homeRecyclerviewInterface
        mFragmentManager = fragmentManager
        this.context = context
        fcmPush = FcmPush()

        //아이디
        user = FirebaseAuth.getInstance().currentUser
        contentArray = java.util.ArrayList()
        contentUidList = java.util.ArrayList()
        comments = ArrayList()
        fcmPush = FcmPush()
        okHttpClient = OkHttpClient()
        SortPosts()
        setHasStableIds(true)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        return CustomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_mysub, parent, false),
            this.homeRecyclerviewInterface!!
        )
    }

    override fun onBindViewHolder(
        holder: HistoryRecyclerviewAdapter.CustomViewHolder,
        position: Int
    ) {
        holder.bind(contentArray[position], mFragmentManager)
    }



    inner class CustomViewHolder(itemView: View, recyclerviewInterface: IHomeRecyclerview) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private var homeRecyclerviewInterface: IHomeRecyclerview? = null

        init {
            itemView.setOnClickListener(this)
            this.homeRecyclerviewInterface = recyclerviewInterface
        }



        //제목
        private val datatitle = itemView.historyviewitem_profile_textview

        //메인사진
        private val datacontext = itemView.historyviewitem_explain_textview

        //좋아요 텍스트
        private val fovorite = itemView.historyviewitem_favoritecounter_textview

        //싫어요 텍스트
        private val meaning = itemView.historyviewitem_meaningcounter_textview

        //좋아요 버튼
        private val fovoritebtn = itemView.historyviewitem_fovorite_imageview

        //싫어요 버튼
        private val meaningbtn = itemView.historyviewitem_meaning_imageview
        //시간
        private val date = itemView.historydate


        fun bind(contentDTOs: ContentDTO, fragmentManager: FragmentManager) {


            //제목
            datatitle.text = contentDTOs.title
            // 메인사진
            Glide.with(itemView.context).load(contentDTOs.imageUrl)
                .into(itemView.historyviewitem_imageview_content)

            // 내용
            datacontext.text = contentDTOs.explain

            // 시간
            val curTime = System.currentTimeMillis()
            var diffTime = (curTime - contentDTOs.timestamp!!) / 1000
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
            date.text = diffTime.toString() + msg.toString()
//
//            itemView.date.text = contentDTOs[po]
//            itemView.date.text =  contentDTOs[position].timestamp.toString()

//            itemView.date.text = contentDTOs[position].timestamp.toString()


//            // 게시물 삭제 버튼
//            itemView.btn_delete.setOnClickListener {
//                deleteData(position)
//
//            }
            // 좋아요
            fovorite.text =
                "좋아요" + contentDTOs!!.favoriteCount + "개"
            fovoritebtn.setOnClickListener {
                favoriteEvent(position)
            }
            //좋아요 버튼 설정
            if (contentDTOs.favorites.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                fovoritebtn.setImageResource(R.drawable.heart_redc)
            } else {
                fovoritebtn.setImageResource(R.drawable.heart_red)
            }

            // 슬퍼요
            meaning.text =
                "슬퍼요" + contentDTOs!!.meaningCount + "개"
            meaningbtn.setOnClickListener {
                meaningEvent(position)
            }
            //슬퍼요 버튼 설정
            if (contentDTOs.meaning.containsKey(FirebaseAuth.getInstance().currentUser!!.uid)) {

                meaningbtn.setImageResource(R.drawable.heart_bluec)

            } else {

                meaningbtn.setImageResource(R.drawable.heart_blue)
            }
//            //댓글 사이즈
//            itemView.homeviewitem_commentcounter_textview.text = "댓글" + comments[position].commentCount + "개"



            //누르면 홈포스트 프래그먼트 호출
            itemView.history_post_item.setOnClickListener {
                homePostData(adapterPosition)

            }

            //내용누르면 홈프래그먼트로 이동
            itemView.historyviewitem_explain_textview.setOnClickListener {
                homePostData(adapterPosition)
            }


        }

        override fun onClick(v: View?) {
            Log.d(Constants.TAG, "CustomViewHolder -onClick() called ")
            this.homeRecyclerviewInterface?.onItemClicked(adapterPosition)
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()


    override fun getItemCount(): Int {

        return contentArray.size
    }

    //시간
    enum class TimeValue(val value: Int, val maximum: Int, val msg: String) {
        SEC(60, 60, "분 전"),
        MIN(60, 24, "시간 전"),
        HOUR(24, 30, "일 전"),
        DAY(30, 12, "달 전"),
        MONTH(12, Int.MAX_VALUE, "년 전")
    }

    //게시글 정렬 순서 필터
    private fun SortPosts(){

        var uid = FirebaseAuth.getInstance().uid
            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    contentArray.clear()
                    contentUidList.clear()
                    if (querySnapshot == null) return@addSnapshotListener
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentArray.add(item!!)
                        Log.d(Constants.TAG, "historytoday size ${contentArray.size}")
                        contentUidList.add(snapshot.id)
                    }
                    notifyDataSetChanged()
                }


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
                favoriteAlarm(contentArray[position].uid!!)
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

                meaningAlarm(contentArray[position].uid!!)
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

    fun homePostData(position: Int) {
        // 데이터  넘겨줌
        val intent = Intent(FacebookSdk.getApplicationContext(), HomePostActivity::class.java)

        intent.putExtra("contentDTO", contentArray[position])

        //uid
        intent.putExtra("destinationUid", contentArray[position].uid)

        //userid
        intent.putExtra("userId", contentArray[position].name)

        //title
        intent.putExtra("title", contentArray[position].title)

        //explain
        intent.putExtra("explain", contentArray[position].explain)

        //imageUrl
        intent.putExtra("imageUrl", contentArray[position].imageUrl)

        //favoriteCount
        intent.putExtra("favoriteCount", contentArray[position].favoriteCount)

        //userIdposition
        intent.putExtra("userIdposition", contentUidList[position])

        //meaningCount
        intent.putExtra("meaningCount", contentArray[position].meaningCount)

        //좋아요버튼
        var hashmap = contentArray[position].favorites

        intent.putExtra("favoriteshashmap", hashmap)
        //싫어요버튼
        var hashmap2 = contentArray[position].meaning

        intent.putExtra("meaninghashmap", hashmap2)
        //putSerializable
        intent.putExtra("hashmap", contentArray[position].favorites)
        context.startActivity(intent);

    }



}