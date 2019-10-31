package com.cau.capstone.beableto.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.repository.SharedPreferenceController
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        var setting  = SharedPreferenceController.getSetting(this@SettingActivity)
        switch_stair.isChecked = setting.stair
        switch_sharp.isChecked = setting.sharp
        switch_gentle.isChecked = setting.gentle

        btn_setting_cancel.setOnClickListener {
            finish()
        }

        btn_setting_save.setOnClickListener {
            SharedPreferenceController.setSetting(
                this@SettingActivity,
                switch_stair.isChecked,
                switch_sharp.isChecked,
                switch_gentle.isChecked
            )
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}