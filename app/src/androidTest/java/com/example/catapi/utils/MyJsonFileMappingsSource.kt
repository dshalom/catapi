package com.example.catapi.utils

import com.github.tomakehurst.wiremock.common.*
import com.github.tomakehurst.wiremock.common.AbstractFileSource.byFileExtension
import com.github.tomakehurst.wiremock.common.Json.writePrivate
import com.github.tomakehurst.wiremock.standalone.MappingFileException
import com.github.tomakehurst.wiremock.standalone.MappingsSource
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import com.github.tomakehurst.wiremock.stubbing.StubMappingCollection
import com.github.tomakehurst.wiremock.stubbing.StubMappings
import com.google.common.collect.Iterables.any
import com.google.common.collect.Iterables.filter
import java.util.*
import kotlin.collections.HashMap


class MyJsonFileMappingsSource(private val mappingsFileSource: FileSource) : MappingsSource {
    private val fileNameMap: MutableMap<UUID, StubMappingFileMetadata>
    override fun save(stubMappings: List<StubMapping>) {
        for (mapping in stubMappings) {
            if (mapping != null && mapping.isDirty) {
                save(mapping)
            }
        }
    }

    override fun save(stubMapping: StubMapping) {
        var fileMetadata = fileNameMap[stubMapping.id]
        if (fileMetadata == null) {
            fileMetadata = StubMappingFileMetadata(SafeNames.makeSafeFileName(stubMapping), false)
        }
        if (fileMetadata.multi) {
            throw NotWritableException("Stubs loaded from multi-mapping files are read-only, and therefore cannot be saved")
        }
        mappingsFileSource.writeTextFile(fileMetadata.path, writePrivate(stubMapping))
        fileNameMap[stubMapping.id] = fileMetadata
        stubMapping.isDirty = false
    }

    override fun remove(stubMapping: StubMapping) {
        val fileMetadata = fileNameMap[stubMapping.id]
        if (fileMetadata!!.multi) {
            throw NotWritableException("Stubs loaded from multi-mapping files are read-only, and therefore cannot be removed")
        }
        mappingsFileSource.deleteFile(fileMetadata.path)
        fileNameMap.remove(stubMapping.id)
    }

    override fun removeAll() {
        if (anyFilesAreMultiMapping()) {
            throw NotWritableException("Some stubs were loaded from multi-mapping files which are read-only, so remove all cannot be performed")
        }
        for (fileMetadata in fileNameMap.values) {
            mappingsFileSource.deleteFile(fileMetadata.path)
        }
        fileNameMap.clear()
    }

    private fun anyFilesAreMultiMapping(): Boolean {
        return any(
            fileNameMap.values
        ) { input -> input!!.multi }
    }

    override fun loadMappingsInto(stubMappings: StubMappings) {
        if (!mappingsFileSource.exists()) {
            return
        }
        val mappingFiles: Iterable<TextFile> =
            filter(mappingsFileSource.listFilesRecursively(), byFileExtension("json"))
        for (mappingFile in mappingFiles) {
            try {
                val stubCollection = Json.read(
                    mappingFile.readContentsAsString(),
                    StubMappingCollection::class.java
                )
                for (mapping in stubCollection.mappingOrMappings) {
                    mapping.isDirty = false
                    stubMappings.addMapping(mapping)
                    val fileMetadata =
                        StubMappingFileMetadata(mappingFile.path, stubCollection.isMulti)
                    fileNameMap[mapping.id] = fileMetadata
                }
            } catch (e: JsonException) {
                throw MappingFileException(mappingFile.path, e.errors.first().detail)
            }
        }
    }

    private class StubMappingFileMetadata(val path: String, val multi: Boolean)

    init {
        fileNameMap = HashMap()
    }
}