package com.andrewkir.andrewforwork.timem8

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.andrewkir.andrewforwork.timem8.Adapters.FinancialAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBfinance
import com.andrewkir.andrewforwork.timem8.Editors.FinanceEditor
import com.yuan.waveview.WaveView
import kotlinx.android.synthetic.main.activity_finance_editor.*
import kotlinx.android.synthetic.main.activity_financial.*
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import java.util.*


class FinancialActivity : AppCompatActivity() {
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
        setContentView(R.layout.activity_financial)
        var Dpref = getSharedPreferences("isFirstDel", Context.MODE_PRIVATE)
        val isDel = Dpref.getBoolean("isFirstDel",true)
        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 && isDel){
            DBfinance(this).deleteAllData()
            var pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
            val ed = pref.edit()
            ed.putInt("MAX_FIN", 0)
            ed.apply()
            val edDel = Dpref.edit()
            edDel.putBoolean("isFirstDel",false)
            edDel.apply()
        } else if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != 2 && !isDel){
            val edDel = Dpref.edit()
            edDel.putBoolean("isFirstDel",true)
            edDel.apply()
        }
    }

    override fun onResume() {
        super.onResume()
        val pr = getPreferences(MODE_PRIVATE)
        if(pr.getBoolean("FIRST",true)) {
            val ed = pr.edit()
            ed.putBoolean("FIRST", false)
            ed.apply()
            println("_______________RECREATED_______________")
            recreate()
        } else {
            val ed = pr.edit()
            ed.putBoolean("FIRST", true)
            ed.apply()
        }
        var len = DBfinance(this).Sum().toLong()
        val pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
        val max = pref.getInt("MAX_FIN",0).toLong()
        waveView.setSpeed(WaveView.SPEED_NORMAL)
        if(max != 0.toLong()){
            waveView.max = max
            waveView.max = max
            waveView.progress = if(len>=max) max else len
            val dsum = if(DBfinance(this).Sum()<0) 0 else DBfinance(this).Sum()
            tipsFin.text = "Потрачено: \n$dsum\u20BD из $max\u20BD"
        } else {
            waveView.max = 100.toLong()
            waveView.progress = 49.toLong()
            tipsFin.text="Самое время установить максимальную сумму!"
        }
        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show()
            val edIntent = Intent(this,FinanceEditor::class.java)
            startActivity(edIntent)
        }
        waveView.setMode(WaveView.MODE_CIRCLE)
        waveView.setWaveColor(resources.getColor(R.color.material_green_500))
        if(waveView.progress > max*0.5){
                waveView.setWaveColor(resources.getColor(R.color.material_yellow_500))
        }
        if(waveView.progress > max*0.8){
            waveView.setWaveColor(resources.getColor(R.color.material_red_500))
        }
        mainFinRecycler.adapter = FinancialAdapter(this){_->}
        val layoutManagerFinance = LinearLayoutManager(this)
        mainFinRecycler.layoutManager = layoutManagerFinance
        mainFinRecycler.setHasFixedSize(true)
        showHideWhenScroll()

    }

    private fun showHideWhenScroll() {
        mainFinRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                    fab.hide()
                else
                    fab.show()
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
}
