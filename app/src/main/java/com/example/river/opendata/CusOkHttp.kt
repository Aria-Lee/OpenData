package com.example.river.opendata

import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class CusOkHttp(val context: Context) {

    lateinit var okHttpClient: OkHttpClient
    lateinit var jsonObject: JSONObject

    fun request(url: String, requestString: String, cb: (JSONObject) -> Unit) {

        okHttpClient = OkHttpClient()

        val requestBuilder = Request.Builder()
        val JSON = MediaType.parse("application/json; charset=utf-8")

        val body = RequestBody.create(JSON, requestString)

        val request = requestBuilder
                .url(url)
                .post(body)
                .build()

        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                jsonObject = JSONObject(response.body()!!.string())

                cb.invoke(jsonObject)
            }
        })
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