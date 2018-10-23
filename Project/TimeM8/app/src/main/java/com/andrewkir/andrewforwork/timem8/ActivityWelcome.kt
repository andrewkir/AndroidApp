package com.andrewkir.andrewforwork.timem8

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.os.Build
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.LayoutInflater
import android.view.WindowManager
import android.support.v4.view.ViewPager
import android.content.Intent
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.text.Html
import android.view.View
import android.widget.TextView
import android.widget.LinearLayout
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.Button
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
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

    //  viewpager change listener
    internal var viewPagerPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            addBottomDots(position)

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts!!.size - 1) {
                // last page. make button text to GOT IT
                btnNext!!.text = "НАЧАТЬ"
                btnSkip!!.visibility = View.GONE
            } else {
                // still pages are left
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
        // Checking for first time launch - before calling setContentView()
        prefManager = FirstLaungPrefs(this)
        if (!prefManager!!.isFirstTimeLaunch()) {
            launchHomeScreen()
            finish()
        } else {

            //creating all tables
            var db = DBHandler(this)
            db.deleteAllData()
            var db2 = DBdaily(this)
            db2.deleteAllData()
            var db3 = DBdetailinfo(this)
            db3.deleteAllData()
            var db4 = DBfinance(this)
            db4.deleteAllData()

            // Making notification bar transparent
            if (Build.VERSION.SDK_INT >= 21) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }

            setContentView(R.layout.activity_welcome)

            viewPager = view_pager
            dotsLayout = layoutDots
            btnSkip = findViewById<View>(R.id.btn_skip) as Button
            btnNext = findViewById<View>(R.id.btn_next) as Button

            // layouts of all welcome sliders
            // add few more layouts if you want
            layouts = intArrayOf(R.layout.welcome_activity_1, R.layout.welcome_activity_2, R.layout.welcome_activity_3)

            // adding bottom dots
            addBottomDots(0)

            // making notification bar transparent
            changeStatusBarColor()

            myViewPagerAdapter = MyViewPagerAdapter()
            viewPager!!.setAdapter(myViewPagerAdapter)
            viewPager!!.addOnPageChangeListener(viewPagerPageChangeListener)

            btnSkip!!.setOnClickListener {
                launchHomeScreen()
            }

            btnNext!!.setOnClickListener {
                // checking for last page
                // if last page home screen will be launched
                val current = getItem(+1)
                if (current < layouts!!.size) {
                    // move to next screen
                    viewPager!!.currentItem = current
                } else {
                    launchHomeScreen()
                }
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls<TextView>(layouts!!.size)

        val colorsActive = resources.getIntArray(R.array.array_dot_active)
        val colorsInactive = resources.getIntArray(R.array.array_dot_inactive)

        dotsLayout!!.removeAllViews()
        for (i in dots!!.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dots[i]!!.setTextColor(colorsInactive[currentPage])
            dotsLayout!!.addView(dots!![i])
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

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * View pager adapter
     */
    inner class MyViewPagerAdapter : PagerAdapter() {
        override fun getCount(): Int {
            return layouts!!.size
        }

        private var layoutInflater: LayoutInflater? = null

//        val count: Int
//            get() {
//                return layouts!!.size
//            }

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