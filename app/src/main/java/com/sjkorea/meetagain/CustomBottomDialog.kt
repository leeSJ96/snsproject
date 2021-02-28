package com.sjkorea.meetagain

import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.homeFragment.HomePostActivity
import com.sjkorea.meetagain.utils.Constants.POSTSHOW
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.fragment_user.*


class CustomBottomDialog : BottomSheetDialogFragment() {


    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var BottomView: View? = null
    var currentUserUid: String? = null
    var fcmPush: FcmPush? = null
    var userId: String? = null
    var bundle = Bundle()
    var path : String? = null
    var contentUidListposition: String? = null

    var followListenerRegistration: ListenerRegistration? = null
    var followingListenerRegistration: ListenerRegistration? = null
    var imageprofileListenerRegistration: ListenerRegistration? = null
    var getTitleListenerRegistration: ListenerRegistration? = null
    var recyclerListenerRegistration: ListenerRegistration? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        //...
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val bottomSheetDialog = BottomSheetDialog(requireContext(), theme)
//        bottomSheetDialog.setOnShowListener { dialog ->
//
//
//            val fragmentView =
//                (dialog as BottomSheetDialog).findViewById<FrameLayout>(R.id.design_bottom_sheet)
//            BottomSheetBehavior.from(fragmentView!!).state = BottomSheetBehavior.STATE_EXPANDED
//            BottomSheetBehavior.from(fragmentView).skipCollapsed = true
//            BottomSheetBehavior.from(fragmentView).isHideable = true
//
//            bottomSheetDialog.setCancelable(true); // true : cancle , false : no cancle
//        }
//        return bottomSheetDialog
//
//    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        BottomView = inflater.inflate(R.layout.custom_dialog, container)
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        fcmPush = FcmPush()
        contentUidListposition = requireArguments().getString("userIdposition")
        path = requireArguments().getString("pathData")
        Log.d(contentUidListposition.toString(), " 홈 포스트 로그 contentUidListposition 받기")

        userId = requireArguments().getString("userId")

        Log.d(this.uid, "uid 2 ")


        if (arguments != null) {

            uid = requireArguments().getString("destinationUid")
            if (uid != null && uid == currentUserUid) {
                //나의 유저페이지
                BottomView?.account_btn_follow_signout?.visibility = View.INVISIBLE
                BottomView?.Bottom_profile_image_my?.visibility = View.VISIBLE
                BottomView?.Bottom_profile_image?.visibility = View.INVISIBLE
            } else {
                //제 3자의 유저페이지
                BottomView?.account_btn_follow_signout?.text = getString(R.string.follow)
                BottomView?.Bottom_profile_image_my?.visibility = View.INVISIBLE
                BottomView?.Bottom_profile_image?.visibility = View.VISIBLE
//                var mainActivity = (activity as MainActivity)
                BottomView?.btoolbar_btn_back?.setOnClickListener {
                    dialog?.dismiss()
                }
                BottomView?.account_btn_follow_signout?.setOnClickListener {
                    requestFollow()
                }


            }


        }

//        contentDTO = requireArguments().getInt("name")
//        Log.d(this.contentDTO.toString(),"btome size 받기 ")
//        bottom_tv_post_count!!.text = contentDTO.toString()
//        Log.d(contentDTO.toString(), "size 결과 ")

//        if(arguments != null){
//            uid = requireArguments().getString("destinationUid")
//            if (uid != null && uid == currentUserUid){
//
//            }
//        } else{
//            BottomView!!.bottom_btn_follow_signout.text = getString(R.string.follow)
//
//            var mainActivity = (activity as MainActivity)
//            mainActivity.toolbar_title_image.visibility = View.GONE
//            mainActivity.toolbar_btn_back.visibility = View.VISIBLE
//            mainActivity.toolbar_username.visibility = View.VISIBLE
//            mainActivity.toolbar_username.text = requireArguments().getString("userId")
//            mainActivity.toolbar_btn_back.setOnClickListener {
//                mainActivity.bottomNavigationView.selectedItemId = R.id.action_home
//            }
//
//        }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()


        return BottomView
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (BottomView != null) {
            val parentViewGroup = BottomView!!.getParent() as ViewGroup
            parentViewGroup.removeView(BottomView)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btoolbar_username.text = userId
//        // Profile Image 가져오기
//        firestore?.collection("profileImages")?.document(uid!!)
//            ?.get()?.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//
//                    val url = task.result!!["image"]
//                    Glide.with(this)
//                        .load(url)
//                        .apply(RequestOptions().circleCrop())
//                        .into(Bottom_profile_image)
//
//                }
//            }


        //팔로우 버튼
        account_btn_follow_signout.setOnClickListener {
            requestFollow()
        }

        //바텀다이얼로그 == 게시글보기 버튼
        this.btitle_text.setOnClickListener {
            dismiss()

            val intent = Intent(context, HistorySubActivity::class.java)

            intent.putExtra("userId", userId)
            intent.putExtra("destinationUid", uid)
            intent.putExtra("userIdposition", contentUidListposition)
            intent.putExtra("pathData", path)
            startActivity(intent)

//            var historyFrag = HistoryFragmentSub()
//
////            bundle.putString("userId", userId)
//            bundle.putString("destinationUid", uid)
//            bundle.putString("userId", userId)
//            bundle.putString("userIdposition", contentUidListposition)
//            bundle.putString("pathData", path)
//            Log.d(contentUidListposition.toString(), "홈포스트contentUid1 로그 ")
//            Log.d(uid.toString(), "로그 바텀 보내기 ")
////            HistoryFrag.arguments = bundle
//
//
////            userFragment.arguments = bundle
//            historyFrag.arguments = bundle
//
//            when (POSTSHOW) {
//                "mainView" -> requireActivity().supportFragmentManager.beginTransaction().replace(
//                    R.id.main_view,
//                    historyFrag,)
//                    .addToBackStack(null)
//                    .commit()
//
//
//                "homePostView" -> requireActivity().supportFragmentManager.beginTransaction().replace(
//                    R.id.homepost_view,
//                    historyFrag,
//                    )
//                    .addToBackStack(null)
//                    .commit()
//
//
//            }
        }

        //바텀다이얼로그 == 상대방 프로필사진 보기
        this.Bottom_profile_image.setOnClickListener {

            bundle.putString("destinationUid", uid)
            bundle.putString("userId", userId)
            bundle.putString("pathData", path)

            val dialog = ProfileImageDialog(requireContext())

            dialog.arguments = bundle

            dialog.show(childFragmentManager, dialog.tag)


        }

        //바텀다이얼로그 == 나의 프로필사진 보기
        this.Bottom_profile_image_my.setOnClickListener {

            bundle.putString("destinationUid", uid)
            bundle.putString("userId", userId)

            val dialog = ProfileImageDialog(requireContext())

            dialog.arguments = bundle

            dialog.show(childFragmentManager, dialog.tag)


        }


    }


