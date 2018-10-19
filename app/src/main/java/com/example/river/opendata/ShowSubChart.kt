package com.example.river.opendata

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.sub_chart.*
import org.json.JSONObject


class ShowSubChart : AppCompatActivity() {
    //    lateinit var okHttpRain: MyOkHttp
//    lateinit var okHttpDengue: MyOkHttp
    lateinit var okHttp: MyOkHttp

    var barEntries = ArrayList<BarEntry>()
    var lineEntries = ArrayList<Entry>()

    var barMaxValue: Float = 0f
    var rainMaxValue: Float = 0f
    var rainMinValue: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_chart)
//        okHttpRain = MyOkHttp(this)
//        okHttpDengue = MyOkHttp(this)
        okHttp = MyOkHttp(this)


        this.title = intent.getStringExtra("district")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        spinnerInit()

        requestData("dengue", "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue")



    }

    fun requestData(type: String, url: String) {
        Thread {
            okHttp.request(
                    url,
                    "{\"year\":\"2015\", \"month\":\"10\", \"district\":\"安南區\"}\n",
                    ::callBack,
                    type
            )
        }.start()
    }

    fun callBack(type: String, jsonObject: JSONObject) {
        if (okHttp.isSuccess(jsonObject)) {

            println("*** ${System.nanoTime()} $type")

            when (type) {
                "dengue" -> {
//                    println("*** dengue $jsonObject")
                    barEntries = getDataList(type, okHttp.getJSONObjectData(jsonObject)) as ArrayList<BarEntry>
                    requestData("rain", "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/rainfall")
                }
                "rain" -> {
//                    println("*** rain $jsonObject")
                    lineEntries = getDataList(type, okHttp.getJSONObjectData(jsonObject)) as ArrayList<Entry>
                    val data = CombinedData()
//        chart_line.data = getLineData()
//        chart_line.data = generateBarData()
                    data.setData(getLineData())
                    data.setData(generateBarData())

//        data.setValueTypeface(mTfLight)

                    chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
                    chart.xAxis.axisMinimum = 0f
                    chart.axisLeft.axisMinimum = 0f
                    chart.axisRight.axisMinimum = 0f
                    chart.data = data
                    chart.invalidate()

                }
            }


        } else {
            Toast.makeText(this, okHttp.getStringData(jsonObject), Toast.LENGTH_SHORT).show()
        }

    }

    fun getDataList(type: String, jsonObject: JSONObject): ArrayList<Any> {

        when (type) {
            "rain" -> {
                var list = ArrayList<Any>()
                for (i in jsonObject.keys()) {
                    val value = jsonObject.getString(i).toFloatOrNull() ?: 0f
                    rainMaxValue = if (value > rainMaxValue) value else rainMaxValue
                    rainMinValue = if (value < rainMinValue) value else rainMinValue
                    list.add(Entry(i.toFloat(), value))
                }
                return list
            }
            "dengue" -> {
                var list = ArrayList<Any>()
                for (i in jsonObject.keys()) {
                    val value = jsonObject.getString(i).toFloatOrNull() ?: 0f
                    barMaxValue = if (value > barMaxValue) value else barMaxValue
                    list.add(BarEntry(i.toFloat(), value))
                }
                barMaxValue *= 1.1f
                return list
            }
            else -> {
                return arrayListOf()
            }
        }
    }

//    val DATA_COUNT = 5
//    private fun getLabels(): List<String> {
//        val chartLabels = ArrayList<String>()
//        for (i in 0 until DATA_COUNT) {
//            chartLabels.add("X$i")
//        }
//        return chartLabels
//    }
//
//    private fun getChartData(): List<Entry> {
//        val chartData = ArrayList<Entry>()
//        for (i in 1..12) {
//            chartData.add(Entry(i.toFloat(), i.toFloat() * 10 + 1600f))
//        }
//        return chartData
//    }

    private fun getLineData(): LineData {
//        for (i in lineEntries) {
//            i.y = i.y + barMaxValue
//        }

        for (i in lineEntries) {
//            println("*** ori ${i.y}")
            i.y = barMaxValue + (((i.y - rainMinValue) * 2 * (barMaxValue / 5)) / (rainMaxValue - rainMinValue)) + rainMinValue
//            println("*** modi ${i.y}")
        }
        val dataSetA = LineDataSet(lineEntries, "雨量(mm)")

        val dataSets = ArrayList<LineDataSet>()
        dataSetA.valueFormatter = Formatter(barMaxValue, rainMaxValue, rainMinValue)

        dataSets.add(dataSetA) // add the datasets

        return LineData(dataSetA)
    }


    private fun generateBarData(): BarData {

//        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()

//        for (index in 0 until DATA_COUNT) {
//            entries1.add(BarEntry(0f, 5f))
//
//            // stacked
//            entries2.add(BarEntry(0f, floatArrayOf(6f, 10f)))
//        }

        val set1 = BarDataSet(barEntries, "登革熱")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT

//        val set2 = BarDataSet(entries2, "")
//        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
//        set2.setColors(*intArrayOf(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)))
//        set2.valueTextColor = Color.rgb(61, 165, 255)
//        set2.valueTextSize = 10f
//        set2.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        val barData = BarData(set1)
        barData.barWidth = barWidth

        // make this BarData object grouped
        //d.groupBars(0f, groupSpace, barSpace) // start at x = 0

        return barData
    }

    fun spinnerInit() {
        val yearList = ArrayAdapter.createFromResource(this, R.array.year_array, R.layout.spinner_center_item)
        val monthList = ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_center_item)

        year_spinner.adapter = yearList
        month_spinner.adapter = monthList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        println("*** ${item!!.itemId}")
        when (item!!.itemId) {
            android.R.id.home -> {
//                onBackPressed()
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}