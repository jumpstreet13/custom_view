package com.example.customview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*

class ExampleAdapter : RecyclerView.Adapter<ExampleAdapter.ItemViewHolder>() {

    private val list = listOf(R.drawable.car, R.drawable.car, R.drawable.car, R.drawable.car, R.drawable.car)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) = holder.bind(list[position])

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(idRes: Int) = itemView.ivCar.setImageResource(idRes)

    }

}