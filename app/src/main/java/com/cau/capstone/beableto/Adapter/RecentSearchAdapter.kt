package com.cau.capstone.beableto.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.R
import kotlinx.android.synthetic.main.list_recent_search.view.*

class RecentSearchAdapter(item: ArrayList<String>) :
    RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder>() {

    private val items: ArrayList<String> = item

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecentSearchAdapter.RecentSearchViewHolder = RecentSearchViewHolder(parent)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(
        holder: RecentSearchAdapter.RecentSearchViewHolder,
        position: Int
    ) {
        items[position].let { item ->
            with(holder) {
                tv_recent_search_name.text = item
            }
        }
    }

    inner class RecentSearchViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_recent_search, parent, false)
    ) {
        val tv_recent_search_name = itemView.tv_recent_search_name
    }
}
