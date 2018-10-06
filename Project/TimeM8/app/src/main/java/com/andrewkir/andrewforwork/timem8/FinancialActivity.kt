package com.andrewkir.andrewforwork.timem8

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_financial.*


class FinancialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_financial)

        waveView.max = 100
        waveView.setWaveColor(Color.GREEN)
        var progress = 10.toLong()
        waveView.progress = progress
        progressAdd.setOnClickListener {
            progress+=10.toLong()
            waveView.progress = progress
            if (waveView.progress in 50..79){
//                waveView.setWaveColor(R.color.material_yellow_700)
                tipsFinancial.text = "Возможно стоит тратить поменьше"
                waveView.setWaveColor(Color.YELLOW)
            } else if(waveView.progress >= 80){
                tipsFinancial.text = "Вы скоро достигнете лимита!"
                waveView.setWaveColor(Color.RED)
            }
        }
    }
}
