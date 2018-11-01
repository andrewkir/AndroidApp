package com.andrewkir.andrewforwork.timem8.Services

import android.animation.AnimatorSet
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator
import android.animation.ObjectAnimator
import android.view.animation.DecelerateInterpolator
import android.animation.Animator
import com.gelitenight.waveview.library.WaveView


class WaveHelper(private val mWaveView: WaveView) {

    private var mAnimatorSet: AnimatorSet? = null
    private var level =0.5f

    fun start(level:Float) {
        this.level = level
        initAnimation()
        mWaveView.isShowWave = true
        if (mAnimatorSet != null) {
            mAnimatorSet!!.start()
        }
    }

    private fun initAnimation() {
        val animators = ArrayList<Animator>()

        // horizontal animation.
        // wave waves infinitely.
        val waveShiftAnim = ObjectAnimator.ofFloat(
                mWaveView, "waveShiftRatio", 0f, 1f)
        waveShiftAnim.repeatCount = ValueAnimator.INFINITE
        waveShiftAnim.duration = 1000
        waveShiftAnim.interpolator = LinearInterpolator()
        animators.add(waveShiftAnim)

        // vertical animation.
        // water level increases from 0 to center of WaveView
        val waterLevelAnim = ObjectAnimator.ofFloat(
                mWaveView, "waterLevelRatio", 0f, level)
        waterLevelAnim.duration = 8000
        waterLevelAnim.interpolator = DecelerateInterpolator()
        animators.add(waterLevelAnim)

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        val amplitudeAnim = ObjectAnimator.ofFloat(
                mWaveView, "amplitudeRatio", 0.008f, 0.03f)
        amplitudeAnim.repeatCount = ValueAnimator.INFINITE
        amplitudeAnim.repeatMode = ValueAnimator.REVERSE
        amplitudeAnim.duration = 5000
        amplitudeAnim.interpolator = LinearInterpolator()
        animators.add(amplitudeAnim)

        mAnimatorSet = AnimatorSet()
        mAnimatorSet!!.playTogether(animators)
    }

    fun cancel() {
        if (mAnimatorSet != null) {
            //            mAnimatorSet.cancel();
            mAnimatorSet!!.end()
        }
    }
}