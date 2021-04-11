package com.sjkorea.meetagain.Adapter

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.homeFragment.HomeFragment
import com.sjkorea.meetagain.homeFragment.HomePostActivity
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.CONTEXT_NULL
import com.sjkorea.meetagain.utils.Constants.TAG
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.activity_firstvisit.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_main.view.*
import java.util.*
import kotlin.collections.ArrayList


class HomeViewRecyclerViewAdapter(
    context: HomeFragment,
    fragmentManager: FragmentManager, homeRecyclerviewInterface: IHomeRecyclerview,
    var contentArray: ArrayList<ContentDTO>,
    var comments: ArrayList<ContentDTO.Comment>,
    var firestore: FirebaseFirestore? = null,
    var fcmPush: FcmPush? = null,
    var contentUidList: ArrayList<String>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var context: HomeFragment
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var user: FirebaseUser? = null
    var uid: String? = null
    var name: String? = null
    var userid: String? = null

    //
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private var recyclerViewState: Parcelable? = null
    private var homeRecyclerviewInterface: IHomeRecyclerview? = null
    private var mFragmentManager: FragmentManager

    init {
        this.homeRecyclerviewInterface = homeRecyclerviewInterface
        mFragmentManager = fragmentManager
        this.context = context
        fcmPush = FcmPush()
        //아이디
        user = FirebaseAuth.getInstance().currentUser
//        contentArray = java.util.ArrayList()
//        contentUidList = java.util.ArrayList()
        comments = ArrayList()
        fcmPush = FcmPush()
        okHttpClient = OkHttpClient()
//        SortPosts()
//        SortPosts()
        setHasStableIds(true)
//        Sortnotify()

    }


    // 뷰의 타입을 정해주는 곳이다.
    override fun getItemViewType(position: Int): Int {
        // 게시물과 프로그레스바 아이템뷰를 구분할 기준이 필요하다.
        return when (contentArray[position].title) {
            " " -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val binding =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_sub, parent, false)
                CustomViewHolder(binding, this.homeRecyclerviewInterface!!)
            }
            else -> {
                val binding =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
                LoadingViewHolder(binding)
            }

        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is CustomViewHolder){
            holder.bind(contentArray[position],mFragmentManager)
        }else{

        }
//        // switch
//        when (holder) {
//            is CustomViewHolder -> {
//
//                if (contentArray.size > 0) {
//                    Constants.CONTEXT_NULL = "CONTEXT_NOT_NULL"
//                    Log.d(Constants.TAG, "1: ${CONTEXT_NULL}")
//                    holder.bind(contentArray[position], mFragmentManager)
//                } else {
//                    Constants.CONTEXT_NULL = "CONTEXT_NULL"
//                    Log.d(Constants.TAG, "2: ${CONTEXT_NULL}")
//                }
//            }
//        }

    }

    // 아이템뷰에 프로그레스바가 들어가는 경우
    inner class LoadingViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
    }

    inner class CustomViewHolder(itemView: View, recyclerviewInterface: IHomeRecyclerview) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        private var homeRecyclerviewInterface: IHomeRecyclerview? = null

        init {
            itemView.setOnClickListener(this)
            this.homeRecyclerviewInterface = recyclerviewInterface

        }

        //프로필 닉네임
        private val dataprofilename = itemView.homeviewitem_profile_name

        //제목
        private val datatitle = itemView.homeviewitem_profile_textview

        //메인사진
        private val datacontext = itemView.homeviewitem_explain_textview

        //좋아요 텍스트
        private val fovorite = itemView.homeviewitem_favoritecounter_textview

        //힘내요 텍스트
        private val meaning = itemView.homeviewitem_meaningcounter_textview

        //좋아요 버튼
        private val fovoritebtn = itemView.homeviewitem_fovorite_imageview

        //힘내요 버튼
        private val meaningbtn = itemView.homeviewitem_meaning_imageview


        fun bind(contentDTOs: ContentDTO, fragmentManager: FragmentManager) {
            //  Profile Image 가져오기
            firestore?.collection("profileImages")?.document(contentDTOs.uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val url = task.result!!["image"]
                        Glide.with(itemView.context)
                            .load(url)
                            .apply(RequestOptions().circleCrop())
                            .placeholder(R.drawable.icon_noimage1)
                            .error(R.drawable.icon_noimage1)
                            .into(itemView.homeviewitem_profile_image)
                    }
                }


            //프로필 닉네임
