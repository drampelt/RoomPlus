package com.seversion.found.ui.views

import com.seversion.found.data.models.Location

/**
 * Created by Daniel on 2016-05-02.
 */
interface TrackView : FingerprintingView {
    fun setLocation(location: Location)

    fun disableTracking()
    fun enableTracking()
}
