package com.example.river.opendata.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.river.opendata.CusOkHttp
import com.example.river.opendata.Formatter
import com.example.river.opendata.MainFormatter
import com.example.river.opendata.R
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.main_chart.*
import org.json.JSONObject

class ChartFragment : Fragment() {


    lateinit var okHttp: CusOkHttp
    private var year: String = "2015"
    private var barEntries = ArrayList<BarEntry>()
    private var rainEntries = ArrayList<Entry>()
    private var humidityEntries = ArrayList<Entry>()
    private var tempEntries = ArrayList<Entry>()


    private var barMaxValue: Float = 0f
    private var lineMaxValue: Float = 0f
    private var lineMinValue: Float = 0f

    private var requestYear: Float = 0f
    private var stackModelList = ArrayList<BarEntry>()
    private var rainModelList = ArrayList<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {

        okHttp = CusOkHttp(context!!)

        year = "2015"

        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
/*
        okHttp.request(
                "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue",
                "{\"year\":\"2015\"}",
                ::callBack,

                )
*/
//        requestAllDengueValue()
//        requestAllRainValue()
//        checkResponsesStandBy()

        super.onStart()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return LayoutInflater.from(container!!.context).inflate(R.layout.main_chart, container, false)

//        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun requestAllDengueValue() {
        val url = "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue"

//        for (i in 2015..2016) {
//            Thread {
//                okHttp.request(
//                        url,
//                        "{\"year\":\"$i\"}",
//                        { jsonObject ->
//                            addAYearDengueValueToList(jsonObject, i.toFloat())
//                        }
//                )
//            }.start()
//        }
    }

    fun requestAllRainValue() {
        val url = "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/rainfall"
//
//        for (i in 2015..2016) {
//            Thread {
//                okHttp.request(
//                        url,
//                        "{\"year\":\"$i\"}",
//                        { jsonObject ->
//
//                            addAYearRainValueToList(jsonObject, i.toFloat())
//                        }
//                )
//            }.start()
//        }
    }

    fun checkResponsesStandBy() {
        while (stackModelList.size != 2 && rainModelList.size != 2) {
            //Stuck here
            //println("*** ${stackModelList.size}")
        }

        val data = CombinedData()
        var aa = getLineData()
        data.setData(aa)
        println("**++ $aa")
        data.setData(generateBarData())

        chart_main.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_main.xAxis.axisMinimum = 2014f
        chart_main.axisLeft.axisMinimum = 0f
        chart_main.axisRight.axisMinimum = 0f
        chart_main.xAxis.axisMaximum = 2017f
        chart_main.xAxis.spaceMin = 1f
        chart_main.data = data
        chart_main.invalidate()
    }

    var type = "dengue"
    /*
    private fun callBack(jsonObject: JSONObject) {
        if (okHttp.isSuccess(jsonObject)) {
            when (type) {
                "dengue" -> {
//                    barEntries = getDataList(type, okHttp.getJSONObjectData(jsonObject)) as ArrayList<BarEntry>

                    addAYearDengueValueToList(jsonObject)

                    //接著讀雨量
                    val url = "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/rainfall"
                    requestData("rain", url, "{\"year\":\"$year\"}")
                }
                "rain" -> {
                    println("*** " + jsonObject.toString())
                    rainEntries = getDataList(type, okHttp.getFloatData(jsonObject)) as ArrayList<Entry>
                    val data = CombinedData()
                    data.setData(getLineData(rainEntries))
                    data.setData(generateBarData())

                    chart_main.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    chart_main.xAxis.axisMinimum = 0f
                    chart_main.axisLeft.axisMinimum = 0f
                    chart_main.axisRight.axisMinimum = 0f
                    chart_main.data = data
                    chart_main.invalidate()
                }
                "humidity" -> {
                    humidityEntries = getDataList(type, okHttp.getJSONObjectData(jsonObject)) as ArrayList<Entry>
                    val data = CombinedData()
                    data.setData(getLineData(humidityEntries))
                    data.setData(generateBarData())

                    chart_main.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    chart_main.xAxis.axisMinimum = 0f
                    chart_main.axisLeft.axisMinimum = 0f
                    chart_main.axisRight.axisMinimum = 0f
                    chart_main.data = data
                    chart_main.invalidate()
                }
                "temp" -> {
                    tempEntries = getDataList(type, okHttp.getJSONObjectData(jsonObject)) as ArrayList<Entry>
                    val data = CombinedData()
                    data.setData(getLineData(humidityEntries))
                    data.setData(generateBarData())

                    chart_main.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    chart_main.xAxis.axisMinimum = 0f
                    chart_main.axisLeft.axisMinimum = 0f
                    chart_main.axisRight.axisMinimum = 0f
                    chart_main.data = data
                    chart_main.invalidate()
                }
            }
        } else {
            Toast.makeText(context!!, okHttp.getStringData(jsonObject), Toast.LENGTH_SHORT).show()
        }
    }
    */

