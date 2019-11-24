package com.cau.capstone.beableto.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.data.RouteDetail
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
                if (position % 2 == 0) {
                    ll_bus_subway.visibility = View.GONE
                } else {
                    ll_walk.visibility = View.GONE
                }
            }
        }

    }

    inner class RouteDetailViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_route_fragment, parent, false)
    ) {
        val ll_bus_subway = itemView.ll_bus_subway
        val ll_walk = itemView.ll_walk
    }
}
