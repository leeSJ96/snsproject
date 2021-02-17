package com.sjkorea.meetagain.Adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.CustomBottomDialog
import com.sjkorea.meetagain.R
import kotlinx.android.synthetic.main.item_comment.view.*
import kotlinx.android.synthetic.main.item_main.view.*


class CommentRecyclerViewAdapter(
    path: String?,
    commentTransaction: FragmentTransaction,
    fragmentManager: FragmentManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()
    private var mFragmentManager: FragmentManager

    init {
        mFragmentManager = fragmentManager

        FirebaseFirestore.getInstance().collection("images").document(path.toString())
            .collection("comments").orderBy("timestamp")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                comments.clear()

                if (querySnapshot == null)
                    return@addSnapshotListener

                for (snapshot in querySnapshot.documents) {
                    comments.add(snapshot.toObject(ContentDTO.Comment::class.java)!!)
                }

                notifyDataSetChanged()

            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CustomViewHolder(view)
    }

     inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(comments: ArrayList<ContentDTO.Comment>, fragmentManager: FragmentManager ) {
            //프로필창
            itemView.commentviewitem_imageview_profile.setOnClickListener {


                val bundle = Bundle()

                val bottomSheetDialogFragment = CustomBottomDialog()

                bundle.putString("destinationUid", comments[position].uid)
                bundle.putString("userId", comments[position].userId)

                bottomSheetDialogFragment.arguments = bundle

                bottomSheetDialogFragment.show(fragmentManager, bottomSheetDialogFragment.tag)


            }


        }


    }


    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var Holder : RecyclerView.ViewHolder = holder as CommentRecyclerViewAdapter.CustomViewHolder
        holder.bind(comments,mFragmentManager)


        var view = holder.itemView

        view.commentviewitem_textview_comment.text = comments[position].comment
        view.commentviewitem_textview_profile.text = comments[position].userId

        // 시간
        val curTime = System.currentTimeMillis()
        var diffTime = (curTime - comments[position].timestamp!!) / 1000
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


        view.comment_time.text = diffTime.toString() + msg.toString()
//


        FirebaseFirestore.getInstance().collection("profileImages")
            .document(comments[position].uid!!).get().addOnCompleteListener { task ->

            if (task.isSuccessful) {

                var url = task.result!!["image"]
                Glide.with(holder.itemView.context).load(url).apply(RequestOptions().circleCrop())
                    .into(view.commentviewitem_imageview_profile)

            }
        }


    }


}
