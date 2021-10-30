package com.daily.events.videoeditor.model

import java.util.*

open class Directory<T : ImageFile> : Comparable<Any?> {
    var id: String? = null
    var name: String? = null
    var path: String? = null
    var isDirectory = false
    private var files: MutableList<T> = ArrayList()
    fun getFiles(): List<T> {
        return files
    }

    fun setFiles(files: MutableList<T>) {
        this.files = files
    }

    fun addFile(file: T) {
        files.add(file)
    }

    @JvmName("getId1")
    fun getId(): String? {
        return id
    }

    @JvmName("setId1")
    fun setId(id: String?) {
        this.id = id
    }

    @JvmName("getName1")
    fun getName(): String? {
        return name
    }

    @JvmName("setName1")
    fun setName(name: String?) {
        this.name = name
    }

    @JvmName("getPath1")
    fun getPath(): String? {
        return path
    }

    @JvmName("setPath1")
    fun setPath(path: String?) {
        this.path = path
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Directory<*>) return false
        return name == other.name
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }

    override fun compareTo(other: Any?): Int {
        return getFiles().size.compareTo((other as Directory<*>).getFiles().size)
    }
}