    /*
    private fun requestData(type: String, url: String, requestString: String) {

        Thread {
            okHttp.request(
                    url,
                    requestString,
                    ::callBack,
                    type
            )
        }.start()
    }
    */

    private fun generateBarData(): BarData {

        val entries2 = ArrayList<BarEntry>()

        for (i in stackModelList) {
//            println("*** year" + i.year)
//            val secondValue = i.totalValue - i.secondValue
//            val elseValue = (i.totalValue - i.firstValue - i.secondValue)
//            println("*** secondValue" + secondValue)
//            println("*** elseValue" + elseValue)
            entries2.add(i)
//            println("***+++ ${i.year}")

        }


        val set2 = BarDataSet(entries2, "登革熱")
        set2.stackLabels = arrayOf("第一高", "第二高", "其他")
        set2.setColors(*intArrayOf(
                Color.rgb(61, 165, 255),
                Color.rgb(23, 197, 255),
                Color.rgb(61, 165, 255)))
        set2.valueTextColor = Color.rgb(0, 0, 0)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT


        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.5f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        val barData = BarData(set2)
        barData.barWidth = barWidth
//        barData.setValueFormatter()
        // make this BarData object grouped
//        .groupBars(0f, groupSpace, barSpace) // start at x = 0

        return barData
    }


    private fun getLineData(): LineData {
//        for (i in lineEntries) {
//            i.y = i.y + barMaxValue
//        }

/*
        for (i in rainModelList) {
//            println("*** ori ${i.y}")
            //i.y = barMaxValue + (((i.y - lineMinValue) * 2 * (barMaxValue / 5)) / (lineMaxValue - lineMinValue)) + lineMaxValue
//            println("*** modi ${i.y}")
        }
        */

        var list = rainModelList.clone() as ArrayList<Entry>
        println("*** $rainModelList")
        val dataSetA = LineDataSet(list, "雨量(mm)")

        val dataSets = ArrayList<LineDataSet>()
        dataSetA.valueFormatter = MainFormatter()

        dataSets.add(dataSetA) // add the datasets

        return LineData(dataSetA)
    }

    private fun addAYearDengueValueToList(jsonObject: JSONObject, year: Float) {

        var jsonObj = jsonObject.getJSONObject("data")
//只會拿到某一年，需加到總 List 之中

        val list = mutableListOf<DistrictAndValue>()
        for (i in jsonObj.keys()) {
            val value = jsonObj.getString(i).toFloatOrNull() ?: 0f
            list.add(DistrictAndValue(i, value))
        }
        //取得前兩名
        var sortedList = list.sortedByDescending { it.value }

        var sum = sortedList.sumByDouble { it.value.toDouble() }
        var elseValue = sum - (sortedList[1].value + sortedList[0].value)

        sortedList = sortedList.drop(2)
        val elseSum = sortedList.sumByDouble { it.value.toDouble() }.toFloat()

//        var model = StackModel(
//                year,
//                sortedList[0].district,
//                sortedList[0].value,
//                sortedList[1].district,
//                sortedList[1].value,
//                sum.toFloat()
//        )

//        var elseValue = sum - (sortedList[1].value + sortedList[0].value)
        stackModelList.add(
                BarEntry(
                        year,
                        floatArrayOf(elseSum, sortedList[1].value, sortedList[0].value)
                ))
    }

    private fun addAYearRainValueToList(jsonObject: JSONObject, year: Float) {

//只會拿到某一年，需加到總 List 之中
        rainModelList.add(Entry(year, jsonObject.getDouble("data").toFloat()))
    }

    private fun getDataList(type: String, float: Float): ArrayList<Any> {

        when (type) {
            "rain" -> {
                var list = ArrayList<Any>()
                for (i in 1..12) {
                    val value = float
                    lineMaxValue = if (value > lineMaxValue) value else lineMaxValue
                    lineMinValue = if (value < lineMinValue) value else lineMinValue
                    list.add(Entry(i.toFloat(), value))
                }
                return list
            }
            else -> {
                return arrayListOf()
            }
        }
    }
}


//Class
data class StackModel(
        var year: Float,
        var firstDistrict: String,
        var firstValue: Float,
        var secondDistrict: String,
        var secondValue: Float,
        var totalValue: Float)

data class DistrictAndValue(var district: String, var value: Float)
data class RainModel(var year: Float, var value: Float)