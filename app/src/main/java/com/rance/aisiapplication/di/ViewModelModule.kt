package com.rance.aisiapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.rance.aisiapplication.ui.home.HomeViewModel
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
    internal abstract fun bindCodeDetailViewModel(viewModel: HomeViewModel): ViewModel
}