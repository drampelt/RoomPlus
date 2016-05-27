package com.seversion.roomplus.ui.views

import com.seversion.roomplus.data.models.Location

/**
 * Created by Daniel on 2016-05-02.
 */

interface LearnView : FingerprintingView {
    fun setLocations(locations: List<Location>)
    fun showList()
    fun showEmptyState()
    fun showDeleteConfirm(location: Location)
    fun showHint(text: String)
}
