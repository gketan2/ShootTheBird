package com.k10.shootthebird.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.k10.shootthebird.R

class Bullet(
    res: Resources,
    screenRatioX: Float,
    screenRatioY: Float
) {
    var x = 0f
    var y = 0f
    private var height = 0f
    private var width = 0f

    var bullet: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bullet)

    init {
        width = (bullet.width / 4f) * screenRatioX
        height = (bullet.height / 4f) * screenRatioY

        bullet = Bitmap.createScaledBitmap(bullet, width.toInt(), height.toInt(), false)
    }

    fun getCollisionShape(): Rect {
        return Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
    }
}