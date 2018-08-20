package com.andrewkir.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
import com.andrewkir.andrewforwork.timem8.DataBase.DBdaily
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.MainScheduleEdit.MainScheduleEditable


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DebugDeleteDetail.setOnClickListener {
            var db = DBdetailinfo(this)
            db.deleteAllData()
            var dbtmp = DBdaily(this)
            dbtmp.deleteAllData()
        }
        debugDeleteMain.setOnClickListener {
            var db = DBHandler(this)
            db.deleteAllData()
        }
        dailyTasksBtn.setOnClickListener{
            val dailyTaskIntent = Intent(this,MainDailySchedule::class.java)
            startActivity(dailyTaskIntent)
        }
        MainScheduleBtn.setOnClickListener{
            val ScheduleIntent = Intent(this, MainSchedule::class.java)
            startActivity(ScheduleIntent)
        }
        MainScheduleBtnEd.setOnClickListener{
            val ScheduleIntentEd = Intent(this, MainScheduleEditable::class.java)
            startActivity(ScheduleIntentEd)
        }
    }

}
