package com.cookietech.namibia.adme.ui.serviceProvider.income

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cookietech.namibia.adme.R
import com.hadiidbouk.charts.BarData
import kotlinx.android.synthetic.main.fragment_income.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


private var myCalendar: Calendar =  Calendar.getInstance()

private var date2: OnDateSetListener? = null
private var date1: OnDateSetListener? = null

class IncomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_income, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeFields()
        initializeClicks()
        initializeListener()


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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IncomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IncomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}