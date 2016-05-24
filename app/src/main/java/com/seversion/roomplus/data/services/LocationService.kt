package com.seversion.roomplus.data.services

import com.seversion.roomplus.data.models.LearnData
import com.seversion.roomplus.data.models.LearnResponse
import com.seversion.roomplus.data.models.LocationResponse
import retrofit2.http.*
import rx.Observable

/**
 * Created by Daniel on 2016-05-02.
 */

interface LocationService {
    @POST
    fun submitFingerprints(@Url type: String, @Body learnData: LearnData): Observable<LearnResponse>
}
