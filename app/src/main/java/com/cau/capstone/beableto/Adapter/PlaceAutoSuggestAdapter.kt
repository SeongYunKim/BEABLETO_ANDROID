package com.cau.capstone.beableto.Adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.cau.capstone.beableto.api.PlaceAPI

class PlaceAutoSuggestAdapter(context: Context, resID: Int) : ArrayAdapter<String>(context, resID),
    Filterable {
    var result: ArrayList<String> = ArrayList()
    val placeAPI = PlaceAPI()

    override fun getCount(): Int {
        return result.size
    }

    override fun getItem(position: Int): String? {
        return result[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    result = placeAPI.autoComplete(constraint.toString())

                    filterResults.values = result
                    filterResults.count = result.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                result = results!!.values as ArrayList<String>
                if (results != null && results.count > 0) {
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }

            }
        }
    }
}