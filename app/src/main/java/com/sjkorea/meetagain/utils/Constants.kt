package com.sjkorea.meetagain.utils

import com.google.firebase.firestore.FirebaseFirestore

object Constants {




    var TAG : String? = "로그"

    var CONTEXT_NULL = "CONTEXT_NOT_NULL"

    var ODRDER : String = "0"
    //댓글 부분 더보기 싱글톤
    var LIST : Int = 0
    //uid싱글톤
    var UID : String? = null
    //Follow데이터 유무확인
    var FOLLOWDATA : Int? = null
    var ST : String = "timestamp"
    var SORT  = FirebaseFirestore.getInstance().collection("images")?.orderBy("timestamp")
    var SORTT = "0"

    //
    var POSTSHOW: String? = null
    //게시물 더보기 스니퍼 싱글톤
    var MORESPINNER : String? = null
        //댓글 더보기 스니퍼 싱글톤
        var COMMENTMORESPINNER : String? = null


        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1


    const val AuthOverLap = "The email address is already in use by another account"
    const val EmailFormError = "he email address is badly formatted"
    const val PasswordFail = "The password is invalid or the user does not have a password"
    const val IdFail = "There is no user record corresponding to this identifier"
}