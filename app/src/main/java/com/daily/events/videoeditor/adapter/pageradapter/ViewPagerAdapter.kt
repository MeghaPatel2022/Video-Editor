package com.daily.events.videoeditor.adapter.pageradapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.daily.events.videoeditor.fragment.MyVideoFragment
import com.daily.events.videoeditor.fragment.SettingsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager?) :
    FragmentPagerAdapter(fragmentManager!!) {
    private val tabTitles =
        arrayOf("My Videos", "Settings")

    // Returns total number of pages
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MyVideoFragment()
            1 -> SettingsFragment()
            else -> MyVideoFragment()
        }
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    companion object {
        private const val NUM_ITEMS = 2
    }
}
