package com.example.andrewforwork.timem8

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.Editors.MainDetailEditor
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*
import java.io.File
import android.support.v4.content.FileProvider
import android.text.method.ScrollingMovementMethod











private const val PERMISSION_REQUEST = 10

class MainScheduleDetail : AppCompatActivity() {
    internal lateinit var db: DBdetailinfo
    var date = ""
    var SubName = ""
    var count = 0
    var path = ""
    var hasimage = 0
    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(this, permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }
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
            //detailTextHomework.movementMethod = ScrollingMovementMethod()
            if(currSub.hasimage == 1){
                val bmImg = BitmapFactory.decodeFile(currSub.path)
                detailImageView.setImageBitmap(bmImg)
                path = currSub.path
                hasimage = currSub.hasimage
            }
        }
        catch(e: Exception) {
        }
    }
    fun onDetailImage(view: View){
        if(hasimage == 1) {
            val file = File(path)
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
            if(currSub.hasimage == 1){
                val bmImg = BitmapFactory.decodeFile(currSub.path)
                detailImageView.setImageBitmap(bmImg)
                path = currSub.path
                hasimage = currSub.hasimage
            }
        }
        catch(e: Exception) {
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //TODO Перенаправление на редактирование предмета
        val DetailEditor = Intent(this,MainDetailEditor::class.java)
        DetailEditor.putExtra("NAME_SUB",SubName)
        DetailEditor.putExtra("DATE",date)
        DetailEditor.putExtra("COUNT_SUB",count)
        startActivity(DetailEditor)
        return true
    }
}
