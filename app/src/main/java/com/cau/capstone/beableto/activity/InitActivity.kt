package com.cau.capstone.beableto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestLogIn
import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_init.*
import kotlinx.android.synthetic.main.activity_register.*

// TODO 아이디, 비밀번호 작성 확인

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        btn_login.setOnClickListener {
            var requestLogIn = RequestLogIn(et_login_id.text.toString(), et_login_password.text.toString())
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestLogIn(
                    requestLogIn
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("Success", response.token)
                    // TODO 로그인 성공 코드
                    if (response.code == 500) {
                        SharedPreferenceController.setAuthorization(this@InitActivity, response.token)
                        //TODO MutableLiveData 처리
                        //isLogin.value = true
                    }
                }, {
                    //TODO 실패시 케이스 분류
                    Log.d("list_data", Log.getStackTraceString(it))
                })
        }

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}