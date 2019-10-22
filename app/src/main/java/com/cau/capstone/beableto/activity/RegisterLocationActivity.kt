package com.cau.capstone.beableto.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.exifinterface.media.ExifInterface
import com.cau.capstone.beableto.R
import com.cau.capstone.beableto.api.BEABLETOAPI
import com.cau.capstone.beableto.api.NetworkCore
import com.cau.capstone.beableto.repository.SharedPreferenceController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register_location.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class RegisterLocationActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    private var string_filePath: String? = null
    private val PICK_IMAGE_REQUEST = 1234
    private val PERMISSION_CODE = 4321
    private var latitude: Float? = null
    private var longitude: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_location)
        tv_slope_none.isSelected = true
        view_slope_none.isSelected = true
        layout_slope_none.isSelected = true
        tv_auto_door.isSelected = true
        view_auto_door.isSelected = true
        layout_auto_door.isSelected = true
        tv_elevator.isSelected = true
        view_elevator.isSelected = true
        layout_elevator.isSelected = true
        tv_toilet.isSelected = true
        view_toilet.isSelected = true
        layout_toilet.isSelected = true

        layout_slope_none.setOnClickListener {
            tv_slope_none.isSelected = true
            view_slope_none.isSelected = true
            layout_slope_none.isSelected = true
            tv_slope_gentle.isSelected = false
            view_slope_gentle.isSelected = false
            layout_slope_gentle.isSelected = false
            tv_slope_sharp.isSelected = false
            view_slope_sharp.isSelected = false
            layout_slope_sharp.isSelected = false
        }

        layout_slope_gentle.setOnClickListener {
            tv_slope_none.isSelected = false
            view_slope_none.isSelected = false
            layout_slope_none.isSelected = false
            tv_slope_gentle.isSelected = true
            view_slope_gentle.isSelected = true
            layout_slope_gentle.isSelected = true
            tv_slope_sharp.isSelected = false
            view_slope_sharp.isSelected = false
            layout_slope_sharp.isSelected = false
        }

        layout_slope_sharp.setOnClickListener {
            tv_slope_none.isSelected = false
            view_slope_none.isSelected = false
            layout_slope_none.isSelected = false
            tv_slope_gentle.isSelected = false
            view_slope_gentle.isSelected = false
            layout_slope_gentle.isSelected = false
            tv_slope_sharp.isSelected = true
            view_slope_sharp.isSelected = true
            layout_slope_sharp.isSelected = true
        }

        layout_auto_door.setOnClickListener {
            tv_auto_door.isSelected = true
            view_auto_door.isSelected = true
            layout_auto_door.isSelected = true
            tv_hand_door.isSelected = false
            view_hand_door.isSelected = false
            layout_hand_door.isSelected = false
        }

        layout_hand_door.setOnClickListener {
            tv_auto_door.isSelected = false
            view_auto_door.isSelected = false
            layout_auto_door.isSelected = false
            tv_hand_door.isSelected = true
            view_hand_door.isSelected = true
            layout_hand_door.isSelected = true
        }

        layout_elevator.setOnClickListener {
            tv_elevator.isSelected = true
            view_elevator.isSelected = true
            layout_elevator.isSelected = true
            tv_no_elevator.isSelected = false
            view_no_elevator.isSelected = false
            layout_no_elevator.isSelected = false
        }

        layout_no_elevator.setOnClickListener {
            tv_elevator.isSelected = false
            view_elevator.isSelected = false
            layout_elevator.isSelected = false
            tv_no_elevator.isSelected = true
            view_no_elevator.isSelected = true
            layout_no_elevator.isSelected = true
        }

        layout_toilet.setOnClickListener {
            tv_toilet.isSelected = true
            view_toilet.isSelected = true
            layout_toilet.isSelected = true
            tv_no_toilet.isSelected = false
            view_no_toilet.isSelected = false
            layout_no_toilet.isSelected = false
        }

        layout_no_toilet.setOnClickListener {
            tv_toilet.isSelected = false
            view_toilet.isSelected = false
            layout_toilet.isSelected = false
            tv_no_toilet.isSelected = true
            view_no_toilet.isSelected = true
            layout_no_toilet.isSelected = true
        }

        iv_register_location.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_CODE
                )
            } else {
                startActivityForResult(
                    Intent.createChooser(intent, "SELECT PICTURE"),
                    PICK_IMAGE_REQUEST
                )
            }
        }

        cb_use_photo.setOnClickListener {
            try {
                if (filePath != null) {
                    val inputStream = getContentResolver().openInputStream(filePath!!)
                    val exif = ExifInterface(inputStream!!)
                    //Exif GPS 정보를 GeoPoint로 변환
                    val attrLatitude: String? = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)
                    val attrLatitude_REF: String? =
                        exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                    val attrLongtitute: String? = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)
                    val attrLongtitute_REF: String? =
                        exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
                    if ((attrLatitude != null) && (attrLatitude_REF != null) && (attrLongtitute != null) && (attrLongtitute_REF != null)) {
                        if (attrLatitude_REF == "N")
                            latitude = convertToDegree(attrLatitude)
                        else
                            latitude = 0 - convertToDegree(attrLatitude)

                        if (attrLongtitute_REF == "E")
                            longitude = convertToDegree(attrLongtitute)
                        else
                            longitude = 0 - convertToDegree(attrLongtitute)

                        Toast.makeText(
                            this@RegisterLocationActivity,
                            latitude.toString() + ", " + longitude.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("GeoPoint", latitude.toString() + ", " + longitude.toString())
                    }
                } else {
                    Toast.makeText(
                        this@RegisterLocationActivity,
                        "등록된 사진이 없습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        layout_register_location.setOnClickListener {
            var part : MultipartBody.Part? = null
            if(string_filePath != null){
                val file = File(string_filePath)
                val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
                part = MultipartBody.Part.createFormData("image", file.name, fileReqBody)
            }

            var slope: Int = 0
            var auto_door: Boolean = true
            var elevator: Boolean = true
            var toilet: Boolean = true

            if (layout_slope_none.isSelected) slope = 0;
            else if (layout_slope_gentle.isSelected) slope = 1;
            else if (layout_slope_sharp.isSelected) slope = 2;
            if (layout_auto_door.isSelected) auto_door = true
            else if (layout_hand_door.isSelected) auto_door = false
            if (layout_elevator.isSelected) elevator = true
            else if (layout_no_elevator.isSelected) elevator = false
            if (layout_toilet.isSelected) toilet = true
            else if (layout_no_toilet.isSelected) toilet = false;

            //필수 항목 Validate
            NetworkCore.getNetworkCore<BEABLETOAPI>()
                .requestRegisterLocation(
                    SharedPreferenceController.getAuthorization(this@RegisterLocationActivity),
                    part,
                    "208관",
                    "흑석동 84",
                    latitude!!,
                    longitude!!,
                    slope,
                    auto_door,
                    elevator,
                    toilet,
                    "Hello"
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("L_Register Success", response.message)
                    Toast.makeText(this@RegisterLocationActivity, "성공!!", Toast.LENGTH_SHORT).show()
                }, {
                    Log.d("L_Register Fail", Log.getStackTraceString(it))
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            string_filePath = getPathFromUri(data!!.data)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                iv_register_location!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            //사진 등록하다가 말았을 경우
        }
    }

    //TODO SDK Version 27 체크
    fun getPathFromUri(uri: Uri?): String {
        val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)

        if (Build.VERSION.SDK_INT > 26) {
            var path: String = ""
            val fileId: String = DocumentsContract.getDocumentId(uri)
            val id: String = fileId.split(":")[1]
            val idString: Array<String> = arrayOf(id)
            val selector: String = MediaStore.Images.Media._ID + "=?"
            val cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                filePathColumn,
                selector,
                idString,
                null
            )
            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
            if (cursor.moveToFirst())
                path = cursor.getString(columnIndex)
            cursor.close()
            return path
        } else {
            var cursor = getContentResolver().query(uri!!, filePathColumn, null, null, null)
            startManagingCursor(cursor)
            cursor!!.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val path: String = cursor.getString(columnIndex)
            cursor.close()
            return path
        }
    }

    fun convertToDegree(stringDMS: String): Float {
        var result: Float?
        val DMS: List<String> = stringDMS.split(",")

        val stringD: List<String> = DMS[0].split("/")
        val d0: Double = stringD[0].toDouble()
        val d1: Double = stringD[1].toDouble()
        val floatD: Double = d0 / d1

        val stringM: List<String> = DMS[1].split("/")
        val m0: Double = stringM[0].toDouble()
        val m1: Double = stringM[1].toDouble()
        val floatM: Double = m0 / m1

        val stringS: List<String> = DMS[2].split("/")
        val s0: Double = stringS[0].toDouble()
        val s1: Double = stringS[1].toDouble()
        val floatS: Double = s0 / s1

        result = (floatD + (floatM / 60) + (floatS / 3600)).toFloat()
        return result
    }
}