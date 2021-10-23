package com.cookietech.namibia.adme.ui.serviceProvider.income

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.cookietech.namibia.adme.architecture.serviceProvider.ServiceProviderViewModel
import com.cookietech.namibia.adme.architecture.serviceProvider.income.IncomeViewModel
import com.cookietech.namibia.adme.databinding.FragmentIncomeBinding
import com.cookietech.namibia.adme.helper.IncomeHelper
import com.hadiidbouk.charts.BarData
import kotlinx.android.synthetic.main.fragment_income.*
import java.text.SimpleDateFormat
import java.util.*


private var myCalendar: Calendar =  Calendar.getInstance()

private var date2: OnDateSetListener? = null
private var date1: OnDateSetListener? = null

class IncomeFragment : Fragment() {

    val serviceProviderViewModel: ServiceProviderViewModel by activityViewModels()
    val incomeViewModel : IncomeViewModel by viewModels()

    private var _binding: FragmentIncomeBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeFields()
        initializeClicks()
        initializeListener()
        initializeObserver()


    }

    private fun initializeObserver() {

        incomeViewModel.monthlyDueListener.observe(viewLifecycleOwner, {
            binding.tvTotDueVal.text = it.toString().trim()
        })

        incomeViewModel.monthlyIncomeListener.observe(viewLifecycleOwner, {
            binding.tvTotIncomeVal.text = it.toString().trim()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initializeListener() {
        /** Date Listener**/

        date1 = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate1()
        }

        date2 = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate2()
        }
    }

    private fun initializeClicks() {

        img_calender1.setOnClickListener {
            val mDatePickerDialog1 = DatePickerDialog(
                requireContext(), date1,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            //                mDatePickerDialog1.getDatePicker().setMaxDate(myCalendar.get(Calendar.DAY_OF_MONTH));
            //                mDatePickerDialog1.getDatePicker().setMinDate((myCalendar.get(Calendar.DAY_OF_MONTH)-10));
            mDatePickerDialog1.show()
        }
        img_calender2.setOnClickListener {
            val mDatePickerDialog2 = DatePickerDialog(
                requireContext(), date2,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            )
            mDatePickerDialog2.show()
        }
    }

    private fun initializeFields() {
        createChart()
        setTotalIncome()
        setRequested()
        setCompleted()
        setPressed()
        setIncomeMonth()

    }

    private fun setIncomeMonth() {
        val dueText = "Due(-10%) in ${IncomeHelper.getCurrentMonth()}"
        binding.tvTotDueTitle.text = dueText
        val incomeText = "Income in ${IncomeHelper.getCurrentMonth()}"
        binding.tvTotIncomeTitle.text = incomeText
    }

    private fun setPressed() {

        binding.tvTapVal.text = serviceProviderViewModel.service_provider_data.value?.pressed?.toString()
    }

    private fun setCompleted() {

        binding.tvComplVal.text = serviceProviderViewModel.service_provider_data.value?.completed?.toString()
    }

    private fun setRequested() {

        binding.tvReqVal.text = serviceProviderViewModel.service_provider_data.value?.requested?.toString()
    }

    private fun setTotalIncome() {
        binding.totalIncomeTv.text = serviceProviderViewModel.service_provider_data.value?.total_income?.let {
            IncomeHelper.getTotalIncome(
                it
            )
        }
    }

    private fun updateDate1() {
        val myFormat = "dd MMM''yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tv_calender1.text = sdf.format(myCalendar.getTime())
//        Toast.makeText(getApplicationContext(), "date : "+sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private fun updateDate2() {
        val myFormat = "dd MMM''yy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        tv_calender2.text = sdf.format(myCalendar.getTime())
//        Toast.makeText(getApplicationContext(), "date : "+sdf.format(myCalendar.getTime()), Toast.LENGTH_SHORT).show();
    }

    private fun createChart() {

        val dataList = ArrayList<BarData>()

        var data = BarData("Sep", 30.4f, "300.4$")
        dataList.add(data)

        data = BarData("Oct", 42f, "420$")
        dataList.add(data)

        data = BarData("Nov", 10.8f, "100.8$")
        dataList.add(data)

        data = BarData("Dec", 87.3f, "870.3$")
        dataList.add(data)

        data = BarData("Jan", 36.2f, "360.2$")
        dataList.add(data)

        data = BarData("Feb", 99.3f, "990.3$")
        dataList.add(data)

        data = BarData("Nov", 71.8f, "710.8$")
        dataList.add(data)

        chart_progress_bar.setDataList(dataList)
        chart_progress_bar.build()

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            IncomeFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

}