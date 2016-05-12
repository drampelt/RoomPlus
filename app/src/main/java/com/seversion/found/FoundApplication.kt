package com.seversion.found

import android.app.Application
import com.seversion.found.dagger.components.ApplicationComponent
import com.seversion.found.dagger.components.DaggerApplicationComponent
import com.seversion.found.dagger.modules.ApplicationModule

/**
 * Created by Daniel on 2016-04-18.
 */

class FoundApplication : Application() {
    companion object {
        @JvmStatic lateinit var graph: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()

        FoundApplication.graph = createComponent()
    }

    protected fun createComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }
}
