package com.cau.capstone.beableto.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.repository.SharedPreferenceController

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if(SharedPreferenceController.getAuthorization(this@SplashActivity) == "") {
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
           }
            else{
               val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }, 1500)

    }
}
