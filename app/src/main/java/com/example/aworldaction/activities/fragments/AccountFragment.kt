package com.example.aworldaction.activities.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.R
import com.example.aworldaction.activities.MainActivity
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class AccountFragment : Fragment() {
    private var mainActivity: MainActivity? = null
    private var nameDisplay: TextView? = null
    private var pictureDisplay: ImageView? = null
    private var logoutBtn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = requireActivity() as? MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameDisplay = view.findViewById(R.id.userName)
        pictureDisplay = view.findViewById(R.id.userPicture)
        logoutBtn = view.findViewById(R.id.logoutBtn)

        displayUser()
        logoutBtn?.setOnClickListener {
            logout()
        }
    }

    private fun displayUser() {
        val user = AppSettings.getUser()

        nameDisplay?.text = user?.getString("name")

        if (user?.has("picture_path") == true) {
            val url = AppSettings.getStorageUrl(user.getString("picture_path"))

            pictureDisplay?.let {
                Glide.with(requireContext())
                    .load(url)
                    .into(pictureDisplay!!)
            }
        }
    }

    private fun logout() {
        val requestQueue = Volley.newRequestQueue(this.context)
        val url = AppSettings.getAPIUrl().toString() + "/logout"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("message")) {
                Log.d("serverApi", responseJSON.getString("message"))
            }

            AppSettings.removeToken()
            mainActivity?.showWelcomeView()
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
        }

        val request = object : StringRequest(
            Method.POST, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + AppSettings.getToken()
                headers["Content-Type"] = "application/json"
                headers["Accept"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }
}