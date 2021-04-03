package com.rance.aisiapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.rance.aisiapplication.databinding.FragmentHomeBinding
import com.rance.aisiapplication.ui.base.BaseFragment
import com.rance.aisiapplication.ui.homepage.HomePageFragment
import com.rance.aisiapplication.ui.homepage.HomePageType

class HomeFragment : BaseFragment() {

    lateinit var binding: FragmentHomeBinding

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabAdapter = TabAdapter(childFragmentManager)

        binding.viewPage.apply {
            adapter = tabAdapter
            currentItem = 0
        }

        binding.tabLayout.setupWithViewPager(binding.viewPage)
    }

    class TabAdapter(fa: FragmentManager) : FragmentPagerAdapter(fa) {

        override fun getCount(): Int {
            return HomePageType.values().size
        }

        override fun getItem(position: Int): Fragment {
            return HomePageFragment.newInstance(HomePageType.values()[position])
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return HomePageType.values()[position].value
        }
    }
}