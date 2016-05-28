package com.seversion.roomplus.ui.adapters

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.seversion.roomplus.R
import com.seversion.roomplus.RoomPlusApplication
import com.seversion.roomplus.ui.fragments.LearnFragment
import com.seversion.roomplus.ui.fragments.TrackFragment
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import javax.inject.Inject

/**
 * Created by Daniel on 2016-05-02.
 */

class MainActivityPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager), AnkoLogger {

    @Inject
    lateinit var resources: Resources

    init {
        RoomPlusApplication.graph.inject(this)
    }

    private val fragments = listOf<Fragment>(LearnFragment.newInstance(), TrackFragment.newInstance())

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0, 1 -> fragments[position]
            else -> null
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> resources.getString(R.string.main_label_learn)
            1 -> resources.getString(R.string.main_label_track)
            else -> null
        }
    }

    fun getFragment(position: Int): Fragment? {
        return fragments.getOrNull(position)
    }
}
