package com.prof18.secureqrreader

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

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
            R.id.action_settings -> true
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