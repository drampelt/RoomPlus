package com.seversion.found.ui.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import com.seversion.found.R

/**
 * Created by Daniel on 2016-05-02.
 */

class SettingsFragment : PreferenceFragment() {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}
