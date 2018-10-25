package com.example.river.opendata

import org.json.JSONObject

data class CusTask(
        var url: String,
        var requestString: String,
        val requestCallback:(JSONObject)->Unit)