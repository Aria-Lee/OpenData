package com.example.river.opendata

import org.json.JSONObject

class MapResponseData {

    companion object {
        var dataList = hashMapOf<Int, JSONObject>()

        fun preAddData() {
            dataList[2014] = JSONObject("{}")
            dataList[2017] = JSONObject("{}")
        }

        fun addData(year: Int, jsonObject: JSONObject) {
            if (!dataList.keys.contains(year)) {
                dataList[year] = jsonObject
            }
        }

        fun getData(year: Int): JSONObject? {
            return dataList[year]
        }

        fun isDataCreated(year: Int): Boolean {
            return dataList[year] != null
        }

        fun getDengueValue(year: Int, district: String): String {

            var inside = false
            for (i in dataList[year]!!.keys()) {
                if (i == district) {
                    inside = true
                }
            }

            return if (inside) {
                dataList[year]?.getInt(district).toString()
            } else {
                "0"
            }
        }

        fun checkAllDatas(): Int? {
            val arry = arrayOf(2014, 2015, 2016, 2017, 2018)
            for (i in dataList.keys) {
                if (i !in arry){
                    return i
                }
            }
            return null
        }
    }

}