package com.rance.aisiapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rance.aisiapplication.ui.downloadlist.DownloadListViewModel

import com.rance.aisiapplication.ui.home.HomeViewModel
import com.rance.aisiapplication.ui.picturesset.PicturesSetViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap



@Module
abstract class ViewModelModule {


    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PicturesSetViewModel::class)
    internal abstract fun bindPicturesSetViewModel(viewModel: PicturesSetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DownloadListViewModel::class)
    internal abstract fun bindDownloadListViewModel(viewModel: DownloadListViewModel): ViewModel
}