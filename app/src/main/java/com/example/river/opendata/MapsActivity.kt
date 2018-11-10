package com.example.river.opendata

import android.app.ProgressDialog.show
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.example.river.opendata.fragments.AboutFragment
import com.example.river.opendata.fragments.ChartFragment
import com.example.river.opendata.fragments.MapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity() {

    enum class FragmentType {
        Map,
        Chart,
        About
    }

    private var type = FragmentType.Map
    private val manager = this.supportFragmentManager
    private var mapFragment: MapFragment? = null
    private var chartFragment: ChartFragment? = null
    private var aboutFragment: AboutFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        setSupportActionBar(toolbar)
        with(supportActionBar) {
            title = "Visual Dengue"
            toolbar.setTitleTextColor(Color.WHITE)
//            toolbar.setLogo(R.drawable.mosquito)
            this?.hide()
        }

        navigation.visibility = View.GONE

        Glide
                .with(this)
                .load(R.drawable.mosquito_clipart_animation)
                .addListener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        initView()
                        return false
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return true
                    }
                })
                .into(loading)

    }

    private fun initView() {

        supportActionBar?.show()
        navigation.visibility = View.VISIBLE

        init()

        navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_map -> {
                    type = FragmentType.Map
                }
                R.id.navigation_chart -> {
                    type = FragmentType.Chart
                }

                R.id.navigation_about -> {
                    type = FragmentType.About
                }

                else -> {
                    return@setOnNavigationItemSelectedListener false
                }
            }
            switchContent()
            true
        }
    }

    private fun init() {
        MapResponseData.preAddData()
        initFragments()
        switchContent()
    }

    private fun initFragments() {

        if (mapFragment == null) {
            mapFragment = MapFragment()
            mapFragment!!.addCallBack(::hideLoadingImage)
        }

        if (chartFragment == null) {
            chartFragment = ChartFragment()
        }

        if (aboutFragment == null) {
            aboutFragment = AboutFragment()
        }
    }

    private fun hideLoadingImage() {
        loading.visibility = View.INVISIBLE
    }

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
                        .hide(aboutFragment!!)
                        .show(mapFragment!!)
                        .commit()
            }

            FragmentType.Chart -> {
                if (!chartFragment!!.isAdded) {
                    transaction.add(R.id.container, chartFragment!!)
                }
                transaction
                        .hide(mapFragment!!)
                        .hide(aboutFragment!!)
                        .show(chartFragment!!)
                        .commit()
            }

            FragmentType.About -> {
                if (!aboutFragment!!.isAdded) {
                    transaction.add(R.id.container, aboutFragment!!)
                }
                transaction
                        .hide(mapFragment!!)
                        .hide(chartFragment!!)
                        .show(aboutFragment!!)
                        .commit()
            }
        }
    }

    var boundsList = mutableListOf<LatLngBounds>()

    fun bounds(list: MutableList<PointF>) {
        val builder = LatLngBounds.Builder()
        for (i in 0 until list.size) {
            builder.include(LatLng(list[i].x.toDouble(), list[i].y.toDouble()))
            boundsList.add(builder.build())
        }
    }


//    fun PtInPolygon(point: LatLng, APoints: MutableList<LatLng>): Boolean {
//        var nCross = 0
//        for (i in 0..APoints.size - 1) {
//            val p1 = APoints.get(i)
//            val p2 = APoints.get((i + 1) % APoints.size)
//            // 求解 y=p.y 與 p1p2 的交點
//            if (p1.longitude == p2.longitude)      // p1p2 與 y=p0.y平行
//                continue;
//            if (point.longitude < Math.min(p1.longitude, p2.longitude))   // 交點在p1p2延長線上
//                continue;
//            if (point.longitude >= Math.max(p1.longitude, p2.longitude))   // 交點在p1p2延長線上
//                continue;
//            // 求交點的 X 坐標 --------------------------------------------------------------
//            val x = (point.longitude - p1.longitude) * (p2.latitude - p1.latitude) / (p2.longitude - p1.longitude) + p1.latitude
//            if (x > point.latitude)
//                nCross++; // 只統計單邊交點
//        }
//        // 單邊交點為偶數，點在多邊形之外 ---
//        return (nCross % 2 == 1)
//    }
}

