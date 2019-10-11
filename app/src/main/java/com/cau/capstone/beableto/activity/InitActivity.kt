package com.cau.capstone.beableto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
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
import retrofit2.HttpException

class InitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init)

        btn_login.isSelected = validateStep()

        btn_login.setOnClickListener {
            if(btn_login.isSelected){
                var requestLogIn = RequestLogIn(et_login_id.text.toString(), et_login_password.text.toString())
                NetworkCore.getNetworkCore<BEABLETOAPI>()
                    .requestLogIn(
                        requestLogIn
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        Log.d("Success", response.token)
                            SharedPreferenceController.setAuthorization(this@InitActivity, response.token)
                            Toast.makeText(this@InitActivity, SharedPreferenceController.getAuthorization(this), Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            //TODO MutableLiveData 처리
                            //isLogin.value = true
                    }, { except ->
                        Log.d("Error", except.message)
                        if (except is HttpException){
                            if(except.code() == 400){
                                Toast.makeText(this@InitActivity, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
            else {
                Toast.makeText(this@InitActivity, "아이디 또는 비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        et_login_id.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })

        et_login_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })

        btn_register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun statusText(boolean: Boolean) {
        btn_login.isSelected = boolean
    }

    fun validateStep() : Boolean = et_login_id.text.isNotEmpty() && et_login_password.text.isNotEmpty()
}