package com.andrewkir.andrewforwork.timem8.Editors

import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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


class MainDetailEditor : AppCompatActivity() {

    var subjectName = ""
    var date = ""
    var path = ""
    var count = 0
    var imagePos = 0
    var color = Color.WHITE
    var imageFilePath = ""
    var newImage = false
    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var subject: SubDetail
    lateinit var adjustedBitmap: Bitmap
    var alreadySavedPhotos = hashMapOf<Bitmap,String>()
    var photos = ArrayList<Bitmap>()
    var photosAttached = ArrayList<Boolean>()
    internal lateinit var db: DBdetailinfo
    lateinit var sPref: SharedPreferences
    var stat: String = ""
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
        //switchPhotoAttach.visibility = View.GONE
        db = DBdetailinfo(this)
        subjectName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        count = intent.getIntExtra("COUNT_SUB",0)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        var count = intent.getIntExtra("COUNT_SUB",0)
        try {
            subject = db.allSubDetailByDay(subjectName,date,count)[0]
            color = subject.color
            var colorCircle = colorView.background.mutate() as GradientDrawable
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
            var dateArr =date.split(".")
            println(dateArr)
            println(alreadySavedPhotos.values)
            path = makePath()
            var subdt = SubDetail(
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
            println("color $color")
            try {
                db.addSub_detail(subdt)
                Toast.makeText(this,"добавлено",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(this,"обновлено",Toast.LENGTH_SHORT).show()
                db.updateSub_detail(subdt)
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
            println(imagePos)
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
            println(imagePos)
        }

    }
    fun OnimageAdd(view: View){
        switchPhotoAttach.visibility = View.VISIBLE
        showPictureDialog()
    }
    fun onColorClick(view: View){
        showColorDialog()
    }
    fun decodePath(d_path:String): HashMap<Bitmap, String> {
        var res_path = ArrayList<String>()
        res_path = d_path.split(";;;") as ArrayList<String>
        res_path.removeAt(res_path.lastIndex)
        var map_res = hashMapOf<Bitmap,String>()
        val file = File(res_path[0])
        var exif =ExifInterface(file.absolutePath)
        val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        var rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->  90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
        var matrix = Matrix()
        if (rotation.toFloat() != 0f) {
            matrix.preRotate(rotationInDegrees.toFloat())
        }
        var btmp = BitmapFactory.decodeFile(file.path)
        adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
        detailEditorImageView.setImageBitmap(adjustedBitmap)
        map_res.put(adjustedBitmap, res_path[0])
        println(res_path)
        for(p in res_path){
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
                photosAttached.add(true)
                map_res.put(adjustedBitmaptmp, p)
        }
        return map_res
    }
    fun makePath():String{
        var tmpPath = ""
        for (index in 0..photos.size-1){
            if(photosAttached[index]){
                if(photos[index] !in alreadySavedPhotos.keys) {
                    tmpPath += saveImage(photos[index]) + ";;;"
                } else {
                    tmpPath +=  alreadySavedPhotos[photos[index]]+";;;"
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
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }
    fun choosePhotoFromGallary() {
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

    fun getRealPathFromURI(uri: Uri): String {
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
        println(storageDir)
        if(!storageDir.exists()) storageDir.mkdirs()
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        imageFilePath = imageFile.absolutePath
        return imageFile
    }
    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
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
                    var exif =ExifInterface(exifData.path)
                    val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                    var rotationInDegrees =
                            when (rotation) {
                                ExifInterface.ORIENTATION_ROTATE_90 ->  90
                                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                                else -> 0
                            }
                    var matrix = Matrix()
                    if (rotation.toFloat() != 0f) {
                        matrix.preRotate(rotationInDegrees.toFloat())
                    }
                    var btmp = BitmapFactory.decodeFile(imageFilePath)
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
            var exif =ExifInterface(exifData.path)
            val rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            var rotationInDegrees =
                when (rotation) {
                    ExifInterface.ORIENTATION_ROTATE_90 ->  90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            var matrix = Matrix()
            if (rotation.toFloat() != 0f) {
                matrix.preRotate(rotationInDegrees.toFloat())
            }
            var btmp = BitmapFactory.decodeFile(imageFilePath)
            adjustedBitmap = Bitmap.createBitmap(btmp, 0, 0, btmp.width, btmp.height, matrix, true)
            detailEditorImageView.setImageBitmap(adjustedBitmap)
            if(photos.size ==0){
                photos.add(adjustedBitmap)
                photosAttached[0]=true
            }
            file.delete()
        }
    }
    fun saveImage(myBitmap: Bitmap):String {
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
        private val IMAGE_DIRECTORY = "/timem8"
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
    fun showColorDialog(){
        val builder = ColorPickerDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK)
        builder.setTitle("Выберите цвет")
        builder.colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this,R.drawable.palette))
        builder.setFlagView(FlagView(this, R.layout.flag_view))
        builder.setPreferenceName(subjectName+date)
        builder.setPositiveButton("выбрать") { colorEnvelope ->
            color = colorEnvelope.color
            color = ColorUtils.setAlphaComponent(color, 100)
            var colorCircle = colorView.background.mutate() as GradientDrawable
            colorCircle.setColor(color)
        }
        builder.setNegativeButton("отмена") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var typedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            setTaskDescription(ActivityManager.TaskDescription("TimeM8", bm, typedValue.data))
        }
    }
}
