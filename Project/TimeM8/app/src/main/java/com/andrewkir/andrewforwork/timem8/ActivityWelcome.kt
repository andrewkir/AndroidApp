package com.andrewkir.andrewforwork.timem8

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.andrewkir.andrewforwork.timem8.DataBase.DBhandler
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.DataBase.DBfinance
import kotlinx.android.synthetic.main.activity_welcome.*


class ActivityWelcome : AppCompatActivity() {

    private var viewPager: ViewPager? = null
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var dots: Array<TextView?>
    private var layouts: IntArray? = null
    private var btnSkip: Button? = null
    private var btnNext: Button? = null
    private var prefManager: FirstLaungPrefs? = null

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            addBottomDots(position)

            if (position == layouts!!.size - 1) {
                btnNext!!.text = "НАЧАТЬ"
                btnSkip!!.visibility = View.GONE
            } else {
                btnNext!!.text = "ДАЛЕЕ"
                btnSkip!!.visibility = View.VISIBLE
            }
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}

        override fun onPageScrollStateChanged(arg0: Int) {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppThemeWelcome)
        // Checking for first time launch
        prefManager = FirstLaungPrefs(this)
        if (!prefManager!!.isFirstTimeLaunch()) {
            launchHomeScreen()
            finish()
        } else {

            //creating all tables
            val db = DBhandler(this)
            db.deleteAllData()
            val db2 = DBdaily(this)
            db2.deleteAllData()
            val db3 = DBdetailinfo(this)
            db3.deleteAllData()
            val db4 = DBfinance(this)
            db4.deleteAllData()

            if (Build.VERSION.SDK_INT >= 21) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }

            setContentView(R.layout.activity_welcome)

            viewPager = viewPager
            dotsLayout = layoutDots
            btnSkip = findViewById<View>(R.id.btn_skip) as Button
            btnNext = findViewById<View>(R.id.btn_next) as Button

            layouts = intArrayOf(R.layout.welcome_activity_1, R.layout.welcome_activity_2, R.layout.welcome_activity_3)

            addBottomDots(0)

            changeStatusBarColor()

            myViewPagerAdapter = MyViewPagerAdapter()
            viewPager!!.adapter = myViewPagerAdapter
            viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)

            btnSkip!!.setOnClickListener {
                launchHomeScreen()
            }

            btnNext!!.setOnClickListener {
                val current = getItem(+1)
                if (current < layouts!!.size) {
                    viewPager!!.currentItem = current
                } else {
                    launchHomeScreen()
                }
            }
        }
    }


    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts!!.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(colorsInactive[currentPage])
            dotsLayout!!.addView(dots[i])
        }

        if (dots.isNotEmpty())
            dots[currentPage]!!.setTextColor(colorsActive[currentPage])
    }


    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }


    private fun launchHomeScreen() {
        prefManager!!.setFirstTimeLaunch(false)
        startActivity(Intent(this@ActivityWelcome, MainActivity::class.java))
        finish()
    }


    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return layouts!!.size
        }

        private var layoutInflater: LayoutInflater? = null


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            val view = layoutInflater!!.inflate(layouts!![position], container, false)
            container.addView(view)

            return view
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }
}