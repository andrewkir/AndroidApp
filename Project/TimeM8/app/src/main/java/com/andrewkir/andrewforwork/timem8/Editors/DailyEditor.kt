package com.andrewkir.andrewforwork.timem8.Editors

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
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
        frog.colorId1 = R.color.material_red_400
        frog.colorId2 = R.color.material_red_200
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
    lateinit var db: DBdaily
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_editor)
        db = DBdaily(this)
        //recyclerCheckBox.adapter = DailyCheckBoxAdapter(this,name,date)
        //frog = db.allFrogByDayName(date = date,name = name)[0]
        recyclerCheckBox.addItemDecoration(DividerItemDecoration(this,1))
        recyclerFrogs.addItemDecoration(DividerItemDecoration(this,1))
        spinnerDaily!!.onItemSelectedListener = this
        var list_colors = arrayOf("Красный","Синий","Зелёный","Оранжевый","Серый","Фиолетовый")
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item,list_colors)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDaily!!.adapter = aa
        refresh()
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
            if(dailyName.text.toString() != "") {
                try {
                    if (frog.id != 0) {
                        frog.name = dailyName.text.toString()
                        frog.description = dailyDesc.text.toString()
                        frog.date = "1.1.1.1"
                    } else {
                        frog.id = Calendar.getInstance().timeInMillis.toInt()
                        frog.name = dailyName.text.toString()
                        frog.description = dailyDesc.text.toString()
                        frog.date = "1.1.1.1"
                        frog.colorId1 = R.color.colorPrimary
                        frog.colorId2 = R.color.LightGrey
                        try {
                            frog.isDone.add(false)
                        } catch (e: Exception) {
                            var tmp = ArrayList<Boolean>()
                            tmp.add(false)
                            frog.isDone = tmp
                        }
                    }
                    db.addFrog(frog)
                } catch (e: Exception) {
                    db.updateFrog(frog)
                }
                refresh()
            } else {
                Toast.makeText(this,"Пожалуйста, введите корректные данные",Toast.LENGTH_SHORT).show()
            }
        }
        clearFrog.setOnClickListener {
            frog = dailyFrog()
            showDialog()
            dailyName.setText("")
            dailyDesc.setText("")
            taskEdit.setText("")
            spinnerDaily.setSelection(0)
            refresh()
        }
        dailyDeleteBtn.setOnClickListener {
            if(frog.id != 0){
                db.deleteFrog(frog)
                dailyName.setText("")
                dailyDesc.setText("")
                taskEdit.setText("")
                spinnerDaily.setSelection(0)
                refresh()
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
    private fun showDialog(){
        val listItems = arrayOf("Понедельник","Вторник","Среда","Четверг","Пятница","Суббота","Воскресенье")
        val checkedItems = BooleanArray(listItems.size)
        checkedItems.fill(false)
        checkedItems[1]=true
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите дни повтора")
        builder.setMultiChoiceItems(listItems,checkedItems) { dialog:DialogInterface, which:Int, isChecked:Boolean->
        }
        builder.setCancelable(false)
        builder.setPositiveButton("Ок"){ dialog:DialogInterface, which:Int->
            for (i in 0 until checkedItems.size){
                if(checkedItems[i]){
                    Toast.makeText(this,listItems[i],Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Отмена"){ dialog:DialogInterface, _:Int->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}
