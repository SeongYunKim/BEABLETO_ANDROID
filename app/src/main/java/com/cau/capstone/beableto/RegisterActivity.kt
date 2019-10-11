package com.cau.capstone.beableto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestRegister
import com.cau.capstone.beableto.repository.UserRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*

//TODO 아이디 중복 확인

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val userRepository: UserRepository

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btn_register_next.isSelected = validateStep()

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
            if(btn_register_next.isSelected){
                if(et_password.text.toString().length >= 8 && et_password.text.toString().length <= 16) {
                    if(et_password.text.toString() == et_re_password.text.toString()){
                        var requestRegister = RequestRegister(et_name.text.toString(), et_id.text.toString(), et_password.text.toString(), et_phone.text.toString(), et_guardian_phone.text.toString(), aids, cb_push_agree.isChecked)
                        NetworkCore.getNetworkCore<BEABLETOAPI>()
                            .requestSignUp(
                                requestRegister
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.d("list_data", it.toString())
                                //val intent = Intent(this, LoginActivity::class.java)
                                //startActivity(intent)
                                finish()
                            }, {
                                Log.d("list_data", Log.getStackTraceString(it))
                            })
                    }
                    else{
                        Toast.makeText(this@RegisterActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this@RegisterActivity, "비밀번호 자릿수를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this@RegisterActivity, "필수 정보를 모두 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        btn_register_cancel.setOnClickListener {
            finish()
        }

        et_name.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })

        et_id.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })

        et_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })

        et_re_password.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                statusText(validateStep())
            }
        })
    }

    fun statusText(boolean: Boolean) {
        btn_register_next.isSelected = boolean
    }

    fun validateStep() : Boolean = et_name.text.isNotEmpty() && et_id.text.isNotEmpty() && et_password.text.isNotEmpty() && et_re_password.text.isNotEmpty()

    fun setAids(position: Int) =
        when (position) {
            0 -> "없음"
            1 -> "전동휠체어"
            2 -> "수동휠체어"
            3 -> "기타"
            else -> null
        }
}