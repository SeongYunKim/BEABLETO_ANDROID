package com.cau.capstone.beableto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var aids: String? = "없음"
        val adapter = ArrayAdapter.createFromResource(this, R.array.spinnerArray, android.R.layout.simple_spinner_item)

        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(AdapterView: AdapterView<*>, view: View, position: Int, l: Long) {
                aids = setAids(position)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        }
    }

    fun setAids(position: Int) =
        when (position) {
            0 -> "없음"
            1 -> "전동휠체어"
            2 -> "수동휠체어"
            3 -> "기타"
            else -> null
        }
}