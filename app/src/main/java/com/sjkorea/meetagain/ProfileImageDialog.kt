package com.sjkorea.meetagain

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.databinding.CustomProfileDialogBinding
import com.sjkorea.meetagain.utils.Constants
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.custom_profile_dialog.*
import kotlinx.android.synthetic.main.custom_profile_dialog.view.*

class ProfileImageDialog(context: Context): DialogFragment(){


    var profilecontext = context
    var profileview: View? = null
    var uid: String? = null
    var userId: String? = null
    var bundle = Bundle()
    var firestore: FirebaseFirestore? = null

    var imageprofileListenerRegistration: ListenerRegistration? = null

    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var customProfileDialogBinding : CustomProfileDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var binding : CustomProfileDialogBinding = CustomProfileDialogBinding.inflate(layoutInflater)
        customProfileDialogBinding = binding

        userId = requireArguments().getString("userId")
        Log.d(userId.toString(), "userId 받기 ")
        uid = requireArguments().getString("destinationUid")
        Log.d(Constants.TAG ,"uid : $uid" )


        firestore = FirebaseFirestore.getInstance()



        return customProfileDialogBinding!!.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }

    override fun onResume() {
        super.onResume()

        getProfileImage()
    }

    fun getProfileImage() {
        imageprofileListenerRegistration = firestore?.collection("profileImages")?.document(uid!!)
            ?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (documentSnapshot == null) return@addSnapshotListener
                if (documentSnapshot.data != null) {
                    val url = documentSnapshot.data!!["image"]
                        customProfileDialogBinding?.let { it1 ->
                            Glide.with(profilecontext).load(url).apply(RequestOptions()).override(1000,1000)
                                .into(it1.profileDial)
                        }



                }
            }
    }



}