package com.sjkorea.meetagain.FollowFragment

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.FavoriteViewRecyclerViewAdapter
import com.sjkorea.meetagain.Adapter.FollowAdapter
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.CustomZoomClass.CenterZoomLayout
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.*
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.FOLLOWDATA
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.fragment_follow.*


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
    private lateinit var adapter: FollowAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = FragmentFollowBinding.inflate(inflater, container,false)
        fragmentFollowBinding = binding

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        firestore = FirebaseFirestore.getInstance()


        adapter = FollowAdapter(this, childFragmentManager, this, contentDTOs, comments, firestore, fcmPush)
        fragmentFollowBinding?.tourRV?.adapter = adapter
        fragmentFollowBinding?.tourRV?.layoutManager = manager
        manager.reverseLayout = true
        manager.stackFromEnd = true


        if (!adapter.hasObservers()) {
            adapter.setHasStableIds(true)
        }
        adapter.notifyDataSetChanged()

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

        var recyclerViewState: Parcelable
        recyclerViewState =
            fragmentFollowBinding?.tourRV?.layoutManager?.onSaveInstanceState()!!


        if (recyclerViewState != null)
            fragmentFollowBinding?.tourRV?.layoutManager?.onRestoreInstanceState(
                recyclerViewState
            );
//        return searchview

        when(Constants.FOLLOWDATA){
            0->  IvVisibility()


            1->  rvVisibility()
        }

        return fragmentFollowBinding?.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)




    }

    override fun onResume() {
        super.onResume()






    }
    fun rvVisibility(){
        fragmentFollowBinding?.noData?.visibility = View.INVISIBLE
        fragmentFollowBinding?.tourRV?.visibility = View.VISIBLE
    }
    fun IvVisibility(){
        fragmentFollowBinding?.noData?.visibility = View.VISIBLE
        fragmentFollowBinding?.tourRV?.visibility = View.INVISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentFollowBinding = null
    }

    override fun onItemClicked(position: Int) {
        TODO("Not yet implemented")
    }


}