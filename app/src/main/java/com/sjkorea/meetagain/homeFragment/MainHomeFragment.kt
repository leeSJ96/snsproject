package com.sjkorea.meetagain.homeFragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.sjkorea.meetagain.Adapter.CustomFragmentStateAdapterSub
import com.sjkorea.meetagain.databinding.FragmentMainhomeBinding
import kotlinx.android.synthetic.main.fragment_mainhome.*
class MainHomeFragment : Fragment() {

    //    val contentDTO: ArrayList<ContentDTO> = arrayListOf()
    private val tabTextList = arrayListOf("최근순", "인기순", "힘내자")
//    private val tabIconList = arrayListOf(
//        R.drawable.ic_arrow_drop_down_black_24dp,
//        R.drawable.ic_arrow_drop_down_black_24dp,
//        R.drawable.ic_arrow_drop_down_black_24dp,
//        R.drawable.ic_arrow_drop_down_black_24dp
//    )
    private var fragmentMainHomeBinding: FragmentMainhomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainhomeBinding.inflate(inflater, container, false)
        fragmentMainHomeBinding = binding

        return fragmentMainHomeBinding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        init()
//

//        titlesize = arguments?.getInt("one",0)!!
//        Log.d(this.titlesize.toString(),"size 받기 ")
//
//        account_tv_post_count.text = titlesize.toString()
//        Log.d(titlesize.toString() , "size 결과 ")


//        account_btn_follow_signout.setOnClickListener {
//            requestFollow()
//        }


    }

    private fun init() {

        viewPager2.adapter = CustomFragmentStateAdapterSub(requireActivity())
        viewPager2.isSaveEnabled = false
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
//            tab.setIcon(tabIconList[position])
            tab.text = tabTextList[position]
        }.attach()

    }

}