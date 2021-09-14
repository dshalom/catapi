package com.example.catapi.utils

import android.content.res.AssetManager
import android.util.Log
import com.github.tomakehurst.wiremock.common.TextFile
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI

class Asset(private val name: String, private val assetPath: String, private val assets: AssetManager) :
    TextFile(uri(assetPath)) {

    override fun name(): String {
        return name
    }

    override fun getStream(): InputStream =
        try {
            assets.open("$assetPath${if (name.isEmpty()) "" else "/"}$name")
        } catch (e: FileNotFoundException) {
            Log.d("dsds", "Asset.getStream exception ${e.message}")
            throw Exception()
        }

    private fun buildStackTraceString(elements: Array<StackTraceElement>?): String? {
        val sb = StringBuilder()
        if (elements != null && elements.size > 0) {
            for (element in elements) {
                sb.append(element.toString())
                sb.append("\n")
            }
        }
        return sb.toString()
    }

    override fun getPath(): String {
        return assetPath
    }

    override fun readContents(): ByteArray {
        return stream.readBytes()
    }

    override fun readContentsAsString(): String {
        return String(readContents())
    }

    override fun toString() = "BinaryFileInAndroidAssetFolder $assetPath"

    companion object {
        private const val RAW_SCHEME = "raw"
        private fun uri(name: String) = URI("$RAW_SCHEME://$name")
    }
}
