package com.example.catapi

import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.catapi.utils.AssetsFileSource
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.matching.RequestPattern
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.URL


@RunWith(AndroidJUnit4::class)
class CatImageLoadingTest {

    // private lateinit var wireMockServer: WireMockServer
    lateinit var scenario: ActivityScenario<MainActivity>

    lateinit var wireMock: WireMock

    @Before
    fun setUp() {

        wireMock = WireMock("localhost", 8080)
        wireMock.resetMappings()

    }

    @After
    fun tearDown() {
   //     wireMock.shutdown()
        scenario.close()
    }

    @Test
    fun loadCatImage1() {

        wireMock.register(
            stubFor(
                get(urlPathMatching("/v1/images/search"))
                    .willReturn(
                        ok()
                            .withHeader("Content-Type", "text/json")
                            .withBody(
                                "[\n" +
                                        "{\n" +
                                        "\"breeds\": [],\n" +
                                        "\"id\": \"bfc\",\n" +
                                        "\"url\": \"https://cdn2.thecatapi.com.bingo/images/bfc.jpg\",\n" +
                                        "\"width\": 501,\n" +
                                        "\"height\": 333666\n" +
                                        "}\n" +
                                        "]"
                            )
                    )
            )
        )


//        val mappings = URL("http://localhost:${wireMockServer.port()}/__admin/mappings").readText()
//        Log.d("dsds", "mappings from wiremock: $mappings")

        scenario = launchActivity()

        onView(withId(R.id.iv))
            .check(matches(isDisplayed()));
    }

//    @Test
//    fun loadCatImage2() {
//        val mappings = URL("http://localhost:${wireMockServer.port()}/__admin/mappings").readText()
//        Log.d("dsds", "mappings from wiremock: $mappings")
//
//        scenario = launchActivity()
//        onView(withId(R.id.iv))
//            .check(matches(isDisplayed()));
//    }
}