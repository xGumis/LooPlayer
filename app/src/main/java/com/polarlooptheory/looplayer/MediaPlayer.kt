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

    private var mp = MediaPlayer()
    private lateinit var runnable: Runnable
    private lateinit var map: Map<String,String>
    private var songid = 0
    private var position = 0
    private var handler: Handler = Handler()
    var activityCallback: Listener? = null

    interface Listener{
        fun getMap(pos: Int): Map<String,String>
        fun getId(): Int
        fun getListSize(): Int
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
        songid = activityCallback!!.getId()
        val con = activity!!.applicationContext
        var resume = true
        position = 0
        Play(resume,view,con,songid)

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
            if(songid-1<0){songid=activityCallback!!.getListSize()-1}
            else {songid--}
            resume = mp.isPlaying
            position = 0
            Play(resume,view,con,songid)
        }

        view.findViewById<ImageButton>(R.id.nextButton).setImageResource(android.R.drawable.ic_media_next)
        view.findViewById<ImageButton>(R.id.nextButton).setOnClickListener {
            if(songid+1>activityCallback!!.getListSize()-1){songid=0}
            else {songid++}
            resume = mp.isPlaying
            position = 0
            Play(resume,view,con,songid)
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

            handler.postDelayed(runnable, 100)
        }
        handler.postDelayed(runnable, 100)
    }

    private fun initializeTitle(view: View){
        view.findViewById<TextView>(R.id.tytul).text = map["title"]
        view.findViewById<TextView>(R.id.wykonawca).text = map["artist"]
        view.findViewById<TextView>(R.id.album).text = map["album"]
    }

    private fun Play(resume: Boolean,view: View,context: Context,id : Int){
        map = activityCallback!!.getMap(id)
        mp.reset()
        mp = MediaPlayer.create(context, Uri.parse(map["path"]))
        initializeSeekBar(view)
        initializeTitle(view)
        if (resume) mp.start()
        mp.setOnCompletionListener {
            if(songid+1>activityCallback!!.getListSize()-1){songid=0}
            else {songid++}
            position = 0
            Play(resume,view,context,songid)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
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
