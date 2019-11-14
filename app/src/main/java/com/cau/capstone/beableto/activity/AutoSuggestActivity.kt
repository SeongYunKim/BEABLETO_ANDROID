package com.cau.capstone.beableto.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.Adapter.PlaceAutoSuggestAdapter
import com.cau.capstone.beableto.R
import kotlinx.android.synthetic.main.activity_src_dest_search.*

class AutoSuggestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_src_dest_search)
        //autocomplete.setAdapter(PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1))
        val adapter = PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1)
        autocomplete_listview.adapter = adapter

        et_route_src_dest_search1.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val filterText = s.toString()
                    if (filterText.length > 0) {
                        autocomplete_listview.setFilterText(filterText)
                    } else {
                        autocomplete_listview.clearTextFilter()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }
            }
        )
    }
}