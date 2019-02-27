package com.example.customview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class MyAdapter : RecyclerView.Adapter<MyAdapter.ItemViewHolder>() {

    override fun getItemCount() = 10

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return ItemViewHolder(view)
    }


    class ItemViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

    }

}