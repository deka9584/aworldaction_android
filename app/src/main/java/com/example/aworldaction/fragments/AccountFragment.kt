package com.example.aworldaction.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.aworldaction.activities.ChangePasswordActivity
import com.example.aworldaction.R
import com.example.aworldaction.activities.HomeActivity
import com.example.aworldaction.activities.UploadPhotoProfileActivity
import com.example.aworldaction.clients.AuthApiClient
import com.example.aworldaction.settings.AppSettings
import org.json.JSONException
import org.json.JSONObject

class AccountFragment : Fragment() {
    private var homeActivity: HomeActivity? = null
    private var nameDisplay: TextView? = null
    private var roleDisplay: TextView? = null
    private var pictureDisplay: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeActivity = requireActivity() as? HomeActivity
    }

    override fun onResume() {
        super.onResume()
        displayUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nameDisplay = view.findViewById(R.id.userName)
        roleDisplay = view.findViewById(R.id.userRole)
        pictureDisplay = view.findViewById(R.id.userPicture)

        pictureDisplay?.setOnClickListener {
            val intent = Intent(requireContext(), UploadPhotoProfileActivity::class.java)
            startActivity(intent)
        }

        val changePasswordBtn = view.findViewById<Button>(R.id.changePasswordBtn)
        changePasswordBtn.setOnClickListener {
            val intent = Intent(requireContext(), ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        val logoutBtn = view.findViewById<Button>(R.id.logoutBtn)
        logoutBtn.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun displayUser() {
        val user = AppSettings.getUser()
        val pictPath = user?.optString("picture_path") ?: ""
        val pictUrl = if (pictPath.isNotBlank()) AppSettings.getStorageUrl(pictPath) else null

        nameDisplay?.text = user?.optString("name")
        roleDisplay?.text = getString(
            if ((user?.optInt("role_id") ?: 0) >= 2) R.string.role_admin
            else R.string.role_user
        )

        pictureDisplay?.let {
            if (pictUrl != null) {
                Glide.with(requireContext())
                    .load(pictUrl)
                    .into(it)
            } else {
                it.setImageResource(R.drawable.ic_baseline_account_circle_24)
            }
        }
    }

    private fun showLogoutDialog() {
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle(resources.getString(R.string.logout_btn))
        builder.setMessage(resources.getString(R.string.logout_confirm))

        builder.setPositiveButton(resources.getString(R.string.confirm_btn)) { dialog, which ->
            logout()
            dialog.cancel()
        }

        builder.setNegativeButton(resources.getString(R.string.cancel_btn)) { dialog, which ->
            dialog.cancel()
        }

        builder.create().show()
    }

    private fun logout() {
        val userToken = AppSettings.getToken()

        if (userToken == null || userToken.isBlank()) {
            homeActivity?.finish()
        }

        AuthApiClient.logout(requireContext(), "$userToken",
            onSuccess = { response ->
                if (response.has("message")) {
                    Log.d("serverApi", response.getString("message"))
                    AppSettings.removeToken()
                    homeActivity?.finish()
                }
            },
            onError = { statusCode ->
                when (statusCode) {
                    0 -> Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                    401, 403 -> {
                        AppSettings.removeToken()
                        homeActivity?.finish()
                        Toast.makeText(context, "Unauthorized ($statusCode)", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "Error $statusCode", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}