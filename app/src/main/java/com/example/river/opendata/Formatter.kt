package com.example.river.opendata

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class Formatter(val barMaxValue: Float, val rainMaxValue: Float, val rainMinValue: Float) : IValueFormatter {

    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        // write your logic here

        println("*** dataSetIndex: $dataSetIndex")
        //var v = value - barMaxValue

        var v = (value - barMaxValue - rainMinValue) * (rainMaxValue - rainMinValue) / (barMaxValue / 5) / 2 + rainMinValue

        return mFormat.format(v) // e.g. append a dollar-sign
    }
}