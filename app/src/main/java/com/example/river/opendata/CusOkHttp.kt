package com.example.river.opendata

import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CusOkHttp(val context: Context) {

    var okHttpClient: OkHttpClient? = null
    lateinit var jsonObject: JSONObject

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
                println("onFailure ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                jsonObject = JSONObject(response.body()!!.string())
                taskQueue.remove(cusTask)

                if (isSuccess(jsonObject)) {
                    cusTask.requestCallback.invoke(jsonObject)
                    if (taskQueue.size != 0) {
                        request(taskQueue[0])
                    }
                } else {
                    val msg = jsonObject.getString("data")
                    println("false $msg")
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

    fun clearQueue() {
        taskQueue.clear()
    }

    fun isSuccess(jsonObject: JSONObject): Boolean {
        return jsonObject.getString("result").toBoolean()
    }

}