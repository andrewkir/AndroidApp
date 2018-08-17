package com.example.andrewforwork.timem8.MainScheduleEdit

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.lang.Math.abs
import java.util.*

class PagerAdapter(fragmentManager: FragmentManager, var day:Int) :
        FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        val fragment = MainScheduleFragment.newInstance()

        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,2018)
        calendar.set(Calendar.MONTH,0)
        calendar.set(Calendar.DAY_OF_YEAR,0)

        calendar.add(Calendar.DATE,position)

        var tmp = calendar.get(Calendar.DAY_OF_WEEK)
        when (tmp) {
            2 -> day = 0
            3 -> day = 1
            4 -> day = 2
            5 -> day = 3
            6 -> day = 4
            7 -> day = 5
            1 -> day = 6
        }
        bundle.putInt("DAY", day)
        bundle.putString("DATE", calendar.get(Calendar.DATE).toString() + "." + (calendar.get(Calendar.MONTH)).toString() + "." + (calendar.get(Calendar.YEAR)).toString())
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        var calendar = Calendar.getInstance()
        var tmp = calendar.get(Calendar.YEAR)-2018
        return 365*(abs(tmp)+3)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR,2018)
        calendar.set(Calendar.MONTH,0)
        calendar.set(Calendar.DAY_OF_YEAR,0)
        calendar.add(Calendar.DATE,position)
        var dayWeek = ""
        when (calendar.get(Calendar.DAY_OF_WEEK)) {
            2 -> dayWeek = "Понедельник"
            3 -> dayWeek = "Вторник"
            4 -> dayWeek = "Среда"
            5 -> dayWeek = "Четверг"
            6 -> dayWeek = "Пятница"
            7 -> dayWeek = "Суббота"
            1 -> dayWeek = "Воскресенье"
        }
        return "$dayWeek, ${calendar.get(Calendar.DAY_OF_MONTH)} число"
    }
}