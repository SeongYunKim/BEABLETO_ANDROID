package com.cau.capstone.beableto.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.ShowRouteActivity
import com.cau.capstone.beableto.data.Location
import kotlinx.android.synthetic.main.viewpage_location.view.*

class LocationSelectFragment : Fragment() {
    private var location_info: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.viewpage_location, container, false)
        val slope = location_info!!.slope
        view.vp_location_name.text = location_info!!.name
        view.vp_location_address.text = location_info!!.address
        if (location_info!!.rate == null)
            location_info!!.rate = 5.0F
        view.vp_location_rating.text = "평점: " + location_info!!.rate.toString() + " / 5.0"
        if (slope == null)
            view.vp_location_slope.text = "정보 없음"
        else if (slope == 0)
            view.vp_location_slope.text = "경사 완만"
        else if (slope == 1)
            view.vp_location_slope.text = "경사 급함"
        else if (slope == 2)
            view.vp_location_slope.text = "계단"

        view.btn_start.setOnClickListener {
            val intent = Intent(activity, ShowRouteActivity::class.java)
            intent.putExtra("latitude", location_info!!.latitude)
            intent.putExtra("longitude", location_info!!.longitude)
            intent.putExtra("name", location_info!!.name)
            intent.putExtra("type", "start_first")
            startActivity(intent)
        }

        view.btn_end.setOnClickListener {
            val intent = Intent(activity, ShowRouteActivity::class.java)
            intent.putExtra("latitude", location_info!!.latitude)
            intent.putExtra("longitude", location_info!!.longitude)
            intent.putExtra("name", location_info!!.name)
            intent.putExtra("type", "end_first")
            startActivity(intent)
        }
        return view
    }

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            location_info = args.getSerializable("location_info") as Location
        }
    }
}