package com.sjkorea.meetagain

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.Adapter.HomeRecyclerviewInterface
import com.sjkorea.meetagain.databinding.HistorysubItemBinding
import com.sjkorea.meetagain.homeFragment.HomePostActivity
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.historysub_item.*
import kotlinx.android.synthetic.main.historysub_item.view.*
import kotlinx.android.synthetic.main.viewpager_history_item.view.*


class HistoryFragmentSub : Fragment(), HomeRecyclerviewInterface  {


    // 내가 선택한 uid
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null

    //현재 나의 uid
    var currentUserUid: String? = null
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentDTO: ArrayList<ContentDTO> = arrayListOf()
    var content: String? = null
    var path : String? = null


    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var historysubItemBinding: HistorysubItemBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HistorysubItemBinding.inflate(inflater, container, false)
        historysubItemBinding = binding

//        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        auth = FirebaseAuth.getInstance()
//        uid = requireArguments().getString("title")
        content = arguments?.getString("userIdposition")
        Log.d(content.toString(), "history sub1 로그 ")
        currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        uid = arguments?.getString("destinationUid")
        Log.d(uid.toString(), "로그 히스토리서브 받기 ")
        path = requireArguments().getString("pathData")
//
//        Log.d(FirebaseAuth.getInstance().currentUser?.uid.toString(), "로그히스토리받기 ")
        firestore = FirebaseFirestore.getInstance()

        Log.d(this.uid, "d ")
        Log.d(TAG, "로그  히스토리서브 버튼: ")
        historysubItemBinding?.viewrvsub?.adapter = HistorySubRecyclerviewAdapter(this)
        Log.d(TAG, "로그  히스토리서브 어뎁터")
        historysubItemBinding?.viewrvsub?.layoutManager = GridLayoutManager(activity, 3)
        Log.d(TAG, "로그  히스토리서브 레이아웃매니저: ")


        return historysubItemBinding!!.root
    }







    //리사이클러뷰
    inner class HistorySubRecyclerviewAdapter(myRecyclerviewInterface: HomeRecyclerviewInterface) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        //        var contentDTO: ArrayList<ContentDTO>
        var commentDTO: ArrayList<ContentDTO.Comment>

        val rvTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        private var myRecyclerviewInterface: HomeRecyclerviewInterface? = null

        init {
            this.myRecyclerviewInterface = myRecyclerviewInterface

            contentUidList = java.util.ArrayList()
            contentDTO = ArrayList()
            commentDTO = ArrayList()

            firestore?.collection("images")?.whereEqualTo("uid", uid)
                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                    contentDTO.clear()
                    if (querySnapshot == null)
                        return@addSnapshotListener
                    for (snapshot in querySnapshot.documents) {
                        Log.d(TAG, "히스토리서브 스냅샷: ")
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
            return CustomViewHolder(imageView, this.myRecyclerviewInterface!!)
        }

        inner class CustomViewHolder(
            var imageView: ImageView,
            recyclerviewInterface: HomeRecyclerviewInterface
        ) :
            RecyclerView.ViewHolder(imageView), View.OnClickListener {
            //인터페이스
            private lateinit var myRecyclerviewInterface: HomeRecyclerviewInterface


            init {
                itemView.setOnClickListener(this)
                this.myRecyclerviewInterface = recyclerviewInterface


            }

            fun bind() {
                Glide.with(itemView.context).load(contentDTO[position].imageUrl).apply(
                    RequestOptions().centerCrop()
                ).into(imageView)

//                imageView.setOnClickListener {
//
//                    val bundle = Bundle()
//                    val homePostfrg = HomePostFragment()
//                    //uid
//                    bundle.putString("destinationUid", contentDTO[position].uid)
//
//                    //userid
//                    bundle.putString("userId", contentDTO[position].userId)
//
//                    //title
//                    bundle.putString("title", contentDTO[position].title)
//
//                    //explain
//                    bundle.putString("explain", contentDTO[position].explain)
//
//                    //imageUrl
//                    bundle.putString("imageUrl", contentDTO[position].imageUrl)
//
//                    //favoriteCount
//                    bundle.putInt("favoriteCount", contentDTO[position].favoriteCount)
////
////                //userIdposition
////                bundle.putString("userIdposition",content)
//
//                    //userIdposition
//                    bundle.putString("userIdposition", content)
//
//                    //meaningCount
//                    bundle.putInt("meaningCount", contentDTO[position].meaningCount)
//
//                    //좋아요버튼
//                    var hashmap = contentDTO[position].favorites
//
//                    bundle.putSerializable("favoriteshashmap", hashmap)
//                    //싫어요버튼
//                    var hashmap2 = contentDTO[position].meaning
//
//                    bundle.putSerializable("meaninghashmap", hashmap2)
//
//                    bundle.putSerializable("hashmap", contentDTO[position].favorites)
//
//                    contentDTO[position].favorites =
//                        bundle.getSerializable("hashmap") as HashMap<String, Boolean>
//
//
//                    //댓글부분
////
//                    bundle.putString("commentuid", content)
//
//
////                bundle.putString("commentuserId", commentDTO[position].userId)
////               bundle.putString("commentcomment", commentDTO[position].comment)
//
//                    homePostfrg.arguments = bundle
//                    rvTransaction.replace(R.id.main_content, homePostfrg).commit()
//
//                }


            }

            override fun onClick(v: View?) {
                this.myRecyclerviewInterface.onItemClicked(adapterPosition)
            }
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var imageView = holder as CustomViewHolder
            holder.bind()


        }


        override fun getItemCount(): Int {
            return contentDTO.size
        }



    }


