package com.example.synctest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    var adapter: RecyclerAdapter? = null
    var data: MutableList<Contact>? = null
    var TAG = "MainActivity"
    var broadcastReceiver: BroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = RecyclerAdapter(this)
        rvNameList.adapter = adapter
        rvNameList.setHasFixedSize(true)
        readFromLocalStorage()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                readFromLocalStorage()
            }
        }
    }

    public fun submitName(view: View) {
        var name: String = editTextTextPersonName.text.toString()
        saveToAppServer(name)
        editTextTextPersonName.setText("")
    }

    private fun readFromLocalStorage() {
        data?.clear()
        adapter?.clear()

        var dbHelper: DbHelper = DbHelper(this)
        var database: SQLiteDatabase = dbHelper.readableDatabase

        var cursor: Cursor = dbHelper.readFromLocalDatabase(database)

        while (cursor.moveToNext()) {
            var name: String = cursor.getString(cursor.getColumnIndex(DbContract().NAME))
            var sync_status: Int = cursor.getInt(cursor.getColumnIndex(DbContract().SYNC_STATUS))
            data?.add(Contact(name, sync_status))
            adapter?.add(Contact(name, sync_status))
        }
        adapter?.notifyDataSetChanged()
        cursor.close()
        dbHelper.close()
    }


    private fun saveToAppServer(name: String) {

        if (checkNetworkConnection()) {

            val stringRequest: StringRequest = object : StringRequest(
                Method.POST,
                DbContract().SERVER_URL,
                Response.Listener { response ->
                    Log.d(TAG, response)
                    try {
                        var jsonObject: JSONObject = JSONObject(response)
                        var Response: String = jsonObject.getString("response")
                        if (Response.equals("OK")) {
                            saveToLocalStorage(name, DbContract().SYNC_STATUS_OK)
                        } else {
                            saveToLocalStorage(name, DbContract().SYNC_STATUS_FAILED)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                },
                Response.ErrorListener { error ->
                    VolleyLog.d(TAG, "Error: " + error.message)
                    Log.d(TAG, "" + error.message + "," + error.toString())
                    saveToLocalStorage(name, DbContract().SYNC_STATUS_FAILED)
                }) {
                override fun getParams(): Map<String, String>? {
                    val params: MutableMap<String, String> =
                        HashMap()
                    params["name"] = name
                    return params
                }

//                @Throws(AuthFailureError::class)
//                override fun getHeaders(): Map<String, String> {
//                    val headers: MutableMap<String, String> =
//                        HashMap()
//                    headers["Content-Type"] = "application/x-www-form-urlencoded"
//                    headers["status"] = "value"
//                    return headers
//                }
            }
            MySingleton(this).getInstance(this).addToRequestQueue(stringRequest);

        } else {
            saveToLocalStorage(name, DbContract().SYNC_STATUS_FAILED)
        }
    }

    @Suppress("DEPRECATION")
    public fun checkNetworkConnection(): Boolean {
        var connectivityManager: ConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connectivityManager.activeNetworkInfo
        return (networkInfo != null && networkInfo.isConnected)
    }

    private fun saveToLocalStorage(name: String, sync: Int) {
        var dbHelper: DbHelper = DbHelper(this)
        var database: SQLiteDatabase = dbHelper.writableDatabase


        dbHelper.saveToLocalDatabase(name, sync, database)
        readFromLocalStorage()
        dbHelper.close()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(DbContract().UI_UPDATE_BROADCAST))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }
}