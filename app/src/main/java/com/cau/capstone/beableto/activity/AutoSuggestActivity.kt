package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cau.capstone.beableto.Adapter.PlaceAutoSuggestAdapter
import com.cau.capstone.beableto.Adapter.RecentSearchAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.repository.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_src_dest_search.*

class AutoSuggestActivity : AppCompatActivity() {

    private var RESEARCH = 13579
    var auto_suggest_adapter: PlaceAutoSuggestAdapter? = null
    var recent_search_adapter: RecentSearchAdapter? = null
    private var type_intent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_src_dest_search)

        if(intent.hasExtra("type")){
            type_intent = intent.getStringExtra("type")
        }

        auto_suggest_adapter = PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1)
        autocomplete_listview.adapter = auto_suggest_adapter
        val filter = auto_suggest_adapter!!.filter
        recent_search_adapter =
            RecentSearchAdapter(SharedPreferenceController.getRecentSearchWord(this))
        recyclerview_recent_search.layoutManager = LinearLayoutManager(this)
        recyclerview_recent_search.adapter = recent_search_adapter
        recyclerview_recent_search.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        et_route_src_dest_search1.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val filterText = s.toString()
                    if (filterText.length > 0) {
                        auto_suggest_adapter!!.notifyDataSetChanged()
                        //autocomplete_listview.setFilterText(filterText)
                        filter.filter(filterText)
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
            val input = parent.getItemAtPosition(position) as String
            if (type_intent == null) {
                val intent = Intent(this@AutoSuggestActivity, LocationSelectActivity::class.java)
                intent.putExtra("input", input)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@AutoSuggestActivity, LocationSelectActivity::class.java)
                intent.putExtra("input", input)
                intent.putExtra("type", type_intent)
                startActivityForResult(intent, RESEARCH)
            }

        }

        recyclerview_recent_search.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                val child = rv.findChildViewUnder(e.x, e.y)
                if (child != null) {
                    if (e.action == MotionEvent.ACTION_UP) {
                        val position = rv.getChildAdapterPosition(child)
                        if (type_intent == null) {
                            val intent = Intent(this@AutoSuggestActivity, LocationSelectActivity::class.java)
                            intent.putExtra("input", SharedPreferenceController.getRecentSearchWord(this@AutoSuggestActivity)[position])
                            intent.putExtra("position", position)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this@AutoSuggestActivity, LocationSelectActivity::class.java)
                            intent.putExtra("input", SharedPreferenceController.getRecentSearchWord(this@AutoSuggestActivity)[position])
                            intent.putExtra("position", position)
                            intent.putExtra("type", type_intent)
                            startActivityForResult(intent, RESEARCH)
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESEARCH && resultCode == Activity.RESULT_OK && data != null) {
            val intent = Intent()
            if (data.hasExtra("research_type")) {
                intent.putExtra("research_latitude", data.getFloatExtra("research_latitude", 0.0F))
                intent.putExtra("research_longitude", data.getFloatExtra("research_longitude", 0.0F))
                intent.putExtra("research_name", data.getStringExtra("research_name"))
                intent.putExtra("research_type", data.getStringExtra("research_type"))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        } else {

        }
    }


    /*
    override fun onResume() {
        super.onResume()
        recent_search_adapter!!.notifyDataSetChanged()
    }
    */
}