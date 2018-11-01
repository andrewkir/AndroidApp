package com.andrewkir.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.andrewkir.andrewforwork.timem8.Models.FinancialOperation
import java.util.Collections.reverse

const val TABLE_FINANCE_NAME="finance_table"
const val COL_FIN_NAME="name"
const val COL_FIN_DAY="day"
const val COL_FIN_AMOUNT="amount"
const val COL_FIN_TYPE="type"

class DBfinance(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null , DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS $TABLE_FINANCE_NAME ($COL_ID INTEGER PRIMARY KEY,$COL_FIN_NAME text,$COL_FIN_TYPE text,$COL_FIN_AMOUNT int,$COL_FIN_DAY int)"
        db!!.execSQL(createTable)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $TABLE_FINANCE_NAME")
        onCreate(db)
    }


    fun insertData(op: FinancialOperation) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID, op.id)
        cv.put(COL_FIN_DAY, op.day)
        cv.put(COL_FIN_NAME, op.name)
        cv.put(COL_FIN_TYPE, op.type)
        cv.put(COL_FIN_AMOUNT, op.amount)
        db.insertOrThrow(TABLE_FINANCE_NAME, null, cv)
        db.close()
    }


    fun allOperations(): List<FinancialOperation> {
        val lstOps = ArrayList<FinancialOperation>()
        val selectQuery = "SELECT * FROM $TABLE_FINANCE_NAME"
        val db = this.writableDatabase
        onCreate(db)
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val operation = FinancialOperation()
                operation.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                operation.day = cursor.getInt(cursor.getColumnIndex(COL_FIN_DAY))
                operation.name = cursor.getString(cursor.getColumnIndex(COL_FIN_NAME))
                operation.type = cursor.getString(cursor.getColumnIndex(COL_FIN_TYPE))
                operation.amount = cursor.getInt(cursor.getColumnIndex(COL_FIN_AMOUNT))
                lstOps.add(operation)
            } while (cursor.moveToNext())
        }
        db.close()
        reverse(lstOps)
        return lstOps
    }


    fun allOpByCat(): HashMap<String, Int> {
        val res = hashMapOf<String, Int>()
        for (i in allOperations()){
            if(res[i.type] != null){
                res[i.type] = res[i.type]!!+i.amount
            } else {
                res[i.type] = 0
                res[i.type] = res[i.type]!!+i.amount
            }
        }
        return res
    }


    fun updateOp(op: FinancialOperation): Int {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID, op.id)
        cv.put(COL_FIN_DAY, op.day)
        cv.put(COL_FIN_NAME, op.name)
        cv.put(COL_FIN_TYPE, op.type)
        cv.put(COL_FIN_AMOUNT, op.amount)
        return db.update(TABLE_FINANCE_NAME, cv, "$COL_ID=?", arrayOf(op.id.toString()))

    }


    fun deleteOp(op: FinancialOperation) {
        val db = this.writableDatabase
        db.delete(TABLE_FINANCE_NAME, "$COL_ID=?", arrayOf(op.id.toString()))
        db.close()
    }


    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FINANCE_NAME")
        onCreate(db)
        db.close()
    }


    fun sum(): Int {
        val db = this.writableDatabase
        val cur = db.rawQuery("SELECT SUM($COL_FIN_AMOUNT) FROM $TABLE_FINANCE_NAME", null)
        if (cur.moveToFirst()) {
            return cur.getInt(0)
        }
        return 0
    }
}
