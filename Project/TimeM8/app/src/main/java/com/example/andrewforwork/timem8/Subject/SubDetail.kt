package com.example.andrewforwork.timem8.Subject

class SubDetail {
    var id: Int = 0
    var date: String = ""
    var parent_sub: String = ""
    var homework: String = ""
    var hasimage: Int = 0
    var path: String = ""
    var count: Int = 0
    var tips: String = ""

    constructor(id:Int,date: String, parent: String,homework: String, hasimage: Int,path: String,tips: String,count: Int){
        this.id = id
        this.date = date
        this.parent_sub = parent
        this.homework = homework
        this.hasimage = hasimage
        this.path = path
        this.tips = tips
        this.count = count
    }

    constructor(){
    }

}