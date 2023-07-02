package com.example.aworldaction.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.aworldaction.settings.AppSettings

class SlideshowAdapter(private val images: List<String>, private val context: Context) : PagerAdapter() {
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageUrl = AppSettings.getStorageUrl(images[position])
        val imageView = ImageView(context)
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .into(imageView)

        container.addView(imageView)

        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}