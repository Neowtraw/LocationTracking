package com.codingub.locationtracking.utils

import android.content.res.AssetFileDescriptor
import com.codingub.locationtracking.MainApplication

object AssetUtil {

    private fun assetPath(filePath: String, fileExt: String): String {
        return "file:///android_asset/$filePath.$fileExt"
    }

    fun soundAfd(name: String): AssetFileDescriptor {
        val path = "sound/$name.${FileExtension.OGG}"

        return MainApplication.getInstance().assets.openFd(path)
    }

    private object FileExtension {
        const val PNG: String = "png"
        const val OGG: String = "ogg"
    }
}