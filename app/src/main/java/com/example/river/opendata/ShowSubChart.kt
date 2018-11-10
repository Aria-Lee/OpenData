package com.example.river.opendata

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.river.opendata.formatter.DecimalFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.sub_chart.*
import org.json.JSONObject


class ShowSubChart : AppCompatActivity() {

    private lateinit var okHttp: CusOkHttp
    private lateinit var district: String

    //View sets
    private var year: String = ""
    private var month: Int = 0

    //Chart sets
    private var barEntries = ArrayList<BarEntry>()
    private var lineEntries = ArrayList<Entry>()
    private var barMaxValue: Float = 0f
    private var rainMaxValue: Float = 0f
    private var rainMinValue: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_chart)

        okHttp = CusOkHttp(this)

        district = intent.getStringExtra("district")

        setSupportActionBar(subtoolbar)
        this.title = district

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        spinnerInit()

        button.setOnClickListener {
            resetValue()

            progressBar.visibility = View.VISIBLE

            year = year_spinner.selectedItem.toString()
            month = month_spinner.selectedItemPosition

            okHttp.addCusTask(getDengueTask())
            okHttp.addCusTask(getRainTask())
            okHttp.startTasks()
        }

        chart.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
//                onBackPressed()
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDengueTask(): CusTask {

        return CusTask(
                year.toInt(),
                "http://member-env-1.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue",
                getRequestStringFromUI()
        ) {
            barEntries =
                    getDengueList(JSONObject(it.getString("data")))
        }
    }

    private fun getDengueList(jsonObject: JSONObject): ArrayList<BarEntry> {
        var list = ArrayList<BarEntry>()
        for (i in jsonObject.keys()) {
            val value = jsonObject.getString(i).toFloatOrNull() ?: 0f
            barMaxValue = if (value > barMaxValue) value else barMaxValue
            list.add(BarEntry(i.toFloat(), value))
        }
        barMaxValue *= 1.1f
        println("777 *1.1 $barMaxValue")


        return list
    }

    private fun getRainTask(): CusTask {
        val jsonObject = JSONObject()
        jsonObject.put("year", year)
        if (month != 0) {
            jsonObject.put("month", month.toString())
        }
        jsonObject.put("district", district)

        return CusTask(
                year.toInt(),
                "http://member-env-1.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/rainfall",
                jsonObject.toString()
        ) {
            lineEntries =
                    getRainList(JSONObject(it.getString("data")))
            showChars()
        }
    }

    private fun getRainList(jsonObject: JSONObject): ArrayList<Entry> {
        val list = ArrayList<Entry>()
        for (i in jsonObject.keys()) {

            val value = jsonObject.getString(i).toFloatOrNull() ?: 0f

            rainMaxValue = if (value > rainMaxValue) value else rainMaxValue
            rainMinValue = if (value < rainMinValue) value else rainMinValue

            list.add(Entry(i.toFloat(), value))
        }
        return list
    }

    private fun generateBarData(): BarData {

        val set = BarDataSet(barEntries, "登革熱")
        set.color = Color.rgb(60, 220, 78)
        set.valueTextColor = Color.rgb(60, 220, 78)
        set.valueTextSize = 10f
        set.axisDependency = YAxis.AxisDependency.LEFT
        set.valueFormatter = DecimalFormatter()

        val barWidth = 0.45f
        val barData = BarData(set)
        barData.barWidth = barWidth

        return barData
    }

    private fun generateLineData(): LineData {

        for (i in lineEntries) {
            if ((rainMaxValue - rainMinValue) == 0f) {
                i.y = (barMaxValue)
                println("777 _0 ${i.y}")
            } else {
                i.y = (barMaxValue) + (((i.y - rainMinValue) * 2 * (barMaxValue / 5)) / (rainMaxValue - rainMinValue))
                println("777 ${i.y}")
            }
        }

        val dataSetA = LineDataSet(lineEntries, "雨量(mm)")
        val dataSets = ArrayList<LineDataSet>()
        dataSetA.valueFormatter = Formatter(barMaxValue, rainMaxValue, rainMinValue)
        dataSets.add(dataSetA)

        return LineData(dataSetA)
    }

    private fun showChars() {
        val data = CombinedData()

        data.setData(generateLineData())
        data.setData(generateBarData())

        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.axisMinimum = 0f
        chart.axisLeft.axisMinimum = 0f
        chart.axisRight.axisMinimum = 0f
        chart.data = data

        chart.invalidate()

        runOnUiThread {
            chart.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun resetValue() {
        barMaxValue = 0f
        rainMaxValue = 0f
        rainMinValue = 0f
    }

    private fun getRequestStringFromUI(): String {
        val jsonObject = JSONObject()
        jsonObject.put("year", year)
        if (month != 0) {
            jsonObject.put("month", month.toString())
        }
        jsonObject.put("district", district)

        return jsonObject.toString()
    }

    private fun spinnerInit() {
        val yearList = ArrayAdapter.createFromResource(this, R.array.year_array, R.layout.spinner_center_item)
        val monthList = ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_center_item)

        year_spinner.adapter = yearList
        month_spinner.adapter = monthList
    }

}