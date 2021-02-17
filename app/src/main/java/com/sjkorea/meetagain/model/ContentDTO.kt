package com.sjkorea.meetagain

import android.os.Parcelable
import android.util.Log
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContentDTO(
    var explain: String? = null,
    var title: String? = null,
    var imageUrl: String? = null,
    var uid: String? = null,
    var userId: String? = null,
    var timestamp: Long? = null,
    var name : String? = null,
    var createdAt: String? = null,
    var favoriteCount: Int = 0,
    var favorites: HashMap<String,Boolean>  = HashMap(),
    var meaningCount: Int = 0,
    var meaning: HashMap<String,Boolean>  = HashMap(),
    var titlesize : Int = 0,
    var pathData : String? = null

)  : Parcelable {

    data class Comment(
        var uid: String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null,
        var commentCount: Int = 0,
    )
}



