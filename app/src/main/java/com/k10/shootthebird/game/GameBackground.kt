package com.k10.shootthebird.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.k10.shootthebird.R

class GameBackground(screenX: Float, screenY: Float, res: Resources) {
    var x = 0f
    var y = 0f
    var background: Bitmap = BitmapFactory.decodeResource(res, R.drawable.background)

    init {
        background = Bitmap.createScaledBitmap(background, screenX.toInt(), screenY.toInt(), false)
    }
}