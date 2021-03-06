package com.cau.capstone.beableto.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.activity.RegisterLocationActivity
import com.cau.capstone.beableto.data.Location
import kotlinx.android.synthetic.main.list_location.view.*

class LocationSelectAdapter(item: ArrayList<Location>, context: Context) :
    RecyclerView.Adapter<LocationSelectAdapter.LocationSelectViewHolder>() {

    private val items: ArrayList<Location> = item
    private val mContext: Context = context
    private val SELECT_LOCATION_REQUEST = 9876

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
                if(item.rate == null)
                    item.rate = 5.0F
                tv_location_rating.text = "평점: " + item.rate.toString() + " / 5.0"
            }
        }
        holder.btn_register_location.setOnClickListener {
            val intent = Intent(mContext, RegisterLocationActivity::class.java)
            intent.putExtra("fix_latitude", items[position].latitude)
            intent.putExtra("fix_longitude", items[position].longitude)
            intent.putExtra("fix_name", items[position].name)
            intent.putExtra("fix_address", items[position].address)
            intent.putExtra("position", position)
            (mContext as Activity).startActivityForResult(intent, SELECT_LOCATION_REQUEST)
            //mContext.finish()
        }
    }

    inner class LocationSelectViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_location, parent, false)
    ) {
        val tv_location_name = itemView.tv_location_name
        val tv_location_address = itemView.tv_location_address
        val tv_location_rating = itemView.tv_location_rating
        val btn_register_location = itemView.btn_register_location
    }
}
