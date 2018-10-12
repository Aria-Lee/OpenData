package com.example.river.opendata

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.example.river.opendata.district.Annan
import com.example.river.opendata.district.Anping
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.content.res.Resources.NotFoundException
import android.R.raw
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.gms.maps.model.*
import android.location.Location.distanceBetween
import com.google.android.gms.common.util.ArrayUtils.contains
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.StringWriter
import java.lang.Exception


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
//        val viewGroup : ViewGroup = R.id.content as ViewGroup
//        view = LayoutInflater.from(this).inflate(R.layout.for_polygon,viewGroup, false)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    var boundsList = mutableListOf<LatLngBounds>()

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json))
            if (!success) {
                Log.e("aaaaa", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("aaaaa", "Can't find style. Error: ", e)
        }

        val bounds = LatLngBounds(LatLng(22.967090, 120.067050), LatLng(23.091322, 120.247097))
        mMap.setLatLngBoundsForCameraTarget(bounds)
        mMap.setMinZoomPreference(11f)

        val jsonString = DataHelper.getJSONString(resources.openRawResource(R.raw.gml_json))

        addPolygons(DataHelper.getList(jsonString))

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                                23.000947952270508,
                                120.14522552490234),
                        11.0f))
        

        mMap.setOnMapLongClickListener {
            val geocoder = Geocoder(this)
            val addressList: MutableList<Address>

            addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            Toast.makeText(this,
                    addressList[0].locality, Toast.LENGTH_SHORT).show()
        }

        mMap.setOnPolygonClickListener {
            Toast.makeText(this, it.tag.toString(), Toast.LENGTH_SHORT).show()
            it.fillColor = Color.WHITE
            it.strokeColor = Color.BLUE
        }
    }

    fun bounds(list: MutableList<PointF>) {
        var builder = LatLngBounds.Builder()
        for (i in 0..list.size - 1) {
            builder.include(LatLng(list[i].x.toDouble(), list[i].y.toDouble()))
            boundsList.add(builder.build())
        }
        Log.wtf("aaaaa", boundsList.toString())

    }

    fun addPolygons(list: MutableList<MutableList<LatLng>>) {

        for (districtList in list) {

            var polygonOptions = PolygonOptions()

            for (x in districtList) {
                polygonOptions.add(x)
            }

            var polygon = mMap.addPolygon(
                    polygonOptions
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.YELLOW)
            )
            polygon.isClickable = true
        }
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
