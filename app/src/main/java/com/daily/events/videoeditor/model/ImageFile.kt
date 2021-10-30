package com.daily.events.videoeditor.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import java.io.Serializable

open class ImageFile : BaseFile(), Parcelable, Serializable {
    var orientation //0, 90, 180, 270
            = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(getId())
        dest.writeString(getName())
        dest.writeString(getPath())
        dest.writeLong(getSize())
        dest.writeString(getBucketId())
        dest.writeString(getBucketName())
        dest.writeString(getDate())
        dest.writeByte((if (isSelected()) 1 else 0).toByte())
        dest.writeInt(orientation)
    }

    override fun describeContents(): Int {
        return 0
    }

    @JvmName("getOrientation1")
    fun getOrientation(): Int {
        return orientation
    }

    @JvmName("setOrientation1")
    fun setOrientation(orientation: Int) {
        this.orientation = orientation
    }


    companion object {
        val CREATOR: Creator<ImageFile?> by lazy {
            object : Creator<ImageFile?> {
                override fun newArray(size: Int): Array<ImageFile?> {
                    return arrayOfNulls(size)
                }

                override fun createFromParcel(`in`: Parcel): ImageFile {
                    val file = ImageFile()
                    file.setId(`in`.readLong())
                    file.setName(`in`.readString())
                    file.setPath(`in`.readString())
                    file.setSize(`in`.readLong())
                    file.setBucketId(`in`.readString())
                    file.setBucketName(`in`.readString())
                    file.setDate(`in`.readString())
                    file.setSelected(`in`.readByte().toInt() != 0)
                    file.orientation = `in`.readInt()
                    return file
                }
            }
        }
    }
}
