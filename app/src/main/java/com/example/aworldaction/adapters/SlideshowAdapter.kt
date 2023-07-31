package com.example.aworldaction.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
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

        val infoBox = slideshowItem.findViewById<LinearLayout>(R.id.infoBox)
        val imageView = slideshowItem.findViewById<ImageView>(R.id.imageView)
        val captionDisplay = slideshowItem.findViewById<TextView>(R.id.camptionDisplay)
        val dateDisplay = slideshowItem.findViewById<TextView>(R.id.dateDisplay)
        val deletePictureBtn = slideshowItem.findViewById<ImageButton>(R.id.deletePictureBtn)
        var loggedUserIsCreator = false

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

        if (imageJSON.has("user_id")) {
            val loggedUser = AppSettings.getUser()

            if (loggedUser != null && loggedUser.has("id")) {
                loggedUserIsCreator =
                    imageJSON.getInt("user_id") == loggedUser.getInt("id")
            }
        }

        deletePictureBtn.visibility = if (loggedUserIsCreator) View.VISIBLE else View.GONE

        deletePictureBtn.setOnClickListener {
            val activity = context as? DetailActivity
            activity?.showDeletePictureDialog(imageJSON.getInt("id"))
        }

        imageView.setOnClickListener {
            val captionVisibility = if (infoBox.isVisible) View.INVISIBLE else View.VISIBLE
            infoBox.visibility = captionVisibility
            deletePictureBtn.visibility = if (loggedUserIsCreator) captionVisibility else View.GONE
        }

        container.addView(slideshowItem)
        return slideshowItem
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}