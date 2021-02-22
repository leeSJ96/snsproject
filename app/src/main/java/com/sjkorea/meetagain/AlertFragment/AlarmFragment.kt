package com.sjkorea.meetagain.AlertFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ListenerRegistration
import com.sjkorea.meetagain.Adapter.AlarmRecyclerViewAdapter
import com.sjkorea.meetagain.ContentDTO
import com.sjkorea.meetagain.R
import com.sjkorea.meetagain.databinding.FragmentAlarmBinding
import com.sjkorea.meetagain.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_alarm.*

class AlarmFragment : Fragment() {


    // 뷰가 사라질때 즉 메모리에서 날라갈때 같이 날리기 위해 따로 빼두기
    private var fragmentAlarmBinding : FragmentAlarmBinding? = null
    var contentDTOs: ArrayList<ContentDTO> = arrayListOf()

    var alarmSnapshot : ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  = FragmentAlarmBinding.inflate(inflater, container,false)
        fragmentAlarmBinding = binding

            fragmentAlarmBinding?.alarmfragmentRecyclerview?.adapter = AlarmRecyclerViewAdapter(contentDTOs,childFragmentManager,)
        fragmentAlarmBinding?.alarmfragmentRecyclerview?.layoutManager = LinearLayoutManager(activity)

        return fragmentAlarmBinding!!.root
    }

    override fun onStop() {
        super.onStop()
        alarmSnapshot?.remove()



        //스냅샷 사용할떄는
        // ListenerRegistration
        // null 맨상단단

    }

    override fun onDestroyView() {
        fragmentAlarmBinding = null
        super.onDestroyView()
    }
}