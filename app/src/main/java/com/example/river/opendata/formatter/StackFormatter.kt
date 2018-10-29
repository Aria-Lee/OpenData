package com.example.river.opendata.formatter

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.StackedValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import java.text.DecimalFormat

class StackFormatter() : StackedValueFormatter(false, "$", 0) {

    private val mAppendix: String? = null
    private val mFormat: DecimalFormat = DecimalFormat("###,###,##0")

    var stack2015 = listOf("其他 13576", "南區 3529", "北區 5772")
    var stack2016 = listOf("其他 32", "東區 5", "永康區 6")

    var firstEntry = 0
    var secondEntry = 0


    override fun getFormattedValue(value: Float, entry: Entry?, dataSetIndex: Int, viewPortHandler: ViewPortHandler?): String {

        val barEntry = entry as BarEntry
        val hashCode = barEntry.hashCode()

        if (firstEntry == 0) {
            firstEntry = hashCode
            return findValue(barEntry.yVals, value, stack2015)
        }
        if (hashCode == firstEntry) {
            return findValue(barEntry.yVals, value, stack2015)
        }
        if (hashCode != 0 && hashCode != firstEntry) {
            secondEntry = hashCode
            return findValue(barEntry.yVals, value, stack2016)
        } else {
            return "ER"
        }

    }

    private fun findValue(vals: FloatArray, value: Float, list: List<String>): String {

        var result = ""
        for (i in 0 until vals.size) {
            if (vals[i] == value) {
                result = list[i]
            }
        }

        return result
    }
}