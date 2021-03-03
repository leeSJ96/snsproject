package com.sjkorea.meetagain.Comment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.Adapter.CommentRecyclerViewAdapter
import com.sjkorea.meetagain.Adapter.IDeletePosition
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.UID
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.activity_home_post.*
import kotlinx.android.synthetic.main.custom_dialog_comments.*
import kotlinx.android.synthetic.main.custom_dialog_comments.view.*
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentFragment : BottomSheetDialogFragment(), IDeletePosition {

    var firestore: FirebaseFirestore? = null
    var commentview: View? = null
    var contentUid: String? = null
    var destinationUid: String? = null
    var user: FirebaseAuth? = null
    var fcmPush: FcmPush? = null
    var commentuid: String? = null
    var commentuserId: String? = null
    var commentcomment: String? = null
    var postpath :String? = null
    var uid : String? = null

    private var contentDTO: ContentDTO? = ContentDTO()
    private var deleteDialog: DeleteDialog? = null
    private var commentTime: Long = 0
    var myuid = FirebaseAuth.getInstance().currentUser!!.uid

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


        postpath = requireArguments().getString("contentDTO")
        commentuid = requireArguments().getString("commentuid")
        commentuserId = requireArguments().getString("commentuserId")
        commentcomment = requireArguments().getString("commentcomment")


        return commentview

    }


    override fun onResume() {
        super.onResume()

        inputText()
        moreSpinnerChange()
        commentview?.let { getData(it) }


    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    private fun inputText(){
        val uid = SharedPreferenceFactory.getStrValue("userToken", "")
        val timeStamp = System.currentTimeMillis()
        val myPath = "${uid}_${timeStamp}"



        comment_btn_send?.setOnClickListener {

            if (System.currentTimeMillis() >= commentTime + 5000){
                commentTime = System.currentTimeMillis()

            Log.d(TAG, "onCreateView: 코멘트01")
            var comment = ContentDTO.Comment()
            Log.d(TAG, "onCreateView: 코멘트12")
            val name = SharedPreferenceFactory.getStrValue("userName", null)
            comment.userId = FirebaseAuth.getInstance().currentUser!!.email
            Log.d(TAG, "onCreateView: 코멘트23")
            comment.comment = comment_edit_message.text.toString()
            Log.d(TAG, "onCreateView: 코멘트34")
            comment.uid = FirebaseAuth.getInstance().currentUser!!.uid
                comment.name = name
            Log.d(TAG, "onCreateView: 코멘트45")
            comment.timestamp = System.currentTimeMillis()
            Log.d(TAG, "onCreateView: 코멘트56")
            comment.myPath = myPath


            FirebaseFirestore.getInstance().collection("images").document(postpath.toString())
                .collection("comments").document(myPath).set(comment)
            Log.d(TAG, "onCreateView: 코멘트 111")

            commentAlarm(destinationUid!!, comment_edit_message.text.toString())
            comment_edit_message.setText("")

            }else{
                Toast.makeText(context, "5초뒤에 다시 보내주세요", Toast.LENGTH_SHORT).show()
            }
        }



    }

    //친구-본인 상황별웬절
    fun moreSpinnerChange(){



    }

    private fun getData(view: View) {

        val commentArray = ArrayList<ContentDTO.Comment>()



        //본인 유아이디 가져오기 확인
        FirebaseFirestore.getInstance().collection("images").document(postpath.toString())
            .collection("comments").whereEqualTo("uid",myuid)?.get()

            ?.addOnCompleteListener {
                if(it.isSuccessful){
                    for(dc in it.result!!.documents){
                        var contentDTO =dc.toObject(ContentDTO.Comment::class.java)

                        SharedPreferenceFactory.putStrValue("commentUid", contentDTO?.uid)
                        Log.d(Constants.TAG, "contentDTO uid: ${contentDTO?.uid} ")
                    }
                }
            }



        FirebaseFirestore.getInstance().collection("images").document(postpath.toString())
            .collection("comments").orderBy("timestamp")
            .addSnapshotListener { querySnapShot, firebaseFirestoreException ->
            if (querySnapShot == null) return@addSnapshotListener
            commentArray.clear()

            for (snapshot in querySnapShot.documents) {
                commentArray.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
            }
            Log.d("로그", "calendar size ${commentArray.size}")
            if (commentArray.size > 0) {
                //코드 안정성
                if(isAdded) {
                    view.comment_recyclerview.adapter =
                        CommentRecyclerViewAdapter(commentArray, childFragmentManager, this)
                    view.comment_recyclerview.layoutManager = LinearLayoutManager(App.instance)
                }
            } else {

            }
        }

        view.comment_recyclerview.adapter?.notifyDataSetChanged()

    }

    fun commentAlarm(destinationUid: String, message: String) {

        var contentDTO = ContentDTO()
        var alarmDTO = AlarmDTO()
        alarmDTO.destinationUid = destinationUid
        alarmDTO.name = SharedPreferenceFactory.getStrValue("userName", null)
        alarmDTO.uid = FirebaseAuth.getInstance().currentUser!!.uid
        alarmDTO.userId = FirebaseAuth.getInstance().currentUser!!.email
        alarmDTO.kind = 1
        alarmDTO.message = message
        alarmDTO.timestamp = System.currentTimeMillis()

        FirebaseFirestore.getInstance().collection("alarms").document().set(alarmDTO)

        var message =
            contentDTO.name + getString(R.string.alarm_who) + message + "댓글을 남기셨습니다."
        fcmPush?.sendMessage(destinationUid, "알림 메시지 입니다", message)
    }

    override fun onClickListener(position: Int, comment: ContentDTO.Comment) {
        commentDelete(comment)
    }


    fun commentDelete(comment: ContentDTO.Comment){
        var postpath = postpath
        var path = comment.myPath

        Log.d(Constants.TAG, "path: ${path} ")

        if (deleteDialog == null) {
            deleteDialog = activity?.let { DeleteDialog(it) }
        }

        deleteDialog?.show()
        if (path != null) {
            deleteDialog?.commentDeleteSetting(path, postpath!!) { result ->

                if (result) {
                    deleteDialog!!.dismiss()
                }

            }
        }
    }


}