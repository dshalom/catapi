package com.example.catapi.utils

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.example.catapi.utils.ConnectedTestsDefaults.DEFAULT_SUBDIRECTORY_NAME
import com.github.tomakehurst.wiremock.common.BinaryFile
import com.github.tomakehurst.wiremock.common.FileSource
import com.github.tomakehurst.wiremock.common.TextFile
import java.net.URI

class AssetsFileSource(
    private val context: Context = InstrumentationRegistry.getInstrumentation().context,
    private val subDirectoryName: String = DEFAULT_SUBDIRECTORY_NAME
) : FileSource {

    override fun getBinaryFileNamed(name: String): BinaryFile? {
        Log.d("dsds", "getBinaryFileNamed $name")
        return getTextFileNamed(name)
    }

    override fun getTextFileNamed(name: String): TextFile? {
        Log.d("dsds", "getTextFileNamed $name")
        val pathSep = with(name) {
            when {
                startsWith(URI_PATH_SEPARATOR) -> ""
                else -> URI_PATH_SEPARATOR
            }
        }
        val assetPath = subDirectoryName + pathSep + name
        return Asset(name, assetPath, context.assets)
    }

    override fun createIfNecessary() {}

    override fun child(subDirectoryName: String): AssetsFileSource {
        Log.d("dsds", "child $subDirectoryName")
        return AssetsFileSource(
            subDirectoryName = this.subDirectoryName +
                    URI_PATH_SEPARATOR +
                    subDirectoryName.replace("__".toRegex(), "")
        )
    }

    override fun getPath(): String {
        Log.d("dsds", "getPath $subDirectoryName")
        return subDirectoryName
    }

    override fun getUri(): Nothing? {
        // TODO do we need to define this? asset://.../mappings
        Log.d("dsds", "getUri")
        return null
    }

    override fun listFilesRecursively(): ArrayList<TextFile> {

        Log.d("dsds", "listFilesRecursively")
        return ArrayList<TextFile>().also {
            collectAssetFiles(subDirectoryName, it)
        }
    }

    private fun collectAssetFiles(path: String, collector: MutableList<TextFile>) {
        Log.d("dsds", "collectAssetFiles $path")
        context.assets.list(path)
            ?.onEach {
                val uri = "raw://$path/$it"
                Log.d("dsds", "collectAssetFiles 2 $uri $it")
//                collector += TextFile(URI(uri))
                collector += Asset(it, path, context.assets)
            } // "$RAW_SCHEME://$name"
            ?.forEach {
                val path1 = "$path/$it"
                Log.d("dsds", "collectAssetFiles 3 $path1")
                collectAssetFiles(path1, collector)
            }
    }

    override fun writeTextFile(name: String, contents: String) {}

    override fun writeBinaryFile(name: String, contents: ByteArray) {}

    override fun exists() = true

    override fun deleteFile(name: String) {}

    companion object {
        const val URI_PATH_SEPARATOR = "/"
    }
}
