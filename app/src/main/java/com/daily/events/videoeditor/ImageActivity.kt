package com.daily.events.videoeditor

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daily.events.videoeditor.adapter.ImageAdapter
import com.daily.events.videoeditor.adapter.SelectedImageAdapter
import com.daily.events.videoeditor.const.Constant
import com.daily.events.videoeditor.databinding.ActivityImageBinding
import com.daily.events.videoeditor.model.Directory
import com.daily.events.videoeditor.model.ImageFile
import es.dmoral.toasty.Toasty
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.ArrayList

class ImageActivity : BaseActivity() {

    private lateinit var gridLayoutManager: GridLayoutManager

    companion object {
        var isGrant: Boolean = false

        var albumPosition: String = "0"
        val TAG: String = ImageActivity::class.java.name
        lateinit var activityImageBinding: ActivityImageBinding
        var mActivity: Activity? = null

        var imageFiles: MutableList<ImageFile> = ArrayList()
        var dateList = ArrayList<String>()
        lateinit var imageAdapter: ImageAdapter
        lateinit var selectImageAdapter: SelectedImageAdapter
    }

    override fun permissionGranted() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_image)
        mActivity = this@ImageActivity
        activityImageBinding.imgClose.setOnClickListener {
            onBackPressed()
        }

        activityImageBinding.imgNext.setOnClickListener {
            val intent = Intent(this@ImageActivity, SelectActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val isGranted = EasyPermissions.hasPermissions(this, *perms1)
        if (!isGrant) {
            isGrant = true
            if (isGranted) {
                gridLayoutManager = GridLayoutManager(this, 4)
                activityImageBinding.rvImages.layoutManager = gridLayoutManager
                activityImageBinding.rvImages.isScrollContainer = true
                activityImageBinding.rvImages.layoutAnimation = null
                activityImageBinding.rvImages.itemAnimator = null

                activityImageBinding.rvSelectedImages.layoutManager =
                    LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
                activityImageBinding.rvSelectedImages.isScrollContainer = true
                activityImageBinding.rvSelectedImages.layoutAnimation = null
                activityImageBinding.rvSelectedImages.itemAnimator = null

                selectImageAdapter = SelectedImageAdapter(this)
                activityImageBinding.rvSelectedImages.adapter = selectImageAdapter

                if (Constant.selectedImages.size > 0) {
                    mActivity?.runOnUiThread {
                        selectImageAdapter.addAll(Constant.selectedImages)
                    }
                }

                imageAdapter = ImageAdapter(ArrayList(), this, object :
                    ImageAdapter.ItemClickListener {
                    override fun onItemClick(position: Int) {
                        val file = imageFiles[position]
                        if (file.isSelected()) {
                            file.setSelected(false)
                            for (i in 0 until Constant.selectedImages.size) {
                                if (file.getName() == Constant.selectedImages[i].getName()) {
                                    Log.e(
                                        "LLL_Path_Select: ",
                                        Constant.selectedImages[i].getName() + "   Path: " + file.getPath()
                                    )
                                    selectImageAdapter.remove(i)
                                    break
                                }
                            }
                            Constant.selectedImages.remove(file)
                            runOnUiThread {
                                imageAdapter.notifyItemChanged(position)
                            }
                        } else {
                            file.setSelected(true)
                            if (Constant.selectedImages.size >= 3) {
                                Toasty.info(
                                    this@ImageActivity,
                                    "Allow only 3 items.", Toasty.LENGTH_SHORT
                                ).show()
                            } else {
                                Constant.selectedImages.add(file)
                                selectImageAdapter.add(file)
                                runOnUiThread {
                                    activityImageBinding.rvSelectedImages.smoothScrollToPosition(
                                        Constant.selectedImages.size - 1
                                    )
                                }
                            }
                        }
                        imageAdapter.notifyItemChanged(position)
                        if (Constant.selectedImages.size > 0) {
                            val count = "Count " + Constant.selectedImages.size
                            activityImageBinding.tvCount.text = count
                        }

                        var video = 0
                        var image = 0
                        for (i in 0 until Constant.selectedImages.size) {
                            val imageFile = Constant.selectedImages[i]
                            if (imageFile.isVideo()) {
                                video++
                            } else {
                                image++
                            }
                        }

                        if (image > 1 && video > 1)
                            activityImageBinding.tvCount.text = "$image Photos, $video Videos"
                        else if (image > 1 && video <= 1) {
                            activityImageBinding.tvCount.text = "$image Photos, $video Video"
                        } else if (image <= 1 && video > 1) {
                            activityImageBinding.tvCount.text = "$image Photo, $video Videos"
                        } else {
                            activityImageBinding.tvCount.text = "$image Photo, $video Video"
                        }
                    }
                })
                activityImageBinding.rvImages.adapter = imageAdapter

                val languages: ArrayList<String> = ArrayList()
                for (i in 0..(Constant.albumList?.size?.minus(1)!!)) {
                    Constant.albumList?.get(i)?.getName()?.let { languages.add(it) }
                }

                // access the spinner
                val adapter = ArrayAdapter(
                    this,
                    R.layout.spinner_item, languages
                )
                activityImageBinding.spinnerAlbum.adapter = adapter

                activityImageBinding.spinnerAlbum.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View, position: Int, id: Long
                    ) {
                        runOnUiThread {
                            albumPosition = languages[position]
                            LoadImages().execute()
                        }

                        Toast.makeText(
                            this@ImageActivity,
                            "Selected Item  " +
                                    "" + languages[position], Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // write code to perform some action
                    }
                }

                if (Constant.albumList!!.size > 0) {
                    albumPosition = Constant.albumList!![0]?.name.toString()
                    LoadImages().execute()
                }
            }
        }
    }

    internal open class LoadImages :
        AsyncTask<Void?, Void?, MutableList<Directory<ImageFile>?>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            mActivity?.runOnUiThread {
                imageFiles.clear()
                dateList.clear()
                imageAdapter.clear()
            }
        }

        override fun doInBackground(vararg params: Void?): MutableList<Directory<ImageFile>?> {

            val FILE_PROJECTION = arrayOf( //Base File
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.ORIENTATION
            )
            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

            val data = CreateVideoActivity.mActivity?.contentResolver?.query(
                MediaStore.Files.getContentUri("external"),
                FILE_PROJECTION,
                selection,
                null,
                MediaColumns.DATE_ADDED + " DESC"
            )

            val directories: MutableList<Directory<ImageFile>?> = ArrayList()
            val directories1: MutableList<Directory<ImageFile>> = ArrayList()

            if (data!!.position != -1) {
                data.moveToPosition(-1)
            }

            while (data.moveToNext()) {
                // Create a File instance
                val img = ImageFile()
                img.setId(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
                img.setName(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)))
                img.setPath(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)))
                img.setSize(data.getLong(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)))
                img.setBucketId(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_ID)))
                img.setBucketName(data.getString(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)))
                img.setDate(
                    Constant.convertTimeDateModified(
                        data.getLong(
                            data.getColumnIndexOrThrow(
                                MediaColumns.DATE_ADDED
                            )
                        )
                    )
                )
                img.setOrientation(data.getInt(data.getColumnIndexOrThrow(MediaStore.Files.FileColumns.ORIENTATION)))

                val extension: String = img.getPath()?.substring(img.getPath()!!.lastIndexOf("."))!!

                if (extension == ".mp4" ||
                    extension == ".mkv" ||
                    extension == ".3gp" ||
                    extension == ".webm" ||
                    extension == ".wav"
                ) {
                    img.setVideo(true)
                } else {
                    img.setVideo(false)
                }

                // Create a Directory
                val directory: Directory<ImageFile> = Directory()
                directory.setId(img.getBucketId())
                directory.setName(img.getBucketName())
                directory.setPath(img.getPath())
                if (!directories1.contains(directory)) {
                    directory.addFile(img)
                    directories.add(directory)
                    directories1.add(directory)
                } else {
                    directories[directories.indexOf(directory)]?.addFile(img)
                }
            }

            return directories
        }

        override fun onPostExecute(directories: MutableList<Directory<ImageFile>?>?) {
            super.onPostExecute(directories)
            Log.e(
                "LLLLL_Size: ",
                "onPostExecute: " + "done ${imageFiles.size}"
            )

            for (i in 0 until directories?.size!!) {
                if (directories[i]?.name.equals(albumPosition)) {
                    imageFiles = directories[i]?.getFiles() as MutableList<ImageFile>
                }
            }
            albumPosition.let { Log.e("LLL_Bucket: ", it) }
            mActivity?.runOnUiThread {
                imageAdapter.addAll(imageFiles)
                imageAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constant.selectedImages.clear()
        imageFiles.clear()
        imageAdapter.clear()
    }
}