package com.k10.shootthebird.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.k10.shootthebird.R

class Bird(
    res: Resources,
    screenRatioX: Float,
    screenRatioY: Float
) {

    var x = 0f
    var y = 0f
    var speed = 10f

    var width = 0f
    var height = 0f

    var wasShot = true

    private var birdCounter = 0

    private var bird1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird1)
    private var bird2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird2)
    private var bird3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird3)
    private var bird4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.bird4)

    init {
        width = (bird1.width / 8f) * screenRatioX
        height = (bird1.height / 8f) * screenRatioY

        bird1 = Bitmap.createScaledBitmap(bird1, width.toInt(), height.toInt(), false)
        bird2 = Bitmap.createScaledBitmap(bird2, width.toInt(), height.toInt(), false)
        bird3 = Bitmap.createScaledBitmap(bird3, width.toInt(), height.toInt(), false)
        bird4 = Bitmap.createScaledBitmap(bird4, width.toInt(), height.toInt(), false)

        y = -height
    }

    fun getBird(): Bitmap{
        when(birdCounter){
            1 -> {
                birdCounter++
                return bird1
            }
            2 -> {
                birdCounter++
                return bird2
            }
            3 -> {
                birdCounter++
                return bird3
            }
            else -> {
                birdCounter = 1
                return bird4
            }
        }
    }

    fun getCollisionShape(): Rect {
        return Rect(x.toInt(), y.toInt(), (x+width).toInt(), (y+ height).toInt())
    }
}