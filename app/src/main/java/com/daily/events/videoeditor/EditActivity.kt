package com.daily.events.videoeditor

import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.daily.events.videoeditor.databinding.ActivityEditBinding
import java.io.File

class EditActivity : AppCompatActivity() {

    lateinit var activityEditBinding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityEditBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit)

        val cw = ContextWrapper(SelectActivity.mActivity)
        val directory =
            cw.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val outputPath =
            File(directory, SelectActivity.mActivity?.getString(R.string.app_name)!!)
        if (!outputPath.exists()) {
            outputPath.mkdirs()
        }
        val outputVideoPath =
            File(outputPath, "Vid_001.mp4")

        activityEditBinding.videoView.setVideoURI(Uri.parse(outputVideoPath.absolutePath))
    }
}