package com.k10.shootthebird

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        setContentView(R.layout.activity_home)

        sharedPreferences = getSharedPreferences("player_data", MODE_PRIVATE)

        playButton.setOnClickListener(this)
        soundToggleButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.playButton -> {
                val i = Intent(this, GameActivity::class.java)
                startActivity(i)
            }
            R.id.soundToggleButton -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        highScoreText.text = "HighScore : ${sharedPreferences.getInt("highscore",0)}"
    }
}