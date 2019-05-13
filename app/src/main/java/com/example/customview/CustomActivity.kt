package com.example.customview

import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_custom.*

class CustomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)

        customToolbar.addImage(R.drawable.ic_post)
        customToolbar.addImage(R.drawable.ic_users)
        customToolbar.addImage(R.drawable.ic_tags)
        customToolbar.addImage(R.drawable.ic_place)

        customToolbar.select(1)

        customToolbar.setColorBackground(Color.WHITE)
        customToolbar.setDuration(2000)
        customToolbar.setUnselectColor(Color.BLUE)
        customToolbar.setRadiusGradient(1000f)
        customToolbar.setSeloctColor(Color.YELLOW)

        customToolbar.setOnPositionClickListener(OnPositionClickListener { position, _ ->
            //Toast.makeText(this, "$position", Toast.LENGTH_SHORT).show()
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                customToolbar.setProgress(progress / 100f)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

        })
    }

}