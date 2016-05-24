package com.seversion.roomplus.data.models

/**
 * Created by Daniel on 2016-05-02.
 */

data class LocationResponse(val success: Boolean, val message: String, val locations: Map<String, List<String>>)
