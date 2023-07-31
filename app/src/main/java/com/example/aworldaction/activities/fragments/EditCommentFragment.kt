package com.example.aworldaction.activities.fragments

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
    private var commentId = 0
    private var commentBody = ""
    private var commentText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            commentId = it.getInt(COMMENT_ID)
            commentBody = it.getString(COMMENT_BODY, "")
        }

        setStyle(STYLE_NORMAL, R.style.TransparentBottomSheetDialogTheme)
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
        commentText?.text = SpannableStringBuilder(commentBody)

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
            if (commentBody != commentText?.text.toString()) {
                saveComment()
            }
            Log.d("text", commentText?.text.toString())
        }
    }

    private fun saveComment() {
        val requestQueue = Volley.newRequestQueue(this.context)
        val url = AppSettings.getAPIUrl().toString() + "/comments/$commentId"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data") || responseJSON.has("comment")) {
                dismiss()
            }

            if (responseJSON.has("message")) {
                val message = responseJSON.getString("message")

                Log.d("serverApi", message)
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
        }

        val request = object : StringRequest(
            Method.PUT, url, listener, errorListener) {

            override fun getParams(): MutableMap<String, String>? {
                val params = HashMap<String, String>()
                params["body"] = commentText?.text.toString()
                return params
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${AppSettings.getToken()}"
                headers["Accept"] = "application/json"
                return headers
            }
        }
        requestQueue.add(request)
    }

    companion object {
        private const val COMMENT_ID = "param1"
        private const val COMMENT_BODY = "param2"

        @JvmStatic
        fun newInstance(param1: Int, param2: String) =
            EditCommentFragment().apply {
                arguments = Bundle().apply {
                    putInt(COMMENT_ID, param1)
                    putString(COMMENT_BODY, param2)
                }
            }
    }
}