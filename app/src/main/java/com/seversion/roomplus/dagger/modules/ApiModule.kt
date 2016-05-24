package com.seversion.roomplus.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.wifi.WifiManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import com.seversion.roomplus.R
import com.seversion.roomplus.data.LocationManager
import com.seversion.roomplus.data.WifiNetworkManager
import com.seversion.roomplus.data.services.LocationService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.schedulers.Schedulers
import javax.inject.Singleton

/**
 * Created by Daniel on 2016-04-19.
 */

@Module
class ApiModule() {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
                .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(resources: Resources, sharedPreferences: SharedPreferences, gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        var baseUrl = sharedPreferences.getString(resources.getString(R.string.settings_key_server_address), "")
        if (baseUrl == "") baseUrl = resources.getString(R.string.settings_default_api)
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
    }

    @Provides
    @Singleton
    fun provideLocationService(retrofit: Retrofit): LocationService = retrofit.create(LocationService::class.java)

    @Provides
    @Singleton
    fun provideLocationManager(wifiNetworkManager: WifiNetworkManager, context: Context, resources: Resources, sharedPreferences: SharedPreferences, locationService: LocationService): LocationManager = LocationManager(wifiNetworkManager, context, resources, sharedPreferences, locationService)

}
