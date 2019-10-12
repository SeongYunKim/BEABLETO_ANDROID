package com.cau.capstone.beableto

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .test2(
                SharedPreferenceController.getAuthorization(this@MainActivity)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({response ->
                Log.d("list_data", response.phone)
                Toast.makeText(this@MainActivity, response.phone + "/" + response.guardian_phone, Toast.LENGTH_SHORT).show()
                temp.text = response.phone
            }, {
                Log.d("list_data", Log.getStackTraceString(it))
            })

        temp_logout.setOnClickListener {
            SharedPreferenceController.logout(this@MainActivity)
            val intent = Intent(this, InitActivity::class.java)
            startActivity(intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
        }
    }
}
