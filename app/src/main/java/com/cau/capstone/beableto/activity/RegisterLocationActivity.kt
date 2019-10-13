package com.cau.capstone.beableto.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import kotlinx.android.synthetic.main.activity_register_location.*

class RegisterLocationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_location)

        layout_slope_none.setOnClickListener {
            tv_slope_none.isSelected = true
            view_slope_none.isSelected = true
            layout_slope_none.isSelected = true
            tv_slope_gentle.isSelected = false
            view_slope_gentle.isSelected = false
            layout_slope_gentle.isSelected = false
            tv_slope_sharp.isSelected = false
            view_slope_sharp.isSelected = false
            layout_slope_sharp.isSelected = false
        }

        layout_slope_gentle.setOnClickListener {
            tv_slope_none.isSelected = false
            view_slope_none.isSelected = false
            layout_slope_none.isSelected = false
            tv_slope_gentle.isSelected = true
            view_slope_gentle.isSelected = true
            layout_slope_gentle.isSelected = true
            tv_slope_sharp.isSelected = false
            view_slope_sharp.isSelected = false
            layout_slope_sharp.isSelected = false
        }

        layout_slope_sharp.setOnClickListener {
            tv_slope_none.isSelected = false
            view_slope_none.isSelected = false
            layout_slope_none.isSelected = false
            tv_slope_gentle.isSelected = false
            view_slope_gentle.isSelected = false
            layout_slope_gentle.isSelected = false
            tv_slope_sharp.isSelected = true
            view_slope_sharp.isSelected = true
            layout_slope_sharp.isSelected = true
        }

        layout_auto_door.setOnClickListener {
            tv_auto_door.isSelected = true
            view_auto_door.isSelected = true
            layout_auto_door.isSelected = true
            tv_hand_door.isSelected = false
            view_hand_door.isSelected = false
            layout_hand_door.isSelected = false
        }

        layout_hand_door.setOnClickListener {
            tv_auto_door.isSelected = false
            view_auto_door.isSelected = false
            layout_auto_door.isSelected = false
            tv_hand_door.isSelected = true
            view_hand_door.isSelected = true
            layout_hand_door.isSelected = true
        }

        layout_elevator.setOnClickListener {
            tv_elevator.isSelected = true
            view_elevator.isSelected = true
            layout_elevator.isSelected = true
            tv_no_elevator.isSelected = false
            view_no_elevator.isSelected = false
            layout_no_elevator.isSelected = false
        }

        layout_no_elevator.setOnClickListener {
            tv_elevator.isSelected = false
            view_elevator.isSelected = false
            layout_elevator.isSelected = false
            tv_no_elevator.isSelected = true
            view_no_elevator.isSelected = true
            layout_no_elevator.isSelected = true
        }

        layout_toilet.setOnClickListener {
            tv_toilet.isSelected = true
            view_toilet.isSelected = true
            layout_toilet.isSelected = true
            tv_no_toilet.isSelected = false
            view_no_toilet.isSelected = false
            layout_no_toilet.isSelected = false
        }

        layout_no_toilet.setOnClickListener {
            tv_toilet.isSelected = false
            view_toilet.isSelected = false
            layout_toilet.isSelected = false
            tv_no_toilet.isSelected = true
            view_no_toilet.isSelected = true
            layout_no_toilet.isSelected = true
        }

    }


}