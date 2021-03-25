package com.rance.aisiapplication.di

import android.app.Application
import androidx.room.Room
import com.rance.aisiapplication.AppConstants
import com.rance.aisiapplication.common.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppConstants.DB_NAME)
            .fallbackToDestructiveMigrationFrom(1)
            .build()
    }
}