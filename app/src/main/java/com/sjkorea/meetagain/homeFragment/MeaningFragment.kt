package com.sjkorea.meetagain.homeFragment


import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.*
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.utils.Constants
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*
import kotlin.collections.ArrayList


class MeaningFragment : Fragment(), IHomeRecyclerview, IOnpostListener {
    var firestore: FirebaseFirestore? = null
    var fcmPush: FcmPush? = null
    var imagesSnapshot: ListenerRegistration? = null
    var manager = LinearLayoutManager(activity)
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
    private lateinit var adapter: MeaningViewRecyclerViewAdapter

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

        adapter = MeaningViewRecyclerViewAdapter(
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
//        fragmentHomeBinding?.alignmentBtn?.setOnClickListener {
//            postDialogWindow()
//
//        }

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

        // .setOnClickListener {
        //     customDialog.show(childFragmentManager, "")
        //
        getData()

    }

    override fun onResume() {
        super.onResume()






//        fragmentHomeBinding?.swipe?.setOnRefreshListener {
//
////            contentArray.clear()
////            this.homeAdapter.clearList()
////            contentArray.addAll(contentArray)
////            homeAdapter.submitList(contentArray)
////            homeAdapter.notifyDataSetChanged()
//
//            swipe.isRefreshing = false
//        }

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


    fun getData() {

        FirebaseFirestore.getInstance().collection("images")?.orderBy("meaningCount").addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
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


//    fun favoriteCountList() {
//        favoriteCount?.addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
//            contentArray.clear()
//            if (querySnapshot == null) return@addSnapshotListener
//            for (snapshot in querySnapshot.documents) {
//                var item1 = snapshot.toObject(com.sjkorea.meetagain.ContentDTO::class.java)
//
//                contentArray.add(item1!!)
//                contentUidList.add(snapshot.id)
//            }
//            fragmentHomeBinding?.homefragmentRecyclerview?.adapter?.notifyDataSetChanged()
//
//        }
//    }
//
//    fun meanigCountList() {
//        meaningCount?.addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
//            contentArray.clear()
//            if (querySnapshot == null) return@addSnapshotListener
//            for (snapshot in querySnapshot.documents) {
//                var item1 = snapshot.toObject(com.sjkorea.meetagain.ContentDTO::class.java)
//
//                contentArray.add(item1!!)
//                contentUidList.add(snapshot.id)
//            }
//            fragmentHomeBinding?.homefragmentRecyclerview?.adapter?.notifyDataSetChanged()
//
//
//        }
//    }


// 게시글다이얼로그 정렬 파업창
//fun postDialogWindow() {
//    val dialog = Dialog(requireContext())
//    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog.setContentView(R.layout.custome_dialog_post_alignment)
//    dialog.show()
//
//    val dateOrder = dialog.findViewById(R.id.dateOrder_btn) as Button
//    dateOrder.setOnClickListener {
//
//        SORTT = SharedPreferenceFactory.putStrValue("SORTT", "0").toString()
//        Constants.SORT =
//            FirebaseFirestore.getInstance().collection("images")?.orderBy("timestamp")
//
//        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
//        ft.detach(this).attach(this).commit()
//
//
//
//        dialog.dismiss()
//        Handler().postDelayed({
//            loading_progress.visibility = View.INVISIBLE
//        }, 2000)
//
//        loading_progress.visibility = View.VISIBLE
//    }
//    val popularOrder = dialog.findViewById(R.id.popularOrder_btn) as Button
//    popularOrder.setOnClickListener {
//
//        Constants.SORT =
//            FirebaseFirestore.getInstance().collection("images")?.orderBy("favoriteCount")
//        SORTT = SharedPreferenceFactory.putStrValue("SORTT", "1").toString()
//
//        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
//        ft.detach(this).attach(this).commit()
//
//
//        dialog.dismiss()
//        Handler().postDelayed({
//
//            loading_progress.visibility = View.INVISIBLE
//        }, 1000)
//
//        loading_progress.visibility = View.VISIBLE
//    }
//
//
//    val sadOrder = dialog.findViewById(R.id.sadOrder_btn) as Button
//    sadOrder.setOnClickListener {
//
//        Constants.SORT = FirebaseFirestore.getInstance().collection("images")?.orderBy("meaningCount")
//        SORTT = SharedPreferenceFactory.putStrValue("SORTT", "2").toString()
//        val ft: FragmentTransaction = requireFragmentManager().beginTransaction()
//        ft.detach(this).attach(this).commit()
//        dialog.dismiss()
//        Handler().postDelayed({
//
//            loading_progress.visibility = View.INVISIBLE
//
//        }, 1000)
//
//        loading_progress.visibility = View.VISIBLE
//    }
//
//
//}
}




