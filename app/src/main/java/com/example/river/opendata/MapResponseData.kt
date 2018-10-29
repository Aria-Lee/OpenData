package com.example.river.opendata

import org.json.JSONObject

class MapResponseData {

    companion object {
        var dataList = hashMapOf<Int, JSONObject>()
        var waitForRemoveList = mutableListOf(2015, 2016, 2018)

        fun preAddData() {
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

        fun removeAfterGetResponse(year: Int) {
            waitForRemoveList.remove(year)
        }

        fun moveYearToFirst(year: Int) {
            waitForRemoveList.remove(year)
            waitForRemoveList.add(0, year)
        }

        fun checkAllDatas(): MutableList<Int>? {
//            val arry = mutableListOf(2015, 2016, 2017, 2018)
//            var arryNow = mutableListOf<Int>()
//            for (i in dataList.keys) {
//                arryNow.add(i)
//            }
//            arry.removeAll(arryNow)
//
//            return if (arry.size != 0) {
//                arry[0]
//            } else {
//                null
//            }
            return if (waitForRemoveList.size != 0) {
                return waitForRemoveList
            } else {
                null
            }
        }
    }
}