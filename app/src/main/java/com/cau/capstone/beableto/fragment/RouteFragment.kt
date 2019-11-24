package com.cau.capstone.beableto.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.ShowRouteActivity
import com.cau.capstone.beableto.data.Route
import kotlinx.android.synthetic.main.activity_show_route.*
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
            view.vp_route_minute2.visibility = View.GONE
            view.vp_route_hour1.visibility = View.GONE
            view.vp_route_hour2.visibility = View.GONE
        } else {
            val hour: Int = route_info!!.time!! / 60 / 60
            if (hour == 0) {
                view.vp_route_hour1.visibility = View.GONE
                view.vp_route_hour2.visibility = View.GONE
            } else{
                view.vp_route_hour1.text = (route_info!!.time!! / 60 / 60).toString()
            }
            view.vp_route_minute1.text = (route_info!!.time!! / 60 % 60).toString()
        }

        view.btn_detail.setOnClickListener {
            (activity as ShowRouteActivity).ll_route_detail.visibility = View.VISIBLE
            (activity as ShowRouteActivity).rl_select_route.visibility = View.GONE
        }

        return view
    }


    override fun setArguments(args: Bundle?) {
        if (args != null) {
            route_info = args.getSerializable("route_info") as Route
        }
    }
}