    override fun onResume() {
        super.onResume()
        getProfileImage()
        gettitlecount()
        getFolloerAndFollowing()

    }

    override fun onStop() {
        super.onStop()

        getTitleListenerRegistration?.remove()
        imageprofileListenerRegistration?.remove()
        followListenerRegistration?.remove()
    }

    fun gettitlecount() {
        var contentDTO: ArrayList<ContentDTO>
        contentDTO = ArrayList()

        getTitleListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTO.clear()
                if (querySnapshot == null)
                    return@addSnapshotListener
                for (snapshot in querySnapshot!!.documents) {
                    Log.d(ContentValues.TAG, "1")
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                    Log.d(ContentValues.TAG, "2")
                    bottom_tv_post_count.text = contentDTO.size.toString()
                    Log.d(contentDTO.size.toString(), "bottomsize테스트")
                }

            }

    }

//    fun gettitleName() {
//        var contentDTO: ArrayList<ContentDTO>
//        contentDTO = ArrayList()
//
//        firestore?.collection("images")?.whereEqualTo("uid", uid)
//            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                contentDTO.clear()
//                if (querySnapshot == null)
//                    return@addSnapshotListener
//                for (snapshot in querySnapshot!!.documents) {
//                    Log.d(ContentValues.TAG, "1")
//                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
//                    Log.d(ContentValues.TAG, "2")
//                    btoolbar_username.text = userId.toString()
//                    Log.d(userId.toString(), "로그 바텀프래그먼트 닉네임")
//                }
//
//            }
//
//    }

    fun getProfileImage() {
        imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid.toString())
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    val url = documentSnapshot.data!!["image"]
                    if (url != null) {
                        BottomView?.Bottom_profile_image?.let {
                            Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                                .into(it)
                        }
                        BottomView?.let {
                            Glide.with(this).load(url).apply(RequestOptions().circleCrop())
                                .into(it.Bottom_profile_image_my)
                        }
                    } else {

                    }
                }
            }
    }


    fun requestFollow() {

        // Save data to my account

        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }


            if (followDTO?.followings?.containsKey(uid)!!) {
                // It remove following third person when a third person follow me
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {
                // It remove following third person when a third person not follow me
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        // Save data to third person
        var tsDocFollower = firestore!!.collection("users").document(uid!!)
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid!!)) {
                // It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {
                // It cancel my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true
                followerAlarm(uid)
            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }


    }

    fun followerAlarm(destinationUid: String?) {
        var alarmDTO = AlarmDTO()


        var contentDTO: ArrayList<ContentDTO>
        contentDTO = ArrayList()

        getTitleListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTO.clear()
                if (querySnapshot == null)
                    return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    Log.d(ContentValues.TAG, "1")
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                    Log.d(ContentValues.TAG, "2")

//
//                        account_tv_post_count.text = contentDTO.size.toString()

                    Log.d(contentDTO.size.toString(), "size테스트")
                }

            }



        alarmDTO.destinationUid = destinationUid
        alarmDTO.userId = auth?.currentUser?.email
        alarmDTO.uid = auth?.currentUser!!.uid
        alarmDTO.kind = 2
        alarmDTO.timestamp = System.currentTimeMillis()
        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message = auth?.currentUser?.email + getString(R.string.alarm_follow)
        fcmPush?.sendMessage(destinationUid!!, "알림 메시지 입니다", message)
    }



    fun getFolloerAndFollowing() {
        followListenerRegistration = firestore?.collection("users")?.document(uid.toString())
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener

                var followDTO = documentSnapshot.toObject(FollowDTO::class.java)

                if (followDTO?.followingCount != null) {
                    BottomView?.bottom_tv_following_count?.text =
                        followDTO?.followingCount?.toString()
                }
                if (followDTO?.followerCount != null) {
                    BottomView?.bottom_tv_follower_count?.text =
                        followDTO?.followerCount?.toString()

                    if (followDTO?.followers?.containsKey(currentUserUid!!)) {
                        BottomView?.account_btn_follow_signout?.text =
                            getString(R.string.follow_cancel)
                        BottomView?.account_btn_follow_signout?.background?.setColorFilter(
                            ContextCompat.getColor(requireActivity(), R.color.colorLightGray),
                            PorterDuff.Mode.MULTIPLY
                        )
                    } else {
                        if (uid != currentUserUid) {
                            BottomView?.account_btn_follow_signout?.text =
                                getString(R.string.follow)
                            BottomView?.account_btn_follow_signout?.background?.colorFilter = null
                        }
                    }
                }
            }
    }


}
