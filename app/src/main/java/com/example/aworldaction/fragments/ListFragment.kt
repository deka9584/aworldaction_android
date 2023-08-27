package com.example.aworldaction.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.aworldaction.R
import com.example.aworldaction.adapters.CampaignAdapter
import com.example.aworldaction.models.ListFragmentModel
import org.json.JSONObject

class ListFragment : Fragment() {
    private lateinit var model: ListFragmentModel
    private var toShow: String? = null
    private var viewTitle: TextView? = null
    private var statusDisplay: TextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!::model.isInitialized) {
            model = ViewModelProvider(this)[ListFragmentModel::class.java]
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

        val fragmentContext = requireContext()
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            model.loadList(fragmentContext, toShow)
            swipeRefreshLayout.isRefreshing = false
        }

        statusDisplay = view.findViewById(R.id.statusDisplay)

        recyclerView = view.findViewById(R.id.campaignList)
        recyclerView?.layoutManager = LinearLayoutManager(fragmentContext)
        recyclerView?.adapter = CampaignAdapter(emptyList(), fragmentContext)

        model.campaignList.observe(viewLifecycleOwner, Observer { campaignList ->
            recyclerView?.adapter?.let { adapter ->
                (adapter as CampaignAdapter).setData(campaignList)
            }

            if (model.campaignList.value?.size == 0) {
                statusDisplay?.visibility = View.VISIBLE
                statusDisplay?.text = resources.getString(R.string.list_empty)
            } else {
                statusDisplay?.visibility = View.INVISIBLE
            }
        })

        model.message.observe(viewLifecycleOwner, Observer { message ->
            if (message.trim() != "") {
                statusDisplay?.text = message
            }
        })

        viewTitle = view.findViewById(R.id.viewTitle)
        viewTitle?.text = when (toShow) {
            "inprogress" -> resources.getString(R.string.list_inprogress)
            "favourites" -> resources.getString(R.string.list_favourites)
            "completed" -> resources.getString(R.string.list_completed)
            else -> "List"
        }

        model.loadList(fragmentContext, toShow)
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