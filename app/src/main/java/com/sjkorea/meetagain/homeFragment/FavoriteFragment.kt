package com.sjkorea.meetagain.homeFragment


import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.FavoriteViewRecyclerViewAdapter
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.Adapter.IOnpostListener
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.utils.Constants
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


class FavoriteFragment : Fragment(), IHomeRecyclerview, IOnpostListener,SwipeRefreshLayout.OnRefreshListener {
    var firestore: FirebaseFirestore? = null
    var fcmPush: FcmPush? = null
    var imagesSnapshot: ListenerRegistration? = null
    var okHttpClient: OkHttpClient? = null
    var contentArray: ArrayList<ContentDTO> = arrayListOf()
    var contentUidList: ArrayList<String> = arrayListOf()
    var user: FirebaseAuth? = null
    var uid: String? = null
    var comments: ArrayList<ContentDTO.Comment> = arrayListOf()
    var tr: Boolean? = null


    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    // lateinit 을 통해 나중에 메모리에 올라가도 된다.
    private lateinit var adapter: FavoriteViewRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //뷰바인딩 가져오기
        // 홈 프레그먼트 -> 프래그먼트 홈 바인딩
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        fragmentHomeBinding = binding
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()


        adapter = FavoriteViewRecyclerViewAdapter(
            this,
            childFragmentManager,
            this,
            contentArray,
            comments,
            firestore,
            fcmPush, contentUidList
        )

        if (!adapter.hasObservers()) {
            adapter.setHasStableIds(true)
        }
        adapter.setHasStableIds(true)


        fragmentHomeBinding?.homefragmentRecyclerview?.adapter = adapter

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager = manager
        manager.reverseLayout = true
        manager.stackFromEnd = true

        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

        var recyclerViewState: Parcelable
        recyclerViewState =
            fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager?.onSaveInstanceState()!!


        if (recyclerViewState != null)
            fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager?.onRestoreInstanceState(
                recyclerViewState
            );



        return fragmentHomeBinding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getData()

    }

    override fun onResume() {
        super.onResume()


//
//        fragmentHomeBinding?.homefragmentRecyclerview?.adapter = adapter
//        fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager = manager
//        manager.reverseLayout = true
//        manager.stackFromEnd = true


//
//        if (!adapter.hasObservers()) {
//            adapter.setHasStableIds(true)
//        }
//
//        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW


    }


    override fun onStop() {
        super.onStop()
        imagesSnapshot?.remove()
    }

    override fun onDestroyView() {
        fragmentHomeBinding = null
        super.onDestroyView()
    }

    override fun onItemClicked(position: Int) {
        Log.d(TAG, "HomeFragment = onItemClicked() called ")


    }

    //게시글 관리 파업창
    override fun onDelete() {
        Log.d(TAG, "삭제")

    }

    override fun onModify() {
        Log.d(TAG, "수정")
    }

    fun datalist() {

//        SORTT = SharedPreferenceFactory.getStrValue("SORTT", null).toString()
//        Log.d(Constants.TAG, "SORTT1: $SORTT ")
//        when (SORTT) {
//            "0" -> timestampData()
//            "1" -> favoriteCountList()
//            "2" -> meanigCountList()
//        }
    }


    fun getData() {

        FirebaseFirestore.getInstance().collection("images")?.orderBy("favoriteCount").addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
            contentArray.clear()
            contentUidList.clear()
            if (querySnapshot == null) return@addSnapshotListener
            for (snapshot in querySnapshot.documents) {
                var item1 = snapshot.toObject(ContentDTO::class.java)


                contentArray.add(item1!!)
                contentUidList.add(snapshot.id)


            }
            fragmentHomeBinding?.homefragmentRecyclerview?.adapter?.notifyDataSetChanged()
            Log.d(Constants.TAG, "SORT:${ Constants.SORT.toString()}")

        }
    }

    //리프레시
    override fun onRefresh() {


        //비우고 데이터 호출
        this.contentArray.clear()
        this.adapter.clearList()

        FirebaseFirestore.getInstance().collection("images")?.orderBy("favoriteCount").addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
            contentArray.clear()
            contentUidList.clear()
            if (querySnapshot == null) return@addSnapshotListener
            for (snapshot in querySnapshot.documents) {
                var item1 = snapshot.toObject(ContentDTO::class.java)


                contentArray.add(item1!!)
                contentUidList.add(snapshot.id)

                fragmentHomeBinding?.swipeRefresh?.isRefreshing = false

            }
            fragmentHomeBinding?.homefragmentRecyclerview?.adapter?.notifyDataSetChanged()
            Log.d(Constants.TAG, "SORT:${ Constants.SORT.toString()}")

        }

//        fragmentHomeBinding?.alignmentBtn?.setOnClickListener {
//            postDialogWindow()
//
//        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

    }




}




