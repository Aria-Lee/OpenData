package com.example.river.opendata.fragments


import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import com.example.river.opendata.DataHelper
import com.example.river.opendata.R
import com.example.river.opendata.ShowSubChart
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Marker
import kotlinx.android.synthetic.main.activity_maps.*


class MapFragment() : SupportMapFragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
        //return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun getMapAsync(p0: OnMapReadyCallback?) {
        println("*** MapAsync")
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

        val bounds =
                LatLngBounds(LatLng(23.091185, 120.228257), LatLng(23.450089, 120.665024))
        mMap.setLatLngBoundsForCameraTarget(bounds)
        mMap.setMinZoomPreference(10.0f)

        val jsonString = DataHelper.getJSONString(resources.openRawResource(R.raw.gml_json))

        addPolygons(DataHelper.getList(jsonString))

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        LatLng(
                                23.000947952270508,
                                120.14522552490234),
                        10.0f))


        mMap.setOnMapLongClickListener {
            removeMarker()
            if (getDistrict(it) != null) {
                addMarker(it)
            }
        }

        mMap.setOnMapClickListener {
            removeMarker()
        }


        mMap.setOnPolygonClickListener {
            removeMarker()
            Toast.makeText(this.context, it.tag.toString(), Toast.LENGTH_SHORT).show()
            val intent = Intent(this.context, ShowSubChart::class.java)
            intent.putExtra("district", it.tag.toString())
            startActivity(intent)
        }

        mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
            override fun onMarkerClick(p0: Marker?): Boolean {
                removeMarker()
                var district = getDistrict(p0!!.position)
                val intent = Intent(context, ShowSubChart::class.java)
                intent.putExtra("district", district)
                startActivity(intent)
                return false
            }
        })

        Thread {
            Thread.sleep(3000)
            println("*** invoke")
            activity!!.runOnUiThread { this.callBack.invoke() }

        }.start()

        println("*** MapReady")
    }

    fun addPolygons(list: MutableList<MutableList<LatLng>>) {

        for (i in 0 until list.size) {

            var polygonOptions = PolygonOptions()

            for (j in list[i]) {
                polygonOptions.add(j)
            }

            var polygon = mMap.addPolygon(
                    polygonOptions
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.YELLOW)
            )
            polygon.isClickable = true
            polygon.tag = DataHelper.districtList[i]
        }
    }


    lateinit var callBack: () -> Unit
    fun addCallBack(callBack: () -> Unit) {
        println("*** addCallBack")
        this.callBack = callBack
    }


    fun addMarker(latLng: LatLng) {
        marker =
                mMap.addMarker(
                        MarkerOptions()
                                .alpha(0f)
                                .position(latLng)
                                .title("Hello world")

                )

        marker!!.showInfoWindow()
    }

    fun removeMarker() {
        if (marker != null) {
            marker!!.remove()
        }
    }

    fun getDistrict(latLng: LatLng): String? {
        val geocoder = Geocoder(this.context)
        val addressList: MutableList<Address>

        addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addressList.size == 0) {
            return null
        }

        Toast.makeText(this.context,
                addressList[0].locality, Toast.LENGTH_SHORT).show()

        return addressList[0].locality
    }
}

