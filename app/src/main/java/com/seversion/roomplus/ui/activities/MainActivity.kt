package com.seversion.roomplus.ui.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.DrawableRes
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.seversion.roomplus.R
import com.seversion.roomplus.ui.FragmentLifecycle
import com.seversion.roomplus.ui.adapters.MainActivityPagerAdapter
import com.tbruyelle.rxpermissions.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    private var adapter: MainActivityPagerAdapter? = null
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        adapter = MainActivityPagerAdapter(supportFragmentManager)
        viewPager.adapter = adapter
        currentPosition = viewPager.currentItem
        val listener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (currentPosition != position) {
                    val current = adapter?.instantiateItem(viewPager, currentPosition)
                    if (current is FragmentLifecycle) {
                        current.onPauseFragment()
                    }
                }

                val next = adapter?.instantiateItem(viewPager, position)
                if (next is FragmentLifecycle) {
                    next.onResumeFragment()
                }

                if (next is FabHandler) {
                    fab.setImageResource(next.getIcon())
                    fab.onClick { next.onClickFab() }
                    fab.show()
                } else {
                    fab.hide()
                    fab.onClick {}
                }

                currentPosition = position
            }
        }
        viewPager.addOnPageChangeListener(listener)
        listener.onPageSelected(currentPosition)
        tabs.setupWithViewPager(viewPager)

        RxPermissions.getInstance(this)
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe { granted ->
                    if (!granted) {
                        Snackbar.make(root, R.string.main_label_location_permission_required, Snackbar.LENGTH_INDEFINITE)
                                .setAction(R.string.main_action_open_settings, {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                                    intent.addCategory(Intent.CATEGORY_DEFAULT)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                })
                                .show()
                    }
                }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        val current = adapter?.instantiateItem(viewPager, currentPosition)
        if (current is FragmentLifecycle) {
            current.onPauseFragment()
        }
    }

    override fun onResume() {
        super.onResume()
        val current = adapter?.instantiateItem(viewPager, currentPosition)
        if (current is FragmentLifecycle) {
            current.onResumeFragment()
        }
        fab.translationY = 0f // Fix a stupid issue with the FAB staying up after opening settings from a snackbar
    }

    interface FabHandler {
        @DrawableRes
        fun getIcon(): Int

        fun onClickFab()
    }
}