//          dataprofilename.text = contentDTOs.name\


            firestore?.collection("profileName")?.document(contentDTOs.uid!!)
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val nameValue = task.result!!["name"]
                        userid = nameValue.toString()
//                        Log.d(Constants.TAG, "userid1: $userid")
                        dataprofilename.text = nameValue.toString()

                    }
                }?.addOnFailureListener {


                }


            //제목
            datatitle.text = contentDTOs.title
            // 메인사진
            Glide.with(itemView.context).load(contentDTOs.imageUrl)
                .into(itemView.homeviewitem_imageview_content)
//            itemView.homeviewitem_profile_name.text = contentDTOs[position].name
            // 내용
            datacontext.text = contentDTOs.explain

//
//            // 유저 아이디
//            itemView.homeviewitem_profile_name.text = contentDTOs[position].name

//            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            val formatter = SimpleDateFormat("yyy년\nMM월 dd일")
//
//            val outputDateString = formatter.format(parser.parse(contentDTOs[position].timestamp.toString()))


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
            itemView.date.text = diffTime.toString() + msg.toString()
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
            if (contentDTOs.favorites.containsKey(FirebaseAuth.getInstance().currentUser?.uid)) {

                fovoritebtn.setImageResource(R.drawable.heart_redc)
            } else {
                fovoritebtn.setImageResource(R.drawable.heart_red)
            }

            // 힘내요
            meaning.text =
                "힘내요" + contentDTOs!!.meaningCount + "개"
            meaningbtn.setOnClickListener {
                meaningEvent(position)
            }
            //힘내요 버튼 설정
            if (contentDTOs.meaning.containsKey(FirebaseAuth.getInstance().currentUser?.uid)) {

                meaningbtn.setImageResource(R.drawable.heart_bluec)

            } else {

                meaningbtn.setImageResource(R.drawable.heart_blue)
            }
//            //댓글 사이즈
//            itemView.homeviewitem_commentcounter_textview.text = "댓글" + comments[position].commentCount + "개"


            //바텀 프래그먼트 프로필 호출
            itemView.homeviewitem_profile_image.setOnClickListener {
                Constants.POSTSHOW = "mainView"


                val bundle = Bundle()
                bundle.putString("pathData", contentDTOs.pathData)
                bundle.putString("destinationUid", contentDTOs.uid)
//                bundle.putString("userId", contentDTOs.name)
                bundle.putString("userId", userid)
                Log.d(Constants.TAG, "userid2: $userid")
                //userIdposition
                bundle.putString("userIdposition", contentUidList[position])
                SharedPreferenceFactory.putStrValue("contentUidList", contentUidList[position])

                val bottomSheetDialogFragment = CustomBottomDialog()
                bottomSheetDialogFragment.arguments = bundle
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

    override fun getItemId(position: Int): Long =
        position.toLong()


    override fun getItemCount(): Int {
        return contentArray.size
    }


    fun setList(notice: MutableList<ContentDTO>) {
        contentArray.addAll(notice)
        contentArray.add(ContentDTO(" ", " ")) // progress bar 넣을 자리
    }

    fun deleteLoading(){
        contentArray.removeAt(contentArray.lastIndex) // 로딩이 완료되면 프로그레스바를 지움
    }


    //시간
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
                favoriteAlarm(contentArray[position].uid!!)
            }
            transaction.set(tsDoc, contentDTO)
        }
    }


    //힘내요 이벤트 기능
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

        var message = alarmDTO.name + "님이 힘내요를 눌렀습니다"
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }

    fun homePostData(position: Int) {
        // 데이터  넘겨줌
        val intent = Intent(getApplicationContext(), HomePostActivity::class.java)

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
        //힘내요버튼
        var hashmap2 = contentArray[position].meaning

        intent.putExtra("meaninghashmap", hashmap2)
        //putSerializable
        intent.putExtra("hashmap", contentArray[position].favorites)
        context.startActivity(intent);

    }

    // 즉 총알을 넣는다.
    fun submitList(contentArray: ArrayList<ContentDTO>) {
        this.contentArray = contentArray
    }

    fun addList(contentArray: ArrayList<ContentDTO>) {
        this.contentArray.addAll(contentArray)
    }

    // 비운다.
    fun clearList() {
        this.contentArray.clear()
    }


}