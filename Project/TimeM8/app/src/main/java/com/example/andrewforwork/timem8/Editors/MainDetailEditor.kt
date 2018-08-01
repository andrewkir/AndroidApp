package com.example.andrewforwork.timem8.Editors

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaScannerConnection
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.andrewforwork.timem8.DataBase.DBdetailinfo
import com.example.andrewforwork.timem8.R
import com.example.andrewforwork.timem8.Subject.SubDetail
import kotlinx.android.synthetic.main.activity_main_detail_editor.*
import kotlinx.android.synthetic.main.activity_main_schedule_detail.*
import java.util.*
import android.os.StrictMode
import android.support.v4.content.FileProvider
import android.view.View
import java.io.*
import java.text.SimpleDateFormat


class MainDetailEditor : AppCompatActivity() {

    var subjectName = ""
    var date = ""
    var path = ""
    var count = 0
    var imageFilePath = ""
    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var subject: SubDetail
    lateinit var adjustedBitmap: Bitmap
    internal lateinit var db: DBdetailinfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_detail_editor)

        db = DBdetailinfo(this)
        subjectName = intent.getStringExtra("NAME_SUB")
        date = intent.getStringExtra("DATE")
        count = intent.getIntExtra("COUNT_SUB",0)

        btnDetailEditorPhoto.setOnClickListener {
            showPictureDialog()
        }

        var count = intent.getIntExtra("COUNT_SUB",0)
        try {
            subject = db.allSubDetailByDay(subjectName,date,count)[0]
            //TODO change later
            when(subject.hasimage){
                1 -> {
                    switchPhotoAttach.performClick()
                    val bmImg = BitmapFactory.decodeFile(subject.path)
                    bmImg.rotate(90F)
                    detailEditorImageView.setImageBitmap(bmImg)
                }
            }
            editTextTips.setText(subject.tips)
            editTextHomework.setText(subject.homework)
        }
        catch(e: Exception) {

        }
        saveBtn.setOnClickListener {
            var dateArr =date.split(".")
            println(dateArr)
            var subdt = SubDetail(
                    id=Integer.parseInt(dateArr[0]+dateArr[1]+dateArr[2]+count.toString()),
                    date = date,
                    parent = subjectName,
                    homework = if(editTextHomework.text.isEmpty()) "" else if(editTextHomework.text.toString().takeLast(1)=="\n")editTextHomework.text.toString() else editTextHomework.text.toString()+"\n",
                    hasimage = if(switchPhotoAttach.isChecked) 1 else 0,
                    path = if(::adjustedBitmap.isInitialized) saveImage(adjustedBitmap) else if(::subject.isInitialized) subject.path else "", //TODO CHANGE LATER
                    tips = if(editTextTips.text.isEmpty()) "" else editTextTips.text.toString()+"\n",
                    count = count

            )
            try {
                db.addSub_detail(subdt)
                Toast.makeText(this,"добавлено",Toast.LENGTH_SHORT).show()
            } catch (e: Exception){
                Toast.makeText(this,"сохранено",Toast.LENGTH_SHORT).show()
                db.updateSub_detail(subdt)
            }
        }
    }
    fun OnimageAdd(view: View){
        showPictureDialog()
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
            val authorities = packageName + ".fileprovider"
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
                val contentURI = data!!.data
                try
                {
                    imageFilePath=getRealPathFromURI(contentURI)
                    val file = File(imageFilePath)
                    val exifData = Uri.fromFile(file)
                    var exif =ExifInterface(exifData.path);
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
            var exif =ExifInterface(exifData.path);
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

    //TODO сделать своё menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_editor_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        db.deleteAllData()
        editTextHomework.setText("")
        return true
    }
}
