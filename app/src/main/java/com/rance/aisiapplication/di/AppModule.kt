package com.rance.aisiapplication.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.rance.aisiapplication.AppConstants
import com.rance.aisiapplication.common.AppDatabase
import com.smartmicky.android.data.api.ApiHelper
import com.rance.aisiapplication.api.AppApiHelper
import com.rance.aisiapplication.api.service.ApiService
import com.smartmicky.android.di.qualifier.ApplicationContext
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {
    companion object {
        const val TIMEOUT_IN_SEC = 20

    }
    @Provides
    @ApplicationContext
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideHttpClient( @ApplicationContext context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_IN_SEC.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_IN_SEC.toLong(), TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(okHttpClient: OkHttpClient): ApiService {
        val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.24tupian.org/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiService(apiHelper: AppApiHelper): ApiHelper = apiHelper

    @Singleton
    @Provides
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, AppConstants.DB_NAME)
            .fallbackToDestructiveMigrationFrom(1)
            .build()
    }
}