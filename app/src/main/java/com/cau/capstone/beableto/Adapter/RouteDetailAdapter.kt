package com.cau.capstone.beableto.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.RegisterBusActivity
import com.cau.capstone.beableto.data.RouteDetail
import com.cau.capstone.beableto.fragment.BusRegisterFragment
import kotlinx.android.synthetic.main.list_route_fragment.view.*

class RouteDetailAdapter(item: ArrayList<RouteDetail>, context: Context) :
    RecyclerView.Adapter<RouteDetailAdapter.RouteDetailViewHolder>() {

    private val items: ArrayList<RouteDetail> = item
    private val mContext: Context = context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteDetailAdapter.RouteDetailViewHolder = RouteDetailViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RouteDetailAdapter.RouteDetailViewHolder,
        position: Int
    ) {
        items[position].let { item ->
            with(holder) {
                when (item.type) {
                    -1 -> {
                        ll_bus_subway.visibility = View.GONE
                        ll_walk.visibility = View.GONE
                        tv_point.text = item.start
                    }
                    0 -> {
                        ll_bus_subway.visibility = View.GONE
                        ll_point.visibility = View.GONE
                        tv_walk_time.text = "도보 약 " + (item.time / 60).toString() + "분"
                    }
                    1 -> {
                        ll_walk.visibility = View.GONE
                        ll_point.visibility = View.GONE
                        tv_detail_start.text = item.start + " 승차"
                        tv_detail_end.text = item.end + " 하차"
                        btn_transfer_id.text = item.transfer_id + "  >"
                        tv_detail_time.text = "버스 약 " + (item.time / 60).toString() + "분"
                        view_detail_transfer_id.getBackground().setColorFilter(Color.parseColor(item.color), PorterDuff.Mode.SRC_ATOP)
                        btn_transfer_id.getBackground().setColorFilter(Color.parseColor(item.color), PorterDuff.Mode.SRC_ATOP)
                        when (item.bus_height) {
                            0 -> tv_detail_elevator.text = "비저상버스"
                            1 -> tv_detail_elevator.text = "저상버스"
                            2 -> tv_detail_elevator.text = "정보없음"
                        }
                        //tv_detail_elevator.text = item.bus_area
                    }
                    2 -> {
                        ll_walk.visibility = View.GONE
                        ll_point.visibility = View.GONE
                        tv_detail_start.text = item.start + " 승차"
                        tv_detail_end.text = item.end + " 하차"
                        btn_transfer_id.text = item.transfer_id + "  >"
                        view_detail_transfer_id.getBackground().setColorFilter(Color.parseColor(item.color), PorterDuff.Mode.SRC_ATOP)
                        btn_transfer_id.getBackground().setColorFilter(Color.parseColor(item.color), PorterDuff.Mode.SRC_ATOP)
                        tv_detail_time.text = "지하철 약 " + (item.time / 60).toString() + "분"
                        if(item.elevator != null)
                            tv_detail_elevator.text = item.elevator + " 앞 승강기"
                        else
                            tv_detail_elevator.visibility = View.GONE
                    }
                }
                btn_transfer_id.setOnClickListener {
                    if(item.type == 1){
                        //val intent = Intent(mContext, RegisterBusActivity::class.java)
                        //(mContext as Activity).startActivity(intent)
                        val busRegisterFragment = BusRegisterFragment(mContext, item.bus_area!!, item.transfer_id!!)
                        busRegisterFragment.show()
                    }
                }
            }
        }
    }

    inner class RouteDetailViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_route_fragment, parent, false)
    ) {
        val ll_bus_subway = itemView.ll_bus_subway
        val ll_walk = itemView.ll_walk
        val ll_point = itemView.ll_point
        val view_detail_transfer_id = itemView.view_detail_transfer_id
        var tv_walk_time = itemView.tv_walk_time
        var tv_detail_time = itemView.tv_detail_time
        var tv_detail_start = itemView.tv_detail_start
        var tv_detail_end = itemView.tv_detail_end
        var btn_transfer_id = itemView.btn_transfer_id
        var tv_detail_elevator = itemView.tv_detail_elevator
        var tv_point = itemView.tv_point
    }
}
