package com.seversion.roomplus.dagger.modules

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.wifi.WifiManager
import android.preference.PreferenceManager
import com.seversion.roomplus.data.WifiNetworkManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Daniel on 2016-04-19.
 */

@Module
class ApplicationModule(protected val application: Application) {

    @Provides
    @Singleton
    fun provideApplication(): Application = application

    @Provides
    @Singleton
    fun provideContext(): Context = application

    @Provides
    @Singleton
    fun provideResources(context: Context): Resources = context.resources

    @Provides
    @Singleton
    fun provideWifiManager(context: Context): WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun provideWifiNetworkManager(wifiManager: WifiManager): WifiNetworkManager = WifiNetworkManager(wifiManager)

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}
