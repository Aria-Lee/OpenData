package com.example.river.opendata.formatter

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class DecimalFormatter: IValueFormatter {

    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0")

    override fun getFormattedValue(value: Float, entry: Entry, dataSetIndex: Int, viewPortHandler: ViewPortHandler): String {
        // write your logic here
        if (value == 0f) return "0"

        return mFormat.format(value) // e.g. append a dollar-sign
    }
}