package com.cookietech.namibia.adme.ui.serviceProvider.today.addservice

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.serviceProvider.today.AddServiceViewModel
import com.cookietech.namibia.adme.models.ServiceCategory
import kotlinx.android.synthetic.main.activity_add_service.*
import kotlinx.android.synthetic.main.fragment_overview.*
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OverviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OverviewFragment : Fragment() {

    private var isTimeValidated: Boolean = false
    private var selectedCategory: ServiceCategory? = null
    private lateinit var mCategoryAdapter: ArrayAdapter<String>


    init {

        Log.d("akash_fragment_debug", "init: overview")
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    val viewmodel: AddServiceViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.d("akash_fragment_debug", "onCreate: overview")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d("akash_fragment_debug", "onCreateView: overview")
        return inflater.inflate(R.layout.fragment_overview, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("akash_fragment_debug", "onViewCreated: overview")
        setUpUi()
        setUpObservers()




    }

    private fun setUpObservers() {

        viewmodel.categories.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                updateCategorySpinner(categories)
            }
        })
    }

    private fun setUpUi() {
        mCategoryAdapter = ArrayAdapter<String>(
            requireContext(), R.layout.support_simple_spinner_dropdown_item,
            ArrayList<String>()
        )
        mCategoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        service_category_spinner.adapter = mCategoryAdapter

        service_category_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("service_debug", "onItemSelected: $position")
                viewmodel.completedFlags[0] = false
                viewmodel.categories.value?.let {
                    viewmodel.selectedCategory = it[position]
                    selectedCategory = viewmodel.selectedCategory
                    viewmodel.service.category = selectedCategory?.category
                    viewmodel.service.categoryId = selectedCategory?.id
                    updateCategorySpinner(it)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("service_debug", "onNothingSelected: ")
            }

        }

        if(viewmodel.categories.value !=null){
            viewmodel.categories.value?.let {
                selectedCategory = viewmodel.selectedCategory
                viewmodel.service.category = selectedCategory?.category
                viewmodel.service.categoryId = selectedCategory?.id
                updateCategorySpinner(it)

            }

        }

        viewmodel.service.description?.let {
            edt_service_description.editText?.setText(it)
        }



        updateStartTimeUi(
            viewmodel.startTime[Calendar.HOUR_OF_DAY],
            viewmodel.startTime[Calendar.MINUTE]
        )
        updateEndTimeUi(viewmodel.endTime[Calendar.HOUR_OF_DAY], viewmodel.endTime[Calendar.MINUTE])

        start_time_btn.setOnClickListener { v: View? ->
            val dialog = TimePickerDialog(
                context,
                { view, hourOfDay, minute ->
                    viewmodel.completedFlags[0] = false
                    viewmodel.startTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    viewmodel.startTime.set(Calendar.MINUTE, minute)
                    updateStartTimeUi(hourOfDay, minute)
                    Log.d(
                        "service_debug",
                        "onTimeSet: "
                    )
                },
                viewmodel.startTime.get(Calendar.HOUR_OF_DAY),
                viewmodel.startTime.get(Calendar.MINUTE),
                false
            )
            dialog.show()
        }


        end_time_btn.setOnClickListener { v: View? ->
            val dialog = TimePickerDialog(
                context,
                { view, hourOfDay, minute ->
                    viewmodel.completedFlags[0] = false
                    viewmodel.endTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    viewmodel.endTime.set(Calendar.MINUTE, minute)
                    updateEndTimeUi(hourOfDay, minute)

                },
                viewmodel.endTime.get(Calendar.HOUR_OF_DAY),
                viewmodel.endTime.get(Calendar.MINUTE),
                false
            )
            dialog.show()
        }


        edt_service_description.editText?.addTextChangedListener(InputValidator())


    }

    inner class InputValidator : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            Log.d("akash_validation_debug", "afterTextChanged: " + s)
            viewmodel.completedFlags[0] = false
            if(s.toString().isEmpty()){
                viewmodel.service.description = null
            }else{
                viewmodel.service.description = s.toString()
            }

        }
        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {


            if (s.length < 40) {
                edt_service_description.isErrorEnabled = true
                edt_service_description.error = "40 characters minimum"
                viewmodel.service.description = null
            } else {
                viewmodel.service.description = s.toString()
                edt_service_description.isErrorEnabled = false
            }
        }
    }



    private fun updateEndTimeUi(hourOfDay: Int, minute: Int) {
        var hod = hourOfDay
        val AM_PM: String
        AM_PM = if (hod < 12) {
            "AM"
        } else {
            "PM"
        }
        hod = if (hod % 12 == 0) {
            12
        } else {
            hod % 12
        }

        val endTimeStr = DecimalFormat("00")
            .format(hod.toLong()) + ":" + DecimalFormat("00")
            .format(minute.toLong()) + " " + AM_PM
        end_time_btn.text = endTimeStr
        if(checkTimeValidation()){
            viewmodel.service.endTime = endTimeStr
        }else{
            viewmodel.service.endTime = null
        }

        Log.i(
            "service_debug",
            "onTimeSet: "
        )
    }

    private fun updateStartTimeUi(hourOfDay: Int, minute: Int) {
        var hod = hourOfDay
        val AM_PM: String
        Log.d(
            "service_debug",
            "onTimeSet: $hod $minute"
        )
        AM_PM = if (hod < 12) {
            "AM"
        } else {
            "PM"
        }
        hod = if (hod % 12 == 0) {
            12
        } else {
            hod % 12
        }

        val startTimeStr = DecimalFormat("00")
            .format(hod.toLong()) + ":" + DecimalFormat("00")
            .format(minute.toLong()) + " " + AM_PM
        start_time_btn.text = startTimeStr
        if(checkTimeValidation()){
            viewmodel.service.startTime = startTimeStr
        }else{
            viewmodel.service.startTime = null
        }
    }

    private fun checkTimeValidation(): Boolean {
        if (viewmodel.endTime[Calendar.HOUR_OF_DAY] - viewmodel.startTime[Calendar.HOUR_OF_DAY] < 0) {
            timeError.visibility = View.VISIBLE
            return false

        } else if (viewmodel.endTime[Calendar.HOUR_OF_DAY] == viewmodel.startTime[Calendar.HOUR_OF_DAY] && viewmodel.endTime[Calendar.MINUTE] - viewmodel.startTime[Calendar.MINUTE] < 0) {
            timeError.visibility = View.VISIBLE
            return false
        } else {
            timeError.visibility = View.GONE
            return true
        }
    }


    private fun updateCategorySpinner(categories: ArrayList<ServiceCategory>){
        val listOfCategories = ArrayList<String>()
        categories.forEach {
            it.category?.let { category -> listOfCategories.add(category) }
        }
        mCategoryAdapter.clear()
        mCategoryAdapter.addAll(listOfCategories)
        mCategoryAdapter.notifyDataSetChanged()
        selectedCategory?.let { category->
            viewmodel.categories.value?.let { categories->
                val position = categories.indexOf(category)
                service_category_spinner.setSelection(position)
            }
        }

    }

    override fun onDestroyView() {
        viewmodel.service.description = edt_service_description.editText?.text.toString()
        super.onDestroyView()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OverviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}