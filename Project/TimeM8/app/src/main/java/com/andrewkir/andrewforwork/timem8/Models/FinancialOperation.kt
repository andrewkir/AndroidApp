package com.andrewkir.andrewforwork.timem8.Models

class FinancialOperation {
    var id: Int = 0
    var day: Int = 0
    var name: String = ""
    var type: String = ""
    var amount: Int = 0

    constructor(id: Int, day: Int, name: String, type: String,amount: Int) {
        this.id = id
        this.day = day
        this.name = name
        this.type = type
        this.amount = amount
    }
    constructor(){

    }
}