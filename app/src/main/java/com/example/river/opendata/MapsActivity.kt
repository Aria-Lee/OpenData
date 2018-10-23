package com.example.river.opendata

import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
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

    fun init() {
        initFragments()
        supportActionBar?.hide()
        navigation.visibility = View.GONE
        container.visibility = View.GONE
        Glide.with(this).load(R.drawable.mosquito_clipart_animation).into(loading)
//        mapFragment!!.getMapAsync(mapFragment)
        switchContent()
    }

    fun showMap(){
        switchContent()
        supportActionBar?.show()
        navigation.visibility = View.VISIBLE
        loading.visibility = View.GONE
        container.visibility = View.VISIBLE
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        init()
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

    }

    private var mapFragment: MapFragment? = null
    private var chartFragment: ChartFragment? = null

    private fun switchContent() {
        val transaction = manager
                .beginTransaction()
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

    fun initFragments(){

        if (mapFragment == null) {
            mapFragment = MapFragment()
            mapFragment!!.addCallBack(::showMap)
        }

        if (chartFragment == null) {
            chartFragment = ChartFragment()
        }
    }

    var boundsList = mutableListOf<LatLngBounds>()

    fun bounds(list: MutableList<PointF>) {
        var builder = LatLngBounds.Builder()
        for (i in 0..list.size - 1) {
            builder.include(LatLng(list[i].x.toDouble(), list[i].y.toDouble()))
            boundsList.add(builder.build())
        }
    }


    fun PtInPolygon(point: LatLng, APoints: MutableList<LatLng>): Boolean {
        var nCross = 0
        for (i in 0..APoints.size - 1) {
            val p1 = APoints.get(i)
            val p2 = APoints.get((i + 1) % APoints.size)
            // 求解 y=p.y 與 p1p2 的交點
            if (p1.longitude == p2.longitude)      // p1p2 與 y=p0.y平行
                continue;
            if (point.longitude < Math.min(p1.longitude, p2.longitude))   // 交點在p1p2延長線上
                continue;
            if (point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交點在p1p2延長線上
                continue;
            // 求交點的 X 坐標 --------------------------------------------------------------
            val x = (point.longitude - p1.longitude) * (p2.latitude - p1.latitude) / (p2.longitude - p1.longitude) + p1.latitude
            if (x > point.latitude)
                nCross++; // 只統計單邊交點
        }
        // 單邊交點為偶數，點在多邊形之外 ---
        return (nCross % 2 == 1)
    }
}

