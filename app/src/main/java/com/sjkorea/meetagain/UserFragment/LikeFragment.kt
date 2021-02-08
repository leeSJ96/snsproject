package com.sjkorea.meetagain.LikeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sjkorea.meetagain.R

class LikeFragment : Fragment() {

    var newsview: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        newsview = LayoutInflater.from(inflater.context).inflate(
            R.layout.viewpager_like_item,
            container,
            false
        )


        return newsview
    }





}