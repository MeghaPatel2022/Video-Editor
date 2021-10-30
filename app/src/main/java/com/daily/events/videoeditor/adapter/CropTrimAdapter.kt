package com.daily.events.videoeditor.adapter

import android.app.Activity
import android.os.Build
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.daily.events.videoeditor.R
import com.daily.events.videoeditor.databinding.ListImagesBinding
import com.daily.events.videoeditor.model.ImageFile
import java.util.*

class CropTrimAdapter(
    files: MutableList<ImageFile>,
    private var activity: Activity,
    var itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var objects: MutableList<ImageFile> = files
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        val imageViewBinding: ListImagesBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_images,
            parent,
            false
        )
        holder = ImageClassView(imageViewBinding)
        return holder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val directory: ImageFile = objects[position]

        val holder = viewHolder as ImageClassView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imageViewBinding.mImage.clipToOutline = true
        }

        val file: ImageFile = objects[position]


        if (file.isVideo()) {
            holder.imageViewBinding.mVid.visibility = View.VISIBLE
        } else {
            holder.imageViewBinding.mVid.visibility = View.GONE
        }

        val options = RequestOptions()
        if (directory.getPath()!!.endsWith(".PNG") || directory.getPath()!!.endsWith(".png")) {
            Glide.with(activity)
                .load(directory.getPath())
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW)
                        .format(DecodeFormat.PREFER_ARGB_8888)
                )
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .centerCrop()
                .into(holder.imageViewBinding.mImage)
        } else {
            Glide.with(activity)
                .load(directory.getPath())
                .apply(
                    options.centerCrop()
                        .skipMemoryCache(true)
                        .priority(Priority.LOW)
                )
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(0.5f)
                .centerCrop()
                .into(holder.imageViewBinding.mImage)
        }
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return objects.size
    }

    override fun getItemViewType(position: Int): Int {
        val directory: ImageFile = objects[position]
        return if (!directory.isDirectory) {
            ITEM
        } else {
            DATE_TYPE
        }
    }

    fun addAll(imgMain1DownloadList: List<ImageFile>?) {
        objects.addAll(imgMain1DownloadList!!)
    }

    fun clear() {
        objects.clear()
        notifyDataSetChanged()
    }

    //Define your Interface method here
    interface ItemClickListener {
        fun onItemClick(position: Int)
    }


    fun getFormattedDate(smsTimeInMilis: Long): String {
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = smsTimeInMilis
        val now = Calendar.getInstance()
        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "dd MMM yyyy"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now[Calendar.DATE] == smsTime[Calendar.DATE]) {
            "Today "
        } else if (now[Calendar.DATE] - smsTime[Calendar.DATE] == 1) {
            "Yesterday "
        } else if (now[Calendar.YEAR] == smsTime[Calendar.YEAR]) {
            DateFormat.format(dateTimeFormatString, smsTime).toString()
        } else {
            DateFormat.format("dd MMM yyyy", smsTime).toString()
        }
    }


    inner class ImageClassView(val imageViewBinding: ListImagesBinding) :
        RecyclerView.ViewHolder(imageViewBinding.root)

    companion object {
        const val DATE_TYPE = 1
        const val ITEM = 0
    }

}