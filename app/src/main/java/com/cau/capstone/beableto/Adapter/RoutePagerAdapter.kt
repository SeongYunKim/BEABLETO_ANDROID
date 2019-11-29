package com.cau.capstone.beableto.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter

class RoutePagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var fragments: ArrayList<Fragment> = ArrayList()
    private var baseId: Long = 0

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun clear(){
        fragments.clear()
    }

    fun addItem(fragment: Fragment){
        fragments.add(fragment)
    }

    override fun getItemId(position: Int): Long {
        return baseId + position
    }

    fun notifyChangePosition(n: Int){
        baseId += count + n
    }
}