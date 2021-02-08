package com.sjkorea.meetagain.utils

import com.sjkorea.meetagain.model.IdDTO

object Constants {

    var USER : String? = ""

    var TAG : String? = "로그"

    var IDDTO: String? = null



    const val sharedPrefFile = "app_preferences"


    const val AuthOverLap = "The email address is already in use by another account"
    const val EmailFormError = "he email address is badly formatted"
    const val PasswordFail = "The password is invalid or the user does not have a password"
    const val IdFail = "There is no user record corresponding to this identifier"
}