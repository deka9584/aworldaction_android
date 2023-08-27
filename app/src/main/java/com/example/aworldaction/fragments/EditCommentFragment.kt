package com.example.aworldaction.fragments

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.DetailActivity
import com.example.aworldaction.settings.AppSettings
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.json.JSONObject

class EditCommentFragment : BottomSheetDialogFragment() {
    private var detailActivity: DetailActivity? = null
    private var commentId = 0
    private var commentBody = ""
    private var commentText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)

        detailActivity = requireActivity() as? DetailActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentText = view.findViewById(R.id.commentText)

        val userPicture = view.findViewById<ImageView>(R.id.userPicture)
        val user = AppSettings.getUser()

        if (user != null && user.has("picture_path")) {
            val url = AppSettings.getStorageUrl(user.getString("picture_path"))

            if (url != null) {
                Glide.with(requireContext())
                    .load(url)
                    .into(userPicture)
            }
        }

        val cancelBtn = view.findViewById<ImageButton>(R.id.cancelBtn)
        cancelBtn.setOnClickListener {
            dismiss()
        }

        val submitBtn = view.findViewById<ImageButton>(R.id.submitBtn)
        submitBtn.setOnClickListener {
            if (commentBody != "${commentText?.text}") {
                detailActivity?.confirmEditComment(commentId, "${commentText?.text}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        commentText?.text = SpannableStringBuilder(commentBody)
    }

    fun setComment(commentId: Int, commentBody: String) {
        this.commentId = commentId
        this.commentBody = commentBody
    }
}