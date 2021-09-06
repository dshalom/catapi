package com.example.catapi

import android.util.Log
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ChangeTextBehaviorTest {


    private lateinit var wireMockServer: WireMockServer

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun setUp() {


        wireMockServer = WireMockServer(8089)

       // configureFor("localhost", 8089)

        wireMockServer.start()

        configureFor("localhost", 8089);

        //No-args constructor will start on port 8080, no HTTPS

        Log.e("dsds", "is running " + wireMockServer.isRunning)
        Log.e("dsds", "is running " + wireMockServer.port())

        print("-----dsds")


    }

    @Test
    fun changeText_sameActivity() {

        stubFor(get(urlEqualTo("/v1/images/search"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "text/plain")
                .withBody("Hello world!")));

        // Type text and then press the button.
        onView(withId(R.id.iv))
            .check(matches(isDisplayed()));

    }
}