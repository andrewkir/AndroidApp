package com.andrewkir.andrewforwork.timem8

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main_schedule.*
import com.andrewkir.andrewforwork.timem8.R


class MainSchedule : AppCompatActivity() {

    ////////////////////////////////////////////////////////////////////////////
    /// Решил пока оставить свою первую версию, будет с чем сравнить в конце)///
    ////////////////////////////////////////////////////////////////////////////

    var cnt = 0
    var flag = 0
    var flag2 = 0
    var flag3 = 0
    var flag4 = 0
    val SAVED_TEXT = "saved_text"
    var SvdTxt=""

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        SvdTxt=flag.toString()+flag2.toString()+flag3.toString()+flag4.toString()
        outState?.putString(SAVED_TEXT,SvdTxt)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if(savedInstanceState != null) {
            SvdTxt = savedInstanceState.getString(SAVED_TEXT)
            loadText()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_schedule)
        progressBarchk.progress = 0
        ProgressText.text="Процесс..."
        loadText()
        Toast.makeText(this,"Решил пока оставить, будет с чем сравнить в конце) А так, тут ловить нечего, уходи",Toast.LENGTH_LONG).show()
        println("svd text is $SvdTxt")

    }
    fun CheckBox1(view: View){
        if(checkBoxBtn1.isChecked==true && flag==0){
            cnt=cnt+25
            progressBarchk.progress = cnt
            flag=1
        }
        else {
            cnt=cnt-25
            flag=0
            progressBarchk.progress = cnt
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox2(view: View){
        if(checkBoxBtn2.isChecked==true && flag2==0){
            cnt=cnt+25
            progressBarchk.progress = cnt
            flag2=1
        }
        else {
            cnt=cnt-25
            flag2=0
            progressBarchk.progress = cnt
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox3(view: View){
        if(checkBoxBtn3.isChecked==true && flag3==0){
            cnt=cnt+25
            progressBarchk.progress = cnt
            flag3=1
        }
        else {
            cnt=cnt-25
            flag3=0
            progressBarchk.progress = cnt
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }
    fun CheckBox4(view: View){
        if(checkBoxBtn4.isChecked==true && flag4==0){
            cnt=cnt+25
            progressBarchk.progress = cnt
            flag4=1
        }
        else {
            cnt=cnt-25
            flag4=0
            progressBarchk.progress = cnt
        }
        if(cnt==100){
            ProgressText.text=""
        }
        else ProgressText.text="Процесс..."
    }

    private fun saveText() {
        val sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val ed = sPref.edit()
        val tmpStr =flag.toString()+flag2.toString()+flag3.toString()+flag4.toString()
        ed.putString(SAVED_TEXT,tmpStr)
        println("string is ${tmpStr}")
        ed.apply()
    }
    private fun loadText(){
        val sPref = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        SvdTxt = sPref.getString(SAVED_TEXT,"0000")
        cnt=0
        if(SvdTxt[0]=='1'){
            checkBoxBtn1.isChecked = true
            flag=1
            cnt+=25
        }
        if(SvdTxt[1]=='1'){
            checkBoxBtn2.isChecked = true
            flag2=1
            cnt+=25
        }
        if(SvdTxt[2]=='1'){
            checkBoxBtn3.isChecked = true
            flag3=1
            cnt+=25
        }
        if(SvdTxt[3]=='1'){
            checkBoxBtn4.isChecked = true
            flag4=1
            cnt+=25
        }
        progressBarchk.progress = cnt
    }

    override fun onDestroy() {
        super.onDestroy()
        saveText()
    }

    override fun onResume() {
        super.onResume()
        loadText()
    }

    override fun onPause() {
        super.onPause()
        saveText()
    }

}
