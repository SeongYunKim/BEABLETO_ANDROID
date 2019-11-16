package com.cau.capstone.beableto.activity

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cau.capstone.beableto.Adapter.LocationSelectAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.PlaceAPI
import kotlinx.android.synthetic.main.activity_select_location.*

class LocationSelectActivity : AppCompatActivity() {

    private val placeAPI = PlaceAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)

        val list = placeAPI.search_place_list(intent.getStringExtra("input"))
        Handler().postDelayed({
            val adapter = LocationSelectAdapter(list)
            recyclerview_select_location.layoutManager = LinearLayoutManager(this)
            recyclerview_select_location.adapter = adapter
            recyclerview_select_location.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        }, 1000)
    }
}