package com.cau.capstone.beableto.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.data.Location
import kotlinx.android.synthetic.main.viewpage_location.view.*

class LocationSelectFragment: Fragment() {
    var location_info: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.viewpage_location, container, false)
        view.vp_location_name.text = location_info!!.name
        view.vp_location_address.text = location_info!!.address
        view.vp_location_rating.text = location_info!!.rate.toString()
        view.vp_location_slope.text = "정보 없음"
        view.vp_location_type.text = "치킨, 강정"
        return view
    }

    override fun setArguments(args: Bundle?) {
        if(args != null){
            location_info = args.getSerializable("location_info") as Location
        }
    }
}