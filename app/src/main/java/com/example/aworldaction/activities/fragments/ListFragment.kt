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
import com.example.aworldaction.managers.ListFragmentManager
import com.example.aworldaction.settings.AppSettings
import org.json.JSONObject

class ListFragment : Fragment() {
    private var manager: ListFragmentManager? = null
    private var toShow: String? = null
    private var viewTitle: TextView? = null
    private var statusDisplay: TextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (manager == null) {
            manager = ListFragmentManager(this)
        }

        arguments?.let {
            toShow = it.getString(TO_SHOW)
        }
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
            manager?.loadList(toShow)
            swipeRefreshLayout.isRefreshing = false
        }

        statusDisplay = view.findViewById(R.id.statusDisplay)

        recyclerView = view.findViewById(R.id.campaignList)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())

        manager?.getCampaignList()?.let {
            recyclerView?.adapter = CampaignAdapter(it, requireContext())
        }

        viewTitle = view.findViewById(R.id.viewTitle)
        viewTitle?.text = when (toShow) {
            "inprogress" -> resources.getString(R.string.list_inprogress)
            "favourites" -> resources.getString(R.string.list_favourites)
            "completed" -> resources.getString(R.string.list_completed)
            else -> "List"
        }

        manager?.loadList(toShow)
    }

    fun displayList(list: ArrayList<JSONObject>) {
        context?.let {
            recyclerView?.adapter = CampaignAdapter(list, it)
        }

        if (list.size == 0) {
            statusDisplay?.visibility = View.VISIBLE
            statusDisplay?.text = resources.getString(R.string.list_empty)
        } else {
            statusDisplay?.visibility = View.INVISIBLE
        }
    }

    fun displayError(message: String) {
        statusDisplay?.visibility = View.VISIBLE
        statusDisplay?.text = message
        statusDisplay?.setTextColor(resources.getColor(R.color.danger, context?.theme))
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