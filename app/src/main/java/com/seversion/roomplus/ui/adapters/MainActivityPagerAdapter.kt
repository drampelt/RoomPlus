package com.seversion.roomplus.ui.adapters

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.seversion.roomplus.RoomPlusApplication
import com.seversion.roomplus.R
import com.seversion.roomplus.ui.fragments.LearnFragment
import com.seversion.roomplus.ui.fragments.TrackFragment
import javax.inject.Inject

/**
 * Created by Daniel on 2016-05-02.
 */

class MainActivityPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    @Inject
    lateinit var resources: Resources

    init {
        RoomPlusApplication.graph.inject(this)
    }

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> LearnFragment.newInstance()
            1 -> TrackFragment.newInstance()
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
}
