package com.polarlooptheory.looplayer

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlin.system.exitProcess

class Main : FragmentActivity(), Frag_List.Listener, MediaPlayer.Listener {

    override fun getId(): Int {
        return id
    }

    override fun getMap(pos: Int): Map<String, String> {
        return list[pos]
    }

    override fun getListSize(): Int {
        return list.size
    }

    var list: ArrayList<Map<String,String>> = ArrayList()
    private lateinit var frag: Fragment
    private var id: Int = -1
    private var state: State = State.PLAYER
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val ord = savedInstanceState?.getInt("state")
        if(ord!=null)
            state = State.values()[ord]
        setupPermissions()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState?.putInt("state",state.ordinal)
    }

    //region List
    fun use_List(){
        if(state!=State.LIST){
            frag = Frag_List()
            supportFragmentManager.beginTransaction().replace(R.id.mainframe,frag).commit()
            state = State.LIST
        }
    }
    override fun onButtonClick(pos: Int) {
        frag = MediaPlayer()
        id = pos
        supportFragmentManager.beginTransaction().replace(R.id.mainframe,frag).commit()
        state = State.PLAYER
    }
    override fun getList(list: ArrayList<Map<String, String>>) {
        this.list = list
    }
    //endregion
    //region Permission
    private val TAG = "Permission"
    private val RECORD_REQUEST_CODE = 101
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }else use_List()
    }
    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            RECORD_REQUEST_CODE)
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                    use_List()
                }
            }
        }
    }

    //endregion
    override fun onBackPressed() {
        if(state == State.PLAYER){
            use_List()
        }else{
            exitProcess(0)
        }
    }

    enum class State{LIST,PLAYER}
}
