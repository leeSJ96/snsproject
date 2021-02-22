package com.sjkorea.meetagain.homeFragment


import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.*
import com.sjkorea.meetagain.Adapter.IHomeRecyclerview
import com.sjkorea.meetagain.Adapter.HomeViewRecyclerViewAdapter
import com.sjkorea.meetagain.Adapter.OnpostListener
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.Constants.ORDER
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import com.squareup.okhttp.OkHttpClient


class HomeFragment : Fragment(),IHomeRecyclerview,OnpostListener {
    var firestore: FirebaseFirestore? = null
    var fcmPush: FcmPush? = null
    var imagesSnapshot: ListenerRegistration? = null
    var manager = LinearLayoutManager(activity)
    var okHttpClient: OkHttpClient? = null
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
    var user: FirebaseAuth? = null
    var uid: String? = null

    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var fragmentHomeBinding : FragmentHomeBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //뷰바인딩 가져오기
        // 홈 프레그먼트 -> 프래그먼트 홈 바인딩
        val binding  = FragmentHomeBinding.inflate(inflater, container, false)
        fragmentHomeBinding = binding
        firestore = FirebaseFirestore.getInstance()
        okHttpClient = OkHttpClient()
        fcmPush = FcmPush()


        return fragmentHomeBinding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // .setOnClickListener {
        //     customDialog.show(childFragmentManager, "")
        //
        fragmentHomeBinding?.alignmentBtn?.setOnClickListener {
            postDialogWindow()
        }


    }

    override fun onResume() {
        super.onResume()
        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()


        val adatper = HomeViewRecyclerViewAdapter(
            this,
            childFragmentManager,
            this,
            contentDTOs,
            comments,
            firestore,
            fcmPush
        ) //RecyclerView에 설정할 adapter

        fragmentHomeBinding?.homefragmentRecyclerview?.adapter = adatper
        fragmentHomeBinding?.homefragmentRecyclerview?.layoutManager = manager
        manager.reverseLayout = true
        manager.stackFromEnd = true

        if (!adatper.hasObservers()) {
            adatper.setHasStableIds(true)
        }
        adatper.notifyDataSetChanged()



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

//        //프로필 데이터 커스텀 바텀 다이로그로 보내기
//        val bundle = Bundle()
//
//        bundle.putString("destinationUid", contentDTOs[position].uid)
//        bundle.putString("userId", contentDTOs[position].userId)
//
//
//        val userFragment = UserFragment()
//        val customDialog: DialogFragment = CustomBottomDialog()
//        val historyF = HistoryFragment()
//
//
//        userFragment.arguments = bundle
//        customDialog.arguments = bundle
//        historyF.arguments = bundle
//        customDialog.show(requireActivity().supportFragmentManager, "")
//
//
//        val bundlet = Bundle()
//        bundlet.putString("title",contentDTOs[position].uid)
//        Log.d(contentDTOs[position].uid.toString(), "로그 홈 타이틀 ")
//        historyF.arguments = bundle

//
//                val fragment = CustomBottomDialog()
//                val bundle = Bundle()
//                bundle.putString("destinationUid", contentDTOs[position].uid)
//                fragment.arguments = bundle
//                customDialog.show(childFragmentManager, "")
//
//                val bundle = Bundle()
//                    for (entry in data.entries) bundle.putString(entry.key, entry.value)
//

//            //프로필 데이터 커스텀 바텀 다이로그로 보내기
//            val bundle = Bundle()
//
//            bundle.putString("destinationUid", contentDTOs[position].uid)
//            bundle.putString("userId", contentDTOs[position].userId)
//                Log.d(contentDTOs[position].uid.toString(), "홈 uid ")
//
//
//            val userFragment = UserFragment()
//            val customDialog: DialogFragment = CustomBottomDialog()
//            val historyF = HistoryFragment()
//
//
//            userFragment.arguments = bundle
//            customDialog.arguments = bundle
//            historyF.arguments = bundle
//            customDialog.show(requireActivity().supportFragmentManager, "")


    }
    //게시글 관리 파업창
    override fun onDelete() {
        Log.d(TAG, "삭제")

    }

    override fun onModify() {
        Log.d(TAG, "수정")
    }


    // 게시글다이얼로그 정렬 파업창
    fun postDialogWindow(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custome_dialog_post_alignment)
        dialog.show()
        val dateOrder = dialog.findViewById(R.id.dateOrder_btn) as Button
        dateOrder.setOnClickListener {
            ORDER = 0
            Log.d(Constants.TAG, "ORDER $ORDER ")
            SharedPreferenceFactory.putStrValue("ORDER", "0")
            dialog.dismiss()
             }

        val popularOrder = dialog.findViewById(R.id.popularOrder_btn) as Button
        popularOrder.setOnClickListener {
            ORDER = 1
            Log.d(Constants.TAG, "ORDER $ORDER ")
            SharedPreferenceFactory.putStrValue("ORDER", "1")
            dialog.dismiss()
             }

        val sadOrder = dialog.findViewById(R.id.sadOrder_btn) as Button
        sadOrder.setOnClickListener {
            ORDER = 2
            Log.d(Constants.TAG, "ORDER $ORDER ")
            SharedPreferenceFactory.putStrValue("ORDER", "2")
            dialog.dismiss()

        }
    }




    }













