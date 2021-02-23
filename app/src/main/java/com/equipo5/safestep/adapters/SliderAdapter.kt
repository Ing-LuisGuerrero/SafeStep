package com.equipo5.safestep.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.equipo5.safestep.R
import com.equipo5.safestep.models.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso


class SliderAdapter(val context: Context, private val mSliderItems: MutableList<SliderItem>) : SliderViewAdapter<SliderAdapter.SliderAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate: View =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem: SliderItem = mSliderItems[position]

        if(sliderItem.imageUrl != null) {
            if(sliderItem.imageUrl!!.isNotEmpty()) {
                Picasso.with(context).load(sliderItem.imageUrl).into(viewHolder.imageViewBackground)
            }
        }

    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        val imageViewBackground: ImageView = itemView.findViewById<ImageView>(R.id.iv_auto_image_slider)
    }

}