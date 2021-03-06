package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.cau.capstone.beableto.Adapter.LocationSelectAdapter
import com.cau.capstone.beableto.Adapter.LocationSelectPagerAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.api.PlaceAPI
import com.cau.capstone.beableto.data.Location
import com.cau.capstone.beableto.data.RequestLocationSearch
import com.cau.capstone.beableto.fragment.LocationSelectFragment
import com.cau.capstone.beableto.repository.SharedPreferenceController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_location.*
import java.util.ArrayList

class LocationSelectActivity2 : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val placeAPI = PlaceAPI()
    private var mContext: Context? = null
    private var intent_input: String? = null
    var list: ArrayList<Location> = ArrayList()
    var item_touch: Boolean = true
    private val SELECT_LOCATION_REQUEST = 9876
    private var location_select_pager_adapter: LocationSelectPagerAdapter? = null
    private var type_intent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_location)
        mContext = this
        intent_input = intent.getStringExtra("input")

        if (intent.hasExtra("type")) {
            type_intent = intent.getStringExtra("type")
        }

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_select_location) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewpager_select_location.clipToPadding = false
        val dp_value = 60
        val d = resources.displayMetrics.density
        val margin = (dp_value + d).toInt()
        viewpager_select_location.setPadding(margin, 0, margin, 0)
        viewpager_select_location.pageMargin = margin / 2

        recyclerview_select_location.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                item_touch = false
            }
        })

        recyclerview_select_location.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    if (e.action == MotionEvent.ACTION_UP && item_touch) {
                        val position = rv.getChildAdapterPosition(child)
                        if (type_intent != null) {
                            val intent = Intent()
                            intent.putExtra("research_latitude", list[position].latitude)
                            intent.putExtra("research_longitude", list[position].longitude)
                            intent.putExtra("research_name", list[position].name)
                            intent.putExtra("research_type", type_intent)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            recyclerview_select_location.visibility = View.GONE
                            //recyclerview_select_location.visibility = View.VISIBLE
                            viewpager_select_location.visibility = View.VISIBLE
                            viewpager_select_location.currentItem = position
                            val selected_location = LatLng(
                                list[position].latitude.toDouble(),
                                list[position].longitude.toDouble()
                            )
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    selected_location,
                                    18.0F
                                )
                            )
                        }
                    } else if (e.action == MotionEvent.ACTION_DOWN) {
                        item_touch = true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })

        viewpager_select_location.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                val selected_location = LatLng(
                    list[position].latitude.toDouble(),
                    list[position].longitude.toDouble()
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selected_location, 18.0F))
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(selected_location).title(list[position].name))
            }
        })

        et_location_select_search.setOnClickListener {
            val intent = Intent(this, AutoSuggestActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        if (viewpager_select_location.visibility == View.VISIBLE) {
            viewpager_select_location.visibility = View.GONE
            recyclerview_select_location.visibility = View.VISIBLE
        } else {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val INIT = LatLng(37.50352, 126.95706)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INIT, 18.0F))
        mapLoadedCallBack()
    }

    private fun mapLoadedCallBack() {
        mMap.setOnMapLoadedCallback {
            val input = intent.getStringExtra("input")
            if (intent.hasExtra("position")) {
                list = SharedPreferenceController.getRecentSearchLocation(
                    this,
                    intent.getIntExtra("position", 0)
                )
                if (list.isEmpty()) {
                    Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                NetworkCore.getNetworkCore<BEABLETOAPI>()
                    .requestLocationSearch(
                        SharedPreferenceController.getAuthorization(this),
                        RequestLocationSearch(input!!)
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        Log.d("ResponseXXX", response.toString())
                        for (marker in response.markers) {
                            list.add(
                                Location(
                                    marker.x_axis,
                                    marker.y_axis,
                                    marker.address,
                                    marker.location_name,
                                    5.0f,
                                    "",
                                    null,
                                    marker.slope
                                )
                            )
                        }
                        val adapter = LocationSelectAdapter(list, this)
                        recyclerview_select_location.layoutManager = LinearLayoutManager(this)
                        recyclerview_select_location.adapter = adapter
                        recyclerview_select_location.addItemDecoration(
                            DividerItemDecoration(
                                this,
                                DividerItemDecoration.VERTICAL
                            )
                        )
                        val first_location =
                            LatLng(list[0].latitude.toDouble(), list[0].longitude.toDouble())
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_location, 18.0F))
                        mMap.addMarker(MarkerOptions().position(first_location).title(list[0].name))

                        location_select_pager_adapter = LocationSelectPagerAdapter(supportFragmentManager)
                        viewpager_select_location.adapter = location_select_pager_adapter
                        for (i in list) {
                            val location_select_fragment = LocationSelectFragment(false)
                            val bundle = Bundle()
                            bundle.putSerializable("location_info", i)
                            //bundle.putString("name", i.name)
                            location_select_fragment.arguments = bundle
                            location_select_pager_adapter!!.addItem(location_select_fragment)
                        }
                        location_select_pager_adapter!!.notifyDataSetChanged()

                        SharedPreferenceController.setRecentSearchList(this, input!!, list)
                    }, { except ->
                    })
            }
            /*
            while (list.isEmpty()) {
                Handler().postDelayed({}, 100)
            }
            Handler().postDelayed({
                if (list[0].latitude == 1.0F) {
                    Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                val adapter = LocationSelectAdapter(list, this)
                recyclerview_select_location.layoutManager = LinearLayoutManager(this)
                recyclerview_select_location.adapter = adapter
                recyclerview_select_location.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                val first_location =
                    LatLng(list[0].latitude.toDouble(), list[0].longitude.toDouble())
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first_location, 18.0F))
                mMap.addMarker(MarkerOptions().position(first_location).title(list[0].name))

                location_select_pager_adapter = LocationSelectPagerAdapter(supportFragmentManager)
                viewpager_select_location.adapter = location_select_pager_adapter
                for (i in list) {
                    val location_select_fragment = LocationSelectFragment()
                    val bundle = Bundle()
                    bundle.putSerializable("location_info", i)
                    //bundle.putString("name", i.name)
                    location_select_fragment.arguments = bundle
                    location_select_pager_adapter!!.addItem(location_select_fragment)
                }
                location_select_pager_adapter!!.notifyDataSetChanged()

                SharedPreferenceController.setRecentSearchList(this, input!!, list)
            }, 1300)
            //ConCatList().execute("")

             */
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_LOCATION_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.hasExtra("slope")) {
                list[data.getIntExtra("position", 0)].slope = data.getIntExtra("slope", 0)
                location_select_pager_adapter!!.notifyDataSetChanged()
            }
        } else {
            //위치 등록하다가 말았을 경우
        }
    }
}