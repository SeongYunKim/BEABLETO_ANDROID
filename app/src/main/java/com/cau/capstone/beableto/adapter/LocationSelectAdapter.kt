package com.cau.capstone.beableto.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.data.Location
import kotlinx.android.synthetic.main.list_location.view.*

class LocationSelectAdapter(item: ArrayList<Location>) :
    RecyclerView.Adapter<LocationSelectAdapter.LocationSelectViewHolder>() {

    private val items: ArrayList<Location> = item

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationSelectAdapter.LocationSelectViewHolder = LocationSelectViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: LocationSelectAdapter.LocationSelectViewHolder,
        position: Int
    ) {
        items[position].let { item ->
            with(holder) {
                tv_location_name.text = item.name
                tv_location_address.text = item.address
                tv_location_type.text = "카페, 커피"
                if(item.rate == null)
                    item.rate = 5.0F
                tv_location_rating.text = "평점: " + item.rate.toString() + " / 5.0"
            }
        }
    }

    inner class LocationSelectViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_location, parent, false)
    ) {
        val tv_location_name = itemView.tv_location_name
        val tv_location_address = itemView.tv_location_address
        val tv_location_type = itemView.tv_location_type
        val tv_location_rating = itemView.tv_location_rating
    }

}
