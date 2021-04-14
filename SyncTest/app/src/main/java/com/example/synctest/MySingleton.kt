package com.example.synctest

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.RequestQueue.RequestEvent
import com.android.volley.toolbox.Volley

class MySingleton {

    var mInstance: MySingleton? = null
    var requestQueuee: RequestQueue? = null
    var mCtx: Context? = null

    constructor(context: Context) {
        mCtx = context
        requestQueuee = getRequestQueue()
    }

    fun getRequestQueue(): RequestQueue {
        if (requestQueuee == null) {
            requestQueuee = Volley.newRequestQueue(mCtx?.applicationContext)
        }
        return requestQueuee!!
    }

    fun getInstance(context: Context):  MySingleton {
        if (mInstance == null) {
            mInstance = MySingleton(context)
        }
        return mInstance!!
    }



    fun <T> addToRequestQueue(request: Request<T>) {
        getRequestQueue().add(request)
    }
}