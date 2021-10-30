package com.daily.events.videoeditor.adapter

import android.app.Activity
import android.os.Build
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
import com.daily.events.videoeditor.databinding.ListSelectFileBinding
import com.daily.events.videoeditor.model.ImageFile
import java.util.*

class SelectedImageAdapter(private var activity: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var imagefiles: MutableList<ImageFile> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder: RecyclerView.ViewHolder
        val listSelectFileBinding: ListSelectFileBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_select_file,
            parent,
            false
        )
        holder = ImageClassView(listSelectFileBinding)
        return holder
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val directory: ImageFile = imagefiles[position]

        val holder = viewHolder as ImageClassView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.listSelectFileBinding.mImage.clipToOutline = true
        }

        if (directory.isVideo()) {
            holder.listSelectFileBinding.mVid.visibility = View.VISIBLE
        } else {
            holder.listSelectFileBinding.mVid.visibility = View.GONE
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
                .into(holder.listSelectFileBinding.mImage)
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
                .into(holder.listSelectFileBinding.mImage)
        }
    }

    override fun getItemCount(): Int {
        return imagefiles.size
    }

    fun add(imgMain1DownloadList: ImageFile) {
        imagefiles.add(imgMain1DownloadList)
        notifyItemInserted(imagefiles.size)
    }

    fun addAll(imgMain1DownloadList: MutableList<ImageFile>) {
        imagefiles.addAll(imgMain1DownloadList)
        notifyDataSetChanged()
    }

    fun clear() {
        imagefiles.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        imagefiles.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ImageClassView(val listSelectFileBinding: ListSelectFileBinding) :
        RecyclerView.ViewHolder(listSelectFileBinding.root)

}