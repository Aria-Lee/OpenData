package com.example.river.opendata

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.river.opendata.fragments.ChartFragment
import com.example.river.opendata.fragments.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity() {


    enum class FragmentType {
        Map,
        Chart
    }

    var type = FragmentType.Map
    val manager = this.supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//                .findFragmentById(R.id.map) as SupportMapFragment


        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_map -> {
                    type = FragmentType.Map
                }
                R.id.navigation_chart -> {
                    type = FragmentType.Chart
                }
                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }

            switchContent()
            true
        }

        switchContent()

    }

    private var mapFragment: MapFragment? = null
    private var chartFragment: ChartFragment? = null

    private fun switchContent() {
        val transaction = manager
                .beginTransaction()
//                .setCustomAnimations(
//                        android.R.anim.fade_in, android.R.anim.fade_out)

        if (mapFragment == null) {
            mapFragment = MapFragment()
            mapFragment!!.getMapAsync(mapFragment)
        }

        if (chartFragment == null) {
            chartFragment = ChartFragment()
        }

        when (type) {
            FragmentType.Map -> {
                if (!mapFragment!!.isAdded) {
                    transaction.add(R.id.container, mapFragment!!)
                }
                transaction
                        .hide(chartFragment!!)
                        .show(mapFragment!!)
                        .commit()
            }

            FragmentType.Chart -> {
                if (!chartFragment!!.isAdded) {
                    transaction.add(R.id.container, chartFragment!!)
                }
                transaction
                        .hide(mapFragment!!)
                        .show(chartFragment!!)
                        .commit()
            }
        }
    }


    var boundsList = mutableListOf<LatLngBounds>()

    fun bounds(list: MutableList<PointF>) {
        var builder = LatLngBounds.Builder()
        for (i in 0..list.size - 1) {
            builder.include(LatLng(list[i].x.toDouble(), list[i].y.toDouble()))
            boundsList.add(builder.build())
        }
        Log.wtf("aaaaa", boundsList.toString())

    }


    fun PtInPolygon(point: LatLng, APoints: MutableList<LatLng>): Boolean {
        var nCross = 0
        for (i in 0..APoints.size - 1) {
            val p1 = APoints.get(i)
            val p2 = APoints.get((i + 1) % APoints.size)
            // 求解 y=p.y 与 p1p2 的交点
            if (p1.longitude == p2.longitude)      // p1p2 与 y=p0.y平行
                continue;
            if (point.longitude < Math.min(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            if (point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交点在p1p2延长线上
                continue;
            // 求交点的 X 坐标 --------------------------------------------------------------
            val x = (point.longitude - p1.longitude) * (p2.latitude - p1.latitude) / (p2.longitude - p1.longitude) + p1.latitude
            if (x > point.latitude)
                nCross++; // 只统计单边交点
        }
        // 单边交点为偶数，点在多边形之外 ---
        return (nCross % 2 == 1)
    }
}
