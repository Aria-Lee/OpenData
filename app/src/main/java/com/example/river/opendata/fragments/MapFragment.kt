package com.example.river.opendata.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.river.opendata.*
import com.example.river.opendata.R
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.fragment_map.view.*
import org.json.JSONObject


class MapFragment : Fragment(), OnMapReadyCallback {


    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    lateinit var thisView: View
    lateinit var okHttp: CusOkHttp
    var district: String? = ""
    private lateinit var markerImage: BitmapDescriptor
    private var polygonList: MutableList<Polygon> = mutableListOf()
    var cameraHeight: Double = 0.0
    var cameraWidth: Double = 0.0
    lateinit var viewPort: VisibleRegion
    var westLng = 120.024
    var eastLng = 120.656363
    var northLat = 23.413767
    var southLat = 22.887520
    var westBoundLng = 0.00
    var eastBoundLng = 0.00
    var northBoundLat = 0.00
    var southBoundLat = 0.00
    val tainanHeight = northLat - southLat
    val tainanWidth = eastLng - westLng
    lateinit var cameraBounds: LatLngBounds
    val tainanCenter = LatLng((northLat + southLat) / 2, (westLng + eastLng) / 2)
    var oriZoom = 0.0f
    var initCameraBounds = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        thisView = inflater.inflate(R.layout.fragment_map, container, false)
        spinnerInit()
        val map = getChildFragmentManager().findFragmentById(R.id.tainanMap) as SupportMapFragment
        map.getMapAsync(this)
        return thisView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        okHttp = CusOkHttp(this.context!!)
        MapsInitializer.initialize(this.context)
        makeMarkerIcon()

    }

    private fun getDengueTask(year: Int): CusTask {

        return CusTask(
                year,
                "http://member-env-1.jdrcjciuxp.ap-northeast-1.elasticbeanstalk.com/api/dengue",
                JSONObject().put("year", year).toString()
        ) {
            MapResponseData.addData(year, it.getJSONObject("data"))

            MapResponseData.removeAfterGetResponse(year)
            if (marker != null) {
                val value = MapResponseData.getDengueValue(year, district!!)
                (context as MapsActivity).runOnUiThread {
                    addMarker(marker!!.position, value)
                }
            }
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
            val data = MapResponseData.getData(year)

            //有資料，顯示在 Marker 上
            if (marker != null && data != null) {
                (context as MapsActivity).runOnUiThread {
                    //已顯示 Marker 表示也會有行政區資訊 (district)
                    val value = MapResponseData.getDengueValue(year, district!!)
                    addMarker(marker!!.position, value)
                }
            } else {
                if (data == null) {
                    //停止目前 request
                    okHttp.cancelAll()
                    //重新請求
                    //把選擇的年放到第一個
                    MapResponseData.moveYearToFirst(year)
                    for (i in MapResponseData.waitForRemoveList) {
                        okHttp.addCusTask(getDengueTask(i))
                    }
                    okHttp.startTasks()
                }
            }

            updateMapColor()
        }
    }

