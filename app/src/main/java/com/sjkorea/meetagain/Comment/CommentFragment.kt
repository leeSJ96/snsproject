package com.sjkorea.meetagain.Comment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.Adapter.CommentRecyclerViewAdapter
import com.sjkorea.meetagain.AlarmDTO
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.custom_dialog_comments.*


class CommentFragment() : BottomSheetDialogFragment() {

    var firestore: FirebaseFirestore? = null
    var commentview: View? = null
    var contentUid: String? = null
    var destinationUid: String? = null
    var user: FirebaseAuth? = null
    var fcmPush: FcmPush? = null
    var commentuid: String? = null
    var commentuserId: String? = null
    var commentcomment: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
        //...
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        user = FirebaseAuth.getInstance()
        fcmPush = FcmPush()
        commentview = inflater.inflate(R.layout.custom_dialog_comments, container)
        Log.d(TAG, "onCreateView: 코멘트0")
        contentUid = requireArguments().getString("contentUid")
        Log.d(contentUid, "onCreateView: 코멘트 로그 contentUid 받기")
        destinationUid = requireArguments().getString("destinationUid")




        commentuid = requireArguments().getString("commentuid")
        commentuserId = requireArguments().getString("commentuserId")
        commentcomment = requireArguments().getString("commentcomment")


        Log.d(TAG, "onCreateView: 코멘트2")
        //어뎁터에서 파라미터 비긴트랜지션
        val commentTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        val CommentRv = commentview!!.findViewById<RecyclerView>(R.id.comment_recyclerview)
        Log.d(TAG, "onCreateView: 코멘트3")
        CommentRv.adapter =
            CommentRecyclerViewAdapter(contentUid, commentTransaction, childFragmentManager)
        Log.d(TAG, "onCreateView: 코멘트4")
        CommentRv.layoutManager = LinearLayoutManager(context)
        Log.d(TAG, "onCreateView: 코멘트5")


        return commentview

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        comment_btn_send?.setOnClickListener {
            Log.d(TAG, "onCreateView: 코멘트01")
            var comment = ContentDTO.Comment()
            Log.d(TAG, "onCreateView: 코멘트12")
            comment.userId = FirebaseAuth.getInstance().currentUser?.email
            Log.d(TAG, "onCreateView: 코멘트23")
            comment.comment = comment_edit_message.text.toString()
            Log.d(TAG, "onCreateView: 코멘트34")
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
            Log.d(TAG, "onCreateView: 코멘트45")
            comment.timestamp = System.currentTimeMillis()
            Log.d(TAG, "onCreateView: 코멘트56")


            FirebaseFirestore.getInstance().collection("images").document(contentUid!!)
                .collection("comments").document().set(comment)
            Log.d(TAG, "onCreateView: 코멘트 111")

            commentAlarm(destinationUid!!, comment_edit_message.text.toString())
            comment_edit_message.setText("")

        }


    }

//    fun gethomeposttitle() {
//        var Comment : ArrayList<ContentDTO.Comment>
//        = arrayListOf()
//
//          firestore?.collection("images")?.whereEqualTo("comments", Comment)
//            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                Comment.clear()
//                if (querySnapshot == null)
//                    return@addSnapshotListener
//                for (snapshot in querySnapshot!!.documents) {
//                }
//            }
//    }

    fun commentAlarm(destinationUid: String, message: String) {

        var contentDTO = ContentDTO()
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)
        alarmDTO.uid = user?.currentUser?.uid
        alarmDTO.kind = 1
        alarmDTO.message = message
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message =
            contentDTO.name + getString(R.string.alarm_who) + message + "댓글을 남기셨습니다."
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }


//    inner class CommentRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()
//
//
//        init {
//
//
//            FirebaseFirestore.getInstance().collection("images").document(contentUid!!)
//                .collection("comments").orderBy("timestamp")
//                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    comments.clear()
//
//                    if (querySnapshot == null)
//                        return@addSnapshotListener
//
//                    for (snapshot in querySnapshot.documents!!) {
//                        comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
//                    }
//
//                    notifyDataSetChanged()
//
//                }
//        }
//
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            var view =
//                LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
//            return CustomViewHolder(view)
//        }
//
//        private inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
//
//        override fun getItemCount(): Int {
//            return comments.size
//        }
//
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//
//            var view = holder.itemView
//            view.commentviewitem_textview_comment.text = comments[position].comment
//            view.commentviewitem_textview_profile.text = comments[position].userId
//
//            FirebaseFirestore.getInstance().collection("profileImages")
//                .document(comments[position].uid!!).get().addOnCompleteListener { task ->
//
//                    if (task.isSuccessful) {
//                        var url = task.result!!["image"]
//                        Glide.with(holder.itemView.context).load(url)
//                            .apply(RequestOptions().circleCrop())
//                            .into(view.commentviewitem_imageview_profile)
//                    }
//                }
//        }
//
//
//    }
//

}