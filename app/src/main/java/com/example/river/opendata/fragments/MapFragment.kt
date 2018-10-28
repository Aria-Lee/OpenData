package com.example.river.opendata.fragments


import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.river.opendata.*
import com.example.river.opendata.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.json.JSONObject


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    lateinit var thisView: View
    lateinit var okHttp: CusOkHttp
    var district: String? = ""
    var dengue: JSONObject? = null
    lateinit var markerImage: BitmapDescriptor


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisView = inflater.inflate(R.layout.fragment_map, container, false)
        spinnerInit()
        val map = getChildFragmentManager().findFragmentById(R.id.tainanMap) as SupportMapFragment
        map.getMapAsync(this)
        return thisView
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        okHttp = CusOkHttp(this.context!!)

        okHttp.addCusTask(getDengueTask(2015))
//        okHttp.addCusTask(getDengueTask(2016))
//        okHttp.addCusTask(getDengueTask(2018))
        okHttp.startTasks()

        MapsInitializer.initialize(this.context)
        makeMarkerIcon()

        super.onCreate(savedInstanceState)
    }

    private fun getDengueTask(year: Int): CusTask {

        return CusTask(
                "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue",
                JSONObject().put("year", year).toString()
        ) {
            dengue = okHttp.getJSONObjectData(it)
            MapResponseData.addData(year, it.getJSONObject("data"))
            if (marker != null) {
                val value = MapResponseData.getDengueValue(year, district!!)
                (context as MapsActivity).runOnUiThread {
                    addMarker(marker!!.position, value)
                }
            }

            val noDataYear = MapResponseData.checkAllDatas()
            if (noDataYear != null){
                okHttp.addCusTask(getDengueTask(noDataYear))
                okHttp.startTasks()
            }
            println("123 $year data downloaded.")
        }
    }

    private fun makeMarkerIcon() {
        val bitmapFactoryOption = BitmapFactory.Options()
        bitmapFactoryOption.inMutable = true
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_action_name, bitmapFactoryOption)
        bitmap.width = 1
        bitmap.height = 1
        markerImage = BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun spinnerInit() {
        val yearList = ArrayAdapter.createFromResource(this.context, R.array.year_array, R.layout.spinner_center_item)
        thisView.all_year_spinner.adapter = yearList
        thisView.all_year_spinner.onItemSelectedListener = spinnerListener
//        spinnerToRequest()
    }

    private val spinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            //
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val year = thisView.all_year_spinner.selectedItem.toString().toInt()
//            requestString = JSONObject().put("year", year).toString()
//            url = "http://member-env.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue"
//            requestData("dengue", url)
            val data = MapResponseData.getData(year)
            //有資料，顯示在 Marker 上
            if (marker != null && data != null) {
                (context as MapsActivity).runOnUiThread {
                    //已顯示 Marker 表示也會有行政區資訊 (district)
                    val value = MapResponseData.getDengueValue(year, district!!)
                    addMarker(marker!!.position, value)
                }
            } else {
                //移除 Marker
                removeMarker()
                //停止目前 request
                okHttp.cancelAll()
                //重新請求
                okHttp.addCusTask(getDengueTask(year))
                okHttp.startTasks()
            }
        }
    }

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
            val year = thisView.all_year_spinner.selectedItem.toString().toInt()

            //還沒有資料
            if (!MapResponseData.isDataCreated(year)) {
                okHttp.cancelAll()
                okHttp.addCusTask(getDengueTask(year))
                okHttp.startTasks()
                return@setOnMapLongClickListener
            }

            //有資料
            removeMarker()
            val district = getDistrict(it)

            if (district != null) {
                val value = MapResponseData.getDengueValue(year, district)
                addMarker(it, value)
            }
        }

        mMap.setOnMapClickListener {
            removeMarker()
        }

        mMap.setOnPolygonClickListener {
            removeMarker()
            val intent = Intent(this.context, ShowSubChart::class.java)
            intent.putExtra("district", it.tag.toString())
            startActivity(intent)
        }

        mMap.setOnMarkerClickListener { p0 ->
            removeMarker()
            val district = getDistrict(p0!!.position)
            val intent = Intent(context, ShowSubChart::class.java)
            intent.putExtra("district", district)
            startActivity(intent)
            false
        }

        //完全載入事件
        mMap.setOnMapLoadedCallback {
            this.callBack.invoke()
            println("*** Map Loaded")
        }

        println("*** Map Ready")
    }

    private fun addPolygons(list: MutableList<MutableList<LatLng>>) {

        for (i in 0 until list.size) {

            val polygonOptions = PolygonOptions()

            for (j in list[i]) {
                polygonOptions.add(j)
            }

            val polygon = mMap.addPolygon(
                    polygonOptions
                            .strokeColor(Color.GREEN)
                            .fillColor(Color.YELLOW)
            )
            polygon.isClickable = true
            polygon.tag = DataHelper.districtList[i]
        }
    }

    private lateinit var callBack: () -> Unit
    fun addCallBack(callBack: () -> Unit) {
        this.callBack = callBack
    }

    fun addMarker(latLng: LatLng, value: String) {
        district = getDistrict(latLng)
//        try {
//            dengueNum = dengue!!.getInt(district)
//        } catch (e: Exception) {
//            dengueNum = 0
//            Log.d("aaaaa", "No Value")
//        }
        marker =
                mMap.addMarker(
                        MarkerOptions()
                                .icon(markerImage)
                                .alpha(0f)
                                .position(latLng)
                                .title("$district : $value")
                )

        marker!!.showInfoWindow()
    }

    fun removeMarker() {
        if (marker != null) {
            marker!!.remove()
        }
    }

    fun getDistrict(latLng: LatLng): String? {
        val geoCoder = Geocoder(this.context)
        val addressList: MutableList<Address>

        addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addressList.size == 0) {
            return null
        }

        return addressList[0].locality
    }
}