//    override fun onCameraIdle() {
//        viewPort = mMap.projection.visibleRegion
//        cameraHeight = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.farLeft)
//        cameraWidth = SphericalUtil.computeDistanceBetween(viewPort.nearLeft, viewPort.nearRight)
//    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        val task = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<Void, ProgressData, MapStyleOptions>() {

            override fun onPreExecute() {


            }

            override fun doInBackground(vararg params: Void?): MapStyleOptions? {
                var res: MapStyleOptions? = null
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    res = MapStyleOptions.loadRawResourceStyle(context, R.raw.style_json)
//                    activity!!.runOnUiThread {
//                        mMap.setMapStyle(res)
//                    }
                } catch (e: Resources.NotFoundException) {
                    Log.e("aaaaa", "Can'northLat find style. Error: ", e)
                }

                val jsonString = DataHelper.getJSONString(resources.openRawResource(R.raw.gml_json))

                val list = DataHelper.getList(jsonString)
                for (i in 0 until list.size) {
//                    Thread.sleep(10)
                    publishProgress(ProgressData(i, list[i]))
                }
                return res
            }

            override fun onProgressUpdate(vararg data: ProgressData) {
//                var list = values
//                addPolygon(data.index, values.asList().toMutableList())
                var progressData = data[0] as ProgressData
                addPolygon(progressData.index, progressData.list)
            }

            override fun onPostExecute(result: MapStyleOptions?) {

                mMap.setMapStyle(result)
                mMap.uiSettings.isRotateGesturesEnabled = false
                mMap.uiSettings.isCompassEnabled  =false

                oriZoom = 9.6f
                mMap.setMinZoomPreference(9.6f)

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tainanCenter,
                        oriZoom))
                setCameraBouns()

                mMap.setOnCameraMoveListener {
                    if (mMap.cameraPosition.zoom != oriZoom) {
                        setCameraBouns()
                        oriZoom = mMap.cameraPosition.zoom
                    }
                }

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
                    val address = getDistrict(it)
                    val district = address?.locality
                    val admin = address?.adminArea
                    if (district != null && admin == "台南市") {
                        val value = MapResponseData.getDengueValue(year, district)
                        addMarker(it, value)
                    }
                }

                mMap.setOnMapClickListener {
                    removeMarker()
                }

                mMap.setOnPolygonClickListener {
                    removeMarker()
                    val intent = Intent(context, ShowSubChart::class.java)

                    intent.putExtra("district", it.tag.toString())

                    startActivity(intent)
                }

                mMap.setOnMarkerClickListener { p0 ->
                    removeMarker()
                    val district = getDistrict(p0!!.position)?.locality
                    val intent = Intent(context, ShowSubChart::class.java)
                    intent.putExtra("district", district)
                    startActivity(intent)
                    false
                }

                //完全載入事件
                mMap.setOnMapLoadedCallback {
                    callBack.invoke()
                }
            }

        }

        task.execute()
    }

    private fun setCameraBouns() {
        viewPort = mMap.projection.visibleRegion

        cameraHeight = viewPort.farLeft.latitude - viewPort.nearLeft.latitude
        cameraWidth = viewPort.nearRight.longitude - viewPort.nearLeft.longitude

        northBoundLat = if (tainanHeight > cameraHeight) (northLat - cameraHeight / 2)
        else tainanCenter.latitude + (cameraHeight - tainanHeight) / 2

        southBoundLat = if (tainanHeight > cameraHeight) (southLat + cameraHeight / 2)
        else tainanCenter.latitude - (cameraHeight - tainanHeight) / 2

        eastBoundLng = if (tainanWidth > cameraWidth) (eastLng - cameraWidth / 2)
        else tainanCenter.longitude + (cameraWidth - tainanWidth) / 2

        westBoundLng = if (tainanWidth > cameraWidth) (westLng + cameraWidth / 2)
        else tainanCenter.longitude - (cameraWidth - tainanWidth) / 2

        cameraBounds = LatLngBounds(LatLng(southBoundLat, westBoundLng), LatLng(northBoundLat, eastBoundLng))
        mMap.setLatLngBoundsForCameraTarget(cameraBounds)
    }

    private fun addPolygon(i: Int, list: MutableList<LatLng>) {
        val polygonOptions = PolygonOptions()

        for (j in list) {
            polygonOptions.add(j)
        }

//        var data = MapResponseData.checkAllDatas()!!
//        val value = data[i]

        val polygon = mMap.addPolygon(
                polygonOptions
                        .strokeWidth(5f)
                        .strokeColor(Color.rgb(0, 163, 11))
                        .fillColor(Color.argb(100, 0, 224, 15))
//                        .fillColor(getPolygonColor(value))
        )

        polygonList.add(polygon)

        polygon.isClickable = true

        polygon.tag = DataHelper.districtList[i]
    }

    private fun addMarker(latLng: LatLng, value: String) {
        district = getDistrict(latLng)?.locality
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

    private fun removeMarker() {
        if (marker != null) {
            marker!!.remove()
        }
    }

    private fun getDistrict(latLng: LatLng): Address? {
        val geoCoder = Geocoder(this.context)
        val addressList: MutableList<Address>

        addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addressList.size == 0) {
            return null
        }

        return addressList[0]
    }

    private lateinit var callBack: () -> Unit
    fun addCallBack(callBack: () -> Unit) {
        this.callBack = callBack
    }

    fun updateMapColor() {
        val year = thisView.all_year_spinner.selectedItem.toString().toInt()

        for (i in polygonList.indices) {

            val district = DataHelper.districtList[i]
            val value = MapResponseData.getDengueValue(year, district)
            polygonList[i].fillColor = getPolygonColor(value.toInt())
        }
    }

    fun getPolygonColor(value: Int): Int {

        return when {
            value in 0..10 -> Color.argb(100, 0, 224, 15)
            value in 11..100 -> Color.argb(100, 255, 225, 0)
            value in 101..1000 -> Color.argb(100, 255, 132, 0)
            value > 1000 -> Color.argb(100, 255, 0, 0)
            else -> Color.argb(100, 0, 224, 15)
        }
    }


    private fun addPolygons(list: MutableList<MutableList<LatLng>>) {

        for (i in 0 until list.size) {

            val polygonOptions = PolygonOptions()

            for (j in list[i]) {
                polygonOptions.add(j)
            }

            val polygon = mMap.addPolygon(
                    polygonOptions
                            .strokeWidth(5f)
                            .strokeColor(Color.rgb(0, 163, 11))
                            .fillColor(Color.argb(100, 0, 224, 15))
            )
            polygon.isClickable = true
            polygon.tag = DataHelper.districtList[i]
        }
    }
}

class ProgressData(var index: Int, var list: MutableList<LatLng>)

/*
* 0 argb(100, 0, 224, 15)
* 10 argb(100, 255, 225, 0)
* 100  argb(100, 255, 162, 0)
* 1000 argb(100, 255, 0, 0)
* */