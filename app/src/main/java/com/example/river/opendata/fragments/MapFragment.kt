package com.example.river.opendata.fragments


import android.app.Application
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.media.Image
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.river.opendata.*
import com.example.river.opendata.R
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.sub_chart.*
import kotlinx.android.synthetic.main.sub_chart.view.*
import org.json.JSONObject
import java.lang.Exception


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    lateinit var thisView: View
    lateinit var okHttp: MyOkHttp
    var year = ""
    var url = ""
    var district: String? = ""
    var dengue : JSONObject? = null
    var dengueNum = 0
    lateinit var requestString: String
    lateinit var markerImage : BitmapDescriptor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        thisView = inflater.inflate(R.layout.fragment_map, container, false)
        spinnerInit()
        val map = getChildFragmentManager().findFragmentById(R.id.tainanMap) as SupportMapFragment
        map.getMapAsync(this)
        return thisView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        okHttp = MyOkHttp(this.context)
        MapsInitializer.initialize(this.context)
        makeMarkerIcon()
        super.onCreate(savedInstanceState)
    }

    fun makeMarkerIcon(){
        val bitmapFactoryOption = BitmapFactory.Options()
        bitmapFactoryOption.inMutable = true
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_action_name, bitmapFactoryOption)
        bitmap.width = 1
        bitmap.height = 1
        markerImage = BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    //    override fun getMapAsync(p0: OnMapReadyCallback?) {
//        println("*** MapAsync")
//        super.getMapAsync(p0)
//    }
    fun spinnerInit() {
        val yearList = ArrayAdapter.createFromResource(this.context, R.array.year_array, R.layout.spinner_center_item)
        thisView.all_year_spinner.adapter = yearList
        thisView.all_year_spinner.onItemSelectedListener = spinnerListener
//        spinnerToRequest()
    }

    val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            year = thisView.all_year_spinner.selectedItem.toString()
            requestString = JSONObject().put("year", year).toString()
            url = "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue"
            requestData("dengue", url)
        }
    }

    fun requestData(type: String, url: String) {
        Thread {
            okHttp.request(
                    url,
                    requestString,
                    ::callBack,
                    type
            )
        }.start()
    }

    fun callBack(type: String, jsonObject: JSONObject) {
        if (okHttp.isSuccess(jsonObject)) {
//            println("*** ${System.nanoTime()} $type")
            if (dengue != null) {
                (context as MapsActivity).runOnUiThread {
                    Toast.makeText(context, "數據已更新", Toast.LENGTH_LONG).show()
                }
            }
            dengue = okHttp.getJSONObjectData(jsonObject)
            if(marker != null){
                (context as MapsActivity).runOnUiThread {
                    addMarker(marker!!.position)
                }
            }
        }
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
//        addPolygons(AllDistricts.list)
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
//            Toast.makeText(this.context, it.tag.toString(), Toast.LENGTH_SHORT).show()
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
        district = getDistrict(latLng)
        try {
            dengueNum = dengue!!.getInt(district)
        } catch (e: Exception) {
            dengueNum = 0
            Log.d("aaaaa", "No Value")
        }
        marker =
                mMap.addMarker(
                        MarkerOptions()
                                .icon(markerImage)
                                .alpha(0f)
                                .position(latLng)
                                .title("$district : $dengueNum")
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

//        Toast.makeText(this.context,
//                addressList[0].locality, Toast.LENGTH_SHORT).show()

        return addressList[0].locality
    }
}

