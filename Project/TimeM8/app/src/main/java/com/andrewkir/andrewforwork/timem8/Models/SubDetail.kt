package com.andrewkir.andrewforwork.timem8.Models

import android.graphics.Color

class SubDetail {
    var id: Int = 0
    var date: String = ""
    var parent_sub: String = ""
    var homework: String = ""
    var hasimage: Int = 0
    var path: String = ""
    var count: Int = 0
    var tips: String = ""
    var color: Int = Color.WHITE

    constructor(id:Int,date: String, parent: String,homework: String, hasimage: Int,path: String,tips: String,count: Int,color: Int){
        this.id = id
        this.date = date
        this.parent_sub = parent
        this.homework = homework
        this.hasimage = hasimage
        this.path = path
        this.tips = tips
        this.count = count
        this.color = color
    }

    constructor(){
    }

}