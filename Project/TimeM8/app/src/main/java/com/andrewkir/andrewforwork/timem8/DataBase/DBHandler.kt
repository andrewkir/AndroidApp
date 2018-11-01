package com.andrewkir.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.andrewkir.andrewforwork.timem8.Models.Sub

const val DATABASE_NAME="Subjects"
const val DATABASE_VERSION=1
const val TABLE_NAME="subjects"
const val COL_NAME="name"
const val COL_CNT="count"
const val COL_DAY="day"
const val COL_TIME_BEGIN="time_begin"
const val COL_TIME_END="time_end"
const val COL_ID="id"
const val COL_ROOM="room"
const val COL_TYPE="type"
const val COL_TEACHER="teacher"


class DBHandler(context: Context):SQLiteOpenHelper(context, DATABASE_NAME,null , DATABASE_VERSION){
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
        db!!.execSQL("drop table if exists $TABLE_NAME")
        onCreate(db)
    }


    val allSub:List<Sub>
        get()
        {
            val lstSubs = ArrayList<Sub>()
            val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COL_DAY , $COL_CNT"
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
            val selectQuery = "SELECT * FROM $TABLE_NAME ORDER BY $COL_DAY , $COL_CNT"
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


    fun subByDayCount(day: Int, count:Int):List<Sub>
        {
            val lstSubs = ArrayList<Sub>()
            val selectQuery = "SELECT * FROM $TABLE_NAME WHERE $COL_DAY='$day' AND $COL_CNT='$count'"
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
        val cv = ContentValues()
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
        val cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_NAME,sub.name)
        cv.put(COL_DAY,sub.day)
        cv.put(COL_TEACHER,sub.teacher)
        cv.put(COL_ROOM,sub.room)
        cv.put(COL_TIME_BEGIN,sub.timeBegin)
        cv.put(COL_TIME_END,sub.timeEnd)
        cv.put(COL_CNT,sub.count)
        cv.put(COL_TYPE,sub.type)

        return db.update(TABLE_NAME,cv, "$COL_ID=?", arrayOf(sub.id.toString()))

    }


    fun deleteSub(sub:Sub)
    {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COL_ID=?", arrayOf(sub.id.toString()))
        db.close()
    }


    fun deleteAllData(){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        db.close()
    }
}