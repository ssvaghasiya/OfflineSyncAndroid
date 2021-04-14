package com.example.synctest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.util.Log
import com.android.volley.Response
import com.android.volley.VolleyLog
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.jar.Attributes

public class NetworkMonitor : BroadcastReceiver() {
    var TAG = "NetworkMonitor"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (checkNetworkConnection(context)) {
            var dbHelper: DbHelper = DbHelper(context!!)
            var database: SQLiteDatabase = dbHelper.writableDatabase

            var cursor: Cursor = dbHelper.readFromLocalDatabase(database)
            while (cursor.moveToNext()) {
                var sync_status: Int =
                    cursor.getInt(cursor.getColumnIndex(DbContract().SYNC_STATUS))
                if (sync_status == DbContract().SYNC_STATUS_FAILED) {
                    var name: String = cursor.getString(cursor.getColumnIndex(DbContract().NAME))
                    val stringRequest: StringRequest = object : StringRequest(
                        Method.POST,
                        DbContract().SERVER_URL,
                        Response.Listener { response ->
                            Log.d(TAG, response)
                            try {
                                var jsonObject: JSONObject = JSONObject(response)
                                var Response: String = jsonObject.getString("response")
                                if (Response.equals("OK")) {
                                    dbHelper.updateLocalDatabase(
                                        name,
                                        DbContract().SYNC_STATUS_OK,
                                        database
                                    )
                                    context.sendBroadcast(Intent(DbContract().UI_UPDATE_BROADCAST))
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }

                        },
                        Response.ErrorListener { error ->
                            VolleyLog.d(TAG, "Error: " + error.message)
                            Log.d(TAG, "" + error.message + "," + error.toString())

                        }) {
                        override fun getParams(): Map<String, String>? {
                            val params: MutableMap<String, String> =
                                HashMap()
                            params["name"] = name
                            return params
                        }
                    }
                    MySingleton(context).getInstance(context).addToRequestQueue(stringRequest);
                }
            }
            dbHelper.close()
        }
    }

    @Suppress("DEPRECATION")
    public fun checkNetworkConnection(context: Context?): Boolean {
        var connectivityManager: ConnectivityManager =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var networkInfo = connectivityManager.activeNetworkInfo
        return (networkInfo != null && networkInfo.isConnected)
    }
}