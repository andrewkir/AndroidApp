package com.example.andrewforwork.timem8

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dailyTasksBtn.setOnClickListener{
            MainText.text="Try again later..."
            val toast = Toast.makeText(this,"Эта функция будет добавлена позже",Toast.LENGTH_SHORT)
            toast.show()

        }
        MainScheduleBtn.setOnClickListener{
            val ScheduleIntent = Intent(this,MainSchedule::class.java)
            MainText.text="Time M8"
            startActivity(ScheduleIntent)
        }

    }
}
