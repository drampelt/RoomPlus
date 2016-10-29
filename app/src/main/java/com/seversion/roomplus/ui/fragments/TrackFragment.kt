package com.seversion.roomplus.ui.fragments

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby.mvp.MvpFragment
import com.seversion.roomplus.R
import com.seversion.roomplus.data.models.Location
import com.seversion.roomplus.inflate
import com.seversion.roomplus.ui.FragmentLifecycle
import com.seversion.roomplus.ui.activities.SettingsActivity
import com.seversion.roomplus.ui.presenters.TrackPresenter
import com.seversion.roomplus.ui.views.TrackView
import kotlinx.android.synthetic.main.fragment_track.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.onClick
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Daniel on 2016-04-19.
 */

class TrackFragment : MvpFragment<TrackView, TrackPresenter>(), TrackView, FragmentLifecycle, AnkoLogger {

    companion object {
        fun newInstance(): TrackFragment = TrackFragment()
    }

    private var animatedLoadingDrawable: AnimatedVectorDrawableCompat? = null

    override fun createPresenter() = TrackPresenter()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = container?.inflate(R.layout.fragment_track)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        animatedLoadingDrawable = AnimatedVectorDrawableCompat.create(context, R.drawable.animated_wifi)
        progress.setImageDrawable(animatedLoadingDrawable)

        tryAgain.onClick { presenter.startTracking() }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stopTracking()
    }

    override fun onPauseFragment() {
        presenter?.stopTracking()
    }

    override fun onResumeFragment() {
        presenter?.startTracking()
    }

    override fun setLocation(location: Location) {
        currentLocation.text = location.name
    }

    override fun showError(error: String, showSettings: Boolean) {
        val snackbar = Snackbar.make(root, error, if (showSettings) Snackbar.LENGTH_INDEFINITE else Snackbar.LENGTH_LONG)
        if (showSettings) {
            snackbar.setAction(R.string.main_action_open_settings, {
                startActivity<SettingsActivity>()
            })
        }
        snackbar.show()
    }

    override fun disableTracking() {
        progress.visibility = View.INVISIBLE
        animatedLoadingDrawable?.stop()
        tryAgain.visibility = View.VISIBLE
        currentLocation.text = resources.getString(R.string.track_label_error)
    }

    override fun enableTracking() {
        progress.visibility = View.VISIBLE
        animatedLoadingDrawable?.start()
        tryAgain.visibility = View.INVISIBLE
        currentLocation.text = resources.getString(R.string.track_label_determining)
    }
}
