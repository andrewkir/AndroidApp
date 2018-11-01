package com.andrewkir.andrewforwork.timem8.Editors

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.GradientDrawable
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.andrewkir.andrewforwork.timem8.DataBase.DBdetailinfo
import com.andrewkir.andrewforwork.timem8.R
import com.andrewkir.andrewforwork.timem8.Models.SubDetail
import kotlinx.android.synthetic.main.activity_main_detail_editor.*
import java.util.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.graphics.ColorUtils
import android.util.TypedValue
import android.view.View
import com.skydoves.colorpickerpreference.ColorPickerDialog
import java.io.*
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val PERMISSION_REQUEST = 10

class MainDetailEditor : AppCompatActivity() {
    var subjectName = ""
    var date = ""
    private var path = ""
    var count = 0
    private var imagePos = 0
    var color = Color.parseColor("#FAFAFA")
    private var imageFilePath = ""
    private var newImage = false
    private val GALLERY = 1
    private val CAMERA = 2
    private lateinit var subject: SubDetail
    private lateinit var adjustedBitmap: Bitmap
    private var alreadySavedPhotos = hashMapOf<Bitmap,String>()
    private var photos = ArrayList<Bitmap>()
    private var photosAttached = ArrayList<Boolean>()
    internal lateinit var db: DBdetailinfo
    lateinit var sPref: SharedPreferences
    var stat: String = ""
    private var permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sPref = getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)
        stat = sPref.getString("THEME", "ORANGE")
        when (stat) {
            "ORANGE" -> setTheme(R.style.AppTheme)
            "GREEN" -> setTheme(R.style.AppThemeGreen)
            "PURPLE" -> setTheme(R.style.AppThemePurple)
            "BLUE" -> setTheme(R.style.AppThemeBlue)
        }

        setContentView(R.layout.activity_main_detail_editor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission(permissions)) {
                requestPermissions(permissions, PERMISSION_REQUEST)
            }
        }

        db = DBdetailinfo(this)
        subjectName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        count = intent.getIntExtra("COUNT_SUB",0)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val count = intent.getIntExtra("COUNT_SUB",0)
        try {
            subject = db.allSubDetailByDay(subjectName,date,count)[0]
            color = subject.color
            val colorCircle = colorView.background.mutate() as GradientDrawable
            colorCircle.setColor(color)
            when(subject.hasimage){
                1 -> {
                    switchPhotoAttach.performClick()
                    alreadySavedPhotos = decodePath(subject.path)
                }
                else -> {
                    photosAttached.add(false)
                }
            }
            editTextTips.setText(subject.tips)
            editTextHomework.setText(subject.homework)
        }
        catch(e: Exception) {
            photosAttached.add(false)
        }
        saveBtn.setOnClickListener {
            if(imagePos==0){
                DetailEditorForward.performClick()
                DetailEditorBack.performClick()
            } else {
                DetailEditorBack.performClick()
                DetailEditorForward.performClick()
            }
            val dateArr =date.split(".")
            path = makePath()
            val subDt = SubDetail(
                    id=Integer.parseInt(dateArr[0]+dateArr[1]+dateArr[2]+count.toString()),
                    date = date,
                    parent = subjectName,
                    homework = if(editTextHomework.text.isEmpty()) "" else editTextHomework.text.toString(),
                    hasimage = if(path.length>3) 1 else 0,
                    path = path,
                    tips = if(editTextTips.text.isEmpty()) "" else editTextTips.text.toString(),
                    count = count,
                    color = color

            )
            try {
                db.addSubDetail(subDt)
                Toast.makeText(this,"добавлено",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(this,"обновлено",Toast.LENGTH_SHORT).show()
                db.updateSubDetail(subDt)
            }
        }

        DetailEditorBack.setOnClickListener {
            if(imagePos - 1 >= 0) {
                if(::adjustedBitmap.isInitialized || ::subject.isInitialized) {
                    if(imagePos >= photos.size) {
                        if(photos[imagePos-1] != adjustedBitmap) {
                            photos.add(adjustedBitmap)
                        }
                        imagePos--
                    } else {
                        if (adjustedBitmap != photos[imagePos]) {
                            photos[imagePos] = adjustedBitmap
                        }
                        imagePos--
                    }
                    if(photosAttached.size <= imagePos+1){
                        photosAttached.add(switchPhotoAttach.isChecked)
                    }
                    else {
                        photosAttached[imagePos + 1] = switchPhotoAttach.isChecked
                    }
                    if(switchPhotoAttach.isChecked && !photosAttached[imagePos] || !switchPhotoAttach.isChecked && photosAttached[imagePos])
                    {
                        switchPhotoAttach.performClick()
                    }
                    detailEditorImageView.setImageBitmap(photos[imagePos])
                    adjustedBitmap = photos[imagePos]
                    }
                }
        }

        DetailEditorForward.setOnClickListener {
            if(imagePos < photos.size) {
                if (::adjustedBitmap.isInitialized || ::subject.isInitialized) {
                    if (imagePos >= photos.size) {
                        if(photos[imagePos-1] != adjustedBitmap) {
                            photos.add(adjustedBitmap)
                        }
                        imagePos++
                    } else {
                        if (adjustedBitmap != photos[imagePos]) {
                            photos[imagePos] = adjustedBitmap
                        }
                        imagePos++
                    }
                    photosAttached[imagePos-1] = switchPhotoAttach.isChecked
                    if (photos.size > imagePos) {
                        if(photosAttached.size <= imagePos-1){
                            photosAttached.add(switchPhotoAttach.isChecked)
                        }
                        if(switchPhotoAttach.isChecked && !photosAttached[imagePos] || !switchPhotoAttach.isChecked && photosAttached[imagePos])
                        {
                            switchPhotoAttach.performClick()
                        }
                        detailEditorImageView.setImageBitmap(photos[imagePos])
                        adjustedBitmap = photos[imagePos]
                    } else {
                        if(photosAttached[imagePos-1]){
                            switchPhotoAttach.performClick()
                        }
                        detailEditorImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_add_new_photo_50dp))
                        newImage = true
                    }
                }
            }
        }
    }


    fun onImageAdd(view: View) {
        switchPhotoAttach.visibility = View.VISIBLE
        showPictureDialog()
    }


    fun onColorClick(view: View) {
        showColorDialog()
    }


    fun decodePath(d_path:String): HashMap<Bitmap, String> {
        val resPath: ArrayList<String> = d_path.split(";;;") as ArrayList<String>
        resPath.removeAt(resPath.lastIndex)
        val mapRes = hashMapOf<Bitmap,String>()
        val file = File(resPath[0])
        val exif =ExifInterface(file.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->  90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
        val matrix = Matrix()
        if (rotation.toFloat() != 0f) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }
        val btmp = BitmapFactory.decodeFile(file.path)
        adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
        detailEditorImageView.setImageBitmap(adjustedBitmap)
        mapRes.put(adjustedBitmap, resPath[0])
        for(p in resPath){
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
            photosAttached.add(true)
            mapRes[adjustedBitmapTmp] = p
        }
        return mapRes
    }


    private fun makePath():String{
        var tmpPath = ""
        for (index in 0 until photos.size){
            if(photosAttached[index]){
                tmpPath += if(photos[index] !in alreadySavedPhotos.keys) {
                    saveImage(photos[index]) + ";;;"
                } else {
                    alreadySavedPhotos[photos[index]]+";;;"
                }
            }
        }
        return tmpPath
    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Выберите действие")
        val pictureDialogItems = arrayOf("Выбрать фото из галереи", "Сделать новое фото")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }


    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }


    private fun takePhotoFromCamera() {
        val imageFile = createImageFile()
        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(callCameraIntent.resolveActivity(packageManager) != null) {
            val authorities = "$packageName.fileprovider"
            val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)
            callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(callCameraIntent, CAMERA)
        }
    }


    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }
    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName: String = "JPEG_" + timeStamp + "_"
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDir.exists()) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
    }


    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data.data
                try
                {
                    imageFilePath=getRealPathFromURI(contentURI)
                    val file = File(imageFilePath)
                    val exifData = Uri.fromFile(file)
                    val exif =ExifInterface(exifData.path)
                    val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    val rotationInDegrees =
                            when (rotation) {
                                ExifInterface.ORIENTATION_ROTATE_90 ->  90
                                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                                else -> 0
                            }
                    val matrix = Matrix()
                    if (rotation.toFloat() != 0f) {
                        matrix.preRotate(rotationInDegrees.toFloat())
                    }
                    val btmp = BitmapFactory.decodeFile(imageFilePath)
                    adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
                    detailEditorImageView.setImageBitmap(adjustedBitmap)
                    if(photos.size ==0){
                        photos.add(adjustedBitmap)
                        photosAttached[0]=true
                    }
                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        else if (requestCode == CAMERA && resultCode == Activity.RESULT_OK)
        {
            val file = File(imageFilePath)
            val exifData = Uri.fromFile( File(imageFilePath))
            val exif =ExifInterface(exifData.path)
            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            val rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->  90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            val matrix = Matrix()
            if (rotation.toFloat() != 0f) {
                matrix.preRotate(rotationInDegrees.toFloat())
            }
            val btmp = BitmapFactory.decodeFile(imageFilePath)
            adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
            detailEditorImageView.setImageBitmap(adjustedBitmap)
            if(photos.size ==0){
                photos.add(adjustedBitmap)
                photosAttached[0]=true
            }
            file.delete()
        }
    }


    private fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        if (!wallpaperDirectory.exists())
        {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()
            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }


    companion object {
        private const val IMAGE_DIRECTORY = "/timem8"
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_editor_menu,menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId != R.id.edit_menu_delete){
            this.finish()
        } else {
            db.deleteAllData()
            recreate()
            editTextHomework.setText("")
            editTextTips.setText("")

            switchPhotoAttach.isChecked = false
        }
        return true
    }


    private fun showColorDialog(){
        val builder = ColorPickerDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        builder.setTitle("Выберите цвет")
        builder.colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this,R.drawable.palette))
        builder.setFlagView(FlagView(this, R.layout.flag_view))
        builder.setPreferenceName(subjectName+date)
        builder.setPositiveButton("выбрать") { colorEnvelope ->
            color = colorEnvelope.color
            color = ColorUtils.setAlphaComponent(color, 100)
            val colorCircle = colorView.background.mutate() as GradientDrawable
            colorCircle.setColor(color)
        }
        builder.setNegativeButton("отмена") { dialogInterface, _ -> dialogInterface.dismiss() }
        builder.show()
    }


    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }
    private fun checkPermission(permissionArray: Array<String>): Boolean {
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
                    val requestAgain = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(permissions[i])
                    if(requestAgain){
                        Toast.makeText(this,"Недостаточно прав",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Перейдите в настройки и предоставьте приложению разрещения",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
