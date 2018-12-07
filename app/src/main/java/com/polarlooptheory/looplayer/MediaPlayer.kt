package com.polarlooptheory.looplayer

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.media.MediaPlayer
import android.os.Handler
import android.view.View
import android.widget.SeekBar
import android.widget.ImageButton

import kotlinx.android.synthetic.main.activity_media_player.*

class MediaPlayer : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private lateinit var runnable: Runnable
    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_player)

        mp = MediaPlayer.create(this, R.raw.test)
        var position = 0
        initializeSeekBar()



        playButton.setOnClickListener {
            if (!mp.isPlaying) {
                mp.seekTo(position)
                mp.start()
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
            }
        }

        pauseButton.setOnClickListener {
            if (mp.isPlaying) {
                position = mp.currentPosition
                mp.pause()
                pauseButton.visibility = View.GONE
                playButton.visibility = View.VISIBLE
            }
        }

        prevButton.setOnClickListener {
            position = mp.currentPosition
            if (position <= 5000) {
                mp.seekTo(0)
                position = 0
            } else {
                mp.seekTo(position - 5000)
                position -= 5000
            }
        }

        nextButton.setOnClickListener {
            position = mp.currentPosition
            if (position + 5000 >= mp.duration) {
                mp.pause()
                position = mp.duration
            } else {
                mp.seekTo(position + 5000)
                position += 5000
            }
        }

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mp.seekTo(progress * 1000)
                }
            }
                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

        })


    }
    private fun initializeSeekBar() {
        seekBar2.max = mp.seconds

        runnable = Runnable {
            seekBar2.progress = mp.currentSeconds

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }

    val MediaPlayer.seconds:Int
        get() {
            return this.duration / 1000
        }



    val MediaPlayer.currentSeconds:Int
        get() {
            return this.currentPosition/1000
        }


}
