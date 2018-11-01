package com.andrewkir.andrewforwork.timem8.ActivityFin

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.andrewkir.andrewforwork.timem8.Adapters.FinancialAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBfinance
import com.andrewkir.andrewforwork.timem8.Editors.FinanceEditor
import kotlinx.android.synthetic.main.activity_financial.*
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.MenuItem
import com.andrewkir.andrewforwork.timem8.R
import java.util.*
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import com.andrewkir.andrewforwork.timem8.Services.WaveHelper


class FinancialActivity : AppCompatActivity() {
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    private lateinit var waveHelper: WaveHelper
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
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        newWave.setWaveColor(
                Color.parseColor("#88b8f1ed"),
                Color.parseColor("#b8f1ed"))
        waveHelper = WaveHelper(newWave)
        newWave.setBorder(1.toPx(), Color.DKGRAY)
        newWave.setShapeType(com.gelitenight.waveview.library.WaveView.ShapeType.CIRCLE)
        newWave.waterLevelRatio = 0f
        newWave.amplitudeRatio = 0.05f
        var spent = DBfinance(this).Sum()
        val pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
        val max = pref.getInt("MAX_FIN",0)

        // check for fin result
        var Dpref = getSharedPreferences("isFirstResLaunch", Context.MODE_PRIVATE)
        val isDel = Dpref.getBoolean("isFirstLaunch",false)
        if(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 2 && isDel) {
            Dpref.edit().putBoolean("isFirstLaunch",false).apply()
            val ResIntent = Intent(this, FinanceResult::class.java)
            ResIntent.putExtra("MAX", max)
            ResIntent.putExtra("SUM", spent)
            startActivity(ResIntent)
        } else if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) != 2 && !isDel){
            Dpref.edit().putBoolean("isFirstLaunch",true).apply()
        }
    }

    override fun onPause() {
        super.onPause()
        waveHelper.cancel()
    }
    override fun onResume() {
        super.onResume()
        var spent = DBfinance(this).Sum()
        val pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
        val max = pref.getInt("MAX_FIN",0)
        if(max != 0){
            staticToAdd.visibility = View.GONE
            newWave.waterLevelRatio = if(spent>=max) 1f else spent/max.toFloat()
            val dsum = if(DBfinance(this).Sum()<0) 0 else DBfinance(this).Sum()
            tipsFin.text = "Потрачено: \n$dsum\u20BD из $max\u20BD"
            val calendar = Calendar.getInstance()
            var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (dayOfWeek == 0) {
                dayOfWeek = 7
            }
            if (newWave.waterLevelRatio >= 1f && dayOfWeek<7){
                FinAdvices.text = "К сожалению вы превысили максимум расходов этой недели"
            } else  when((0..1).shuffled().last()){
                0 ->{
                    if (dayOfWeek <= 6 && newWave.waterLevelRatio >= 0.8f) {
                        FinAdvices.text = "Вы уже в красной зоне, исключите ненужные покупки, если вы хотите уложиться в максимум!"
                    }else if(dayOfWeek in 3..5 && newWave.waterLevelRatio >= 0.7f){
                        FinAdvices.text = "Такими темпами вы не уложитесь в максимум, старайтесь тратить деньги только на нужные вещи"
                    }else if (dayOfWeek <= 3 && newWave.waterLevelRatio >= 0.5f){
                        if (dayOfWeek != 1) {
                            FinAdvices.text = "Прошло только $dayOfWeek дня,а вы уже потратили $dsum\u20BD. Старайтесь избегать ненужных покупок"
                        } else {
                            FinAdvices.text = "Прошёл только 1 день,а вы уже потратили $dsum\u20BD. Старайтесь избегать ненужных покупок"
                        }
                    }else if (dayOfWeek <= 2 && newWave.waterLevelRatio > 0.3f) {
                        FinAdvices.text = "Постарайтесь меньше тратить, если вы хотите уложиться в указанный максимум"
                    }else if (dayOfWeek <= 2 && newWave.waterLevelRatio >= 0.2f) {
                        FinAdvices.text = "Старайтесь избегать ненужных покупок!"
                    }else {
                        FinAdvices.text = "Так держать! Такими темпами вы уложитесь в максимум"
                    }
                }
                1 ->{
                    when((0..4).shuffled().last()) {
                        0 -> FinAdvices.text = "Перед покупкой всегда задавайте себе вопрос необходимости данной вещи"
                        1 -> FinAdvices.text = "Старайтесь откладывать с каждого дохода часть(10%-30%) средств"
                        2 -> FinAdvices.text = "Отслеживаете ваши затраты, именно для этого хорошо подойдёт данное приложение :)"
                        3 -> FinAdvices.text = "Старайтесь ходить по магазинам со списком, так вы с меньшей вероятностью купите ненужную вещь"
                        4 -> FinAdvices.text = "Старайтесь держать не больше 30% от всех ваших средств в одном месте"
                    }
                }
            }
        } else {
            tipsFin.text="Самое время установить максимальную сумму!"
            staticToAdd.visibility = View.VISIBLE
        }
        fab.setOnClickListener { view ->
            val edIntent = Intent(this,FinanceEditor::class.java)
            startActivity(edIntent)
        }
        newWave.setWaveColor(resources.getColor(R.color.material_green_200),resources.getColor(R.color.material_green_300))
        if(newWave.waterLevelRatio >= 0.4f){
            newWave.setWaveColor(resources.getColor(R.color.material_yellow_200), resources.getColor(R.color.material_yellow_300))
        }
        if(newWave.waterLevelRatio >= 0.8f){
            newWave.setWaveColor(resources.getColor(R.color.material_red_200), resources.getColor(R.color.material_red_300))
        }
        mainFinRecycler.adapter = FinancialAdapter(this){_->}
        val layoutManagerFinance = LinearLayoutManager(this)
        mainFinRecycler.layoutManager = layoutManagerFinance
        mainFinRecycler.setHasFixedSize(true)
        showHideWhenScroll()
        waveHelper.start(if(max == 0) 0.5f else if(spent>=max) 1f else spent/max.toFloat())
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
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        this.finish()
        return true
    }
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
