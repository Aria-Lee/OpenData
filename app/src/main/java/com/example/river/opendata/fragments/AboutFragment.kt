package com.example.river.opendata.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.example.river.opendata.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_about.view.*
import kotlinx.android.synthetic.main.main_chart.view.*

class AboutFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = LayoutInflater.from(container!!.context).inflate(R.layout.fragment_about, container, false)
        v.what_is_SDG_pref.movementMethod = LinkMovementMethod.getInstance()
        v.what_is_SGD_doc.movementMethod = LinkMovementMethod.getInstance()
        return v
    }

}

