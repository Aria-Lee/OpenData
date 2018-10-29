package com.example.river.opendata

import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CusOkHttp(val context: Context) {

    var okHttpClient: OkHttpClient? = null
    lateinit var jsonObject: JSONObject
    var runnableNow = 0

    var taskQueue = mutableListOf<CusTask>()

    fun request(cusTask: CusTask) {

        okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, cusTask.requestString)
        val request = requestBuilder
                .url(cusTask.url)
                .post(body)
                .build()

        val call = okHttpClient!!.newCall(request)

        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                println("777 ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                jsonObject = JSONObject(response.body()!!.string())
                if (isSuccess(jsonObject)) {
                    cusTask.requestCallback.invoke(jsonObject)
//                    if (runnableNow < taskQueue.size) {
//                        request(taskQueue[runnableNow++])
//                    }
                } else {
                    val msg = jsonObject.getString("data")
                    //Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    println("$msg")
                }
            }
        })
    }

    fun cancelAll() {
        okHttpClient?.dispatcher()?.cancelAll()
        clearQueue()
    }

    fun addCusTask(cusTask: CusTask) {
        taskQueue.add(cusTask)
    }

    fun startTasks() {
        request(taskQueue[0])
    }

    fun removeQueue(year: Int) {
        var cusTask: CusTask? = null
        for (i in taskQueue) {
            if (i.year == year) {
                cusTask = i
            }
        }
        if (cusTask != null) {
            taskQueue.remove(cusTask)
        }
    }

    fun clearQueue() {
        taskQueue.clear()
    }

    fun isSuccess(jsonObject: JSONObject): Boolean {
        return jsonObject.getString("result").toBoolean()
    }

    fun getFloatData(jsonObject: JSONObject): Float {
        return jsonObject.getString("data").toFloat()
    }

    fun getJSONObjectData(jsonObject: JSONObject): JSONObject {
        return JSONObject(jsonObject.getString("data"))
    }

    fun getStringData(jsonObject: JSONObject): String {
        return jsonObject.getString("data")
    }


}