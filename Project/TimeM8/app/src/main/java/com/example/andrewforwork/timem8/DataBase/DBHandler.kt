package com.example.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.andrewforwork.timem8.Models.Sub

val DATABASE_NAME="Subjects"
val DATABASE_VERSION=1
val TABLE_NAME="subjects"
val COL_NAME="name"
val COL_CNT="count"
val COL_DAY="day"
val COL_TIME_BEGIN="time_begin"
val COL_TIME_END="time_end"
val COL_ID="id"
val COL_ROOM="room"
val COL_TYPE="type"
val COL_TEACHER="teacher"

class DBHandler(var contex: Context):SQLiteOpenHelper(contex, DATABASE_NAME,null , DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +" ("+ COL_ID+" INTEGER PRIMARY KEY," +
                COL_NAME + " text," +
                COL_DAY + " int," +
                COL_CNT + " int," +
                COL_TYPE + " text," +
                COL_ROOM + " text," +
                COL_TEACHER + " text," +
                COL_TIME_BEGIN + " text," +
                COL_TIME_END +" text)"
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
        cv.put(COL_TEACHER,sub.teacher)
        cv.put(COL_ROOM,sub.room)
        cv.put(COL_TIME_BEGIN,sub.timeBegin)
        cv.put(COL_TIME_END,sub.timeEnd)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TYPE,sub.type)

        var result = db.insertOrThrow(TABLE_NAME,null,cv)
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
                    sub.timeBegin = cursor.getString(cursor.getColumnIndex(COL_TIME_BEGIN))
                    sub.teacher = cursor.getString(cursor.getColumnIndex(COL_TEACHER))
                    sub.room = cursor.getString(cursor.getColumnIndex(COL_ROOM))
                    sub.timeEnd = cursor.getString(cursor.getColumnIndex(COL_TIME_END))
                    sub.count = cursor.getInt(cursor.getColumnIndex(COL_CNT))
                    sub.type = cursor.getString(cursor.getColumnIndex(COL_TYPE))

                    lstSubs.add(sub)
                }while (cursor.moveToNext())
            }
            db.close()
            return lstSubs
        }
    fun allSubByDay(day: Int):List<Sub>
        {
            val lstSubs = ArrayList<Sub>()
            val selectQuery = "SELECT * FROM "+ TABLE_NAME+" ORDER BY $COL_DAY , $COL_CNT"
            val db = this.writableDatabase
            onCreate(db)
            val cursor = db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst()){
                do{
                    val sub = Sub()
                    sub.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    sub.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    sub.day = cursor.getInt(cursor.getColumnIndex(COL_DAY))
                    sub.room = cursor.getString(cursor.getColumnIndex(COL_ROOM))
                    sub.timeBegin = cursor.getString(cursor.getColumnIndex(COL_TIME_BEGIN))
                    sub.timeEnd = cursor.getString(cursor.getColumnIndex(COL_TIME_END))
                    sub.teacher = cursor.getString(cursor.getColumnIndex(COL_TEACHER))
                    sub.count = cursor.getInt(cursor.getColumnIndex(COL_CNT))
                    sub.type = cursor.getString(cursor.getColumnIndex(COL_TYPE))
                    if(sub.day == day) {
                        lstSubs.add(sub)
                    }
                }while (cursor.moveToNext())
            }
            db.close()
            return lstSubs
        }
    fun SubByDayCount(day: Int,count:Int):List<Sub>
        {
            val lstSubs = ArrayList<Sub>()
            val selectQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE $COL_DAY='$day' AND $COL_CNT='$count'"
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery,null)
            if(cursor.moveToFirst()){
                do{
                    val sub = Sub()
                    sub.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                    sub.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
                    sub.day = cursor.getInt(cursor.getColumnIndex(COL_DAY))
                    sub.room = cursor.getString(cursor.getColumnIndex(COL_ROOM))
                    sub.timeBegin = cursor.getString(cursor.getColumnIndex(COL_TIME_BEGIN))
                    sub.timeEnd = cursor.getString(cursor.getColumnIndex(COL_TIME_END))
                    sub.teacher = cursor.getString(cursor.getColumnIndex(COL_TEACHER))
                    sub.count = cursor.getInt(cursor.getColumnIndex(COL_CNT))
                    sub.type = cursor.getString(cursor.getColumnIndex(COL_TYPE))
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
        cv.put(COL_TEACHER,sub.teacher)
        cv.put(COL_ROOM,sub.room)
        cv.put(COL_TIME_BEGIN,sub.timeBegin)
        cv.put(COL_TIME_END,sub.timeEnd)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TYPE,sub.type)

        db.insertOrThrow(TABLE_NAME,null,cv)
        db.close()
    }
    fun updateSub(sub:Sub):Int
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_NAME,sub.name)
        cv.put(COL_DAY,sub.day)
        cv.put(COL_TEACHER,sub.teacher)
        cv.put(COL_ROOM,sub.room)
        cv.put(COL_TIME_BEGIN,sub.timeBegin)
        cv.put(COL_TIME_END,sub.timeEnd)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TYPE,sub.type)

        return db.update(TABLE_NAME,cv, COL_ID+"=?", arrayOf(sub.id.toString()))

    }
    fun deleteSub(sub:Sub)
    {
        val db = this.writableDatabase
        db.delete(TABLE_NAME,COL_ID+"=?", arrayOf(sub.id.toString()))
        db.close()
    }
    fun deleteAllData(){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        db.close()
    }
}