package com.example.catapi

import android.util.Log
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.example.catapi.utils.AssetsFileSource
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.hamcrest.CoreMatchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URL

class WiremockExtensionsVerificationChecks {
    @get:Rule
    var rule: WireMockRule = WireMockRule(
        wireMockConfig()
            .fileSource(AssetsFileSource(subDirectoryName = "wiremock-checks"))
    )

    init {
        Log.d("dsds", "init")
    }

    @Before
    fun setUp() {
        Log.d("dsds", "setup")
    }

    @Test
    fun loadsTextFileUsingPathAsNameNotQueryParams() {
        WireMock.stubFor(
            get(anyUrl())
                .willReturn(
                    aResponse()
                        .withBodyFile("hello")
                )
        )

        val result = URL("http://localhost:${rule.port()}/hello").readText()
        assertThat(result, `is`("hi there!5"))
    }

    @Test
    fun loadsTextFileUsingPathAsNameNotQueryParams2() {
        val mappings = URL("http://localhost:${rule.port()}/__admin/mappings").readText()
        Log.d("dsds", "mappings from wiremock: $mappings")
        val result = URL("http://localhost:${rule.port()}/hellomapping").readText()
        assertThat(result, `is`("Hello world!"))
    }
}
