package com.sjkorea.meetagain.SearchFragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.matteobattilana.weather.PrecipType
import com.sjkorea.meetagain.Adapter.TourAdapter
import com.sjkorea.meetagain.CustomZoomClass.CenterZoomLayout
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.databinding.*
import kotlinx.android.synthetic.main.fragment_search.view.*


class SearchFragment : Fragment() {

    var searchview: View? = null
    private lateinit var  viewPager2: ViewPager2
    private val sliderHandler = Handler()


    lateinit var weather: PrecipType
    private var number = 0

    private var fragmentSearchBinding : FragmentSearchBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = FragmentSearchBinding.inflate(inflater, container,false)
        fragmentSearchBinding = binding

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


//
//        searchview = LayoutInflater.from(inflater.context)
//            .inflate(R.layout.fregment_search, container, false)








        var weatherSpeed = 0
        var weatherParticles = 0f

        //날씨UI조정
         fun chageWeather() {

            if (number < 2)++number else number = 0

            when(number) {

                1 -> {
                    weather = PrecipType.SNOW
                    weatherParticles = 60f
                    weatherSpeed = 200

                }
               0 -> {
                    weather = PrecipType.RAIN
                    weatherSpeed = 600
                    weatherParticles = 60f

                }

               2 -> {
                    weather = PrecipType.CLEAR

                }

            }

            fragmentSearchBinding?.wvWeatherView?.apply{
                setWeatherData(weather)
                speed = weatherSpeed
                emissionRate = weatherParticles
                angle = 45
                fadeOutPercent = .85f

            }

        }

        //날씨UI조정버튼
        fragmentSearchBinding?.root?.btn_change_weather?.setOnClickListener {
            chageWeather()
        }

//        return searchview
        return fragmentSearchBinding?.root

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        // 어레이리스트에 아이템추가
        //  val contentDTO = ArrayList<ContentDTO>()

        //viewPager2.tourRV?.adapter = TourAdapter()
        //   viewPager2.clipToPadding = false
        // viewPager2.clipChildren = false
        //     viewPager2.offscreenPageLimit = 3
        //   viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        //     val compositePageTransformer = CompositePageTransformer()
        //      compositePageTransformer.addTransformer(MarginPageTransformer(30))
        //     compositePageTransformer.addTransformer{ page, position ->
        //       val r = 1 - kotlin.math.abs(position)
        //     page.scaleY = 0.85f + r * 0.25f
        //   }

        //   viewPager2.setPageTransformer(compositePageTransformer)
        //    viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        //      override fun onPageSelected(position: Int) {

        //           sliderHandler.removeCallbacks(sliderRunnable)
        //            sliderHandler.postDelayed(sliderRunnable, 3000)
        //           }
        //       })


        //    }

        //   private val sliderRunnable = Runnable {
        //       viewPager2.currentItem = viewPager2.currentItem + 1
        //    }

    }

    override fun onResume() {
        super.onResume()


        // 초기화 뷰
        val toursRV = fragmentSearchBinding?.root?.findViewById<RecyclerView>(R.id.tourRV)
        // 레이아웃 매니저 초기화
        val layoutManager = CenterZoomLayout(requireActivity())
        //자동가운데 줌
        val snapHelper = LinearSnapHelper()


        fragmentSearchBinding?.root?.tourRV?.adapter = TourAdapter()
        fragmentSearchBinding?.root?.tourRV?.layoutManager = LinearLayoutManager(activity)


        snapHelper.attachToRecyclerView(toursRV)
        fragmentSearchBinding?.root?.tourRV?.isNestedScrollingEnabled = false
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        fragmentSearchBinding?.root?.tourRV?.layoutManager = layoutManager



    }

    override fun onDestroyView() {
        fragmentSearchBinding = null
        super.onDestroyView()
    }


}