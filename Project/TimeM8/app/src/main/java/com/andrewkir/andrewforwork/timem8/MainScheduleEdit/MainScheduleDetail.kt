package com.andrewkir.andrewforwork.timem8.MainScheduleEdit

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.Editors.MainDetailEditor
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*
import java.io.File
import android.support.v4.content.FileProvider
import android.util.TypedValue
import com.andrewkir.andrewforwork.timem8.DataBase.DBhandler
import com.andrewkir.andrewforwork.timem8.R



class MainScheduleDetail : AppCompatActivity() {
    internal lateinit var db: DBdetailinfo
    private lateinit var dbMain: DBhandler
    var date = ""
    private var viewDate = ""
    var day = 0
    private var subName = ""
    var count = 0
    private var path = ""
    private var hasImage = 0
    private var currentImage = 0
    private var photosQuantity = 0
    private var photoPathMap = hashMapOf<Bitmap,String>()
    private var photos = ArrayList<Bitmap>()
    lateinit var sPref: SharedPreferences
    var stat: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs",Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_main_schedule_detail)
        subName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        viewDate = date.split(".")[0]+"."+(Integer.parseInt(date.split(".")[1])+1).toString()+"."+date.split(".")[2]
        count = intent.getIntExtra("COUNT_SUB",0)
        day = intent.getIntExtra("DAY",0)
        detailTextSubName.text = subName
        detailTextDate.text = viewDate

        db = DBdetailinfo(this)
        dbMain = DBhandler(this)
        try {
            val currSub = db.allSubDetailByDay(subName, date,count)[0]
            val schSub = dbMain.subByDayCount(day,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
            detailRoom.text = if(schSub.room.isEmpty()) "" else schSub.room
            detailTeacher.text = if(schSub.teacher.isEmpty()) "" else schSub.teacher
            if(currSub.hasimage == 1){
                buttonDetForw.visibility = View.VISIBLE
                buttonDetBack.visibility = View.VISIBLE
                detailImageView.visibility = View.VISIBLE
                photoPathMap = decodePath(currSub.path)
                path = currSub.path
                hasImage = currSub.hasimage
            } else {
                detailImageView.visibility = View.GONE
            }
        }
        catch(e: Exception) {
            buttonDetForw.visibility = View.GONE
            buttonDetBack.visibility = View.GONE
            detailImageView.visibility = View.GONE
        }

        buttonDetBack.setOnClickListener {
            if (currentImage-1>=0){
                currentImage--
                detailImageView.setImageBitmap(photos[currentImage])
            }
        }

        buttonDetForw.setOnClickListener {
            if (currentImage+1<photosQuantity){
                currentImage++
                detailImageView.setImageBitmap(photos[currentImage])
            }
        }
    }


    fun onDetailImage(view: View) {
        if(hasImage == 1) {
            val file = File(photoPathMap[photos[currentImage]])
            val callCameraIntent = Intent(Intent.ACTION_VIEW)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = "$packageName.fileprovider"
                val imageUri = FileProvider.getUriForFile(this, authorities, file)
                callCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                callCameraIntent.setDataAndType(imageUri, "image/*")
                startActivity(callCameraIntent)
            }
        }
    }


    private fun decodePath(d_path:String): HashMap<Bitmap, String> {
        val resultPath: ArrayList<String> = d_path.split(";;;") as ArrayList<String>
        resultPath.removeAt(resultPath.lastIndex)
        photosQuantity = resultPath.size
        if(photosQuantity == 1){
            buttonDetForw.visibility = View.GONE
            buttonDetBack.visibility = View.GONE
        } else {
            buttonDetForw.visibility = View.VISIBLE
            buttonDetBack.visibility = View.VISIBLE
        }
        val mapRes = hashMapOf<Bitmap, String>()
        val file = File(resultPath[0])
        val exif = ExifInterface(file.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
        val matrix = Matrix()
        if (rotation.toFloat() != 0f) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }
        val btmp = BitmapFactory.decodeFile(file.path)
        val adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
        detailImageView.setImageBitmap(adjustedBitmap)
        for (p in resultPath) {
            val fileTmp = File(p)
            val exif = ExifInterface(fileTmp.absolutePath)
            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val rotationInDegrees =
                    when (rotation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
            val matrix = Matrix()
            if (rotation.toFloat() != 0f) {
                matrix.preRotate(rotationInDegrees.toFloat())
            }
            val btmp = BitmapFactory.decodeFile(fileTmp.path)
            val adjustedBitmapTmp = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
            photos.add(adjustedBitmapTmp)
            mapRes[adjustedBitmapTmp] = p
        }
        return mapRes
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
        try {
            db = DBdetailinfo(this)
            dbMain = DBhandler(this)
            val currSub = db.allSubDetailByDay(subName, date,count)[0]
            val schSub = dbMain.subByDayCount(day,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
            detailRoom.text = if(schSub.room.isEmpty()) "" else schSub.room
            detailTeacher.text = if(schSub.teacher.isEmpty()) "" else schSub.teacher
            photos.clear()
            if(currSub.hasimage == 1){
                buttonDetForw.visibility = View.VISIBLE
                buttonDetBack.visibility = View.VISIBLE
                photoPathMap = decodePath(currSub.path)
                path = currSub.path
                hasImage = currSub.hasimage
                detailImageView.visibility = View.VISIBLE
                detailImageView.setImageBitmap(photos[currentImage])
            } else {
                buttonDetForw.visibility = View.GONE
                buttonDetBack.visibility = View.GONE
                detailImageView.visibility = View.GONE
            }
        }
        catch(e: Exception) {
            val schSub = dbMain.subByDayCount(day,count)[0]
            detailRoom.text = if(schSub.room.isEmpty()) "" else schSub.room
            detailTeacher.text = if(schSub.teacher.isEmpty()) "" else schSub.teacher
            detailTextHomework.text = ""
            detailTextTips.text = ""
                    detailImageView.visibility = View.GONE
            buttonDetForw.visibility = View.GONE
            buttonDetBack.visibility = View.GONE
            val pr = getPreferences(MODE_PRIVATE)
            if(pr.getBoolean("FIRST"+schSub.id.toString(),true)) {
                val ed = pr.edit()
                ed.putBoolean("FIRST"+schSub.id.toString(), false)
                ed.apply()
                val detailEditor = Intent(this, MainDetailEditor::class.java)
                detailEditor.putExtra("NAME_SUB", subName)
                detailEditor.putExtra("DATE", date)
                detailEditor.putExtra("COUNT_SUB", count)
                startActivity(detailEditor)
            } else {
                val ed = pr.edit()
                ed.putBoolean("FIRST"+schSub.id.toString(), true)
                ed.apply()
                finish()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.edit_menu) {
            val detailEditor = Intent(this, MainDetailEditor::class.java)
            detailEditor.putExtra("NAME_SUB", subName)
            detailEditor.putExtra("DATE", date)
            detailEditor.putExtra("COUNT_SUB", count)
            startActivity(detailEditor)
        } else {
            this.finish()
        }
        return true
    }
}
