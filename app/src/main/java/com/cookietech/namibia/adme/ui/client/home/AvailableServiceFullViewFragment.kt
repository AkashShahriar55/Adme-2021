package com.cookietech.namibia.adme.ui.client.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.client.home.ClientHomeViewModel
import com.cookietech.namibia.adme.architecture.client.home.SearchServiceAdapter
import kotlinx.android.synthetic.main.fragment_available_service_full_view.*
import kotlinx.android.synthetic.main.fragment_available_service_full_view.available_service_rv
import kotlinx.android.synthetic.main.fragment_home.*

class AvailableServiceFullViewFragment : Fragment() {
    private lateinit var topRatedServicesAdapter: TopRatedServicesAdapter
    val viewModel: ClientHomeViewModel by activityViewModels()
    private var availableServiceAdapter: AvailableServiceAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_available_service_full_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        initializeObservers()
    }

    private fun initializeObservers() {
        viewModel.categories.observe(viewLifecycleOwner, { service_list ->
            availableServiceAdapter?.apply {
                categories = service_list
            }
        })
    }

    private fun initializeViews() {
        initializeServicesRecyclerView()
    }

    private fun initializeServicesRecyclerView() {
        available_service_rv.layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
        availableServiceAdapter = AvailableServiceAdapter(context,true)
        viewModel.categories.value?.apply {
            availableServiceAdapter?.categories = this
        }
        available_service_rv.adapter = availableServiceAdapter

        top_rated_service_provider_rv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        topRatedServicesAdapter = TopRatedServicesAdapter(arrayListOf(),requireContext())
        top_rated_service_provider_rv.adapter = topRatedServicesAdapter
        viewModel.services.observe(viewLifecycleOwner, {
            Log.d("akash_debug", "initializeServicesRecyclerView: "+ it.size)
            topRatedServicesAdapter.resetSearchData(it)
        })

        viewModel.services.value?.apply {
            topRatedServicesAdapter.resetSearchData(this)
        }

    }



    companion object {
        @JvmStatic
        fun newInstance() =
            AvailableServiceFullViewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}