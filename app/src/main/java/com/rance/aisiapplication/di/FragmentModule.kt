package com.rance.aisiapplication.di

import com.rance.aisiapplication.ui.dashboard.DashboardFragment
import com.rance.aisiapplication.ui.home.HomeFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
import com.rance.aisiapplication.ui.picturesset.PicturesSetFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributePicturesSetFragment(): PicturesSetFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchImagesFragment(): WatchImagesFragment
}