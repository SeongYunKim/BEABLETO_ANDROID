package com.cau.capstone.beableto.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestSaveBus
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.register_bus_dialog.*

class BusRegisterFragment(context: Context, bus_area: String, bus_num: String): Dialog(context) {

    val bus_area = bus_area
    val bus_num = bus_num
    val mContext = context

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        setContentView(R.layout.register_bus_dialog)

        tv_bus_area.text = "버스 운행지역: " + bus_area
        tv_bus_num.text = "버스 번호: " + bus_num

        dialog_yes.setOnClickListener {
            val requestSaveBus = RequestSaveBus(bus_area, bus_num, 1)
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestSaveBus(
                    SharedPreferenceController.getAuthorization(mContext), requestSaveBus
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dismiss()
                }, {
                    Log.d("Bus_Save_Error", Log.getStackTraceString(it))
                })
        }

        dialog_no.setOnClickListener {
            val requestSaveBus = RequestSaveBus(bus_area, bus_num, 0)
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestSaveBus(
                    SharedPreferenceController.getAuthorization(mContext), requestSaveBus
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dismiss()
                }, {
                    Log.d("Bus_Save_Error", Log.getStackTraceString(it))
                })
        }
    }
}