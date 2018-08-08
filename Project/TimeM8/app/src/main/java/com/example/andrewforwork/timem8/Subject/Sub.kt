package com.example.andrewforwork.timem8.Subject

class Sub{
    var id: Int = 0
    var day: Int = 0
    var name: String=""
    var count: Int = 0
    var timeBegin: String  = ""
    var timeEnd: String = ""
    var type: String = ""

    constructor(id:Int,day:Int,count:Int,name:String,timeBegin:String,timeEnd:String,type:String){
        this.id = id
        this.day = day
        this.count = count
        this.name = name
        this.timeBegin = timeBegin
        this.timeEnd = timeEnd
        this.type = type
    }

    constructor(){
    }

}