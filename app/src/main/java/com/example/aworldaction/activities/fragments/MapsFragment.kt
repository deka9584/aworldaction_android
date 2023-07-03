package com.example.aworldaction.activities.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.aworldaction.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {
    private var position: LatLng? = null
    private var webView: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            val lat = it.getDouble(LAT)
            val lng = it.getDouble(LNG)
            position = LatLng(lat, lng)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        webView = view.findViewById(R.id.mapWebView)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = WebViewClient()
        loadMap()

        return view
    }

    private fun loadMap() {
        val baseUrl = "https://maps.google.com/maps?q=+${position?.latitude}+,+${position?.longitude}+&hl=it&z=14&output=embed"

        val html = """
            <html>
                <head>
                    <style>
                        body {
                            margin: 0;
                        }
                    </style>
                </head>
                <body>
                    <iframe src="${baseUrl}" width="100%" height="300" frameborder="0" style="border:0" allowfullscreen></iframe>
                </body>
            </html>
        """.trimIndent()

        webView?.loadDataWithBaseURL(baseUrl, html, "text/html", "UTF-8", null)
    }

    companion object {
        private const val LAT = "param1"
        private const val LNG = "param2"

        fun newInstance(param1: Double, param2: Double): MapsFragment {
            val fragment = MapsFragment()
            val args = Bundle()
            args.putDouble(LAT, param1)
            args.putDouble(LNG, param2)
            fragment.arguments = args
            return fragment
        }
    }
}