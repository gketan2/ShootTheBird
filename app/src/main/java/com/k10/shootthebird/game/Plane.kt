package com.k10.shootthebird.game

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import com.k10.shootthebird.R

class Plane(
    private val gameView: GameView,
    screenY: Float,
    screenRatioX: Float,
    screenRatioY: Float,
    res: Resources
) {

    var isGoingUp = false
    var toShoot: Int = 0
    private var shootCounter: Int = 0

    var x = 0f
    var y = 0f
    private var wingCount:Int = 0

    var width = 1f
    var height = 1f

    private var plane1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.plane1)
    private var plane2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.plane2)
    private var dead: Bitmap = BitmapFactory.decodeResource(res, R.drawable.dead)

    private var shoot1: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot1)
    private var shoot2: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot2)
    private var shoot3: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot3)
    private var shoot4: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot4)
    private var shoot5: Bitmap = BitmapFactory.decodeResource(res, R.drawable.shoot5)

    init {
        //resizing to accomodate screensize
        width = (plane1.width / 4f) * screenRatioX
        height = (plane1.height / 4f) * screenRatioY

        plane1 = Bitmap.createScaledBitmap(plane1, width.toInt(), height.toInt(), false)
        plane2 = Bitmap.createScaledBitmap(plane2, width.toInt(), height.toInt(), false)

        shoot1 = Bitmap.createScaledBitmap(shoot1, width.toInt(), height.toInt(), false)
        shoot2 = Bitmap.createScaledBitmap(shoot2, width.toInt(), height.toInt(), false)
        shoot3 = Bitmap.createScaledBitmap(shoot3, width.toInt(), height.toInt(), false)
        shoot4 = Bitmap.createScaledBitmap(shoot4, width.toInt(), height.toInt(), false)
        shoot5 = Bitmap.createScaledBitmap(shoot5, width.toInt(), height.toInt(), false)
        dead  = Bitmap.createScaledBitmap(dead, width.toInt(), height.toInt(), false)

        //initial y position of the plane
        y = screenY / 2 - height / 2

        //fixed x position of plane (will never change)
        x = 64 * screenRatioX
    }

    //provide plane1,plane2,plane1.... alternative
    fun getPlane(): Bitmap{
        if(toShoot != 0){
            when(shootCounter){
                1-> {
                    shootCounter++
                    return shoot1
                }
                2-> {
                    shootCounter++
                    return shoot2
                }
                3-> {
                    shootCounter++
                    return shoot3
                }
                4-> {
                    shootCounter++
                    return shoot4
                }
                else -> {
                    shootCounter = 1
                    toShoot--
                    gameView.newBullet()
                    return shoot5
                }
            }
        }

        if(wingCount == 0){
            wingCount = 1
            return plane1
        }
        wingCount = 0
        return plane2
    }

    fun getDead(): Bitmap{
        return dead
    }

    fun getCollisionShape(): Rect {
        return Rect(x.toInt(), y.toInt(), (x+width).toInt(), (y+ height).toInt())
    }
}