package com.seversion.roomplus.ui.presenters

import android.content.res.Resources
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.seversion.roomplus.R
import com.seversion.roomplus.RoomPlusApplication
import com.seversion.roomplus.data.LocationManager
import com.seversion.roomplus.data.WifiNetworkManager
import com.seversion.roomplus.data.models.Location
import com.seversion.roomplus.ui.views.TrackView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by Daniel on 2016-05-02.
 */

class TrackPresenter : MvpBasePresenter<TrackView>(), AnkoLogger {

    @Inject
    lateinit var wifiNetworkManager: WifiNetworkManager

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var resources: Resources

    private val compositeSubscription = CompositeSubscription()

    init {
        RoomPlusApplication.graph.inject(this)
    }

    fun startTracking() {
        stopTracking()

        view?.enableTracking()
        val sub = Observable.interval(3L, TimeUnit.SECONDS, Schedulers.io())
                .doOnNext {
                    wifiNetworkManager.blockReceiver = true
                    wifiNetworkManager.scan()
                }
                .flatMap { Observable.timer(1L, TimeUnit.SECONDS) }
                .flatMap { locationManager.submitFingerprints() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    info("yay")
                    view?.setLocation(Location(result.message.substring(18)))
                }, { throwable ->
                    error("Problem submitting fingerprints", throwable)
                    view?.showError(throwable.message ?: resources.getString(R.string.main_error_unknown), throwable is LocationManager.NoSettingsException || throwable is LocationManager.UnknownErrorException)
                    stopTracking()
                })
        compositeSubscription.add(sub)
    }

    fun stopTracking() {
        compositeSubscription.clear()
        view?.disableTracking()
    }
}
