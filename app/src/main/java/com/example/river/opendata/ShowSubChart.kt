package com.example.river.opendata

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.sub_chart.*
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData


class ShowSubChart : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_chart)


        this.title = intent.getStringExtra("district")

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        spinnerInit()

        val data = CombinedData()
//        chart_line.data = getLineData()
//        chart_line.data = generateBarData()
        data.setData(getLineData())
        data.setData(generateBarData())

//        data.setValueTypeface(mTfLight)

//        xAxis.setAxisMaximum(data.xMax + 0.25f)

        chart.data = data
        chart.invalidate()

    }


    val DATA_COUNT = 5

    private fun getChartData(): List<Entry> {
        val chartData = ArrayList<Entry>()
        for (i in 0 until DATA_COUNT) {
            chartData.add(Entry(i * 2f, i.toFloat()))
        }
        return chartData
    }

    private fun getLabels(): List<String> {
        val chartLabels = ArrayList<String>()
        for (i in 0 until DATA_COUNT) {
            chartLabels.add("X$i")
        }
        return chartLabels
    }

    private fun getLineData(): LineData {
        val dataSetA = LineDataSet(getChartData(), "LabelA")

        val dataSets = ArrayList<LineDataSet>()
        dataSets.add(dataSetA) // add the datasets

        return LineData(dataSetA)
    }


    private fun generateBarData(): BarData {

        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()

        for (index in 0 until DATA_COUNT) {
            entries1.add(BarEntry(0f, 5f))

            // stacked
            entries2.add(BarEntry(0f, floatArrayOf(6f, 10f)))
        }

        val set1 = BarDataSet(entries1, "Bar 1")
        set1.color = Color.rgb(60, 220, 78)
        set1.valueTextColor = Color.rgb(60, 220, 78)
        set1.valueTextSize = 10f
        set1.axisDependency = YAxis.AxisDependency.LEFT

        val set2 = BarDataSet(entries2, "")
        set2.stackLabels = arrayOf("Stack 1", "Stack 2")
        set2.setColors(*intArrayOf(Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)))
        set2.valueTextColor = Color.rgb(61, 165, 255)
        set2.valueTextSize = 10f
        set2.axisDependency = YAxis.AxisDependency.LEFT

        val groupSpace = 0.06f
        val barSpace = 0.02f // x2 dataset
        val barWidth = 0.45f // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        val d = BarData(set1, set2)
        d.barWidth = barWidth

        // make this BarData object grouped
        d.groupBars(0f, groupSpace, barSpace) // start at x = 0

        return d
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