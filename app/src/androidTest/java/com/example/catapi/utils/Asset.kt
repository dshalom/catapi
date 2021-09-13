package com.example.catapi.utils

import android.content.res.AssetManager
import com.github.tomakehurst.wiremock.common.TextFile
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URI

class Asset(private val assetPath: String, private val assets: AssetManager) : TextFile(uri(assetPath)) {

    override fun name() = assetPath

    override fun getStream(): InputStream =
            try { assets.open(assetPath) } catch (e: FileNotFoundException) {
                    throw Exception()
                }

    override fun getPath() = assetPath

    override fun readContents() = stream.readBytes()

    override fun readContentsAsString() = String(readContents())

    override fun toString() = "BinaryFileInAndroidAssetFolder $assetPath"

    companion object {
        private const val RAW_SCHEME = "raw"
        private fun uri(name: String) = URI("$RAW_SCHEME://$name")
    }
}
