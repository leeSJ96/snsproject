package com.sjkorea.meetagain.Adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.CustomBottomDialog
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.item_comment.view.*


class CommentRecyclerViewAdapter(
    private val commentArray: ArrayList<ContentDTO.Comment>,
    fragmentManager: FragmentManager,
    clickInterface: IDeletePosition,
) : RecyclerView.Adapter<CommentRecyclerViewAdapter.CustomViewHolder>() {

    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()
    private var mFragmentManager: FragmentManager
    private var iClickInterface : IDeletePosition? = null

    init {
        mFragmentManager = fragmentManager
        this.iClickInterface = clickInterface

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)

        return CustomViewHolder(view, this.iClickInterface!!)
    }
    override fun getItemCount(): Int {
        return commentArray.size
    }

    override fun onBindViewHolder(holder: CustomViewHolder , position: Int) {

        holder.bind(commentArray[position],mFragmentManager)



    }

    inner class CustomViewHolder(view: View, clickInterface : IDeletePosition) : RecyclerView.ViewHolder(view) {

        private val dateImg = itemView.commentviewitem_imageview_profile
        private val dateNameTv = itemView.commentviewitem_textview_profile
        private val dateTv = itemView.commentviewitem_textview_comment
        private val dateDete = itemView.comment_time
        private val dateProfile = itemView.commentviewitem_imageview_profile
        private val dateMore = itemView.comment_more_spinner
        private var clickInterface = clickInterface

        fun bind(dateList: ContentDTO.Comment, fragmentManager: FragmentManager ) {

            FirebaseFirestore.getInstance().collection("profileImages")
                .document(dateList.uid!!).get().addOnCompleteListener { task ->

                    if (task.isSuccessful) {

                        var url = task.result!!["image"]

                        if (url != null) {
                            Glide.with(itemView.context).load(url)
                                .placeholder(R.drawable.icon_noimage1)
                                .error(R.drawable.icon_noimage1)
                                .apply(RequestOptions().circleCrop())
                                .into(dateImg)
                        }
                    }
                }


//            dateNameTv.text = dateList.name

            FirebaseFirestore.getInstance().collection("profileName")?.document(dateList.uid.toString())
                ?.get()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val nameValue = task.result!!["name"]
                        Log.d(Constants.TAG, "commentNameValue: $nameValue")
                        dateNameTv.text  = nameValue.toString()

                    }
                }


            dateTv.text = dateList.comment

            // 시간
            val curTime = System.currentTimeMillis()
            var diffTime = (curTime - dateList.timestamp!!) / 1000
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


            dateDete.text = diffTime.toString() + msg.toString()

            //프로필창
            dateProfile.setOnClickListener {

                Constants.POSTSHOW = "homePostView"
                val bundle = Bundle()

                val bottomSheetDialogFragment = CustomBottomDialog()

                bundle.putString("destinationUid", dateList.uid)
                bundle.putString("userId", dateList.name)

                bottomSheetDialogFragment.arguments = bundle

                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)


            }



            // 댓글 목록스피너
            dateMore.setOnClickListener {
                Constants.LIST = 1
                Log.d(Constants.TAG, "Constants.LIST: ${Constants.LIST}")
                this.clickInterface.onClickListener(adapterPosition, dateList)
            }

            var uid = SharedPreferenceFactory.getStrValue("commentUid", "")
            Log.d(Constants.TAG, "comment adapter uid : $uid ")


            if (dateList.uid == uid)

                Constants.COMMENTMORESPINNER = "my"

            else{
                Constants.COMMENTMORESPINNER = "friend"

            }
            Log.d(Constants.TAG, "COMMENTMORESPINNER: ${Constants.COMMENTMORESPINNER} ")
            when(Constants.COMMENTMORESPINNER){

                "my"->{
                    dateMore.visibility = View.VISIBLE
                }

                "friend"->{
                    dateMore.visibility = View.INVISIBLE
                }

            }

        }


    }



}
