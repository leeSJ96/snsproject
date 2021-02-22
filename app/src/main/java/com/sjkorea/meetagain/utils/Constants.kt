package com.sjkorea.meetagain.utils

import com.sjkorea.meetagain.model.IdDTO

object Constants {


    var TAG : String? = "로그"

    var ORDER : Int = 0

    var IDDTO: String? = null

    //댓글 부분 더보기 싱글톤
    var LIST : Int = 0

    //
    var POSTSHOW: String? = null
    //게시물 더보기 스니퍼 싱글톤
    var MORESPINNER : String? = null
        //댓글 더보기 스니퍼 싱글톤
        var COMMENTMORESPINNER : String? = null

    const val AuthOverLap = "The email address is already in use by another account"
    const val EmailFormError = "he email address is badly formatted"
    const val PasswordFail = "The password is invalid or the user does not have a password"
    const val IdFail = "There is no user record corresponding to this identifier"
}