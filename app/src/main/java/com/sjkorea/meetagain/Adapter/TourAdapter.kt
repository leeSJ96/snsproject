package com.sjkorea.meetagain.Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.R
import kotlinx.android.synthetic.main.item_slide_container.view.*


class TourAdapter :  RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var firestore: FirebaseFirestore? = null
    var contentUidList: ArrayList<String> = arrayListOf()
    var contentDTO : ArrayList<ContentDTO> = arrayListOf()



    init {

        var uid = FirebaseAuth.getInstance().currentUser?.uid

        FirebaseFirestore.getInstance().collection("images").orderBy("timestamp").addSnapshotListener { querySnapshot , firebaseFirestoreException ->
            if (querySnapshot == null) return@addSnapshotListener
            contentDTO.clear()
            for (snapshot in querySnapshot!!.documents){
                contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
            }
            notifyDataSetChanged()
        }
    }

    fun getCotents(following:MutableMap<String,Boolean>){

        FirebaseFirestore.getInstance().collection("images").orderBy("timestamp").addSnapshotListener { querySnapshot , firebaseFirestoreException ->
            if (querySnapshot == null) return@addSnapshotListener
            contentDTO.clear()
            for (snapshot in querySnapshot!!.documents){
                contentDTO.add(snapshot.toObject(ContentDTO::class.java)!!)
            }
            notifyDataSetChanged()
        }

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
           //onck
        }
        init {
            itemView.setOnClickListener {  }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_slide_container,parent,false)

        return  ViewHolder(itemView)
      //  return SliderViewHolder(
        //     LayoutInflater.from(parent.context).inflate(
        //        R.layout.slide_item_container,
        //        parent,
        //        false
    //   )
        // )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        var viewholder = (holder as ViewHolder).itemView

        //제목
        viewholder.Noticeviewitem_profile_textview.text = contentDTO!![position].title

        // 메인이미지
        Glide.with(holder.itemView.context).load(contentDTO!![position].imageUrl)
            .into(viewholder.Noticeviewitem_imageview_content)

        // 내용
        viewholder.Noticeviewitem_explain_textview.text = contentDTO!![position].explain


        // 하트
        viewholder.Noticeviewitem_favoritecounter_textview.text =
            "좋아요" + contentDTO!![position].favoriteCount + "개"

        //위로
        viewholder.Noticeviewitem_meaningcounter_textview.text =
            "슬퍼요" + contentDTO!![position].meaningCount + "개"





//                bundle.putSerializable("hashmap", contentDTOs[position].favorites)

//                contentDTOs[position].favorites =
//                    bundle.getSerializable("hashmap") as HashMap<String, Boolean>
//
//
//                Log.d(
//                    this.contentDTOs[position].meaningCount.toString(),
//                    "홈 포스트 로그 contentDTOs[position].favoriteCount.toString() 보내기 ")





    }


    override fun getItemCount(): Int {

      return contentDTO.size
    }

}