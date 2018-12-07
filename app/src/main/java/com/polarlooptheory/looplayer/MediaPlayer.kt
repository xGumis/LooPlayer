package com.polarlooptheory.looplayer

import android.content.Context
import android.os.Bundle
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.ImageButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_media_player.*

class MediaPlayer : Fragment() {

    private lateinit var mp: MediaPlayer
    private lateinit var runnable: Runnable
    private lateinit var map: Map<String,String>
    private var handler: Handler = Handler()
    var activityCallback: Listener? = null

    interface Listener{
        fun getMap(pos: Int): Map<String,String>
        fun getId(): Int
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            activityCallback = context as Listener
        }catch(e: ClassCastException){
            throw ClassCastException(context?.toString()+" must implement Listener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_media_player,container,false)
        var id = activityCallback!!.getId()
        val con = activity!!.applicationContext
        var resume = false
        map = activityCallback!!.getMap(id)
        mp = MediaPlayer.create(con,Uri.parse(map["path"]))
        var position = 0
        initializeSeekBar(view)
        initializeTitle(view)
        mp.start()

        view.findViewById<ImageButton>(R.id.playButton).setImageResource(android.R.drawable.ic_media_play)
        view.findViewById<ImageButton>(R.id.playButton).setOnClickListener {
            if (!mp.isPlaying) {
                mp.seekTo(position)
                mp.start()
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
            }
        }

        view.findViewById<ImageButton>(R.id.pauseButton).setImageResource(android.R.drawable.ic_media_pause)
        view.findViewById<ImageButton>(R.id.pauseButton).setOnClickListener {
            if (mp.isPlaying) {
                position = mp.currentPosition
                mp.pause()
                pauseButton.visibility = View.GONE
                playButton.visibility = View.VISIBLE
            }
        }


        view.findViewById<ImageButton>(R.id.reverseButton).setImageResource(android.R.drawable.ic_media_rew)
        view.findViewById<ImageButton>(R.id.reverseButton).setOnClickListener {
            position = mp.currentPosition
            if (position <= 5000) {
                mp.seekTo(0)
                position = 0
            } else {
                mp.seekTo(position - 5000)
                position -= 5000
            }
        }


        view.findViewById<ImageButton>(R.id.forwardButton).setImageResource(android.R.drawable.ic_media_ff)
        view.findViewById<ImageButton>(R.id.forwardButton).setOnClickListener {
            position = mp.currentPosition
            if (position + 5000 >= mp.duration) {
                mp.pause()
                position = mp.duration
            } else {
                mp.seekTo(position + 5000)
                position += 5000
            }
        }

        view.findViewById<ImageButton>(R.id.prevButton).setImageResource(android.R.drawable.ic_media_previous)
        view.findViewById<ImageButton>(R.id.prevButton).setOnClickListener {
            resume = mp.isPlaying
            mp.stop()
            map = activityCallback!!.getMap(id+1)
            mp = MediaPlayer.create(con,Uri.parse(map["path"]))
            position = 0
            initializeSeekBar(view)
            initializeTitle(view)
            if(resume) mp.start()
        }

        view.findViewById<ImageButton>(R.id.nextButton).setImageResource(android.R.drawable.ic_media_next)
        view.findViewById<ImageButton>(R.id.nextButton).setOnClickListener {
            resume = mp.isPlaying
            mp.stop()
            mp = MediaPlayer.create(con,Uri.parse(map["path"]))
            position = 0
            initializeSeekBar(view)
            initializeTitle(view)
            if(resume) mp.start()
        }

        view.findViewById<SeekBar>(R.id.seekBar2).setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
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
        return view
    }
    private fun initializeSeekBar(view: View) {
        view.findViewById<SeekBar>(R.id.seekBar2).max = mp.seconds


        runnable = Runnable {
            view.findViewById<SeekBar>(R.id.seekBar2).progress = mp.currentSeconds
            val minuty = mp.currentSeconds/60
            val sekundy = mp.currentSeconds-minuty*60

            if(sekundy<10) ubyo.text = "$minuty:0$sekundy"
            else ubyo.text = "$minuty:$sekundy"
            val diff = mp.seconds - mp.currentSeconds
            val diffmin = diff/60
            val diffsec = diff-diffmin*60
            if(diffsec<10) zostao.text = "$diffmin:0$diffsec"
            else zostao.text = "$diffmin:$diffsec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }

    private fun initializeTitle(view: View){
        view.findViewById<TextView>(R.id.tytul).text = map["title"]
        view.findViewById<TextView>(R.id.wykonawca).text = map["artist"]
        view.findViewById<TextView>(R.id.album).text = map["album"]
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
