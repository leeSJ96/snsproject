package com.sjkorea.meetagain.UserFragment


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.databinding.ViewpagerPostItemBinding

import kotlinx.android.synthetic.main.item_post.view.*
import kotlinx.android.synthetic.main.viewpager_history_item.view.*
import kotlinx.android.synthetic.main.viewpager_post_item.*
import kotlinx.android.synthetic.main.viewpager_post_item.view.*


//뷰페이저 3번째
class PostFragment : Fragment() {
    var firestore: FirebaseFirestore? = null
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null

    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var viewpagerPostItemBinding : ViewpagerPostItemBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val binding  = ViewpagerPostItemBinding.inflate(inflater, container,false)
        viewpagerPostItemBinding = binding

        Log.d(TAG, "로그1 ")
        firestore = FirebaseFirestore.getInstance()
        uid = arguments?.getString("destinationUid")
        auth = FirebaseAuth.getInstance()
        Log.d(TAG, "로그2 ")
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        viewpagerPostItemBinding?.postfragmentRv?.adapter = PostViewRecyclerViewAdapter()
        Log.d(TAG, "로그3 ")
        viewpagerPostItemBinding?.postfragmentRv?.layoutManager = LinearLayoutManager(activity)
        Log.d(TAG, "로그4 ")
        return viewpagerPostItemBinding!!.root
    }


    override fun onDestroyView() {
        viewpagerPostItemBinding = null
        super.onDestroyView()
    }

    inner class PostViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTO: ArrayList<ContentDTO>
        var contentUidList: ArrayList<String>

        init {

            //아이디
            contentDTO = java.util.ArrayList()
            contentUidList = java.util.ArrayList()
            firestore?.collection("images")?.orderBy("timestamp")
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    Log.d(TAG, "음5")
                    contentDTO.clear()
                    Log.d(TAG, "음4")
                    contentUidList.clear()
                    Log.d(TAG, "음3")
                    if (querySnapshot == null)
                        return@addSnapshotListener
                    Log.d(TAG, "음2")
                    for (snapshot in querySnapshot!!.documents) {
                        Log.d(TAG, "음1")
                        var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTO.add(item!!)
                        contentUidList.add(snapshot.id)
                    }



                    notifyDataSetChanged()
                }
        }



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTO.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewholder = (holder as CustomViewHolder).itemView


            //제목
            viewholder.postviewitem_profile_textview.text = contentDTO[position].title

            // Image
            Glide.with(holder.itemView.context).load(contentDTO[position].imageUrl)
                .into(viewholder.postviewitem_imageview_content)

            // Explain
            viewholder.postviewitem_explain_textview.text = contentDTO[position].explain

            // likes
            viewholder.postviewitem_favoritecounter_textview.text =
                "좋아요" + contentDTO!![position].favoriteCount + "개"
            // m
            viewholder.postviewitem_meaningcounter_textview.text =
                "싫어요" + contentDTO!![position].favoriteCount + "개"




                }
            }



}