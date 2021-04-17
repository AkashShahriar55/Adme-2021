package com.cookietech.namibia.adme.ui.client.home.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.client.home.SearchServiceAdapter
import com.cookietech.namibia.adme.architecture.client.home.SearchServiceRepository
import com.cookietech.namibia.adme.architecture.client.home.SearchServiceViewModel
import kotlinx.android.synthetic.main.fragment_search_services.*
import java.util.ArrayList
import kotlin.math.log


class SearchServicesFragment : Fragment() {

    val searchServiceViewModel:SearchServiceViewModel by viewModels()
    private  var adapter: SearchServiceAdapter? = null

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
        return inflater.inflate(R.layout.fragment_search_services, container, false)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeField()
        initializeRV()
    }

    private fun initializeField() {
        searchServiceViewModel.bindSearch(
            searchView,
            object : SearchServiceRepository.SearchCallback {
                override fun onInvalidData() {

                }

                override fun onError() {

                }

                override fun onFetchedSearchResult(allData: ArrayList<SearchData>) {
                    Log.d("search_debug", "onFetchedSearchResult: " + allData)
                    adapter!!.resetSearchData(allData)

                }

            })
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            SearchServicesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    private fun initializeRV() {
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)

        adapter = SearchServiceAdapter(ArrayList<SearchData>(), requireContext())
        rv_service_result.layoutManager = mLayoutManager
        rv_service_result.itemAnimator = DefaultItemAnimator()
        rv_service_result.adapter = adapter
    }
}