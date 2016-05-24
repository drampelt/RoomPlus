package com.seversion.roomplus.ui.presenters

import android.content.SharedPreferences
import android.content.res.Resources
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter
import com.seversion.roomplus.R
import com.seversion.roomplus.RoomPlusApplication
import com.seversion.roomplus.data.LocationManager
import com.seversion.roomplus.data.WifiNetworkManager
import com.seversion.roomplus.data.models.Location
import com.seversion.roomplus.ui.adapters.LocationAdapter
import com.seversion.roomplus.ui.views.LearnView
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

class LearnPresenter : MvpBasePresenter<LearnView>(), LocationAdapter.SelectionListener, AnkoLogger {

    @Inject
    lateinit var wifiNetworkManager: WifiNetworkManager

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var resources: Resources

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val compositeSubscription = CompositeSubscription()

    private var locations: MutableList<Location> = mutableListOf()

    init {
        RoomPlusApplication.graph.inject(this)
    }

    fun loadLocations() {
        val savedLocations = sharedPreferences.getStringSet(resources.getString(R.string.settings_key_locations), setOf())
        locations.clear()
        locations.addAll(savedLocations.map { Location(it) })
        view?.setLocations(locations)
        if (locations.size == 0) {
            view?.showEmptyState()
        } else {
            view?.showList()
        }
    }

    fun addLocation(name: String) {
        if (name != "") {
            locations.add(Location(name))
            view?.setLocations(locations)
            if (locations.size == 0) {
                view?.showEmptyState()
            } else {
                view?.showList()
            }
            saveLocations()
        }
    }

    fun confirmDelete(location: Location) {
        locations.remove(location)
        view?.setLocations(locations)
        if (locations.size == 0) {
            view?.showEmptyState()
        } else {
            view?.showList()
        }
        saveLocations()
    }

    override fun onSelect(location: Location): Boolean {
        onDeselect()

        val sub = Observable.interval(3L, TimeUnit.SECONDS, Schedulers.io())
                .doOnNext {
                    wifiNetworkManager.blockReceiver = true
                    wifiNetworkManager.scan()
                }
                .flatMap { Observable.timer(1L, TimeUnit.SECONDS) }
                .flatMap { locationManager.submitFingerprints(location.name) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    info("yay")
                }, { throwable ->
                    error("Problem submitting fingerprints", throwable)
                    view?.showError(throwable.message ?: resources.getString(R.string.main_error_unknown), throwable is LocationManager.NoSettingsException || throwable is LocationManager.UnknownErrorException)
                })
        compositeSubscription.add(sub)
        return true
    }

    override fun onDeselect() {
        compositeSubscription.clear()
    }

    override fun onDelete(location: Location) {
        view?.showDeleteConfirm(location)
    }

    private fun saveLocations() {
        var newLocations = emptySet<String>()
        for ((location) in locations) {
            newLocations += location
        }
        sharedPreferences.edit().putStringSet(resources.getString(R.string.settings_key_locations), newLocations).apply()
    }

}
