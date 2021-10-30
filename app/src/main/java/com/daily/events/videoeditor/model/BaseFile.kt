package com.daily.events.videoeditor.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

open class BaseFile : Parcelable {
    private var id: Long = 0
    private var dateTitle: String? = null
    private var position = 0
    var isDirectory = false
    private var name: String? = null
    private var path: String? = null
    private var size //byte
            : Long = 0
    private var bucketId //Directory ID
            : String? = null
    private var bucketName //Directory Name
            : String? = null
    private var date //Added Date
            : String? = null
    private var isSelected = false
    private var isVideo = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseFile) return false
        return path == other.path
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    fun getDateTitle(): String? {
        return dateTitle
    }

    fun setDateTitle(dateTitle: String?) {
        this.dateTitle = dateTitle
    }

    fun getPosition(): Int {
        return position
    }

    fun setPosition(position: Int) {
        this.position = position
    }

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getPath(): String? {
        return path
    }

    fun setPath(path: String?) {
        this.path = path
    }

    fun getSize(): Long {
        return size
    }

    fun setSize(size: Long) {
        this.size = size
    }

    fun getBucketId(): String? {
        return bucketId
    }

    fun setBucketId(bucketId: String?) {
        this.bucketId = bucketId
    }

    fun getBucketName(): String? {
        return bucketName
    }

    fun setBucketName(bucketName: String?) {
        this.bucketName = bucketName
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }

    fun isVideo(): Boolean {
        return isVideo
    }

    fun setVideo(video: Boolean) {
        isVideo = video
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(dateTitle)
        dest.writeInt(position)
        dest.writeString(name)
        dest.writeString(path)
        dest.writeLong(size)
        dest.writeString(bucketId)
        dest.writeString(bucketName)
        dest.writeString(date)
        dest.writeByte((if (isSelected) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField
        val CREATOR: Creator<BaseFile?> = object : Creator<BaseFile?> {
            override fun newArray(size: Int): Array<BaseFile?> {
                return arrayOfNulls(size)
            }

            override fun createFromParcel(`in`: Parcel): BaseFile? {
                val file = BaseFile()
                file.id = `in`.readLong()
                file.name = `in`.readString()
                file.path = `in`.readString()
                file.size = `in`.readLong()
                file.dateTitle = `in`.readString()
                file.position = `in`.readInt()
                file.bucketId = `in`.readString()
                file.bucketName = `in`.readString()
                file.date = `in`.readString()
                file.isSelected = `in`.readByte().toInt() != 0
                return file
            }
        }
    }
}