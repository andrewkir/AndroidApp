package com.example.andrewforwork.timem8

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.DialogInterface
import android.R.string.cancel
import android.widget.LinearLayout
import android.widget.TextView
import com.skydoves.colorpickerpreference.ColorEnvelope
import com.skydoves.colorpickerpreference.ColorListener
import android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK
import com.skydoves.colorpickerpreference.ColorPickerDialog
import com.example.andrewforwork.timem8.R.id.colorPickerView
import kotlinx.android.synthetic.main.activity_main_daily_schedule.*
import android.R.string.cancel
import android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK
import android.support.v4.content.ContextCompat


class MainDailySchedule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_daily_schedule)

        colorPickerView.setColorListener(ColorListener { colorEnvelope ->
            val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
            linearLayout.setBackgroundColor(colorEnvelope.color)
        })

        val builder = ColorPickerDialog.Builder(this)
        builder.setTitle("ColorPicker Dialog")
        builder.setPreferenceName("MyColorPickerDialog")
        builder.colorPickerView.setPaletteDrawable(ContextCompat.getDrawable(this,R.drawable.palette))
        builder.setFlagView(FlagView(this, R.layout.flag_view))
        builder.setPositiveButton("добавить") { colorEnvelope ->
            val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
            linearLayout.setBackgroundColor(colorEnvelope.color)
        }
        builder.setNegativeButton("отмена") { dialogInterface, i -> dialogInterface.dismiss() }
        builder.show()
    }
}
