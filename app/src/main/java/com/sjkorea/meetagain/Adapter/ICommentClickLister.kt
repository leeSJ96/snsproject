package com.sjkorea.meetagain.Adapter

import com.sjkorea.meetagain.ContentDTO

interface IDeletePosition {

    fun onClickListener(position: Int, comment : ContentDTO.Comment )
}

