package com.daily.events.videoeditor

import VideoHandle.EpEditor
import VideoHandle.EpVideo
import VideoHandle.OnEditorListener
import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.daasuu.imagetovideo.EncodeListener
import com.daasuu.imagetovideo.ImageToVideoConverter
import com.daily.events.videoeditor.adapter.CropTrimAdapter
import com.daily.events.videoeditor.const.Constant
import com.daily.events.videoeditor.databinding.ActivitySelectBinding
import com.daily.events.videoeditor.model.ImageFile
import com.gowtham.library.utils.LogMessage
import com.gowtham.library.utils.TrimVideo
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


class SelectActivity : AppCompatActivity() {

    lateinit var cropTrimAdapter: CropTrimAdapter

    var newPos: Int = 0

    companion object {
        val TAG: String = SelectActivity::class.java.name
        var mActivity: Activity? = null
        lateinit var activitySelectBinding: ActivitySelectBinding
    }

    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK &&
                result.data != null
            ) {
                val uri: Uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))

                val img = ImageFile()
                val file = File(uri.path!!)

                img.setId(1)
                img.setName(file.name)
                img.setPath(file.absolutePath)
                img.setSize(file.length())
                img.setBucketName(file.parentFile!!.name)
                img.setDate(Constant.convertTimeDateModified(file.lastModified()))
                img.setVideo(true)
                img.setSelected(true)
                Constant.selectedImages.removeAt(newPos)
                Constant.selectedImages.add(newPos, img)

                System.gc()

                Log.d("LLL_PAth: ", "Trimmed path:: $uri")
            } else
                LogMessage.v("videoTrimResultLauncher data is null")
        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySelectBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_select)
        mActivity = this
        activitySelectBinding.imgClose.setOnClickListener {
            onBackPressed()
        }

        activitySelectBinding.imgDone.setOnClickListener {
            activitySelectBinding.rlLoading.visibility = View.VISIBLE
            LoadAlbumTask().execute()
        }

    }

    class LoadAlbumTask : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            var iscomplete: Boolean = false
            for (i in 0 until Constant.selectedImages.size) {
                val imageFile: ImageFile = Constant.selectedImages[i]

                val cw = ContextWrapper(mActivity)
                val directory = cw.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                val outputPath = File(directory, mActivity?.getString(R.string.app_name)!!)
                if (!outputPath.exists()) {
                    outputPath.mkdirs()
                }
                val outputVideoPath =
                    File(outputPath, "/.nomedia")

                if (!outputVideoPath.exists()) {
                    outputVideoPath.mkdirs()
                }

                Log.e("LLL_Name: ", imageFile.getName()!!)

                val outputVideoFile = File(
                    "$outputVideoPath/${
                        imageFile.getName()!!
                    }"
                )

                if (!imageFile.isVideo()) {
                    val imageToVideo = ImageToVideoConverter(
                        outputPath = outputVideoFile.absolutePath,
                        inputImagePath = imageFile.getPath()!!,
                        duration = TimeUnit.SECONDS.toMicros(3),
                        listener = object : EncodeListener {
                            override fun onProgress(progress: Float) {
                                Log.e("progress$i", "progress = $progress")
                            }

                            override fun onCompleted() {

                                mActivity?.runOnUiThread {

                                    Handler().postDelayed({
                                        val img = ImageFile()

                                        val file = File(outputVideoFile.absolutePath)

                                        img.setId(1)
                                        img.setName(file.name)
                                        img.setPath(file.absolutePath)
                                        img.setSize(file.length())
                                        img.setBucketName(file.parentFile!!.name)
                                        img.setDate(Constant.convertTimeDateModified(file.lastModified()))
                                        img.setVideo(true)
                                        img.setSelected(true)
                                        Constant.selectedImages.removeAt(i)
                                        Constant.selectedImages.add(i, img)

                                        System.gc()

                                        if (i == Constant.selectedImages.size - 1) {
                                            iscomplete = true
                                        }

                                    }, 2000)

                                }
                            }

                            override fun onFailed(exception: Exception) {

                            }
                        }
                    )
                    imageToVideo.start()
                } else {
                    if (i == Constant.selectedImages.size - 1) {
                        iscomplete = true
                    }
                }
            }

            Log.e("LLLL_Bolle: ", iscomplete.toString())
            return iscomplete
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            Log.d(
                TAG,
                "onPostExecute: " + "done"
            )
            if (result) {

                val cw = ContextWrapper(mActivity)
                val directory =
                    cw.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                val outputPath =
                    File(directory, mActivity?.getString(R.string.app_name)!!)
                if (!outputPath.exists()) {
                    outputPath.mkdirs()
                }

                val imageFile1: ImageFile = Constant.selectedImages[0]
                val imageFile2: ImageFile = Constant.selectedImages[1]
                val imageFile3: ImageFile = Constant.selectedImages[2]

                val outputVideoPath =
                    File(outputPath, "Vid_001.mp4")

                val epVideos = ArrayList<EpVideo>()
                epVideos.add(EpVideo(imageFile1.getPath())) // Video 1 Example
                epVideos.add(EpVideo(imageFile2.getPath())) // Video 2 Exmaple
                epVideos.add(EpVideo(imageFile3.getPath())) // Video 2 Exmaple
                val outputOption =
                    EpEditor.OutputOption(outputVideoPath.absolutePath) //Output
                outputOption.setWidth(720) // output video width, default 480
                outputOption.setHeight(1080)
                outputOption.frameRate =
                    25 // output video frame rate, default 30

                EpEditor.merge(
                    epVideos,
                    outputOption,
                    object : OnEditorListener {
                        override fun onSuccess() {
                            Log.e("LLL_Progress", "onSuccess()")
                            mActivity?.runOnUiThread {
                                activitySelectBinding.rlLoading.visibility = View.GONE
                            }

                            var intent = Intent(mActivity, EditActivity::class.java)
                            mActivity?.startActivity(intent)
                            /* val imageFile3: ImageFile = Constant.selectedImages[0]
                             val outputVideoPath1 =
                                 File(outputPath, "Vid_002.mp4")

                             val epVideos = ArrayList<EpVideo>()
                             epVideos.add(EpVideo(imageFile3.getPath())) // Video 1 Example
                             epVideos.add(EpVideo(outputVideoPath.absolutePath)) // Video 2 Exmaple
                             val outputOption =
                                 EpEditor.OutputOption(outputVideoPath1.absolutePath) //Output
                             outputOption.setWidth(720) // output video width, default 480
                             outputOption.setHeight(1280)
                             outputOption.frameRate =
                                 25 // output video frame rate, default 30

                             EpEditor.merge(
                                 epVideos,
                                 outputOption,
                                 object : OnEditorListener {
                                     override fun onSuccess() {
                                         Log.d("LLL_Progress", "onSuccess()")
                                     }

                                     override fun onFailure() {
                                         Log.d("LLL_Progress", "onFailure()")
                                     }


                                     override fun onProgress(progress: Float) {
                                         Log.d("LLL_Progress", "$progress")
                                     }

                                 })*/
                        }

                        override fun onFailure() {
                            Log.d("LLL_Progress", "onFailure()")
                        }


                        override fun onProgress(progress: Float) {
                            Log.d("LLL_Progress", "$progress")
                        }

                    })
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cropTrimAdapter = CropTrimAdapter(Constant.selectedImages, this, object :
            CropTrimAdapter.ItemClickListener {
            override fun onItemClick(position: Int) {
                newPos = position
                val file = Constant.selectedImages[position]
                if (!file.isVideo()) {
                    val intent = Intent(this@SelectActivity, CropActivity::class.java)
                    intent.putExtra("position", position)
                    startActivity(intent)
                    finish()
                } else {
                    TrimVideo.activity(Uri.fromFile(File(file.getPath()!!)).toString())
                        .setHideSeekBar(true)
                        .start(this@SelectActivity, startForResult)
                }
            }
        })

        val gridLayoutManager = GridLayoutManager(this, 4)
        activitySelectBinding.rvImages.layoutManager = gridLayoutManager
        activitySelectBinding.rvImages.isScrollContainer = true
        activitySelectBinding.rvImages.layoutAnimation = null
        activitySelectBinding.rvImages.itemAnimator = null
        activitySelectBinding.rvImages.adapter = cropTrimAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        ImageActivity.isGrant = false
        Constant.selectedImages.clear()
        val intent = Intent(this@SelectActivity, ImageActivity::class.java)
        startActivity(intent)
        finish()
    }

}