package com.example.andrewforwork.timem8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import kotlinx.android.synthetic.main.activity_main_schedule.*

class MainSchedule : AppCompatActivity() {
    var cnt = 0
    var flag = 0
    var flag2 = 0
    var flag3 = 0
    var flag4 = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule)
        progressBarchk.setProgress(0)
        ProgressText.text="Процесс..."
    }
    fun CheckBox1(view: View){
        if(checkBoxBtn1.isChecked==true && flag==0){
            cnt=cnt+25
            progressBarchk.setProgress(cnt)
            flag=1
        }
        else {
            cnt=cnt-25
            flag=0
            progressBarchk.setProgress(cnt)
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox2(view: View){
        if(checkBoxBtn2.isChecked==true && flag2==0){
            cnt=cnt+25
            progressBarchk.setProgress(cnt)
            flag2=1
        }
        else {
            cnt=cnt-25
            flag2=0
            progressBarchk.setProgress(cnt)
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox3(view: View){
        if(checkBoxBtn3.isChecked==true && flag3==0){
            cnt=cnt+25
            progressBarchk.setProgress(cnt)
            flag3=1
        }
        else {
            cnt=cnt-25
            flag3=0
            progressBarchk.setProgress(cnt)
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox4(view: View){
        if(checkBoxBtn4.isChecked==true && flag4==0){
            cnt=cnt+25
            progressBarchk.setProgress(cnt)
            flag4=1
        }
        else {
            cnt=cnt-25
            flag4=0
            progressBarchk.setProgress(cnt)
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
}
