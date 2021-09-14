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
        Log.d("dsds", "Asset.name $name")
        return name
    }

    override fun getStream(): InputStream =
        try {
            Log.d("dsds", "Asset.getStream $assetPath")

//            Log.d("dsds", "stack ${buildStackTraceString(Thread.currentThread().stackTrace)}")


            // TODO find out why mapping files require /name after assetPath but __files do not
            assets.open("$assetPath/$name")
        } catch (e: FileNotFoundException) { // TODO find out why are mappings not using this class
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
        Log.d("dsds", "Asset.getPath $assetPath")
        return assetPath
    }

    override fun readContents(): ByteArray {
        Log.d("dsds", "Asset.readContents $stream")
        return stream.readBytes()
    }

    override fun readContentsAsString(): String {
        Log.d("dsds", "Asset.readContentsAsString ${readContents()}")
        return String(readContents())
    }

    override fun toString() = "BinaryFileInAndroidAssetFolder $assetPath"

    companion object {
        private const val RAW_SCHEME = "raw"
        private fun uri(name: String) = URI("$RAW_SCHEME://$name")
    }
}
