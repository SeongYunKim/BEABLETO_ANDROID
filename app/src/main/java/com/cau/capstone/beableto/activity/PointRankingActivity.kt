package com.cau.capstone.beableto.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cau.capstone.beableto.Adapter.PointRankingAdapter
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.PointRank
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_point_ranking.*

class PointRankingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_ranking)

        var list: ArrayList<PointRank>

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestRanking(
                SharedPreferenceController.getAuthorization(this)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                list = response.ranking
                val adapter = PointRankingAdapter(list, this)
                recyclerview_rank.layoutManager = LinearLayoutManager(this)
                recyclerview_rank.adapter = adapter
                recyclerview_rank.addItemDecoration(
                    DividerItemDecoration(
                        this,
                        DividerItemDecoration.VERTICAL
                    )
                )
                my_rank.text = response.my_rank.rank.toString()
                my_id.text = response.my_rank.email
                my_month_point.text = response.my_rank.point.toString() + "P"
                my_point.text = response.my_rank.point.toString() + "P"
            }, { except ->

            })

        btn_point_ranking_cancel.setOnClickListener {
            finish()
        }
    }
}