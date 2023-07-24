package com.example.aworldaction.adapters

import android.content.Context
import android.opengl.Visibility
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class SlideshowAdapter(private val images: List<JSONObject>, private val context: Context) : PagerAdapter() {
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageJSON = images[position]
        val slideshowItem = View.inflate(context, R.layout.slideshow_item, null)
        val imageView = slideshowItem.findViewById<ImageView>(R.id.imageView)
        val captionDisplay = slideshowItem.findViewById<TextView>(R.id.camptionDisplay)
        val dateDisplay = slideshowItem.findViewById<TextView>(R.id.dateDisplay)

        if (imageJSON.has("path")) {
            val imageUrl = AppSettings.getStorageUrl(imageJSON.getString("path"))

            Glide.with(context)
                .load(imageUrl)
                .centerCrop()
                .into(imageView)
        }

        if (imageJSON.has("caption")) {
            captionDisplay.text = imageJSON.getString("caption")
        }

        if (imageJSON.has("created_at")) {
            val date = imageJSON.getString("created_at")
            dateDisplay.text = AppSettings.formatDateString(date)
        }

        imageView.setOnClickListener {
            val infoBox = slideshowItem.findViewById<LinearLayout>(R.id.infoBox)
            infoBox.visibility = if (infoBox.isVisible) View.INVISIBLE else View.VISIBLE
        }

        container.addView(slideshowItem)
        return slideshowItem
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}