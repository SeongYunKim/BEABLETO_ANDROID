package com.cau.capstone.beableto.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cau.capstone.beableto.Adapter.RouteDetailAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.ShowRouteActivity
import com.cau.capstone.beableto.data.Route
import kotlinx.android.synthetic.main.activity_show_route.*
import kotlinx.android.synthetic.main.activity_show_route.view.*
import kotlinx.android.synthetic.main.viewpager_route.view.*

class RouteFragment : Fragment() {
    private var route_info: Route? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.viewpager_route, container, false)
        lateinit var route_detail_adapter: RouteDetailAdapter
        var hour = 0
        var minute = 0
        var text = "도보"
        if (route_info!!.bus) {
            text += ", 버스"
        }
        if (route_info!!.subway) {
            text += ", 지하철"
        }
        view.vp_route_type.text = text
        if (route_info!!.time == 0) {
            view.vp_route_minute1.text = "정보 없음"
            view.btn_detail.visibility = View.INVISIBLE
            view.vp_route_minute2.visibility = View.GONE
            view.vp_route_hour1.visibility = View.GONE
            view.vp_route_hour2.visibility = View.GONE
        } else {
            hour = route_info!!.time!! / 60 / 60
            minute = route_info!!.time!! / 60 % 60
            if (hour == 0) {
                view.vp_route_hour1.visibility = View.GONE
                view.vp_route_hour2.visibility = View.GONE
            } else if(minute == 0){
                view.vp_route_minute1.visibility = View.GONE
                view.vp_route_minute2.visibility = View.GONE
            }
            view.vp_route_hour1.text = hour.toString()
            view.vp_route_minute1.text = minute.toString()
        }

        view.btn_detail.setOnClickListener {
            val activity = activity as ShowRouteActivity
            var detail_time = ""
            var detail_walk_time = "도보 "
            Log.d("RouteDetail", route_info!!.route_detail_list.toString())
            route_detail_adapter = RouteDetailAdapter(route_info!!.route_detail_list, context!!)
            activity.recyclerview_show_route.layoutManager = LinearLayoutManager(context)
            activity.recyclerview_show_route.adapter = route_detail_adapter
            if(hour != 0){
                detail_time += hour.toString() + "시간 "
            }
            if(minute != 0){
                detail_time += minute.toString() + "분"
            }
            val walk_hour = route_info!!.walk_time!! / 60 / 60
            val walk_minute = route_info!!.walk_time!! / 60 % 60
            if(walk_hour != 0){
                detail_walk_time += walk_hour.toString() + "시간 "
            }
            if(walk_minute != 0){
                detail_walk_time += walk_minute.toString() + "분"
            }
            activity.tv_detail_time2.text = detail_time
            activity.tv_detail_walk_time.text = detail_walk_time
            activity.ll_route_detail.visibility = View.VISIBLE
            activity.rl_select_route.visibility = View.GONE
        }
        return view
    }


    override fun setArguments(args: Bundle?) {
        if (args != null) {
            route_info = args.getSerializable("route_info") as Route
        }
    }
}