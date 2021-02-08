package com.sjkorea.meetagain

import android.app.Application

class App : Application() {



    companion object {
        lateinit var  instance : App    // 자기 자신(class)을 가져온다
            private set
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}