//
//    fun onBackPressed() {
//
//        var subfrag = HistoryFragmentSub()
//        if (subfrag != null) { //상세정보창 프래그먼트를 킨 상태면 뒤로가기했을 때 해당 프래그먼트를 삭제해줌
//            if (requireActivity().supportFragmentManager.findFragmentById(R.id.main_view) != null) {
//                requireActivity().supportFragmentManager.findFragmentById(R.id.main_view)?.let {
//                    requireActivity().supportFragmentManager
//                        .beginTransaction().remove(it).commit()
//                };
//            }
//        }
//    }

    override fun onItemClicked(position: Int) {
        Log.d(TAG, "HistoryFragmentSub  - onItemClicked(position: Int) called")
        val rvTransaction: FragmentTransaction =
            activity?.supportFragmentManager!!.beginTransaction()
        val titles: String = this.contentDTO[position].title ?: ""
        Toast.makeText(context, "$titles,ddd", Toast.LENGTH_LONG).show()


        val intent = Intent(context, HomePostActivity::class.java)

        intent.putExtra("contentDTO",contentDTO[position])

        intent.putExtra("pathData",contentDTO[position].pathData)
        //uid
        intent.putExtra("destinationUid", contentDTO[position].uid)

        //userid
        intent.putExtra("userId", contentDTO[position].name)

        //title
        intent.putExtra("title", contentDTO[position].title)

        //explain
        intent.putExtra("explain", contentDTO[position].explain)

        //imageUrl
        intent.putExtra("imageUrl", contentDTO[position].imageUrl)

        //favoriteCount
        intent.putExtra("favoriteCount", contentDTO[position].favoriteCount)

        //userIdposition
        intent.putExtra("userIdposition", content)

        //meaningCount
        intent.putExtra("meaningCount", contentDTO[position].meaningCount)

        //좋아요버튼
        var hashmap = contentDTO[position].favorites

        intent.putExtra("favoriteshashmap", hashmap)
        //싫어요버튼
        var hashmap2 = contentDTO[position].meaning

        intent.putExtra("meaninghashmap", hashmap2)

        intent.putExtra("hashmap", contentDTO[position].favorites)

//        contentDTO[position].favorites =
//            intent.putStringArrayListExtra("hashmap") as HashMap<String, Boolean>

        //댓글부분
        intent.putExtra("commentuid", content)
        startActivity(intent)


    }



}