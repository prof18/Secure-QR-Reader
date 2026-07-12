/*
 * Copyright 2026 Marco Gomiero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prof18.secureqrreader.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
    }

    @Test
    fun criticalUserJourney() = baselineProfileRule.collect(
        packageName = PACKAGE_NAME,
        includeInStartupProfile = false,
    ) {
        pressHome()
        startActivityAndWait()

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        if (device.wait(Until.hasObject(By.text("Start scanning")), UI_TIMEOUT)) {
            device.findObject(By.text("Start scanning")).click()
        }
        device.wait(
            Until.findObject(
                By.res(
                    "com.android.permissioncontroller",
                    "permission_allow_foreground_only_button",
                )
            ),
            SHORT_TIMEOUT,
        )?.click()

        device.wait(Until.hasObject(By.desc("About")), UI_TIMEOUT)
        device.findObject(By.desc("About"))?.visibleCenter?.let { center ->
            device.click(center.x, center.y)
        }
        device.wait(Until.hasObject(By.text("Privacy, by design")), UI_TIMEOUT)
    }

    private companion object {
        const val PACKAGE_NAME = "com.prof18.secureqrreader"
        const val UI_TIMEOUT = 5_000L
        const val SHORT_TIMEOUT = 2_000L
    }
}
