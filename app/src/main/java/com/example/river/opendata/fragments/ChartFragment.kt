package com.example.river.opendata.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.river.opendata.formatter.MainFormatter
import com.example.river.opendata.R
import com.example.river.opendata.formatter.BarFormatter
import com.example.river.opendata.formatter.StackFormatter
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.main_chart.*
import kotlinx.android.synthetic.main.main_chart.view.*
import org.json.JSONObject

class ChartFragment : Fragment() {

    private var barMaxValue: Float = 50f

    private var rainMaxValue: Float = 2953.909f
    private var rainMinValue: Float = 192.321f

    private var tempMaxValue: Float = 25.318772f
    private var tempMinValue: Float = 21.079167f

    private var humidityMaxValue: Float = 77.304759f
    private var humidityMinValue: Float = 70.576111f

    private var dengueList = ArrayList<BarEntry>()
    private var rainList = ArrayList<Entry>()
    private var humidityList = ArrayList<Entry>()
    private var tempList = ArrayList<Entry>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = LayoutInflater.from(container!!.context).inflate(R.layout.main_chart, container, false)
        drawChar(v.chart_main)
        return v
    }

    private fun drawChar(chart_main: CombinedChart) {
        //Dengue
        val dengue2015 = JSONObject(FakeData.dengue2015)
        addAYearDengueValueToList(dengue2015, 2015f)
        val dengue2016 = JSONObject(FakeData.dengue2016)
        addAYearDengueValueToList(dengue2016, 2016f)

        //Rain
        val rain = JSONObject(FakeData.rain)
        for (i in rain.keys()) {
            addAYearRainValueToList(rain, i.toFloat())
        }

        //Temperature
        val temp = JSONObject(FakeData.temprature)
        for (i in rain.keys()) {
            addAYearTempValueToList(temp, i.toFloat())
        }

        //Humidity
        val humidity = JSONObject(FakeData.humidity)
        for (i in rain.keys()) {
            addAYearHumidityValueToList(humidity, i.toFloat())
        }

        setChartMain(chart_main)
    }

    fun setChartMain(chart_main:CombinedChart){
        val colorArray = listOf(Color.rgb(0, 224, 15),
                Color.rgb(255, 123, 0),
                Color.rgb(195, 0, 255),
                Color.argb(0,0,0,0),
                Color.rgb(0, 146, 199),
                Color.rgb(0, 176, 240),
                Color.rgb(122, 220, 255))
        val stringArray = listOf("雨量", "溫度", "濕度", "登革熱", "其他", "第二高", "第一高")
        val legendEntryList = mutableListOf<LegendEntry>()

        for ( i in 0..6){
            legendEntryList.add(LegendEntry(stringArray[i],
                    Legend.LegendForm.DEFAULT,
                    Float.NaN,
                    Float.NaN,
                    null,
                    colorArray[i]))
        }

        val data = CombinedData()

//        data.setData(generateBarData())
        data.setData(getLineData())
        data.setData(generateStackData())

        chart_main.legend.setCustom(legendEntryList)
        chart_main.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart_main.xAxis.axisMinimum = 2012f
        chart_main.axisLeft.axisMinimum = 0f
        chart_main.axisRight.axisMinimum = 0f
        chart_main.xAxis.axisMaximum = 2019f
        chart_main.xAxis.spaceMin = 1f
        chart_main.data = data
        chart_main.description.text = ""

        chart_main.invalidate()
    }

    private fun generateStackData(): BarData {

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(2013f, 30f))
        entries.add(BarEntry(2014f, 40f))
        entries.add(BarEntry(2017f, 0f))
        entries.add(BarEntry(2018f, 10f))

        val set = BarDataSet(entries, "")
        set.setColors(Color.rgb(0, 146, 199))
        set.valueTextColor = Color.rgb(0, 0, 0)
        set.valueTextSize = 10f
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueFormatter = BarFormatter()

        val entries2 = ArrayList<BarEntry>()

        for (i in dengueList) {
            entries2.add(i)
        }

        val set2 = BarDataSet(entries2, "登革熱")
        set2.stackLabels = arrayOf("其他", "第二高", "第一高")
        set2.setColors(
                Color.rgb(0, 146, 199),
                Color.rgb(0, 176, 240),
                Color.rgb(122, 220, 255))
        set2.valueTextColor = Color.rgb(0, 0, 0)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT

        set2.valueFormatter = StackFormatter()

        val barWidth = 0.5f // x2 dataset

        val barData = BarData(set, set2)
        barData.barWidth = barWidth


        return barData
    }

    private fun getLineData(): LineData {


        for (i in rainList) {
            if ((rainMaxValue - rainMinValue) == 0f) {
                i.y = (1.1f * barMaxValue)
            } else {
                i.y = (1.1f * barMaxValue) + (((i.y - rainMinValue) * 1f * (barMaxValue / 5)) / (rainMaxValue - rainMinValue))
            }
//            i.y = barMaxValue + (((i.y - rainMinValue) * 2 * (barMaxValue / 5)) / (rainMaxValue - rainMinValue)) + rainMinValue
        }

        for (i in tempList) {
            if ((tempMaxValue - tempMinValue) == 0f) {
                i.y = (1.3f * barMaxValue)
            } else {
                i.y = (1.3f * barMaxValue) + (((i.y - tempMinValue) * 1f * (barMaxValue / 5)) / (tempMaxValue - tempMinValue))
            }
            //i.y = barMaxValue + (((i.y - tempMinValue) * 2 * (barMaxValue / 5)) / (tempMaxValue - tempMinValue)) + tempMinValue
        }

        for (i in humidityList) {
            if ((humidityMaxValue - humidityMinValue) == 0f) {
                i.y = (1.5f * barMaxValue)
            } else {
                i.y = (1.5f * barMaxValue) + (((i.y - humidityMinValue) * 1f * (barMaxValue / 5)) / (humidityMaxValue - humidityMinValue))
            }
//            i.y = barMaxValue + (((i.y - humidityMinValue) * 2 * (barMaxValue / 5)) / (humidityMaxValue - humidityMinValue)) + humidityMinValue
        }


        val dataSetRain = LineDataSet(rainList, "雨量")
        dataSetRain.color = Color.rgb(0, 224, 15)
        dataSetRain.setCircleColor(Color.rgb(0, 224, 15))
        val dataSetTemp = LineDataSet(tempList, "溫度")
        dataSetTemp.color = Color.rgb(255, 123, 0)
        dataSetTemp.setCircleColor(Color.rgb(255, 123, 0))
        val dataSetHumidity = LineDataSet(humidityList, "濕度")
        dataSetHumidity.color = Color.rgb(195, 0, 255)
        dataSetHumidity.setCircleColor(Color.rgb(195, 0, 255))

        val dataSets = ArrayList<LineDataSet>()

        dataSetRain.valueFormatter = MainFormatter(barMaxValue, rainMaxValue, rainMinValue, 1.1f)
        dataSetTemp.valueFormatter = MainFormatter(barMaxValue, tempMaxValue, tempMinValue, 1.3f)
        dataSetHumidity.valueFormatter = MainFormatter(barMaxValue, humidityMaxValue, humidityMinValue, 1.5f)

        dataSets.add(dataSetRain)
        dataSets.add(dataSetTemp)
        dataSets.add(dataSetHumidity)

        return LineData(dataSets as List<ILineDataSet>?)
    }

    private fun addAYearDengueValueToList(jsonObject: JSONObject, year: Float) {

        //var jsonObj = jsonObject.getJSONObject("data")
//只會拿到某一年，需加到總 List 之中

        val list = mutableListOf<DistrictAndValue>()
        for (i in jsonObject.keys()) {
            val value = jsonObject.getString(i).toFloatOrNull() ?: 0f
            list.add(DistrictAndValue(i, value))
        }
        //取得前兩名
        var sortedList = list.sortedByDescending { it.value }

        val sum = sortedList.sumByDouble { it.value.toDouble() }
        //var elseValue = sum - (sortedList[1].value + sortedList[0].value)

        val max = sortedList[0].value
        val second = sortedList[1].value

        sortedList = sortedList.drop(2)
        val elseSum = sortedList.sumByDouble { it.value.toDouble() }.toFloat()


        var modiTotal = 0
        when (year) {
            2015f -> modiTotal = 50
            2016f -> modiTotal = 20
        }
        val modiElse = (elseSum / sum * modiTotal).toFloat()
        val modiFirst = (max / sum * modiTotal).toFloat()
        val modiSecond = (second / sum * modiTotal).toFloat()

        println("999 sum:$sum, elseSum:$elseSum, modiElse:$modiElse, modiFirst:$modiFirst, modiSecond:$modiSecond")
        println("999 " + max + ", " + second)
//  var elseValue = sum - (sortedList[1].value + sortedList[0].value)
        dengueList.add(
                BarEntry(
                        year,
                        floatArrayOf(modiElse, modiSecond, modiFirst)
                ))
    }

    private fun addAYearRainValueToList(jsonObject: JSONObject, year: Float) {
        val key = year.toInt().toString()
        val value = jsonObject.getDouble(key).toFloat()
        rainList.add(Entry(year, value))
    }

    private fun addAYearTempValueToList(jsonObject: JSONObject, year: Float) {
        val key = year.toInt().toString()
        tempList.add(Entry(year, jsonObject.getDouble(key).toFloat()))
    }

    private fun addAYearHumidityValueToList(jsonObject: JSONObject, year: Float) {
        val key = year.toInt().toString()
        humidityList.add(Entry(year, jsonObject.getDouble(key).toFloat()))
    }
}

