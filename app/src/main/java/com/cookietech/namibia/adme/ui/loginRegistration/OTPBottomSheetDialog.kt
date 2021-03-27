package com.cookietech.namibia.adme.ui.loginRegistration

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.cookietech.namibia.adme.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_registration.*
import kotlinx.android.synthetic.main.otp_bottom_sheet_dialog.*

class OTPBottomSheetDialog: BottomSheetDialogFragment() {
    var callback:OtpBottomSheetCallback? = null
    private var codeSent: Boolean = true
    private var code0Ok = false
    private  var code1Ok:Boolean = false
    private  var code2Ok:Boolean = false
    private  var code3Ok:Boolean = false
    private  var code4Ok:Boolean = false
    private  var code5Ok:Boolean = false
    private var verificationCode = arrayOfNulls<String>(6)
    var countDownTimer:CountDownTimer? = null
    private var timeInMillSec: Long = 120000
    val CURRENT_TIMER = "current_timer"
    val IS_CODE_SENT = "is_countdown_started"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.otp_bottom_sheet_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            val is_code_sent = savedInstanceState.getBoolean(IS_CODE_SENT)
            if (is_code_sent) {
                codeSent = true
                user_info_code_text.visibility = View.VISIBLE
                user_info_resend_code_btn.visibility = View.VISIBLE
                user_info_timer_text.visibility = View.VISIBLE
                timeInMillSec =
                    savedInstanceState.getLong(CURRENT_TIMER)
                if (timeInMillSec == 0L) {
                    user_info_resend_code_btn.isEnabled = true
                    user_info_timer_text.text = null
                    user_info_timer_text.visibility = View.GONE
                } else {
                    updateTimer()
                    StartTimer()
                }
            }
        }
        setUpViews()
    }

    private fun setUpViews() {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        StartTimer()
        btn_done.setOnClickListener {
            verifyCode()
        }

        btn_cancel.setOnClickListener {
            dismiss()
        }

        user_info_resend_code_btn.setOnClickListener { v: View? ->
            resetCodeFields()
            user_info_resend_code_btn.isEnabled = false
            callback?.resendVerification()
        }

        edt_code_1.addTextChangedListener(LoginOTPTextListener(edt_code_1))
        edt_code_2.addTextChangedListener(LoginOTPTextListener(edt_code_2))
        edt_code_3.addTextChangedListener(LoginOTPTextListener(edt_code_3))
        edt_code_4.addTextChangedListener(LoginOTPTextListener(edt_code_4))
        edt_code_5.addTextChangedListener(LoginOTPTextListener(edt_code_5))
        edt_code_6.addTextChangedListener(LoginOTPTextListener(edt_code_6))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (codeSent) {
            outState.putBoolean(
                IS_CODE_SENT,
                true
            )
            outState.putLong(
                CURRENT_TIMER,
                timeInMillSec
            )
        }
        super.onSaveInstanceState(outState)

    }

    private fun updateTimer() {
        val minute = timeInMillSec.toInt() / 60000
        val second = timeInMillSec.toInt() % 60000 / 1000
        var timeLeftText = ""
        timeLeftText += minute
        timeLeftText += " : "
        if (second < 10) {
            timeLeftText += "0"
        }
        timeLeftText += second
        user_info_timer_text.text = timeLeftText
    }

    private fun StartTimer() {
        countDownTimer = object : CountDownTimer(timeInMillSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMillSec = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                user_info_resend_code_btn.isEnabled = true
                user_info_timer_text.text = null
                user_info_timer_text.visibility = View.GONE
            }
        }.start()
    }

    private fun resetCodeFields() {
        timeInMillSec = 120000
        verificationCode = arrayOfNulls(6)
        edt_code_2.text = null
        edt_code_3.text = null
        edt_code_4.text = null
        edt_code_5.text = null
        edt_code_6.text = null
        edt_code_1.isEnabled = true
        edt_code_1.requestFocus()
    }

    inner class LoginOTPTextListener internal constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.edt_code_1 -> if (text.length == 1) {
                    code0Ok = true
                    edt_code_2.requestFocus()
                    verificationCode[0] = edt_code_1.text.toString()
                    showKeyboard()
                } else {
                    code0Ok = false
                }
                R.id.edt_code_2 -> if (text.length == 1) {
                    code1Ok = true
                    edt_code_3.requestFocus()
                    verificationCode[1] = edt_code_2.text.toString()
                    showKeyboard()
                } else {
                    code1Ok = false
                }
                R.id.edt_code_3 -> if (text.length == 1) {
                    code2Ok = true
                    edt_code_4.requestFocus()
                    verificationCode[2] = edt_code_3.text.toString()
                    showKeyboard()
                } else {
                    code2Ok = true
                }
                R.id.edt_code_4 -> if (text.length == 1) {
                    code3Ok = true
                    edt_code_5.requestFocus()
                    verificationCode[3] = edt_code_4.text.toString()
                    showKeyboard()
                } else {
                    code3Ok = false
                }
                R.id.edt_code_5 -> if (text.length == 1) {
                    code4Ok = true
                    edt_code_6.requestFocus()
                    verificationCode[4] = edt_code_5.text.toString()
                    showKeyboard()
                } else {
                    code4Ok = false
                }
                R.id.edt_code_6 -> if (text.length == 1) {
                    code5Ok = true
                    verificationCode[5] = edt_code_6.text.toString()
                    showKeyboard()
                } else {
                    code5Ok = false
                }
            }
        }
    }




    private fun verifyCode() {
        if (code0Ok && code1Ok && code2Ok && code3Ok && code4Ok && code5Ok) {
            closeKeyboard()
            val verificationCodeString = StringBuilder()
            for (s in verificationCode) {
                verificationCodeString.append(s)
            }

            if(verificationCodeString.isNotEmpty()){
                callback?.onCodeGiven(verificationCodeString.toString())
                dismiss()
            }
        }
    }

    fun closeKeyboard() {
        activity?.let {
            val inputMethodManager = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

    }

    fun showKeyboard() {
        activity?.let {
            val inputMethodManager =
                (it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)!!
            inputMethodManager.toggleSoftInput(
                InputMethodManager.SHOW_FORCED,
                InputMethodManager.HIDE_IMPLICIT_ONLY
            )
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(): OTPBottomSheetDialog {
            val fragment = OTPBottomSheetDialog()
            return fragment
        }
    }

    interface OtpBottomSheetCallback{
        fun onCodeGiven(code:String)
        fun resendVerification()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }
}