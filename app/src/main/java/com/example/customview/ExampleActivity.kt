package com.example.customview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.example_activity.*

class ExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity)

        customToolbar.addImage(R.drawable.ic_post)
        customToolbar.addImage(R.drawable.ic_users)
        customToolbar.addImage(R.drawable.ic_tags)

        rvExample.layoutManager = LinearLayoutManager(this)
        rvExample.adapter = ExampleAdapter()

        customToolbar.setOnPositionClickListener(OnPositionClickListener { position, view ->
            Toast.makeText(
                view?.context,
                "Position $position",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

}