package com.seversion.roomplus.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Daniel on 2016-04-19.
 */
data class LearnData(
        val group: String,
        val username: String,
        val password: String,
        val time: Long,
        val location: String,
        @SerializedName("wifi-fingerprint") val fingerprints: List<WifiFingerprint>
)
