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
import com.sjkorea.meetagain.Adapter.HomeViewRecyclerViewAdapter
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.Adapter.IOnpostListener
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FcmPush
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.utils.Constants
import com.squareup.okhttp.OkHttpClient
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*


class HomeFragment : Fragment(), IHomeRecyclerview, IOnpostListener ,SwipeRefreshLayout.OnRefreshListener{
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
    var LIST_STATE_KEY = "list_state"

    private var recyclerViewState: Parcelable? = null


    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    // lateinit 을 통해 나중에 메모리에 올라가도 된다.
    private lateinit var adapter: HomeViewRecyclerViewAdapter

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
        retainInstance = true;
        //스와이프리프레시시
        fragmentHomeBinding?.swipeRefresh?.setOnRefreshListener(this)

        adapter = HomeViewRecyclerViewAdapter(
            this,
            childFragmentManager,
            this,
            contentArray,
            comments,
            firestore,
            fcmPush, contentUidList
        )


        fragmentHomeBinding?.homefragmentRecyclerview?.adapter = adapter

        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager = manager
        manager.reverseLayout = true
        manager.stackFromEnd = true


        if (!adapter.hasObservers()) {
            adapter.setHasStableIds(true)
        }

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


        timestampData()

    }




    override fun onResume() {
        super.onResume()






//
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

    private fun saveRecyclerViewState() {
        // LayoutManager를 불러와 Parcelable 변수에 리사이클러뷰 상태를 Bundle 형태로 저장한다
        recyclerViewState = homefragment_recyclerview.layoutManager!!.onSaveInstanceState()

    }

    private fun setSavedRecyclerViewState() {
        homefragment_recyclerview.layoutManager!!.onRestoreInstanceState(recyclerViewState)
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



    fun timestampData() {

        FirebaseFirestore.getInstance().collection("images")?.orderBy("timestamp").addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
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



        FirebaseFirestore.getInstance().collection("images")?.orderBy("timestamp").addSnapshotListener() { querySnapshot, firebaseFirestoreException ->
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


//// 게시글다이얼로그 정렬 파업창
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


//}
}




