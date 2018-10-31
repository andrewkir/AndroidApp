package com.andrewkir.andrewforwork.timem8.Editors

import android.app.ActivityManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.Adapters.FinancialAdapter
import com.andrewkir.andrewforwork.timem8.DataBase.DBfinance
import com.andrewkir.andrewforwork.timem8.Models.FinancialOperation
import com.andrewkir.andrewforwork.timem8.R
import kotlinx.android.synthetic.main.activity_finance_editor.*
import java.lang.Exception
import java.util.*
import kotlin.math.abs
import android.widget.TextView
import android.support.v4.content.res.ResourcesCompat
import android.view.MenuItem
import android.view.ViewGroup



class FinanceEditor : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    var op = FinancialOperation()

    override fun onNothingSelected(parent: AdapterView<*>?) {
        op.type = "Продукты"
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
            0 -> {
                op.type = "Продукты"
                edFinName.hint = "покупка"
                edFinValue.hint = "стоимость, \u20BD"
            }
            1 -> {
                op.type = "Кафе/рестораны"
                edFinName.hint = "покупка"
                edFinValue.hint = "стоимость, \u20BD"
            }
            2 -> {
                op.type = "Развлечения"
                edFinName.hint = "покупка"
                edFinValue.hint = "стоимость, \u20BD"
            }
            3 -> {
                op.type = "Магазины"
                edFinName.hint = "покупка"
                edFinValue.hint = "стоимость, \u20BD"
            }
            4 -> {
                op.type = "Пополнение"
                edFinName.hint = "пополнение"
                edFinValue.hint = "\u20BD"
            }
            5 -> {
                op.type = "Другое"
                edFinName.hint = "покупка"
                edFinValue.hint = "стоимость, \u20BD"
            }
        }
    }
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
        setContentView(R.layout.activity_finance_editor)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        financeSpinner!!.onItemSelectedListener = this
        val pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
        val max = pref.getInt("MAX_FIN",0)
        if(max != 0){
            finMax.setText(max.toString())
        }
        var categories = arrayOf("Продукты","Кафе/рестораны","Развлечения","Магазины","Пополнение","Другое")
        val adapterSpinner = object : ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, categories) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)

                val externalFont = ResourcesCompat.getFont(context, R.font.rubik)
                (v as TextView).typeface = externalFont

                return v
            }


            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = ResourcesCompat.getFont(context, R.font.rubik)
//                (v as TextView).typeface = externalFont
//                v.setBackgroundColor(Color.GREEN)
                return v
            }
        }
        financeSpinner!!.adapter = adapterSpinner



        financeEditorRecycler.adapter = FinancialAdapter(context = this){
            Op ->
            op = Op
            edFinName.setText(op.name)
            edFinValue.setText(abs(op.amount).toString())
            when(op.type){
                "Продукты" -> financeSpinner.setSelection(0)
                "Кафе/рестораны" -> financeSpinner.setSelection(1)
                "Развлечения" -> financeSpinner.setSelection(2)
                "Магазины" -> financeSpinner.setSelection(3)
                "Пополнение" ->financeSpinner.setSelection(4)
                "Другое" -> financeSpinner.setSelection(5)
                else -> financeSpinner.setSelection(0)
            }
        }

        val layoutManagerFinance = LinearLayoutManager(this)
        financeEditorRecycler.layoutManager = layoutManagerFinance
        financeEditorRecycler.setHasFixedSize(true)

        finAdd.setOnClickListener {
            if(finMax.text.toString().isEmpty()) {
                Toast.makeText(this, "Введите максимальную недельную сумму", Toast.LENGTH_SHORT).show()
            } else {
                if (edFinName.text.toString().isEmpty() && edFinValue.text.toString().isEmpty()) {
                    if(finMax.text.toString().isNotEmpty()) {
                        var max = Integer.parseInt(finMax.text.toString())
                        var pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
                        val ed = pref.edit()
                        ed.putInt("MAX_FIN", max)
                        ed.apply()
                    }
                } else if(edFinName.text.toString().isNotEmpty() && edFinValue.text.toString().isNotEmpty() && finMax.text.toString().isNotEmpty() && Integer.parseInt(edFinValue.text.toString())!=0) {
                    var max = Integer.parseInt(finMax.text.toString())
                    var pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
                    val ed = pref.edit()
                    ed.putInt("MAX_FIN", max)
                    ed.apply()
                    if (op.id == 0) {
                        op.id = Calendar.getInstance().timeInMillis.toInt()
                    }
                    try {
                        op.name = edFinName.text.toString()
                        op.amount = if(op.type == "Пополнение") -Integer.parseInt(edFinValue.text.toString()) else Integer.parseInt(edFinValue.text.toString())
                        var db = DBfinance(this)
                        db.insertData(op)
                        println(op.type + "OP TYPE")
                    } catch (e: Exception) {
                        op.name = edFinName.text.toString()
                        op.amount = if(op.type == "Пополнение") -Integer.parseInt(edFinValue.text.toString()) else Integer.parseInt(edFinValue.text.toString())
                        var db = DBfinance(this)
                        db.updateOp(op)
                    }
                    op = FinancialOperation()
                    refresh()
                } else {
                    Toast.makeText(this, "Пожалуйста, введите корректные данные", Toast.LENGTH_SHORT).show()
                }
            }
        }
        finClear.setOnClickListener {
            op = FinancialOperation()
            refresh()
        }
        finDel.setOnClickListener {
            val db = DBfinance(this)
            try {
                db.deleteOp(op)
                refresh()
            } catch (e:Exception){}
        }
    }

    private fun refresh(){
        edFinName.setText("")
        edFinValue.setText("")
        financeSpinner.setSelection(0)
        val pref = getSharedPreferences("maxFinValue", Context.MODE_PRIVATE)
        val max = pref.getInt("MAX_FIN",0)
        if(max != 0){
            finMax.setText(max.toString())
        }
        financeEditorRecycler.adapter = FinancialAdapter(context = this){
            Op ->
            op = Op
            edFinName.setText(op.name)
            edFinValue.setText(abs(op.amount).toString())
            when(op.type){
                "Продукты" -> financeSpinner.setSelection(0)
                "Кафе/рестораны" -> financeSpinner.setSelection(1)
                "Развлечения" -> financeSpinner.setSelection(2)
                "Магазины" -> financeSpinner.setSelection(3)
                "Пополнение" -> financeSpinner.setSelection(4)
                "Другое" -> financeSpinner.setSelection(5)
                else -> financeSpinner.setSelection(0)
            }
        }
        val layoutManagerFinance = LinearLayoutManager(this)
        financeEditorRecycler.layoutManager = layoutManagerFinance
        financeEditorRecycler.setHasFixedSize(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        this.finish()
        return true
    }
}
