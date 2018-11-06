package com.example.river.opendata.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class BarFormatter() : IValueFormatter {

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {

        return when (value.toInt()) {
            10 -> "1"
            30 -> "51"
            40 -> "175"
            else -> ""
        }
    }
}