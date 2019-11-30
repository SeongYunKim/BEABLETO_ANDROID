package com.cau.capstone.beableto.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_mypage.*

class MyPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestRanking(
                SharedPreferenceController.getAuthorization(this)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                tv_mypage_email.text = response.my_rank.email
                tv_mypage_point.text = response.my_rank.point.toString() + "P"
            }, { except ->

            })

        btn_mypage_cancel.setOnClickListener {
            finish()
        }

        btn_ranking.setOnClickListener {
            val intent = Intent(this, PointRankingActivity::class.java)
            startActivity(intent)
        }
    }
}
