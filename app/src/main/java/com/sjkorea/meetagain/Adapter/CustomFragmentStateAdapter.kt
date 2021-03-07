package com.sjkorea.meetagain.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sjkorea.meetagain.LikeFragment.LikeFragment
import com.sjkorea.meetagain.UserFragment.HistoryFragment
import com.sjkorea.meetagain.UserFragment.PostFragment
import com.sjkorea.meetagain.WalletFragment.WalletFragment
import com.sjkorea.meetagain.homeFragment.FavoriteFragment
import com.sjkorea.meetagain.homeFragment.HomeFragment
import com.sjkorea.meetagain.homeFragment.MeaningFragment


//프래그먼트 어뎁터
 class CustomFragmentStateAdapter(FragmentActivity: FragmentActivity) :
    FragmentStateAdapter(FragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> WalletFragment()
            else ->HistoryFragment()

        }
    }

}

class CustomFragmentStateAdapterSub(FragmentActivity: FragmentActivity) :
    FragmentStateAdapter(FragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> FavoriteFragment()
            else ->MeaningFragment()

        }
    }

}


