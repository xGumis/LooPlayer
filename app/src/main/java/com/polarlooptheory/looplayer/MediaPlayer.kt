package com.polarlooptheory.looplayer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.media.MediaPlayer
import android.view.View
import android.widget.ImageButton

import kotlinx.android.synthetic.main.activity_media_player.*

class MediaPlayer : AppCompatActivity() {

    private lateinit var mp: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        mp = MediaPlayer.create(this, R.raw.test)
        var position = 0


        playButton.setOnClickListener {
            if(mp.isPlaying==false) {
                mp.seekTo(position)
                mp.start()
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
            }
        }

        pauseButton.setOnClickListener {
            if(mp.isPlaying) {
                position = mp.currentPosition
                mp.pause()
                pauseButton.visibility = View.GONE
                playButton.visibility = View.VISIBLE
            }
        }

        prevButton.setOnClickListener {
            position = mp.currentPosition
            if(position<=5000){
                mp.seekTo(0)
                position = 0
            }
            else{
                mp.seekTo(position-5000)
                position-=5000
            }
        }

        nextButton.setOnClickListener {
            position = mp.currentPosition
            if(position+5000>=mp.duration){
                mp.pause()
                position=mp.duration
            }
            else{
                mp.seekTo(position+5000)
                position+=5000
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }
}
