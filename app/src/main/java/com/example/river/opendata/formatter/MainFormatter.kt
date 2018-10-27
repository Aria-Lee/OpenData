package com.example.river.opendata.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class MainFormatter(val barMaxValue: Float,
                    val maxValue: Float,
                    val minValue: Float,
                    val modifyLocation:Float) : IValueFormatter {

    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        // write your logic here

        println("*** dataSetIndex: $dataSetIndex")
        //var v = value - barMaxValue

        var v = (value - modifyLocation*barMaxValue) * (maxValue - minValue) / (barMaxValue / 5)  + minValue
// i.y = (1.1f * barMaxValue) + (((i.y - rainMinValue) * 1f * (barMaxValue / 5)) / (rainMaxValue - rainMinValue))
        return mFormat.format(v) // e.g. append a dollar-sign
    }
}