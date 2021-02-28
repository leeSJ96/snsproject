package com.sjkorea.meetagain

data class AlarmDTO (
    var destinationUid: String? = null,
    var userId: String? = null,
    var uid: String? = null,
    var kind: Int = 0, //0 : 좋아요, 1: 메세지, 2: 팔로우 3: 싫어요
    var message: String? = null,
    var timestamp: Long? = null,
    var name : String? =  null,
    var favoriteCount: Int = 0,
    var favorites: HashMap<String,Boolean>  = HashMap(),
    var meaningCount: Int = 0,
    var meaning: HashMap<String,Boolean>  = HashMap(),
    var titlesize : Int = 0,
    var pathData : String? = null,
    var explain: String? = null,
    var title: String? = null,
    var imageUrl: String? = null,
)