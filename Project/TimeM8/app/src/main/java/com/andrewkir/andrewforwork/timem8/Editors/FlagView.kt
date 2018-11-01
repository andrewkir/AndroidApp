package com.andrewkir.andrewforwork.timem8.Editors

import android.content.Context
import android.view.View
import android.widget.TextView
import com.andrewkir.andrewforwork.timem8.R
import com.skydoves.colorpickerpreference.ColorEnvelope
import com.skydoves.colorpickerpreference.FlagView


class FlagView(context: Context, layout: Int) : FlagView(context, layout) {
    private val textView: TextView
    private val view: View

    init {
        textView = findViewById(R.id.flagColorCode)
        view = findViewById<View>(R.id.flagColorLayout)
    }

    override fun onRefresh(colorEnvelope: ColorEnvelope?) {
        val color = colorEnvelope
        textView.text = "#" + String.format("%06X", 0xFFFFFF and color!!.color)
        view.setBackgroundColor(color.color)
    }
}