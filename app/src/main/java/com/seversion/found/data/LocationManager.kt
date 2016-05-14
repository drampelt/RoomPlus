package com.seversion.found.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.seversion.found.R
import com.seversion.found.data.models.LearnData
import com.seversion.found.data.models.LearnResponse
import com.seversion.found.data.services.LocationService
import org.jetbrains.anko.AnkoLogger
import rx.Observable

/**
 * Created by Daniel on 2016-05-04.
 */

class LocationManager(val wifiNetworkManager: WifiNetworkManager, val context: Context, val resources: Resources, val sharedPreferences: SharedPreferences, val locationService: LocationService): AnkoLogger {

    fun submitFingerprints(location: String? = null): Observable<LearnResponse> {
        if (!wifiNetworkManager.isWifiEnabled()) {
            return Observable.error(WifiNotEnabledException(resources.getString(R.string.main_error_wifi_disabled)))
        }

        val group = sharedPreferences.getString(resources.getString(R.string.settings_key_group), "")
        val username = sharedPreferences.getString(resources.getString(R.string.settings_key_username), "")

        if (group == "" || username == "") return Observable.error(NoSettingsException(resources.getString(R.string.main_error_no_settings)))

        val type = if (location == null) "track" else "learn"
        var baseUrl = sharedPreferences.getString(resources.getString(R.string.settings_key_server_address), "")
        if (baseUrl == "") baseUrl = resources.getString(R.string.settings_default_api)

        return wifiNetworkManager.getNetworks()
                .map { fingerprints -> LearnData(group, username, "none", System.currentTimeMillis(), location ?: "tracking", fingerprints) }
                .flatMap { data ->
                    if (data.fingerprints.size == 0) {
                        Observable.error<LearnResponse>(NoFingerprintsException(resources.getString(R.string.main_error_no_fingerprints)))
                    } else {
                        locationService.submitFingerprints("$baseUrl/$type", data)
                    }
                }
                .flatMap { response ->
                    if (!response.success) {
                        if (response.message.contains("insert fingerprints", true)) {
                            return@flatMap Observable.error<LearnResponse>(NoLocationsException(resources.getString(R.string.main_error_no_locations)))
                        } else {
                            return@flatMap Observable.error<LearnResponse>(UnknownErrorException(resources.getString(R.string.main_error_unknown)))
                        }
                    } else {
                        if (type == "track" && response.message.substring(18).length == 0) {
                            return@flatMap Observable.error<LearnResponse>(NoResponseException(resources.getString(R.string.main_error_no_response)))
                        }
                    }
                    Observable.just(response)
                }
    }

    class NoSettingsException(message: String) : Exception(message)
    class NoFingerprintsException(message: String) : Exception(message)
    class NoLocationsException(message: String) : Exception(message)
    class UnknownErrorException(message: String) : Exception(message)
    class NoResponseException(message: String) : Exception(message)
    class WifiNotEnabledException(message: String) : Exception(message)
}
