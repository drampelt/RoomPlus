package com.seversion.roomplus.dagger.components

import com.seversion.roomplus.RoomPlusApplication
import com.seversion.roomplus.dagger.modules.ApiModule
import com.seversion.roomplus.dagger.modules.ApplicationModule
import com.seversion.roomplus.ui.activities.MainActivity
import com.seversion.roomplus.ui.activities.WelcomeActivity
import com.seversion.roomplus.ui.adapters.MainActivityPagerAdapter
import com.seversion.roomplus.ui.fragments.LearnFragment
import com.seversion.roomplus.ui.fragments.TrackFragment
import com.seversion.roomplus.ui.presenters.LearnPresenter
import com.seversion.roomplus.ui.presenters.TrackPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * Created by Daniel on 2016-04-19.
 */
@Singleton
@Component(modules = arrayOf(ApplicationModule::class, ApiModule::class))
interface ApplicationComponent {
    fun inject(roomPlusApplication: RoomPlusApplication)

    fun inject(learnFragment: LearnFragment)
    fun inject(trackFragment: TrackFragment)

    fun inject(trackPresenter: TrackPresenter)
    fun inject(learnPresenter: LearnPresenter)

    fun inject(welcomeActivity: WelcomeActivity)
    fun inject(mainActivity: MainActivity)

    fun inject(mainActivityPagerAdapter: MainActivityPagerAdapter)
}
