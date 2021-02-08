package com.sjkorea.meetagain.UserFragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.MainActivity
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.databinding.ViewpagerHistoryItemBinding
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.viewpager_history_item.view.*
import kotlin.math.log


class HistoryFragment : Fragment() {



    // 내가 선택한 uid
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    //현재 나의 uid
    var currentUserUid: String? = null

    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var viewpagerHistoryItemBinding : ViewpagerHistoryItemBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = ViewpagerHistoryItemBinding.inflate(inflater, container,false)
        viewpagerHistoryItemBinding = binding
//        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        auth = FirebaseAuth.getInstance()
//        uid = requireArguments().getString("title")

        currentUserUid   = FirebaseAuth.getInstance().currentUser?.uid
//        uid = arguments?.getString("destinationUid")
        Log.d(uid.toString(), "로그 히스토리 받기 ")
//
//        Log.d(FirebaseAuth.getInstance().currentUser?.uid.toString(), "로그히스토리받기 ")
        firestore = FirebaseFirestore.getInstance()

        Log.d(this.uid,"d ")

        Log.d(TAG, "로그  히스토리 화면: ")
        Log.d(TAG, "로그  히스토리 버튼: ")
        viewpagerHistoryItemBinding?.viewrv?.adapter = HistoryRecyclerviewAdapter()
        Log.d(TAG, "로그  히스토리 어뎁터")
        viewpagerHistoryItemBinding?.viewrv?.layoutManager = GridLayoutManager(activity, 3)
        Log.d(TAG, "로그  히스토리 레이아웃매니저: ")


        return viewpagerHistoryItemBinding!!.root
    }

    override fun onDestroyView() {
        viewpagerHistoryItemBinding = null
        super.onDestroyView()
    }


    inner class HistoryRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTO: ArrayList<ContentDTO>



        init {
            contentDTO = ArrayList()
            firestore?.collection("images")?.whereEqualTo("uid", currentUserUid)?.addSnapshotListener { querySnapshot, firebaseFirestoreException  ->

                contentDTO.clear()
                if (querySnapshot == null)
                    return@addSnapshotListener
                for (snapshot in querySnapshot.documents){
                    Log.d(TAG, "음 스냅샷: ")
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)



                }
                notifyDataSetChanged()

//                 val userFragment = UserFragment()
//                 val bundle = Bundle()
//                 bundle.putInt("one", contentDTO.size)
//                 Log.d(TAG,"size 보내기 ")
//                 userFragment.arguments = bundle


                notifyDataSetChanged()

            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context).load(contentDTO[position].imageUrl).apply(
                RequestOptions().centerCrop()
            ).into(imageView)



        }

        override fun getItemCount(): Int {
            return contentDTO.size
        }



    }




}