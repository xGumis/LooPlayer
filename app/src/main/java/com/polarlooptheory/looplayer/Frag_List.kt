package com.polarlooptheory.looplayer

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item.view.*
import java.lang.ClassCastException

class Frag_List : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    var list: ArrayList<Map<String, String>> = ArrayList()
    var activityCallback: Listener? = null

    interface  Listener{
        fun onButtonClick(pos: Int)
        fun getList(list: ArrayList<Map<String,String>>)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try{
            activityCallback = context as Listener
        }catch(e: ClassCastException){
            throw ClassCastException(context?.toString()+" must implement Listener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view : View = inflater.inflate(R.layout.list, container, false)
        val act = activity
        findMusic()
        viewManager = LinearLayoutManager(act)
        if(savedInstanceState!=null){
            val state: Parcelable? = savedInstanceState.getParcelable("Key")
            if(state!=null)
                viewManager.onRestoreInstanceState(state)
        }
        viewAdapter = MyAdapter(list){activityCallback?.onButtonClick(it)}
        recyclerView = view.findViewById<RecyclerView>(R.id.main).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("Key",viewManager.onSaveInstanceState())
    }

    fun findMusic() {
        val contRes: ContentResolver = context!!.contentResolver
        val songUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? = contRes.query(songUri, null, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val album = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
            val uri = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            do {
                val cT = cursor.getString(title)
                val cAr = cursor.getString(artist)
                val cAl = cursor.getString(album)
                val cU = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cursor.getLong(uri)).toString()
                val map = mapOf("title" to cT, "artist" to cAr, "album" to cAl,"path" to cU)
                list.add(map)
            } while (cursor.moveToNext())
            cursor.close()
            activityCallback?.getList(list)
        }
    }
    class MyAdapter(private val myDataset: ArrayList<Map<String, String>>,val listener: (Int) -> Unit) :
        RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

        class MyViewHolder(val hold: LinearLayout) : RecyclerView.ViewHolder(hold)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                MyViewHolder {
            val hold = LayoutInflater.from(parent.context)
                .inflate(R.layout.item, parent, false) as LinearLayout
            return MyViewHolder(hold)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int){
            holder.hold.title.text = myDataset[position].getValue("title")
            holder.hold.artist.text = myDataset[position].getValue("artist")
            holder.hold.album.text = myDataset[position].getValue("album")
            holder.hold.test.text = myDataset[position].getValue("path")
            holder.hold.setOnClickListener{listener(position)}
        }

        override fun getItemCount() = myDataset.size
    }
}