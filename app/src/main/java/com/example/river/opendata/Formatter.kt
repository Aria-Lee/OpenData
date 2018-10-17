package com.example.river.opendata

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class Formatter : IValueFormatter {

    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0.0")

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        // write your logic here
        //20
        //1000
        return mFormat.format(value) + " $" // e.g. append a dollar-sign
    }
}