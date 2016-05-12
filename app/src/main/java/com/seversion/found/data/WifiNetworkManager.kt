package com.seversion.found.data

import android.net.wifi.WifiManager
import com.seversion.found.data.models.WifiFingerprint
import rx.Observable
import java.util.*

/**
 * Created by Daniel on 2016-04-22.
 */

class WifiNetworkManager(private val wifiManager: WifiManager) {

    var blockReceiver = false

    fun getNetworks(): Observable<List<WifiFingerprint>> {
        val fingerprints: MutableList<WifiFingerprint> = ArrayList()

        for (result in wifiManager.scanResults) {
            fingerprints.add(WifiFingerprint(result.BSSID, result.level))
        }
        return Observable.just(fingerprints)
    }

    fun scan() {
        wifiManager.startScan()
    }

    fun isWifiEnabled(): Boolean = wifiManager.isWifiEnabled

}
