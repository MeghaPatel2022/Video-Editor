package com.daily.events.videoeditor

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.daily.events.videoeditor.adapter.pageradapter.ViewPagerAdapter
import com.daily.events.videoeditor.const.Constant
import com.daily.events.videoeditor.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    lateinit var mainBinding: ActivityMainBinding
    lateinit var viewPagerAdapter: ViewPagerAdapter
    override fun permissionGranted() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        setViewPager()
    }

    private fun setViewPager() {
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPager.adapter = viewPagerAdapter
        viewPager.currentItem = Constant.pagerPosition
        mainBinding.topNavigationConstraint.setCurrentActiveItem(Constant.pagerPosition)

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                mainBinding.topNavigationConstraint.setCurrentActiveItem(i)
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        mainBinding.topNavigationConstraint.setNavigationChangeListener { _, position ->
            Constant.pagerPosition = position
            viewPager.setCurrentItem(position, true)
        }
    }

}