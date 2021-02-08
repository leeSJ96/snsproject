package com.sjkorea.meetagain.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.AlarmDTO
import com.sjkorea.meetagain.R
import kotlinx.android.synthetic.main.item_alarm.view.*
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_imageview_profile
import kotlinx.android.synthetic.main.item_comment.view.commentviewitem_textview_profile


class AlarmRecyclerViewAdapter(fragmentManager: FragmentManager) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var alarmSnapshot : ListenerRegistration? = null
    var alarmDTOList: ArrayList<AlarmDTO> = arrayListOf()
    private var mFragmentManager: FragmentManager

    init {



        mFragmentManager = fragmentManager

        var uid = FirebaseAuth.getInstance().currentUser!!.uid

        alarmSnapshot = FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid", uid)
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                alarmDTOList.clear()
                if (querySnapshot == null) return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java)!!)
                }

                alarmDTOList.sortByDescending { it.timestamp }
                notifyDataSetChanged()
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)

        return CustomViewHolder(view)
    }

    inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view!!){

        fun bind(alarmDTOList: ArrayList<AlarmDTO>, fragmentManager: FragmentManager) {
//            //프로필창
//            itemView.commentviewitem_imageview_profile.setOnClickListener {
//
//
//                val bundle = Bundle()
//
//                val homepostFragmnet = HomePostFragment()
//
//                bundle.putString("destinationUid", alarmDTOList[position].uid)
//                bundle.putString("userId", alarmDTOList[position].userId)
//
//
//                homepostFragmnet.arguments = bundle
//
//                homepostFragmnet.show(fragmentManager, homepostFragmnet.tag)
//            }
        }


    }

    override fun getItemCount(): Int {
        return alarmDTOList.size

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var Holder : RecyclerView.ViewHolder = holder as AlarmRecyclerViewAdapter.CustomViewHolder
        holder.bind(alarmDTOList,mFragmentManager)


        val commentTextView = holder.itemView.alarmviewitem_textview_profile
        val profileImage = holder.itemView.alarmviewitem_imageview_profile

        val view = holder.itemView


        // 시간
        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - alarmDTOList[position].timestamp!!) / 1000
        var msg: String? = null
        if (diffTime < HomeViewRecyclerViewAdapter.TimeValue.SEC.value)
            msg = "초 전"
        else {
            for (i in HomeViewRecyclerViewAdapter.TimeValue.values()) {
                diffTime /= i.value
                if (diffTime < i.maximum) {
                    msg = i.msg
                    break
                }
            }
        }


        view.alarm_time.text = diffTime.toString() + msg.toString()


        //상황별 알람
        FirebaseFirestore.getInstance().collection("profileImages")
            .document(alarmDTOList[position].uid.toString()).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val url = task.result!!["image"]
                    Glide.with(view.context).load(url).apply(RequestOptions().circleCrop())
                        .into(profileImage)
                }
            }

        when (alarmDTOList[position].kind) {

            0 -> {
                val str_0 = alarmDTOList[position].name + "님이 좋아요를 눌렀습니다."
                commentTextView.text = str_0
            }

            1 -> {
                val str_1 =
                    alarmDTOList[position].name + " " + "님이 댓글을 남겼습니다.\n\n" + "댓글내용 : " + alarmDTOList[position].message
                commentTextView.text = str_1
            }

            2 -> {
                val str_2 =
                    alarmDTOList[position].name + "님이 당신의 팬이 되었습니다."
                commentTextView.text = str_2
            }

            3 -> {
                val str_3 = alarmDTOList[position].name + "님이 싫어요를 눌렀습니다."
                commentTextView.text = str_3

            }
        }
//        .commentviewitem_textview_comment.visibility = View.INVISIBLE

    }

}