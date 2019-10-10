package com.cau.capstone.beableto

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val userRepository: UserRepository

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

        btn_register_next.setOnClickListener{

            var requestRegister = RequestRegister(et_name.text.toString(), et_id.text.toString(), et_password.text.toString(), et_phone.text.toString(), et_guardian_phone.text.toString(), aids, cb_push_agree.isChecked)
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestSignUp(
                    requestRegister
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d("list_data", it.toString())
                }, {
                    Log.d("list_data", Log.getStackTraceString(it))
                })
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