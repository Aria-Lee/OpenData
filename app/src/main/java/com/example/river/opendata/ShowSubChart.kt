package com.example.river.opendata

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.sub_chart.*

class ShowSubChart: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sub_chart)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        spinnerInit()

    }

    fun spinnerInit(){
        val yearList = ArrayAdapter.createFromResource(this,  R.array.year_array, R.layout.spinner_center_item)
        val monthList = ArrayAdapter.createFromResource(this, R.array.month_array, R.layout.spinner_center_item)

        year_spinner.adapter = yearList
        month_spinner.adapter = monthList
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



}