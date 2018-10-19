package com.example.river.opendata

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringWriter
import java.text.DecimalFormat

class DataHelper {
    companion object {

        val districtList = mutableListOf<String>()

        fun getJSONString(iStream: InputStream): String {
            val writer = StringWriter()
            val buffer = CharArray(1024)
            try {
                val reader = BufferedReader(InputStreamReader(iStream, "UTF-8"))
                var n = 0
                while (n != -1) {
                    n = (reader.read(buffer))
                    if (n == -1) {
                        continue
                    }
                    writer.write(buffer, 0, n)
                }
            } finally {
                iStream.close()
            }

            return writer.toString()
        }

        fun getList(json: String): MutableList<MutableList<LatLng>> {
            var jsonObject = JSONObject(json)

            var result = mutableListOf<MutableList<LatLng>>()

            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {

                val key = iterator.next()
                districtList.add(key)
//                println("*** $key")

                var list = mutableListOf<LatLng>()
                try {
                    var district = jsonObject[key].toString().split(" ")
                    for (i in district) {
                        var bb = i.split(",")
                        var latLng = LatLng(bb[1].toDouble(), bb[0].toDouble())
                        list.add(latLng)
                    }
                    result.add(list)
                } catch (e: JSONException) {
                    // GG
                }
            }

            println(districtList.toString())
            return result
        }


    }


}
