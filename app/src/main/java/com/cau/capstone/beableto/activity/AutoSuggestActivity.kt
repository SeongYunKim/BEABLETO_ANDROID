package com.cau.capstone.beableto.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.adapter.PlaceAutoSuggestAdapter
import com.cau.capstone.beableto.repository.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_src_dest_search.*

class AutoSuggestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_src_dest_search)
        Log.d("Recent_Before", SharedPreferenceController.getRecentSearchList(this).toString())
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

        autocomplete_listview.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, LocationSelectActivity::class.java)
            val input = parent.getItemAtPosition(position) as String
            intent.putExtra("input", input)
            SharedPreferenceController.setRecentSearchList(this, input)
            Log.d("Recent_After", SharedPreferenceController.getRecentSearchList(this).toString())
            startActivity(intent)
            finish()
        }
    }
}