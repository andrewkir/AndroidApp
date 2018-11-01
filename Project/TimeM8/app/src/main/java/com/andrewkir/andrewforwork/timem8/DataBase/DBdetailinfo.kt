package com.andrewkir.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.andrewkir.andrewforwork.timem8.Models.SubDetail

const val TABLE_DETAIL_NAME="subjects_detail_info"
const val COL_DETAIL_SUB_PARENT="sub_name"
const val COL_DETAIL_SUB_HOMEWORK="homework"
const val COL_DETAIL_IMAGE_ATT="img"
const val COL_DETAIL_IMAGE_PATH="path"
const val COL_DETAIL_DATE="date"
const val COL_DETAIL_TIPS="tips"
const val COL_DETAIL_COUNT="count"
const val COL_DETAIL_COLOR="color"

class DBdetailinfo(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,null , DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_DETAIL_NAME +" ("+ COL_ID+" INTEGER PRIMARY KEY," +
                COL_DETAIL_SUB_PARENT + " text," +
                COL_DETAIL_SUB_HOMEWORK + " text," +
                COL_DETAIL_IMAGE_ATT + " int," +
                COL_DETAIL_COUNT + " int," +
                COL_DETAIL_COLOR + " int," +
                COL_DETAIL_IMAGE_PATH + " text," +
                COL_DETAIL_TIPS + " text," +
                COL_DETAIL_DATE + " text)"
        db!!.execSQL(createTable)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists $TABLE_DETAIL_NAME")
        onCreate(db)
    }


    fun allSubDetailByDay(sub_parent: String,date: String,count: Int):List<SubDetail>
    {
        val lstSubs = ArrayList<SubDetail>()
        val selectQuery = "SELECT * FROM $TABLE_DETAIL_NAME WHERE $COL_DETAIL_SUB_PARENT = '$sub_parent' AND $COL_DETAIL_DATE = '$date' AND $COL_DETAIL_COUNT = '$count'"
        val db = this.writableDatabase
        onCreate(db)
        val cursor = db.rawQuery(selectQuery,null)
        if(cursor.moveToFirst()){
            do{
                val subDetail = SubDetail()
                subDetail.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                subDetail.parentSub = cursor.getString(cursor.getColumnIndex(COL_DETAIL_SUB_PARENT))
                subDetail.hasimage = cursor.getInt(cursor.getColumnIndex(COL_DETAIL_IMAGE_ATT))
                subDetail.count = cursor.getInt(cursor.getColumnIndex(COL_DETAIL_COUNT))
                subDetail.color = cursor.getInt(cursor.getColumnIndex(COL_DETAIL_COLOR))
                subDetail.homework = cursor.getString(cursor.getColumnIndex(COL_DETAIL_SUB_HOMEWORK))
                subDetail.path = cursor.getString(cursor.getColumnIndex(COL_DETAIL_IMAGE_PATH))
                subDetail.date = cursor.getString(cursor.getColumnIndex(COL_DETAIL_DATE))
                subDetail.tips = cursor.getString(cursor.getColumnIndex(COL_DETAIL_TIPS))
                lstSubs.add(subDetail)
            }while (cursor.moveToNext())
        }
        db.close()
        return lstSubs
    }


    fun addSubDetail(sub: SubDetail)
    {
        val db = this.writableDatabase
        onCreate(db)
        val cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_DETAIL_SUB_PARENT,sub.parentSub)
        cv.put(COL_DETAIL_SUB_HOMEWORK,sub.homework)
        cv.put(COL_DETAIL_IMAGE_ATT,sub.hasimage)
        cv.put(COL_DETAIL_IMAGE_PATH,sub.path)
        cv.put(COL_DETAIL_DATE,sub.date)
        cv.put(COL_DETAIL_COLOR,sub.color)
        cv.put(COL_DETAIL_COUNT,sub.count)
        cv.put(COL_DETAIL_TIPS,sub.tips)

        db.insertOrThrow(TABLE_DETAIL_NAME,null,cv)
        db.close()
    }


    fun updateSubDetail(sub: SubDetail):Int
    {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_DETAIL_SUB_PARENT,sub.parentSub)
        cv.put(COL_DETAIL_SUB_HOMEWORK,sub.homework)
        cv.put(COL_DETAIL_IMAGE_ATT,sub.hasimage)
        cv.put(COL_DETAIL_COUNT,sub.count)
        cv.put(COL_DETAIL_IMAGE_PATH,sub.path)
        cv.put(COL_DETAIL_DATE,sub.date)
        cv.put(COL_DETAIL_COLOR,sub.color)
        cv.put(COL_DETAIL_TIPS,sub.tips)

        return db.update(TABLE_DETAIL_NAME,cv, "$COL_ID=?", arrayOf(sub.id.toString()))
    }


    fun deleteAllData(){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $TABLE_DETAIL_NAME")
        onCreate(db)
        db.close()
    }
}