/*
 * Copyright 2020 Marco Gomiero
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

package com.prof18.secureqrreader

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mikepenz.aboutlibraries.LibsBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var scanFragment: ScanFragment
    private var flashActive = false
    private var hideFlashMenu = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        scanFragment = ScanFragment()

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, scanFragment, ScanFragment.SCANNER_FRAGMENT_TAG)
                .commit()

        supportFragmentManager.registerFragmentLifecycleCallbacks(object: FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
                super.onFragmentResumed(fm, fragment)

                if (fragment is ScanFragment) {
                    hideFlashMenu(false)
                } else if (fragment is ResultFragment) {
                    hideFlashMenu(true)
                }

            }
        }, true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (!hasFlash()) {
            menu.findItem(R.id.action_flash).isVisible = false
        }

        menu.findItem(R.id.action_flash).isVisible = !hideFlashMenu

        if (flashActive) {
            menu.findItem(R.id.action_flash).icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_off)
        } else {
            menu.findItem(R.id.action_flash).icon = ContextCompat.getDrawable(this, R.drawable.ic_flash_on)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_source -> {
                LibsBuilder()
                    .withLicenseShown(true)
                    .withActivityTitle("Open Source Libraries")
                    .withEdgeToEdge(true)
                    .start(this)
                true
            }
            R.id.action_about -> {

                true
            }
            R.id.action_flash -> {
                if (flashActive) {
                    flashActive = false
                    scanFragment.enableFlash(false)
                } else {
                    flashActive = true
                    scanFragment.enableFlash(true)
                }
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun hideFlashMenu(hide: Boolean) {
        hideFlashMenu = hide
        invalidateOptionsMenu()
    }
}