data class DistrictAndValue(var district: String, var value: Float)

class FakeData {
    companion object {
        var dengue2015 = """ {
        "七股區": 21,
        "下營區": 16,
        "中西區": 3492,
        "仁德區": 305,
        "佳里區": 69,
        "六甲區": 14,
        "北區": 5772,
        "南化區": 11,
        "南區": 3529,
        "善化區": 89,
        "大內區": 10,
        "學甲區": 27,
        "安南區": 1875,
        "安定區": 53,
        "安平區": 904,
        "官田區": 22,
        "將軍區": 7,
        "山上區": 6,
        "左鎮區": 10,
        "後壁區": 7,
        "新化區": 155,
        "新市區": 69,
        "新營區": 108,
        "東區": 3141,
        "東山區": 4,
        "柳營區": 18,
        "楠西區": 8,
        "歸仁區": 208,
        "永康區": 2703,
        "玉井區": 65,
        "白河區": 9,
        "西港區": 20,
        "關廟區": 57,
        "鹽水區": 15,
        "麻豆區": 53,
        "龍崎區": 5
    }"""

        var dengue2016 = """{
        "七股區": 2,
        "下營區": 1,
        "中西區": 4,
        "仁德區": 3,
        "北區": 3,
        "南區": 3,
        "安南區": 4,
        "安定區": 1,
        "安平區": 2,
        "官田區": 1,
        "新市區": 2,
        "新營區": 1,
        "東區": 5,
        "歸仁區": 3,
        "永康區": 6,
        "麻豆區": 1,
        "龍崎區": 1
    }"""

        var rain = """ {
       "2013": 1706,
       "2014": 1289,
       "2015": 304.258,
       "2016": 2953.909,
       "2017": 1564.862,
       "2018": 192.321
    }"""


        var temprature = """{
       "2013": 21.079167,
       "2014": 24.594471,
       "2015": 25.012329,
       "2016": 24.895754,
       "2017": 25.071758,
       "2018": 25.318772
   }"""

        var humidity = """ {
       "2013": 70.576111,
       "2014": 74.244691,
       "2015": 75.646175,
       "2016": 77.304759,
       "2017": 72.733676,
       "2018": 73.665024
   }"""
    }

}