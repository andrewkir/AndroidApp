package com.andrewkir.andrewforwork.timem8.MainScheduleEdit

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
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
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.Editors.MainDetailEditor
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*
import java.io.File
import android.support.v4.content.FileProvider
import android.util.TypedValue
import com.andrewkir.andrewforwork.timem8.DataBase.DBHandler
import com.andrewkir.andrewforwork.timem8.R

private const val PERMISSION_REQUEST = 10

class MainScheduleDetail : AppCompatActivity() {
    internal lateinit var db: DBdetailinfo
    internal lateinit var dbMain: DBHandler
    var date = ""
    var viewDate = ""
    var day = 0
    var SubName = ""
    var count = 0
    var path = ""
    var hasimage = 0
    var currentImage = 0
    var photosQuantity = 0
    var photopathMap = hashMapOf<Bitmap,String>()
    private var photos = ArrayList<Bitmap>()
    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
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
        SubName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        viewDate = date.split(".")[0]+"."+(Integer.parseInt(date.split(".")[1])+1).toString()+"."+date.split(".")[2]
        count = intent.getIntExtra("COUNT_SUB",0)
        day = intent.getIntExtra("DAY",0)
        println(day)
        detailTextSubName.text = SubName
        detailTextDate.text = viewDate
        db = DBdetailinfo(this)
        dbMain = DBHandler(this)
        try {
            var currSub = db.allSubDetailByDay(SubName, date,count)[0]
            var schSub = dbMain.SubByDayCount(day,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
            detailRoom.text = if(schSub.room.isEmpty()) "" else schSub.room
            detailTeacher.text = if(schSub.teacher.isEmpty()) "" else schSub.teacher
            if(currSub.hasimage == 1){
                buttonDetForw.visibility = View.VISIBLE
                buttonDetBack.visibility = View.VISIBLE
                detailImageView.visibility = View.VISIBLE
                photopathMap = decodePath(currSub.path)
                path = currSub.path
                hasimage = currSub.hasimage
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
    fun onDetailImage(view: View){
        if(hasimage == 1) {
            println("onDetail=")
            println(photopathMap)
            println(photos[currentImage])
            val file = File(photopathMap[photos[currentImage]])
            val callCameraIntent = Intent(Intent.ACTION_VIEW)
            if (callCameraIntent.resolveActivity(packageManager) != null) {
                val authorities = packageName + ".fileprovider"
                val imageUri = FileProvider.getUriForFile(this, authorities, file)
                callCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                callCameraIntent.setDataAndType(imageUri, "image/*");
                startActivity(callCameraIntent)
            }

        }
    }
    fun decodePath(d_path:String): HashMap<Bitmap, String> {
        var resultPath = ArrayList<String>()
        resultPath = d_path.split(";;;") as ArrayList<String>
        resultPath.removeAt(resultPath.lastIndex)
        println("fun res path=")
        println(resultPath)
        photosQuantity = resultPath.size
        if(photosQuantity == 1){
            buttonDetForw.visibility = View.GONE
            buttonDetBack.visibility = View.GONE
        } else {
            buttonDetForw.visibility = View.VISIBLE
            buttonDetBack.visibility = View.VISIBLE
        }
        var map_res = hashMapOf<Bitmap, String>()
        val file = File(resultPath[0])
        var exif = ExifInterface(file.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        var rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
        var matrix = Matrix()
        if (rotation.toFloat() != 0f) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }
        var btmp = BitmapFactory.decodeFile(file.path)
        var adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
        detailImageView.setImageBitmap(adjustedBitmap)
        //map_res.put(adjustedBitmap, resultPath[0])

        for (p in resultPath) {
            val fileTmp = File(p)
            var exif = ExifInterface(fileTmp.absolutePath)
            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            var rotationInDegrees =
                    when (rotation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
            var matrix = Matrix()
            if (rotation.toFloat() != 0f) {
                matrix.preRotate(rotationInDegrees.toFloat())
            }
            var btmp = BitmapFactory.decodeFile(fileTmp.path)
            var adjustedBitmaptmp = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
            photos.add(adjustedBitmaptmp)
            map_res.put(adjustedBitmaptmp, p)
        }
        println("fun map res=")
        println(map_res)
        return map_res
    }

    fun checkPermission(context: Context, permissionArray: Array<String>): Boolean {
        var allSuccess = true
        for (i in permissionArray.indices){
            if(checkCallingOrSelfPermission(permissionArray[i]) == PackageManager.PERMISSION_DENIED)
                allSuccess = false
        }
        return allSuccess
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST){
            for(i in permissions.indices){
                if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                    var requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if(requestAgain){
                        Toast.makeText(this,"Недостаточно прав",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Перейдите в настройки и предоставьте приложению разрещения",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.activity_main_schedule_menu,menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
        try {
            db = DBdetailinfo(this)
            dbMain = DBHandler(this)
            var currSub = db.allSubDetailByDay(SubName, date,count)[0]
            var schSub = dbMain.SubByDayCount(day,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
            detailRoom.text = if(schSub.room.isEmpty()) "" else schSub.room
            detailTeacher.text = if(schSub.teacher.isEmpty()) "" else schSub.teacher
            photos.clear()
            if(currSub.hasimage == 1){
                buttonDetForw.visibility = View.VISIBLE
                buttonDetBack.visibility = View.VISIBLE
                photopathMap = decodePath(currSub.path)
                path = currSub.path
                hasimage = currSub.hasimage
                detailImageView.visibility = View.VISIBLE
                detailImageView.setImageBitmap(photos[currentImage])
            } else {
                buttonDetForw.visibility = View.GONE
                buttonDetBack.visibility = View.GONE
                detailImageView.visibility = View.GONE
            }
        }
        catch(e: Exception) {
            var schSub = dbMain.SubByDayCount(day,count)[0]
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
                val DetailEditor = Intent(this, MainDetailEditor::class.java)
                DetailEditor.putExtra("NAME_SUB", SubName)
                DetailEditor.putExtra("DATE", date)
                DetailEditor.putExtra("COUNT_SUB", count)
                startActivity(DetailEditor)
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
            val DetailEditor = Intent(this, MainDetailEditor::class.java)
            DetailEditor.putExtra("NAME_SUB", SubName)
            DetailEditor.putExtra("DATE", date)
            DetailEditor.putExtra("COUNT_SUB", count)
            startActivity(DetailEditor)
        } else {
            this.finish()
        }
        return true
    }
}
