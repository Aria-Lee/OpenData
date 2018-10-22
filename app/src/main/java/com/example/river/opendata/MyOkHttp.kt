package com.example.river.opendata

import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MyOkHttp(val context: Context?) {


    fun request(url: String, request: String, cb: (String, JSONObject) -> Unit, type: String) {

        val client = OkHttpClient()

        val requestBuilder = Request.Builder()
        val JSON = MediaType.parse("application/json; charset=utf-8")

        val body = RequestBody.create(JSON, request)

        val request = requestBuilder
                .url(url)
                .post(body)
                .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                 var jsonObject: JSONObject =JSONObject(response.body()!!.string())
//                when (type) {
//                    "rain" -> jsonObjectRain = JSONObject(response.body()!!.string())
//                    "dengue" -> jsonObjectDengue = JSONObject(response.body()!!.string())
//                }
                cb.invoke(type, jsonObject)
            }
        })
    }


    fun isSuccess(jsonObject:JSONObject): Boolean {
        return jsonObject.getString("result").toBoolean()
    }

    fun getFloatData(jsonObject:JSONObject): Float {
        return jsonObject.getString("data").toFloat()
    }

    fun getJSONObjectData(jsonObject: JSONObject): JSONObject {
        return JSONObject(jsonObject.getString("data"))
    }

    fun getStringData(jsonObject:JSONObject): String {
        return jsonObject.getString("data")
    }


}