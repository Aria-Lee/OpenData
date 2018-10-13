package com.example.river.opendata.fragments


import android.content.res.Resources
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.river.opendata.DataHelper
import com.example.river.opendata.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : SupportMapFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        //return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun getMapAsync(p0: OnMapReadyCallback?) {
        super.getMapAsync(p0)
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
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this.context, R.raw.style_json))
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
            val geocoder = Geocoder(this.context)
            val addressList: MutableList<Address>

            addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            Toast.makeText(this.context,
                    addressList[0].locality, Toast.LENGTH_SHORT).show()
        }

        mMap.setOnPolygonClickListener {
            Toast.makeText(this.context, it.tag.toString(), Toast.LENGTH_SHORT).show()
            it.fillColor = Color.WHITE
            it.strokeColor = Color.BLUE
        }
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


}