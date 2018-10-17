package com.example.river.opendata

import android.content.Context
import android.widget.Toast
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MyOkHttp(val context:Context) {

    lateinit var jsonObject: JSONObject

    fun request(url: String, cb: () -> Unit) {
        val client = OkHttpClient()

        val requestBuilder = Request.Builder()
        val JSON = MediaType.parse("application/json; charset=utf-8")
        val body = RequestBody.create(JSON, "{\"year\":\"2015\", \"district\":\"東區\"}")


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
                //println("*** ${response.body()!!.string()}")
                jsonObject = JSONObject(response.body()!!.string())
                cb.invoke()
            }

        })
    }


    fun isSuccess(): Boolean {
        return jsonObject.getString("result").toBoolean()
    }

    fun getFloatData(): Float {
        return jsonObject.getString("data").toFloat()
    }

    fun getJSONObjectData(): JSONObject {
        return JSONObject(jsonObject.getString("data"))
    }

    fun getStringData(): String {
        return jsonObject.getString("data")
    }


}