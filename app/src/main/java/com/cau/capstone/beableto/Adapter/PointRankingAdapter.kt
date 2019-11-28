package com.cau.capstone.beableto.Adapter

import com.cau.capstone.beableto.data.PointRank
import kotlinx.android.synthetic.main.list_point.view.*
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R

class PointRankingAdapter(item: ArrayList<PointRank>, context: Context) :
    RecyclerView.Adapter<PointRankingAdapter.PointRankingViewHolder>() {

    private val items: ArrayList<PointRank> = item
    private val mContext: Context = context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PointRankingAdapter.PointRankingViewHolder = PointRankingViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: PointRankingAdapter.PointRankingViewHolder,
        position: Int
    ) {
        items[position].let { item ->
            with(holder) {
                tv_rank.text = item.rank.toString()
                tv_rank_id.text = item.email
                tv_month_point.text = item.point.toString() + "P"
                tv_point.text = item.point.toString() + "P"
            }
        }
    }

    inner class PointRankingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_point, parent, false)
    ) {
        val tv_rank = itemView.tv_rank
        val tv_rank_id = itemView.tv_rank_id
        val tv_month_point = itemView.tv_month_point
        val tv_point = itemView.tv_point
    }
}
