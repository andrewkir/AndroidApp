package com.example.andrewforwork.timem8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.DividerItemDecoration
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdaily
import com.example.andrewforwork.timem8.Models.dailyFrog
import com.github.aakira.expandablelayout.Utils
import kotlinx.android.synthetic.main.activity_main_daily_schedule.*


class MainDailySchedule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_daily_schedule)
        expandRecycler.addItemDecoration(DividerItemDecoration(this,1))
        expandRecycler.layoutManager = LinearLayoutManager(this)


        val db =  DBdaily(this)
        val data = ArrayList<dailyFrog>()
        var isdone = ArrayList<Boolean>()
        isdone.add(false)
        isdone.add(false)
        data.add(dailyFrog(
                id = 1,
                name = "test 2",
                count = 2,
                date = "1.1.1.1",
                isDone =isdone,
                description = "тестим",
                colorId1 = R.color.colorPrimary,
                colorId2 = R.color.LightGrey,
                tasks = "asd;;;test;;;"))
        isdone.add(false)
        data.add(dailyFrog(
                id = 2,
                name = "asd 3",
                count = 3,
                date = "1.1.1.1",
                isDone =isdone,
                description = "тесссссс",
                colorId1 = R.color.colorPrimary,
                colorId2 = R.color.LightGrey,
                tasks = "asd;;;test;;;qwerty;;;"))
        try {
            db.addFrog(data[1])
            db.addFrog(data[0])
        } catch (e:Exception){
            Toast.makeText(this,"already added",Toast.LENGTH_SHORT).show()
        }
        expandRecycler.adapter = expandAdapter(db.allFrogByDay("1.1.1.1"))
    }
}
