package com.seversion.found.ui.views

import com.seversion.found.data.models.Location

/**
 * Created by Daniel on 2016-05-02.
 */

interface LearnView : FingerprintingView {
    fun setLocations(locations: List<Location>)
}
