package com.k10.shootthebird.game

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.SurfaceView
import com.k10.shootthebird.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class GameView(
    context: Context,
    private val screenX: Float,
    private val screenY: Float
) : SurfaceView(context) {

    private var sharedPreferences: SharedPreferences

    private var isPlaying = false

    private val paint: Paint = Paint().apply {
        textSize = 128f
        color = 0xFF000000.toInt()
    }

    private val screenRatioX = 1920f / screenX
    private val screenRatioY = 1080f / screenY

    private var background1: GameBackground = GameBackground(screenX, screenY, resources)
    private var background2: GameBackground = GameBackground(screenX, screenY, resources)

    private val plane: Plane = Plane(this, screenY, screenRatioX, screenRatioY, resources)

    private val bullets: ArrayList<Bullet> = ArrayList()
    private val trashBullets: ArrayList<Bullet> = ArrayList()

    private val rnd: Random = Random()

    private var isGameOver = false

    private var score = 0

    private var soundPool: SoundPool
    private var soundId: Int

    private val birds = arrayOf(
        Bird(resources, screenRatioX, screenRatioY),
        Bird(resources, screenRatioX, screenRatioY),
        Bird(resources, screenRatioX, screenRatioY),
        Bird(resources, screenRatioX, screenRatioY)
    )

    private var bound = 10 * screenRatioX

    init {
        background2.x = screenX

        for (bird in birds)
            bird.speed = rnd.nextFloat() * bound
        bound = 30 * screenRatioX

        sharedPreferences = context.getSharedPreferences("player_data", MODE_PRIVATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            soundPool = SoundPool.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build()
                )
                .build()
        } else {
            soundPool = SoundPool(1, AudioManager.STREAM_MUSIC, 0)
        }

        soundId = soundPool.load(context, R.raw.shoot, 1)
    }

    fun resume() {
        if (!isPlaying) {
            isPlaying = true
            startBackgroundAnimation()
        }
    }

    fun pause() {
        isPlaying = false
    }

    private fun startBackgroundAnimation() {
        CoroutineScope(Default).launch {
            while (isPlaying) {
                update()
                draw()
                delay(16)
            }
        }
    }

    private fun update() {
        background1.x -= 10 * screenRatioX
        background2.x -= 10 * screenRatioX

        if (background1.x + background1.background.width < 0) {
            background1.x = screenX
        }

        if (background2.x + background2.background.width < 0) {
            background2.x = screenX
        }

        if (plane.isGoingUp)
            plane.y -= 30 * screenRatioY
        else
            plane.y += 30 * screenRatioY

        if (plane.y < 0)
            plane.y = 0f

        if (plane.y > screenY - plane.height)
            plane.y = screenY - plane.height

        for (bullet in bullets) {
            if (bullet.x > screenX) {
                trashBullets.add(bullet)
            }
            bullet.x += 50 * screenRatioX

            for (bird in birds) {
                if (Rect.intersects(bird.getCollisionShape(), bullet.getCollisionShape())) {
                    score++
                    bird.x = -500f
                    bullet.x = screenX + 500
                    bird.wasShot = true
                }
            }
        }
        for (b in trashBullets) {
            bullets.remove(b)
        }
        trashBullets.clear()

        for (bird in birds) {

            bird.x -= bird.speed

            if (bird.x + bird.width < 0) {

                if (!bird.wasShot) {
                    isGameOver = true
                    return
                }

                //increasing bird speed
                bird.speed *= 1.05f
                if (bird.speed > bound) {
                    bird.speed = bound
                }

//                if (bird.speed < 10 * screenRatioX)
//                    bird.speed = 10 * screenRatioX

                bird.x = screenX
                bird.y = rnd.nextFloat() * (screenY - bird.height)

                bird.wasShot = false
            }

            if (Rect.intersects(bird.getCollisionShape(), plane.getCollisionShape())) {
                isGameOver = true
                return
            }
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas: Canvas = holder.lockCanvas()
            //draw background
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint)
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint)

            //draw birds
            for (bird in birds) {
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint)
            }

            canvas.drawText("Score: $score", screenX / 2 - 250, 164f, paint)

            if (isGameOver) {
                isPlaying = false
                canvas.drawBitmap(plane.getDead(), plane.x, plane.y, paint)
                holder.unlockCanvasAndPost(canvas)
                saveHighScore()
                waitBeforeExiting()
                return
            }

            //draw plane
            canvas.drawBitmap(plane.getPlane(), plane.x, plane.y, paint)

            //draw bullet
            for (bullet in bullets) {
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint)
            }

            holder.unlockCanvasAndPost(canvas)
        }
    }

    private var touchEventPoint: PointerCoords = PointerCoords()
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {

            val pointerIndex = it.actionIndex

            //val pointerId = it.getPointerId(pointerIndex)

            it.getPointerCoords(pointerIndex, touchEventPoint)

            when (it.actionMasked) {
                ACTION_DOWN -> {
                    if (touchEventPoint.x < screenX / 2) {
                        plane.isGoingUp = true
                    }
                }
                ACTION_POINTER_DOWN -> {
                }
                ACTION_MOVE -> {
                    if (touchEventPoint.x < screenX / 2) {
                        plane.isGoingUp = true
                    }
                }
                ACTION_UP -> {
                    plane.isGoingUp = false
                    if (touchEventPoint.x > screenX / 2) {
                        //shoot bullet
                        plane.toShoot++
                    }
                }
                ACTION_POINTER_UP -> {
                    if (touchEventPoint.x > screenX / 2) {
                        //shoot bullet
                        plane.toShoot++
                    }
                }
            }
        }
        return true
    }

    fun newBullet() {
        val bullet = Bullet(resources, screenRatioX, screenRatioY)
        bullet.x = plane.x + plane.width
        bullet.y = plane.y + plane.height / 2

        bullets.add(bullet)
        soundPool.play(soundId, 0.1f, 0.1f, 0,0, 1f)
    }

    private fun saveHighScore() {
        if (sharedPreferences.getInt("highscore", 0) < score) {
            sharedPreferences.edit().putInt("highscore", score).apply()
        }
    }

    private fun waitBeforeExiting() {
    }
}