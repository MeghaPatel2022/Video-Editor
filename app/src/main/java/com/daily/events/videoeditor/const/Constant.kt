package com.daily.events.videoeditor.const

import android.annotation.SuppressLint
import com.daily.events.videoeditor.model.Directory
import com.daily.events.videoeditor.model.ImageFile
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

class Constant {

    companion object {
        lateinit var NAV_SELECTED_ITEM: String
        var pagerPosition: Int = 0

        var albumList: MutableList<Directory<ImageFile>?>? = ArrayList()
        var selectedImages: MutableList<ImageFile> = ArrayList()


        fun convertTimeDateModified(time: Long): String? {
            val date = Date(time * 1000)
            @SuppressLint("SimpleDateFormat") val format: Format = SimpleDateFormat("dd MMM yyyy")
            return format.format(date)
        }

        fun extractPathWithoutSeparator(url: String): String {
            return url.substring(0, url.lastIndexOf("/"))
        }

    }

}