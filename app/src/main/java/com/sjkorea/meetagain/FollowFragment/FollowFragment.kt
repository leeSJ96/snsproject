package com.sjkorea.meetagain.FollowFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.FollowAdapter
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.CustomZoomClass.CenterZoomLayout
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.*
import com.squareup.okhttp.OkHttpClient


class FollowFragment : Fragment(), IHomeRecyclerview {

    var firestore: FirebaseFirestore? = null
    var fcmPush: FcmPush? = null
    var imagesSnapshot: ListenerRegistration? = null
    var manager = LinearLayoutManager(activity)
    var okHttpClient: OkHttpClient? = null
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    var user: FirebaseAuth? = null
    var uid: String? = null
    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

    private var fragmentFollowBinding : FragmentFollowBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = FragmentFollowBinding.inflate(inflater, container,false)
        fragmentFollowBinding = binding

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        firestore = FirebaseFirestore.getInstance()



//        return searchview
        return fragmentFollowBinding?.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




    }

    override fun onResume() {
        super.onResume()


        // 레이아웃 매니저 초기화
        val layoutManager = CenterZoomLayout(requireActivity())


        val adapter = FollowAdapter(this, childFragmentManager, this, contentDTOs, comments, firestore, fcmPush)
        fragmentFollowBinding?.tourRV?.adapter = adapter
        fragmentFollowBinding?.tourRV?.layoutManager = LinearLayoutManager(activity)


        fragmentFollowBinding?.tourRV?.isNestedScrollingEnabled = false
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true


        if (!adapter.hasObservers()) {
            adapter.setHasStableIds(true)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentFollowBinding = null
    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }


}