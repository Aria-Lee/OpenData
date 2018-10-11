package com.example.river.opendata

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.river.opendata.district.Annan
import com.example.river.opendata.district.Anping
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        var list = Anping.getLatLng()
        var list2 = Annan.getLatLng()

        addDistrincsPolygons(list, "Anping")
        addDistrincsPolygons(list2, "Annan")


        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                                23.000947952270508,
                                120.14522552490234),
                        12.0f))






        mMap.setOnPolygonClickListener {
            Toast.makeText(this, it.tag.toString(), Toast.LENGTH_SHORT).show()
//            mMap.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                            LatLng(.latitude, location.longitude),
//                            12.0f))

//            mMap.moveCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                            LatLng(it.points[0].latitude,
//                                    it.points[0].longitude),
//                            12.0f))
        }


        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun addDistrincsPolygons(list: MutableList<PointF>, tag: String) {

        var polygonOptions = PolygonOptions()

        for (i in list) {
            polygonOptions.add(LatLng(i.x.toDouble(), i.y.toDouble()))
        }

        var polygon = mMap.addPolygon(
                polygonOptions
                        .strokeColor((Color.GREEN))
                        .fillColor(Color.YELLOW))
        polygon.isClickable = true
        polygon.tag = tag

    }
}
