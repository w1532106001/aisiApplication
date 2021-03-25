package com.rance.aisiapplication.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.rance.aisiapplication.common.AiSiApp
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector


class AppInjector {
    companion object {
        fun init(app: AiSiApp) {
            DaggerAppComponent.builder().application(app).build().inject(app)
            app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityPaused(p0: Activity?) {
                }

                override fun onActivityResumed(p0: Activity?) {
                }

                override fun onActivityStarted(p0: Activity?) {
                }

                override fun onActivityDestroyed(p0: Activity?) {
                }

                override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
                }

                override fun onActivityStopped(activity: Activity?) {
                }

                override fun onActivityCreated(activity: Activity?, p1: Bundle?) {
                    activity?.let {
                        if (it is HasSupportFragmentInjector) {
                            AndroidInjection.inject(activity)
                        }
                        (it as? FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
                            object : FragmentManager.FragmentLifecycleCallbacks() {
                                override fun onFragmentCreated(
                                    fm: FragmentManager,
                                    f: Fragment,
                                    savedInstanceState: Bundle?
                                ) {
                                    try {
                                        AndroidSupportInjection.inject(f)
                                    } catch (e: Exception) {
                                        e.message?.let { it1 -> Log.e("AppInjector", it1) }
                                    }
                                }
                            }, true
                        )
                    }
                }
            })
        }
    }
}