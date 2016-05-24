package com.seversion.roomplus

import android.app.Application
import com.seversion.roomplus.dagger.components.ApplicationComponent
import com.seversion.roomplus.dagger.components.DaggerApplicationComponent
import com.seversion.roomplus.dagger.modules.ApplicationModule

/**
 * Created by Daniel on 2016-04-18.
 */

class RoomPlusApplication : Application() {
    companion object {
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        RoomPlusApplication.graph = createComponent()
    }

    protected fun createComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}
