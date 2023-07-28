package com.example.aworldaction.activities.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.aworldaction.R
import com.example.aworldaction.adapters.CampaignAdapter
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class ListFragment : Fragment() {
    private var campaginList = ArrayList<JSONObject>()
    private var toShow: String? = null
    private var viewTitle: TextView? = null
    private var statusDisplay: TextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            toShow = it.getString(TO_SHOW)
        }

        loadList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            loadList()
            swipeRefreshLayout.isRefreshing = false
        }

        statusDisplay = view.findViewById(R.id.statusDisplay)

        recyclerView = view.findViewById(R.id.campaignList)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.adapter = CampaignAdapter(campaginList, requireContext())

        viewTitle = view.findViewById(R.id.viewTitle)
        viewTitle?.text = when (toShow) {
            "inprogress" -> resources.getString(R.string.list_inprogress)
            "favourites" -> resources.getString(R.string.list_favourites)
            "completed" -> resources.getString(R.string.list_completed)
            else -> "List"
        }
    }

    private fun loadList() {
        val requestQueue = Volley.newRequestQueue(this.context)
        val api = AppSettings.getAPIUrl().toString()
        val url = when (toShow) {
            "inprogress" -> "$api/inprogress"
            "favourites" -> "$api/favourites"
            "completed" -> "$api/completed"
            else -> return
        }

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)

            if (responseJSON.has("data")) {
                val campaigns = responseJSON.getJSONArray("data")

                campaginList.clear()

                for (i in 0 until campaigns.length()) {
                    campaginList.add(campaigns.getJSONObject(i))
                }

                recyclerView?.adapter = CampaignAdapter(campaginList, requireContext())

                if (campaginList.size == 0) {
                    statusDisplay?.visibility = View.VISIBLE
                    statusDisplay?.text = resources.getString(R.string.list_empty)
                } else {
                    statusDisplay?.visibility = View.INVISIBLE
                }
            }
        }

        val errorListener = Response.ErrorListener { error ->
            Log.e("serverAPI", error.toString())
            Log.e("serverAPI", error.networkResponse.statusCode.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

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

    companion object {
        private const val TO_SHOW = "param1"

        @JvmStatic
        fun newInstance(param1: String): ListFragment {
            val fragment = ListFragment()
            val args = Bundle()
            args.putString(TO_SHOW, param1)
            fragment.arguments = args
            return fragment
        }
    }
}