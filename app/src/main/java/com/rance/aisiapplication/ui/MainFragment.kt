package com.rance.aisiapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.rance.aisiapplication.R
import com.rance.aisiapplication.databinding.FragmentMainBinding
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.dashboard.DashboardFragment
import com.rance.aisiapplication.ui.home.HomeFragment
import com.rance.aisiapplication.ui.notifications.NotificationsFragment


class MainFragment : BaseFragment() {

    lateinit var binding: FragmentMainBinding
    lateinit var bottomAdapter: BottomAdapter
    private lateinit var lastItem: MenuItem


    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomAdapter = BottomAdapter(requireActivity())

        binding.bottomNavigationView.apply {
            lastItem = menu.getItem(0)
            setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
                if (lastItem !== item) { // 判断当前点击是否为item自身
                    lastItem = item
                    binding.viewPage2.currentItem = when (item.itemId) {
                         R.id.navigation_home -> {
                            0
                        }
                        R.id.navigation_dashboard -> {
                            1
                        }
                        else -> {
                            2
                        }
                    }
                    return@OnNavigationItemSelectedListener true
                }
                false
            })
        }

        binding.viewPage2.apply {
            adapter = bottomAdapter
            currentItem = 0
        }

    }

    class BottomAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val fragmentList =
            arrayListOf(HomeFragment(), NotificationsFragment(), DashboardFragment())

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}