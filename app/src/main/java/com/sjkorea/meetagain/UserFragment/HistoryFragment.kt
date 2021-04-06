package com.sjkorea.meetagain.UserFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.Adapter.HistoryRecyclerviewAdapter
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.Adapter.IOnpostListener
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.ViewpagerHistoryItemBinding
import kotlin.collections.ArrayList


class HistoryFragment : Fragment(), IHomeRecyclerview, IOnpostListener {

    // 내가 선택한 uid
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    //현재 나의 uid
    var currentUserUid: String? = null
    var fcmPush: FcmPush? = null
    var manager = LinearLayoutManager(activity)
    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var viewpagerHistoryItemBinding : ViewpagerHistoryItemBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = ViewpagerHistoryItemBinding.inflate(inflater, container,false)
        viewpagerHistoryItemBinding = binding
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserUid   = FirebaseAuth.getInstance().currentUser?.uid



        return viewpagerHistoryItemBinding!!.root
    }

    override fun onDestroyView() {
        viewpagerHistoryItemBinding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        getData()

    }

    //파이어베이스 리사이클러뷰 히스토리 대이터
    private fun getData() {
        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

        val adatper = HistoryRecyclerviewAdapter(
            this,
            childFragmentManager,
            this,
            contentDTOs,
            comments,
            firestore,
            fcmPush
        ) //RecyclerView에 설정할 adapter

        viewpagerHistoryItemBinding?.viewrv?.adapter = adatper
         viewpagerHistoryItemBinding?.viewrv?.layoutManager = manager
        manager.reverseLayout = true
        manager.stackFromEnd = true

        if (!adatper.hasObservers()) {
            adatper.setHasStableIds(true)
        }
        adatper.notifyDataSetChanged()

    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onDelete() {
        TODO("Not yet implemented")
    }

    override fun onModify() {
        TODO("Not yet implemented")
    }


}