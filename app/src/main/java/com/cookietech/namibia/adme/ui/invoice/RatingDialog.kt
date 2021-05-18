package com.cookietech.namibia.adme.ui.invoice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cookietech.namibia.adme.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_rating_dialog.*

class RatingDialog : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var callback:ReviewCallback? = null

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
        return inflater.inflate(R.layout.fragment_rating_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bt_send.setOnClickListener {
            val rating:Float = rating_bar.rating
            val review = tv_comment.text.toString()
            callback?.onReviewed(rating,review)
            dismiss()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(reviewCallback: ReviewCallback) =
            RatingDialog().apply {
                arguments = Bundle().apply {

                }
                callback = reviewCallback
            }
    }


    interface ReviewCallback{
        fun onReviewed(rating:Float,review:String)
    }
}