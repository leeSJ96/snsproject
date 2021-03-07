package com.sjkorea.meetagain.UserFragment


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.CustomFragmentStateAdapter
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.FollowDTO
import com.sjkorea.meetagain.LikeFragment.LikeFragment
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.WalletFragment.WalletFragment
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import com.sjkorea.meetagain.databinding.FragmentUserBinding
import com.sjkorea.meetagain.intro.FirstVisitActivity
import com.sjkorea.meetagain.utils.Constants
import com.sjkorea.meetagain.utils.SharedPreferenceFactory
import kotlinx.android.synthetic.main.custom_dialog.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.viewpager_history_item.view.*

class UserFragment : Fragment() {


    var firestore: FirebaseFirestore? = null
    var PICK_PROFILE_FROM_ALBUM = 10
    var uid: String? = null
    var auth: FirebaseAuth? = null
    var currentUserUid: String? = null


    private var fragmentUserBinding : FragmentUserBinding? = null

    var followListenerRegistration: ListenerRegistration? = null
    var imageprofileListenerRegistration: ListenerRegistration? = null
    var getTitleListenerRegistration: ListenerRegistration? = null
    var viewpagerRegistration: ListenerRegistration? = null

    //    val contentDTO: ArrayList<ContentDTO> = arrayListOf()
    private val tabTextList = arrayListOf("HOME", "CHATTING", "NEWS", "SETTING")
    private val tabIconList = arrayListOf(
        R.drawable.ic_arrow_drop_down_black_24dp,
        R.drawable.ic_arrow_drop_down_black_24dp,
        R.drawable.ic_arrow_drop_down_black_24dp,
        R.drawable.ic_arrow_drop_down_black_24dp
    )


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = FragmentUserBinding.inflate(inflater, container,false)
        fragmentUserBinding = binding
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUserUid = auth?.currentUser?.uid


        if (arguments != null) {
//            contentDTO = requireArguments().getString("qwer")
           uid = requireArguments().getString("destinationUid")
            if (uid != null && uid == currentUserUid) {
//                //나의 유저페이지
//                userview?.account_btn_follow_signout?.text = getString(R.string.signout)
////                fragmentView?.account_btn_follow_signout?.setOnClickListener {
//                    activity?.finish()
//                    startActivity(Intent(activity, LoginActivity::class.java))
//                    auth?.signOut()
//
//                }
            } else {
//                //제 3자의 유저페이지
//                userview?.account_btn_follow_signout?.text = getString(R.string.follow)

//                var mainActivity = (activity as MainActivity)
//                mainActivity.toolbar_title_image.visibility = View.GONE
//                mainActivity.toolbar_btn_back.visibility = View.VISIBLE
//                mainActivity.toolbar_username.visibility = View.VISIBLE
//                mainActivity.toolbar_username.text = requireArguments().getString("userId")
//                mainActivity.toolbar_btn_back.setOnClickListener {
//                    mainActivity.bottom_navigation.selectedItemId = R.id.action_home
//                }
//                userview?.account_btn_follow_signout?.setOnClickListener {
//                    requestFollow()
//                }

            }
        }


