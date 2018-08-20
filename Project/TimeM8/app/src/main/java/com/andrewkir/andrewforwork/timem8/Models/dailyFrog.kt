package com.andrewkir.andrewforwork.timem8.Models

import android.animation.TimeInterpolator



class dailyFrog {
    var id: Int = 0
    var name: String = ""
    var count: Int = 0
    var date: String = ""
    lateinit var isDone: ArrayList<Boolean>
    var description: String = ""
    var colorId1: Int = 0
    var colorId2: Int = 0
    var tasks: String = ""

    constructor(id:Int,name:String,count:Int,date:String,isDone:ArrayList<Boolean>,description: String, colorId1: Int, colorId2: Int,tasks:String){
        this.id = id
        this.name = name
        this.count = count
        this.date = date
        this.isDone = isDone
        this.description = description
        this.colorId1 = colorId1
        this.colorId2 = colorId2
        this.tasks = tasks
    }
    constructor(){

    }
}