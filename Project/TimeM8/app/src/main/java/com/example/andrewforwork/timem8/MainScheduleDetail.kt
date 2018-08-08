package com.example.andrewforwork.timem8

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.Editors.MainDetailEditor
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*
import java.io.File
import android.support.v4.content.FileProvider

private const val PERMISSION_REQUEST = 10

class MainScheduleDetail : AppCompatActivity() {
    internal lateinit var db: DBdetailinfo
    var date = ""
    var SubName = ""
    var count = 0
    var path = ""
    var hasimage = 0
    var currentImage = 0
    var photosQuantity = 0
    var photopathMap = hashMapOf<Bitmap,String>()
    private var photos = ArrayList<Bitmap>()
    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(R.layout.activity_main_schedule_detail)
        SubName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        count = intent.getIntExtra("COUNT_SUB",0)
        detailTextSubName.text = SubName
        detailTextDate.text = date
        db = DBdetailinfo(this)
        try {
            var currSub = db.allSubDetailByDay(SubName, date,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
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
        try {
            db = DBdetailinfo(this)
            var currSub = db.allSubDetailByDay(SubName, date,count)[0]
            detailTextHomework.text = if(currSub.homework.isEmpty()) "" else currSub.homework
            detailTextTips.text = if(currSub.tips.isEmpty()) "" else currSub.tips
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
            detailTextHomework.text = ""
            detailTextTips.text = ""
                    detailImageView.visibility = View.GONE
            buttonDetForw.visibility = View.GONE
            buttonDetBack.visibility = View.GONE
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