        fragmentUserBinding?.accountIvProfile?.setOnClickListener {
            var photoPcikerIntent = Intent(Intent.ACTION_PICK)
            photoPcikerIntent.type = "image/*"
            activity?.startActivityForResult(photoPcikerIntent, PICK_PROFILE_FROM_ALBUM)
        }
        return fragmentUserBinding!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        init()
//

//        titlesize = arguments?.getInt("one",0)!!
//        Log.d(this.titlesize.toString(),"size 받기 ")
//
//        account_tv_post_count.text = titlesize.toString()
//        Log.d(titlesize.toString() , "size 결과 ")


//        account_btn_follow_signout.setOnClickListener {
//            requestFollow()
//        }


    }


    private fun init() {

        viewPager2.adapter = CustomFragmentStateAdapter(requireActivity())
        viewPager2.isSaveEnabled = false
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setIcon(tabIconList[position])
            tab.text = tabTextList[position]
        }.attach()

    }

    override fun onResume() {
        super.onResume()
        getProfileImages()
        getFolloerAndFollowing()
        gettitlecount()
        profileUserName()
        profileUserNamechange()
    }

    override fun onStop() {
        super.onStop()
        followListenerRegistration?.remove()
        getTitleListenerRegistration?.remove()
        imageprofileListenerRegistration?.remove()
    }

    override fun onDestroyView() {
        fragmentUserBinding = null
        super.onDestroyView()
    }



    fun gettitlecount() {
        var contentDTO: ArrayList<ContentDTO>
        contentDTO = ArrayList()

        getTitleListenerRegistration = firestore?.collection("images")?.whereEqualTo("uid", uid)
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTO.clear()
                if (querySnapshot == null)
                    return@addSnapshotListener
                for (snapshot in querySnapshot.documents) {
                    Log.d(TAG, "1")
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
                    Log.d(TAG, "2")

                    account_tv_post_count.text = contentDTO.size.toString()
                    Log.d(Constants.TAG, "size테스트${contentDTO.size.toString()}")
                }

            }

    }


    fun getProfileImages() {
        imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid.toString())
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    val url = documentSnapshot.data!!["image"]
                    Glide.with(requireActivity()).load(url).apply(RequestOptions().circleCrop())
                        .placeholder(R.drawable.icon_noimage1)
                        .error(R.drawable.icon_noimage1)
                        .into(account_iv_profile)

                }
            }
    }
    private fun profileUserName(){

        val userName = SharedPreferenceFactory.getStrValue("userName", null)
        fragmentUserBinding?.userNameTv?.text = userName

    }

    private fun profileUserNamechange() {
        fragmentUserBinding?.userNamechangeTv?.setOnClickListener {

        val intent = Intent(activity, FirstVisitActivity::class.java)
        startActivity(intent)
        }
    }





    private fun requestFollow() {

        // Save data to my account

        var tsDocFollowing = firestore!!.collection("users").document(currentUserUid.toString())
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollowing!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO.followingCount = 1
                followDTO.followings[uid!!] = true

                transaction.set(tsDocFollowing, followDTO)
                return@runTransaction

            }


            if (followDTO?.followings?.containsKey(uid)!!) {
                // It remove following third person when a third person follow me
                followDTO?.followingCount = followDTO?.followingCount - 1
                followDTO?.followings.remove(uid)
            } else {
                // It remove following third person when a third person not follow me
                followDTO?.followingCount = followDTO?.followingCount + 1
                followDTO?.followings[uid!!] = true
            }
            transaction.set(tsDocFollowing, followDTO)
            return@runTransaction
        }

        // Save data to third person
        var tsDocFollower = firestore!!.collection("users").document(uid.toString())
        firestore?.runTransaction { transaction ->

            var followDTO = transaction.get(tsDocFollower!!).toObject(FollowDTO::class.java)
            if (followDTO == null) {

                followDTO = FollowDTO()
                followDTO!!.followerCount = 1
                followDTO!!.followers[currentUserUid!!] = true

                transaction.set(tsDocFollower, followDTO!!)
                return@runTransaction
            }

            if (followDTO!!.followers.containsKey(currentUserUid!!)) {
                // It cancel my follower when I follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount - 1
                followDTO!!.followers.remove(currentUserUid!!)
            } else {
                // It cancel my follower when I don't follow a third person
                followDTO!!.followerCount = followDTO!!.followerCount + 1
                followDTO!!.followers[currentUserUid!!] = true

            }
            transaction.set(tsDocFollower, followDTO!!)
            return@runTransaction
        }

    }

    fun getFolloerAndFollowing(){
        followListenerRegistration = firestore?.collection("users")?.document(uid.toString())?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if (documentSnapshot==null) return@addSnapshotListener

            var followDTO = documentSnapshot.toObject(FollowDTO::class.java)

            if(followDTO?.followingCount != null){
                fragmentUserBinding?.accountTvFollowingCount?.text = followDTO?.followingCount?.toString()
            }
            if(followDTO?.followerCount != null){
                fragmentUserBinding?.accountTvFollowerCount?.text = followDTO?.followerCount?.toString()

//                if(followDTO?.followers?.containsKey(currentUserUid!!)) {
//                    userview?.account_btn_follow_signout?.text = getString(R.string.follow_cancel)
//                    userview?.account_btn_follow_signout?.background?.setColorFilter(ContextCompat.getColor(activity!!,R.color.colorLightGray),PorterDuff.Mode.MULTIPLY)
//                }else{
//                    if (uid != currentUserUid) {
//                        userview?.account_btn_follow_signout?.text = getString(R.string.follow)
//                        userview?.account_btn_follow_signout?.background?.colorFilter = null
//                    }
//                }
            }
        }
    }



}