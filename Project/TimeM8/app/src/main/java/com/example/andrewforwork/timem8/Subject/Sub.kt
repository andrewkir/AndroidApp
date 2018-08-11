package com.example.andrewforwork.timem8.Subject

class Sub{
    var id: Int = 0
    var day: Int = 0
    var name: String=""
    var count: Int = 0
    var room: String = ""
    var timeBegin: String  = ""
    var timeEnd: String = ""
    var type: String = ""
    var teacher: String = ""

    constructor(id:Int,day:Int,count:Int,name:String,timeBegin:String,timeEnd:String,type:String,room:String,teacher: String){
        this.room = room
        this.id = id
        this.day = day
        this.count = count
        this.name = name
        this.timeBegin = timeBegin
        this.timeEnd = timeEnd
        this.type = type
        this.teacher = teacher
    }

    constructor(){
    }

}