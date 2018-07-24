package com.example.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import com.example.andrewforwork.timem8.Subject.Sub

val DATABASE_NAME="Subjects"
val DATABASE_VERSION=1
val TABLE_NAME="subjects"
val COL_NAME="name"
val COL_CNT="count"
val COL_DAY="day"
val COL_IMP="imp"
val COL_TIME="time"
val COL_ID="id"

class DBHandler(var contex: Context):SQLiteOpenHelper(contex, DATABASE_NAME,null , DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("+ COL_ID+" INTEGER PRIMARY KEY," +
                COL_NAME + " text," +
                COL_DAY + " int," +
                COL_CNT + " int," +
                COL_IMP + " int," +
                COL_TIME +" text)"
        db!!.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists " + TABLE_NAME)
        onCreate(db)
    }

    fun insertData(sub:Sub){
        val db =  this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_NAME,sub.name)
        cv.put(COL_DAY,sub.day)
        cv.put(COL_IMP,sub.importance)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TIME,sub.time)

        var result = db.insert(TABLE_NAME,null,cv)

        if(result == (-1).toLong())
            Toast.makeText(contex,"Fail",Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(contex,"Succ",Toast.LENGTH_SHORT).show()
    }

    val allSub:List<Sub>
        get()
        {
            val lstSubs = ArrayList<Sub>()
            val selectQuery = "SELECT * FROM "+ TABLE_NAME+" ORDER BY $COL_DAY , $COL_CNT"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst()){
                do{
                    val sub = Sub()
                    sub.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    sub.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    sub.day = cursor.getInt(cursor.getColumnIndex(COL_DAY))
                    sub.importance = cursor.getInt(cursor.getColumnIndex(COL_IMP))
                    sub.count = cursor.getInt(cursor.getColumnIndex(COL_CNT))
                    sub.time = cursor.getString(cursor.getColumnIndex(COL_TIME))

                    lstSubs.add(sub)
                }while (cursor.moveToNext())
            }
            db.close()
            return lstSubs
        }

    fun addSub(sub:Sub)
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_NAME,sub.name)
        cv.put(COL_DAY,sub.day)
        cv.put(COL_IMP,sub.importance)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TIME,sub.time)

        db.insert(TABLE_NAME,null,cv)
        db.close()
    }
    fun updateSub(sub:Sub):Int
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_NAME,sub.name)
        cv.put(COL_DAY,sub.day)
        cv.put(COL_IMP,sub.importance)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TIME,sub.time)

        return db.update(TABLE_NAME,cv, COL_ID+"=?", arrayOf(sub.id.toString()))

    }
    fun deleteSub(sub:Sub)
    {
        val db = this.writableDatabase
        db.delete(TABLE_NAME,COL_ID+"=?", arrayOf(sub.id.toString()))
        db.close()
    }
}