package com.seversion.found.ui.views

import com.hannesdorfmann.mosby.mvp.MvpView

/**
 * Created by daniel on 2016-05-08.
 */

interface FingerprintingView : MvpView {
    fun showError(error: String, showSettings: Boolean = false)
}
