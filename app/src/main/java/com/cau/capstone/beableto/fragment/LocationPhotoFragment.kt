package com.cau.capstone.beableto.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.DownloadListener
import android.widget.Toast
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.data.RequestLocationPhoto
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.photo_dialog.*
import java.lang.Exception
import java.net.URL

class LocationPhotoFragment(context: Context, x: Float, y: Float): Dialog(context) {

    val x = x
    val y = y
    val mContext = context

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.8f
        window!!.attributes = layoutParams
        setContentView(R.layout.photo_dialog)

        NetworkCore.getNetworkCore<BEABLETOAPI>()
            .requestLocationPhoto(
                SharedPreferenceController.getAuthorization(mContext),
                RequestLocationPhoto(x, y)
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("Photo_Success", response.toString())
                if(response.image == ""){
                    dismiss()
                    Toast.makeText(mContext, "등록된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
                } else{
                    DownloadImageTask().execute(response.image)
                }
            }, {
                Log.d("Photo_Error", Log.getStackTraceString(it))
            })

        btn_image_cancel.setOnClickListener {
            dismiss()
        }
    }

    inner class DownloadImageTask: AsyncTask<String, Void, Bitmap>() {
        override fun doInBackground(vararg params: String?): Bitmap {
            val urldisplay = NetworkCore.BASE_URL + "/media/" + params[0]
            var bitmap: Bitmap? = null
            try{
                val instream = URL(urldisplay).openStream()
                bitmap = BitmapFactory.decodeStream(instream)
                val matrix = Matrix()
                matrix.postRotate(90f)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } catch (e: Exception){
                Log.d("Photo_Exception", "PhotoPhoto")
                e.printStackTrace()
            }
            return bitmap!!
        }

        override fun onPostExecute(result: Bitmap?) {
            iv_photo.setImageBitmap(result)
        }
    }
}