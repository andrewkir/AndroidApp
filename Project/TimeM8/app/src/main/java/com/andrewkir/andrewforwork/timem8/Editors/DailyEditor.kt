package com.andrewkir.andrewforwork.timem8.Editors

import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.Adapters.DailyCheckBoxAdapter
import com.andrewkir.andrewforwork.timem8.Adapters.DailyEditorFrogs
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.Models.dailyFrog
import com.andrewkir.andrewforwork.timem8.R
import kotlinx.android.synthetic.main.activity_daily_editor.*
import java.util.*
import kotlin.collections.ArrayList

class DailyEditor : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {
        frog.colorId1 = R.color.material_grey_400
        frog.colorId2 = R.color.material_grey_200
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
            0 -> {
                frog.colorId1 = R.color.material_red_400
                frog.colorId2 = R.color.material_red_200
            }
            1 -> {
                frog.colorId1 = R.color.material_blue_400
                frog.colorId2 = R.color.material_blue_200
            }
            2 -> {
                frog.colorId1 = R.color.material_green_400
                frog.colorId2 = R.color.material_green_200
            }
            3 -> {
                frog.colorId1 = R.color.material_deep_orange_400
                frog.colorId2 = R.color.material_deep_orange_200
            }
            4 -> {
                frog.colorId1 = R.color.material_grey_400
                frog.colorId2 = R.color.material_grey_200
            }
            5 -> {
                frog.colorId1 = R.color.material_deep_purple_400
                frog.colorId2 = R.color.material_deep_purple_200
            }

        }
    }

    var name = "test"
    var date = "1.1.1.1"
    var frog = dailyFrog()
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    lateinit var db: DBdaily
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }
        setContentView(R.layout.activity_daily_editor)
        db = DBdaily(this)
        recyclerCheckBox.addItemDecoration(DividerItemDecoration(this,1))
        recyclerFrogs.addItemDecoration(DividerItemDecoration(this,1))
        spinnerDaily!!.onItemSelectedListener = this
        var list_colors = arrayOf("Красный","Синий","Зелёный","Оранжевый","Серый","Фиолетовый")
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,list_colors)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDaily!!.adapter = aa
        refresh()
        spinnerDaily.setSelection(3)
        dailyAddBtn.setOnClickListener {
            frog.tasks = frog.tasks+taskEdit.text.toString()+";;;"
            frog.count++
            try {
                frog.isDone.add(false)
            } catch (e:Exception){
                var tmp = ArrayList<Boolean>()
                tmp.add(false)
                frog.isDone = tmp
            }
            println(frog.tasks)
            db.updateFrog(frog)
            refresh()
            taskEdit.setText("")
        }
        dailyDelBtn.setOnClickListener {
            if (frog.count >= 1) {
                var tasks = frog.tasks.split(";;;")
                frog.tasks = ""
                for (i in 0 until frog.count-1) {
                    frog.tasks += tasks[i] + ";;;"
                }
                println(frog.tasks)
                frog.count--
                frog.isDone.removeAt(frog.isDone.lastIndex)
                db.updateFrog(frog)
                refresh()
            }
        }
        addFrog.setOnClickListener {
            if(frog.date != "") {
                if (dailyName.text.toString() != "") {
                    try {
                        if (frog.id != 0) {
                            frog.name = dailyName.text.toString()
                            frog.description = dailyDesc.text.toString()
                        } else {
                            frog.id = Calendar.getInstance().timeInMillis.toInt()
                            frog.name = dailyName.text.toString()
                            frog.description = dailyDesc.text.toString()
                            try {
                                frog.isDone.add(false)
                            } catch (e: Exception) {
                                var tmp = ArrayList<Boolean>()
                                tmp.add(false)
                                frog.isDone = tmp
                            }
                        }
                        db.addFrog(frog)
                        Toast.makeText(this,"Добавлено",Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this,"Обновлено",Toast.LENGTH_SHORT).show()
                        db.updateFrog(frog)
                    }
                    refresh()
                } else {
                    Toast.makeText(this, "Пожалуйста, введите корректные данные", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Пожалуйста, выберите дни повтора", Toast.LENGTH_SHORT).show()
            }
        }
        clearFrog.setOnClickListener {
            frog = dailyFrog()
            dailyName.setText("")
            dailyDesc.setText("")
            taskEdit.setText("")
            editDays.text = "Дни повтора"
            refresh()
            frog.colorId1 = R.color.material_deep_orange_400
            frog.colorId2 = R.color.material_deep_orange_200
        }
        dailyDeleteBtn.setOnClickListener {
            if(frog.id != 0){
                db.deleteFrog(frog)
                dailyName.setText("")
                dailyDesc.setText("")
                taskEdit.setText("")
                editDays.text = "Дни повтора"
                refresh()
                spinnerDaily.setSelection(3)
                frog.colorId1 = R.color.material_deep_orange_400
                frog.colorId2 = R.color.material_deep_orange_200
            } else {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refresh(){
        recyclerCheckBox.adapter = DailyCheckBoxAdapter(context = this,frog = frog)
        recyclerFrogs.adapter = DailyEditorFrogs(context = this, itemClick = { Frog ->
            frog = Frog
            dailyName.setText(Frog.name)
            dailyDesc.setText(Frog.description)
            if(frog.date!="") {
                var tmp = frog.date.split(";;;") as ArrayList
                println(tmp)
                tmp.removeAt(tmp.lastIndex)
                var res = ""
                for (i in tmp) {
                    res += ", $i"
                }
                editDays.text = res.removeRange(0, 2)
            }
            spinnerDaily.setSelection(
                    when(frog.colorId1){
                        R.color.material_red_400 -> 0
                        R.color.material_blue_400 -> 1
                        R.color.material_green_400 -> 2
                        R.color.material_deep_orange_400 -> 3
                        R.color.material_grey_400 -> 4
                        R.color.material_deep_purple_400 -> 5
                        else -> 0
                    }
            )
            recyclerCheckBox.adapter = DailyCheckBoxAdapter(context = this,frog = frog)
            val layoutManager = LinearLayoutManager(this)
            recyclerCheckBox.layoutManager = layoutManager
            recyclerCheckBox.setHasFixedSize(true)
        })
        val layoutManager = LinearLayoutManager(this)
        recyclerCheckBox.layoutManager = layoutManager
        recyclerCheckBox.setHasFixedSize(true)
        val layoutManagerFrog = LinearLayoutManager(this)
        recyclerFrogs.layoutManager = layoutManagerFrog
        recyclerFrogs.setHasFixedSize(true)
    }
    fun daysView(view: View?){
        showDialog()
    }
    private fun showDialog(){
        val listItems = arrayOf("Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье")
        val resList = arrayOf("Пн","Вт","Ср","Чт","Пт","Сб","Вс")
        val checkedItems = BooleanArray(listItems.size)
        checkedItems.fill(false)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите дни повтора")
        builder.setMultiChoiceItems(listItems,checkedItems) { dialog:DialogInterface, which:Int, isChecked:Boolean->
        }
        builder.setCancelable(false)
        if(frog.date!=""){
            var tmp = frog.date.split(";;;")
            var res = ""
            for(i in resList){
                if(i in tmp){
                    checkedItems[resList.indexOf(i)] = true
                    res += ", $i"
                }
            }
            editDays.text = res.removeRange(0, 2)
        }
        builder.setPositiveButton("Ок"){ dialog:DialogInterface, which:Int->
            frog.date = ""
            editDays.text = ""
            var res = ""
            for (i in 0 until checkedItems.size){
                if(checkedItems[i]){
                    frog.date +=resList[i]+";;;"
                    res += ", ${resList[i]}"
                }
            }
            editDays.text = res.removeRange(0, 2)
        }
        builder.setNegativeButton("Отмена"){ dialog:DialogInterface, _:Int->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }
}
