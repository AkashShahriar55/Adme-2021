package com.cookietech.namibia.adme.ui.client.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.models.ServicesPOJO
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_marker_click_details.*
import kotlinx.android.synthetic.main.layout_marker_click_details.tv_category
import kotlinx.android.synthetic.main.leaderboard_item.*
import kotlinx.android.synthetic.main.services_item_layout.*

class MarkerClickDetailsDialog(): BottomSheetDialogFragment() {

    var service:ServicesPOJO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            service = getParcelable("data")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_marker_click_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpClicks()
    }

    private fun setUpClicks() {
        /**View Profile**/
        btn_view_profile.setOnClickListener{
           findNavController().navigate(R.id.marker_dialog_to_sp_activity)
        }
    }

    private fun setUpViews() {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        service?.apply {

            tv_username.text = user_name
            tv_category.text = category
            tv_details.text = description

            Glide.with(requireContext())
                .load(pic_url)
                .into(marker_profile_image)

            ratingBar.rating = rating.toFloat()
            tv_work_done.text = reviews

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): MarkerClickDetailsDialog {
            val fragment = MarkerClickDetailsDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

}