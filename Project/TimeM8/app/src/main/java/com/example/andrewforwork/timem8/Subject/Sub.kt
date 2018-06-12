package com.example.andrewforwork.timem8.Subject

class Sub{
    var id: Int = 0
    var day: Int = 0
    var name: String=""
    var count: Int = 0
    var time: String= ""
    var importance: Int = 0
    var type: String = ""

    constructor(id:Int,d:Int,c:Int,name:String,time:String,i:Int,t:String){
        this.id = id
        this.day = d
        this.count = c
        this.name = name
        this.time = time
        this.importance = i
        this.type = t
    }

    constructor(){
    }

}