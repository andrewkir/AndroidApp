package com.example.andrewforwork.timem8.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.andrewforwork.timem8.Subject.SubDetail

val TABLE_DETAIL_NAME="subjects_detail_info"
val COL_DETAIL_SUB_PARENT="sub_name"
val COL_DETAIL_SUB_HOMEWORK="homework"
val COL_DETAIL_IMAGE_ATT="img"
val COL_DETAIL_IMAGE_PATH="path"
val COL_DETAIL_DATE="date"

class DBdetailinfo(var contex: Context): SQLiteOpenHelper(contex, DATABASE_NAME,null , DATABASE_VERSION){
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_DETAIL_NAME +" ("+ COL_ID+" INTEGER PRIMARY KEY," +
                COL_DETAIL_SUB_PARENT + " text," +
                COL_DETAIL_SUB_HOMEWORK + " text," +
                COL_DETAIL_IMAGE_ATT + " int," +
                COL_DETAIL_IMAGE_PATH + " text," +
                COL_DETAIL_DATE + " text)"
        db!!.execSQL(createTable)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("drop table if exists " + TABLE_DETAIL_NAME)
        onCreate(db)
    }
    fun test():String{
        return "OK"
    }
    fun insertData(sub: SubDetail){
        val db =  this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_DETAIL_SUB_PARENT,sub.parent_sub)
        cv.put(COL_DETAIL_SUB_HOMEWORK,sub.homework)
        cv.put(COL_DETAIL_IMAGE_ATT,sub.hasimage)
        cv.put(COL_DETAIL_IMAGE_PATH,sub.path)
        cv.put(COL_DETAIL_DATE,sub.date)

        var result = db.insertOrThrow(TABLE_DETAIL_NAME,null,cv)
    }
//TODO решить че с этим делать
//    val allSub:List<SubDetail>
////        get()
////        {
////            val lstSubs = ArrayList<SubDetail>()
////            val selectQuery = "SELECT * FROM "+ TABLE_DETAIL_NAME+" ORDER BY $COL_DAY , $COL_CNT"
////            val db = this.writableDatabase
////            val cursor = db.rawQuery(selectQuery,null)
////            if(cursor.moveToFirst()){
////                do{
////                    val sub = SubDetail()
////                    sub.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
////                    sub.name = cursor.getString(cursor.getColumnIndex(COL_NAME))
////                    sub.day = cursor.getInt(cursor.getColumnIndex(COL_DAY))
////                    sub.importance = cursor.getInt(cursor.getColumnIndex(COL_IMP))
////                    sub.count = cursor.getInt(cursor.getColumnIndex(COL_CNT))
////                    sub.time = cursor.getString(cursor.getColumnIndex(COL_TIME))
////
////                    lstSubs.add(sub)
////                }while (cursor.moveToNext())
////            }
////            db.close()
////            return lstSubs
////        }
    fun allSubDetailByDay(sub_parent: String,date: String):List<SubDetail>
    {
        val lstSubs = ArrayList<SubDetail>()
        val selectQuery = "SELECT * FROM "+ TABLE_DETAIL_NAME+" WHERE $COL_DETAIL_SUB_PARENT = '$sub_parent' AND $COL_DETAIL_DATE = '$date'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        if(cursor.moveToFirst()){
            do{
                val sub_detail = SubDetail()
                sub_detail.id = cursor.getInt(cursor.getColumnIndex(COL_ID))
                sub_detail.parent_sub = cursor.getString(cursor.getColumnIndex(COL_DETAIL_SUB_PARENT))
                sub_detail.hasimage = cursor.getInt(cursor.getColumnIndex(COL_DETAIL_IMAGE_ATT))
                sub_detail.homework = cursor.getString(cursor.getColumnIndex(COL_DETAIL_SUB_HOMEWORK))
                sub_detail.path = cursor.getString(cursor.getColumnIndex(COL_DETAIL_IMAGE_PATH))
                sub_detail.date = cursor.getString(cursor.getColumnIndex(COL_DETAIL_DATE))
                lstSubs.add(sub_detail)
            }while (cursor.moveToNext())
        }
        db.close()
        return lstSubs
    }

    fun addSub_detail(sub: SubDetail)
    {
        val db = this.writableDatabase
        onCreate(db)
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_DETAIL_SUB_PARENT,sub.parent_sub)
        cv.put(COL_DETAIL_SUB_HOMEWORK,sub.homework)
        cv.put(COL_DETAIL_IMAGE_ATT,sub.hasimage)
        cv.put(COL_DETAIL_IMAGE_PATH,sub.path)
        cv.put(COL_DETAIL_DATE,sub.date)

        db.insertOrThrow(TABLE_DETAIL_NAME,null,cv)
        db.close()
    }
    fun updateSub_detail(sub: SubDetail):Int
    {
        val db = this.writableDatabase
        var cv = ContentValues()
        cv.put(COL_ID,sub.id)
        cv.put(COL_DETAIL_SUB_PARENT,sub.parent_sub)
        cv.put(COL_DETAIL_SUB_HOMEWORK,sub.homework)
        cv.put(COL_DETAIL_IMAGE_ATT,sub.hasimage)
        cv.put(COL_DETAIL_IMAGE_PATH,sub.path)
        cv.put(COL_DETAIL_DATE,sub.date)

        return db.update(TABLE_DETAIL_NAME,cv, COL_ID+"=?", arrayOf(sub.id.toString()))

    }
    fun deleteSub(sub: SubDetail)
    {
        val db = this.writableDatabase
        db.delete(TABLE_DETAIL_NAME,COL_ID+"=?", arrayOf(sub.id.toString()))
        db.close()
    }
    fun deleteAllData(){
        val db = this.writableDatabase
        db.execSQL("DROP TABLE $TABLE_DETAIL_NAME")
        onCreate(db)
        db.close()
    }
}