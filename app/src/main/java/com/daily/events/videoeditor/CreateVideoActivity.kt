package com.daily.events.videoeditor

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.daily.events.videoeditor.const.Constant
import com.daily.events.videoeditor.databinding.ActivityCreateVideoBinding
import com.daily.events.videoeditor.model.Directory
import com.daily.events.videoeditor.model.ImageFile
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class CreateVideoActivity : BaseActivity() {

    var isGrant: Boolean = false
    lateinit var activityCreateVideoBinding: ActivityCreateVideoBinding

    override fun permissionGranted() {

    }

    override fun onResume() {
        super.onResume()
        val isGranted = EasyPermissions.hasPermissions(this, *perms1)
        if (!isGrant) {
            isGrant = true
            if (isGranted) LoadAlbumTask().execute()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateVideoBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_create_video)

        mActivity = this@CreateVideoActivity
        activityCreateVideoBinding.imgGallery.setOnClickListener {
            imageFiles[0].getBucketName()?.let { it1 -> Log.e("LLLL_Name: ", it1) }
            if (imageFiles.size > 0) {
                val intent = Intent(this, ImageActivity::class.java)
                intent.putExtra("BucketName", imageFiles[0].getBucketId())
                startActivity(intent)
            }
        }
    }

    class LoadAlbumTask : AsyncTask<Void, Void, MutableList<Directory<ImageFile>?>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            imageFiles.clear()
            Constant.albumList?.clear()
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
            val selectionArgs: Array<String> =
                arrayOf("image/jpeg", "image/png", "image/jpg", "image/gif")
            val data = mActivity?.contentResolver?.query(
                MediaStore.Files.getContentUri("external"),
                FILE_PROJECTION,
                selection,
                null,
                MediaColumns.DATE_ADDED + " DESC"
            )
            val directories: MutableList<Directory<ImageFile>?> = ArrayList<Directory<ImageFile>?>()
            val directories1: MutableList<Directory<ImageFile>> = ArrayList()

            if (data!!.position != -1) {
                data.moveToPosition(-1)
            }

            while (data.moveToNext()) {
                //Create a File instance
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
                if (!img.getBucketName()?.startsWith(".")!!) {
                    //Create a Directory
                    val directory: Directory<ImageFile> = Directory()
                    directory.setId(img.getBucketId())
                    directory.setName(img.getBucketName())
                    directory.setPath(Constant.extractPathWithoutSeparator(img.getPath()!!))
                    if (!directories1.contains(directory)) {
                        directory.addFile(img)
                        directories.add(directory)
                        directories1.add(directory)
                    } else {
                        directories[directories.indexOf(directory)]?.addFile(img)
                    }
                    imageFiles.add(img)
                }
            }
            return directories
        }

        override fun onPostExecute(result: MutableList<Directory<ImageFile>?>?) {
            super.onPostExecute(result)
            Log.d(
                TAG,
                "onPostExecute: " + "done"
            )
            Constant.albumList = result
        }
    }

    companion object {
        val TAG: String = CreateVideoActivity::class.java.name
        var mActivity: Activity? = null

        val imageFiles: MutableList<ImageFile> = ArrayList()

    }

}