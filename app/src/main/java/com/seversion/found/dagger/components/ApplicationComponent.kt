package com.seversion.found.dagger.components

import com.seversion.found.FoundApplication
import com.seversion.found.dagger.modules.ApiModule
import com.seversion.found.dagger.modules.ApplicationModule
import com.seversion.found.ui.activities.WelcomeActivity
import com.seversion.found.ui.adapters.MainActivityPagerAdapter
import com.seversion.found.ui.fragments.LearnFragment
import com.seversion.found.ui.fragments.TrackFragment
import com.seversion.found.ui.presenters.LearnPresenter
import com.seversion.found.ui.presenters.TrackPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Daniel on 2016-04-19.
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class, ApiModule::class))
interface ApplicationComponent {
    fun inject(foundApplication: FoundApplication)

    fun inject(learnFragment: LearnFragment)
    fun inject(trackFragment: TrackFragment)

    fun inject(trackPresenter: TrackPresenter)
    fun inject(learnPresenter: LearnPresenter)

    fun inject(welcomeActivity: WelcomeActivity)

    fun inject(mainActivityPagerAdapter: MainActivityPagerAdapter)
}
