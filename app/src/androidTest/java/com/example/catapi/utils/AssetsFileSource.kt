package com.example.catapi.utils

import android.content.Context
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
        return getTextFileNamed(name)
    }

    override fun getTextFileNamed(name: String): TextFile? {
        val pathSep = with(name) {
            when {
                startsWith(URI_PATH_SEPARATOR) -> ""
                else -> URI_PATH_SEPARATOR
            }
        }
        val assetPath = subDirectoryName + pathSep + name
        return Asset(assetPath, context.assets)
    }

    override fun createIfNecessary() {}

    override fun child(subDirectoryName: String) = AssetsFileSource(
        subDirectoryName = this.subDirectoryName +
                URI_PATH_SEPARATOR +
                subDirectoryName.replace("__".toRegex(), "")
    )

    override fun getPath(): String = subDirectoryName

    override fun getUri() = null

    override fun listFilesRecursively() =
        ArrayList<TextFile>().also { collectAssetFiles(subDirectoryName, it) }

    private fun collectAssetFiles(path: String, collector: MutableList<TextFile>) {
        context.assets.list(path)
            ?.onEach { collector += TextFile(URI("asset://$path/$it")) }
            ?.forEach { collectAssetFiles("$path/$it", collector) }
    }

    override fun writeTextFile(name: String, contents: String) {}

    override fun writeBinaryFile(name: String, contents: ByteArray) {}

    override fun exists() = true

    override fun deleteFile(name: String) {}

    companion object {
        const val URI_PATH_SEPARATOR = "/"
    }
}
