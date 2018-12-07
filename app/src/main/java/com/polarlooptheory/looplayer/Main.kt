package com.polarlooptheory.looplayer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log

class Main : FragmentActivity(), Frag_List.Listener {

    var list: ArrayList<Map<String,String>> = ArrayList()
    private lateinit var frag: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
    }

    //region List
    fun use_List(){
        frag = Frag_List()
        supportFragmentManager.beginTransaction().replace(R.id.mainframe,frag).commit()
    }
    override fun onButtonClick(pos: Int) {
        println(list[pos].getValue("title"))
        //TODO("połączenie fragmentów")
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
}
