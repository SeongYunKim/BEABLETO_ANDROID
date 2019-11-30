package com.cau.capstone.beableto.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.cau.capstone.beableto.R
import kotlinx.android.synthetic.main.slope_info_dialog.*

class SlopeInfoFragment(context: Context): Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        setContentView(R.layout.slope_info_dialog)

        btn_info_cancel.setOnClickListener {
            dismiss()
        }
    }
}