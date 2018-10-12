package com.example.river.opendata

import android.opengl.Visibility
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.main.*

class Main : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        init()
    }

    fun init() {
        supportActionBar?.hide()
        Glide.with(this).load(R.drawable.mosquito_clipart_animation).into(img)
        Thread(Runnable {
            Thread.sleep(3000)
            runOnUiThread {
                img.visibility = View.GONE
                supportActionBar?.show()
            }
        }).start()
    }

}