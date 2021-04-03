package com.rance.aisiapplication.di

import com.rance.aisiapplication.ui.modellist.ModelListFragment
import com.rance.aisiapplication.ui.downloadlist.DownloadListFragment
import com.rance.aisiapplication.ui.home.HomeFragment
import com.rance.aisiapplication.ui.homepage.HomePageFragment
import com.rance.aisiapplication.ui.imageview.WatchImagesFragment
import com.rance.aisiapplication.ui.model.ModelFragment
import com.rance.aisiapplication.ui.picturesset.PicturesSetFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): ModelListFragment

    @ContributesAndroidInjector
    abstract fun contributePicturesSetFragment(): PicturesSetFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchImagesFragment(): WatchImagesFragment

    @ContributesAndroidInjector
    abstract fun contributeDownloadListFragment(): DownloadListFragment

    @ContributesAndroidInjector
    abstract fun contributeHomePageFragment(): HomePageFragment

    @ContributesAndroidInjector
    abstract fun contributeModelFragment(): ModelFragment
//    @ContributesAndroidInjector
//    abstract fun contributeModelListFragment(): ModelListFragment
}