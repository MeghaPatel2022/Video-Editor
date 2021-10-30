package com.daily.events.videoeditor

import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daily.events.videoeditor.const.Constant
import com.daily.events.videoeditor.databinding.ActivityCropBinding
import com.daily.events.videoeditor.model.ImageFile
import com.isseiaoki.simplecropview.CropImageView
import java.io.File
import java.io.FileOutputStream

class CropActivity : AppCompatActivity() {

    private lateinit var acCropActivity: ActivityCropBinding
    var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acCropActivity = DataBindingUtil.setContentView(this, R.layout.activity_crop)

        position = intent.getIntExtra("position", 0)
        acCropActivity.cropImageView.setCropMode(CropImageView.CropMode.FREE)
        setBitmap()
        acCropActivity.imgBack.setOnClickListener {
            onBackPressed()
        }

        acCropActivity.imgDone.setOnClickListener {
            saveCropImage()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this@CropActivity, SelectActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setBitmap() {
        System.gc()
        runOnUiThread {
            Glide.with(this@CropActivity)
                .asBitmap()
                .load(Constant.selectedImages[position].getPath())
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(acCropActivity.cropImageView)
        }
    }

    private fun saveCropImage() {

        val cw = ContextWrapper(applicationContext)
        val directory = cw.getExternalFilesDir(Environment.DIRECTORY_DCIM)

        val myDir = File("$directory/${getString(R.string.app_name) + "/.nomedia"}")

        var path = ""
        Log.d("Data", "data: $path")
        myDir.mkdirs()
        myDir.mkdirs()

        val currentDateAndTime: String? = Constant.selectedImages[position].getName()

        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        val file = File(myDir, currentDateAndTime!!)
        path = file.absolutePath

        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            acCropActivity.cropImageView.croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()

            val img = ImageFile()

            img.setId(1)
            img.setName(file.name)
            img.setPath(file.absolutePath)
            img.setSize(file.length())
            img.setBucketName(file.parentFile.name)
            img.setDate(Constant.convertTimeDateModified(file.lastModified()))

            img.setVideo(false)
            img.setSelected(true)
            Constant.selectedImages.removeAt(position)
            Constant.selectedImages.add(position, img)

            System.gc()

            val intent = Intent(this@CropActivity, SelectActivity::class.java)
            intent.putExtra("filePath", path)
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}