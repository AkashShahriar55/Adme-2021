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
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.architecture.loginRegistration.LoginRegistrationMainViewModel
import com.cookietech.namibia.adme.architecture.loginRegistration.RegistrationViewModel
import com.cookietech.namibia.adme.managers.LoginAndRegistrationManager
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import com.cookietech.namibia.adme.views.LoadingDialog
import kotlinx.android.synthetic.main.fragment_registration.*
import java.lang.Exception


class RegistrationFragment : Fragment(),RegistrationViewModel.RegistrationCallbacks {

    private var codeSent: Boolean = false
    val registrationViewModel: RegistrationViewModel by viewModels()


    private lateinit var dialog: LoadingDialog
    private var code0Ok = false
    private  var code1Ok:Boolean = false
    private  var code2Ok:Boolean = false
    private  var code3Ok:Boolean = false
    private  var code4Ok:Boolean = false
    private  var code5Ok:Boolean = false
    private var verificationCode = arrayOfNulls<String>(6)
    var countDownTimer:CountDownTimer? = null
    private var timeInMillSec: Long = 120000
    val PHONE_NO = "phone_no"
    val CURRENT_TIMER = "current_timer"
    val IS_CODE_SENT = "is_countdown_started"
    val mainViewModel : LoginRegistrationMainViewModel by activityViewModels()

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
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()

        if (savedInstanceState != null) {
            val phone_no = savedInstanceState.getString(PHONE_NO)
            phoneText.setText(phone_no)
            val is_code_sent = savedInstanceState.getBoolean(IS_CODE_SENT)
            if (is_code_sent) {
                codeSent = true
                reg_join_btn.visibility = View.GONE
                codeText.visibility = View.VISIBLE
                policy_text.visibility = View.GONE
                resend_code_btn.visibility = View.VISIBLE
                timer_txt.visibility = View.VISIBLE
                phoneText.isEnabled = false
                ccp.isEnabled = false
                timeInMillSec =
                    savedInstanceState.getLong(CURRENT_TIMER)
                if (timeInMillSec == 0L) {
                    resend_code_btn.isEnabled = true
                    timer_txt.text = null
                    timer_txt.visibility = View.GONE
                } else {
                    updateTimer()
                    StartTimer()
                }
            }
        }

    }

    private fun setUpViews() {

        dialog = context?.let { LoadingDialog(it, "Logging in", "Please wait...") }!!
        ccp.registerCarrierNumberEditText(phoneText)
        ccp.detectSIMCountry(true)
        ccp.setCustomMasterCountries("NA,BD")
        registrationViewModel.registrationCallbacks = this

        editText.addTextChangedListener(LoginOTPTextListener(editText))
        editText6.addTextChangedListener(LoginOTPTextListener(editText6))
        editText5.addTextChangedListener(LoginOTPTextListener(editText5))
        editText4.addTextChangedListener(LoginOTPTextListener(editText4))
        editText3.addTextChangedListener(LoginOTPTextListener(editText3))
        editText2.addTextChangedListener(LoginOTPTextListener(editText2))

        reg_join_btn.setOnClickListener { v: View? ->
           sendVerificationCode()
            dialog.show()
        }

        resend_code_btn.setOnClickListener { v: View? ->
            resetCodeFields()
            resend_code_btn.isEnabled = false
            sendVerificationCode()
        }

        goto_login_btn.setOnClickListener { v: View? -> findNavController().navigateUp() }


    }

    private fun sendVerificationCode() {
        Log.d("akash_debug", "onCreate: button clicked")
        if(ccp.fullNumberWithPlus != null){
            activity?.let { activity->
                registrationViewModel.sendVerificationCode(
                    ccp.fullNumberWithPlus,
                    activity,
                        false
                )
            }
        }
        else
            Log.d("akash_debug", "setUpViews: ")

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(
            PHONE_NO,
            phoneText.text.toString()
        )
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




    inner class LoginOTPTextListener internal constructor(private val view: View) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.editText -> if (text.length == 1) {
                    code0Ok = true
                    editText2.requestFocus()
                    verificationCode[0] = editText.text.toString()
                    showKeyboard()
                    verifyCode()
                } else {
                    code0Ok = false
                }
                R.id.editText2 -> if (text.length == 1) {
                    code1Ok = true
                    editText3.requestFocus()
                    verificationCode[1] = editText2.text.toString()
                    verifyCode()
                    showKeyboard()
                } else {
                    code1Ok = false
                }
                R.id.editText3 -> if (text.length == 1) {
                    code2Ok = true
                    editText4.requestFocus()
                    verificationCode[2] = editText3.text.toString()
                    verifyCode()
                    showKeyboard()
                } else {
                    code2Ok = true
                }
                R.id.editText4 -> if (text.length == 1) {
                    code3Ok = true
                    editText5.requestFocus()
                    verificationCode[3] = editText4.text.toString()
                    verifyCode()
                    showKeyboard()
                } else {
                    code3Ok = false
                }
                R.id.editText5 -> if (text.length == 1) {
                    code4Ok = true
                    editText6.requestFocus()
                    verificationCode[4] = editText5.text.toString()
                    verifyCode()
                    showKeyboard()
                } else {
                    code4Ok = false
                }
                R.id.editText6 -> if (text.length == 1) {
                    code5Ok = true
                    verificationCode[5] = editText6.text.toString()
                    verifyCode()
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
                dialog.show()
                closeKeyboard()
                registrationViewModel.signInWithPhoneAuthCredentialForCode(verificationCodeString.toString())
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
        fun newInstance(param1: String, param2: String) =
            RegistrationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onCodeSend() {
        dialog.dismiss()
        codeSent = true
        phoneText.isEnabled = false
        ccp.isEnabled = false
        goto_login_btn.isEnabled = false
        StartTimer()
        reg_join_btn.visibility = View.GONE
        codeText.visibility = View.VISIBLE
        policy_text.visibility = View.GONE
        resend_code_btn.visibility = View.VISIBLE
        timer_txt.visibility = View.VISIBLE
    }

    override fun onLoginSuccessful() {
        val login = mainViewModel.tryToLogin(object : LoginAndRegistrationManager.UserCreationCallback {
            override fun onUserCreationSuccessful() {
                Log.d("login_debug", "onUserCreationSuccessful: ")
                findNavController().navigate(R.id.registration_to_user_info)
                dialog.dismiss()
            }

            override fun onUserFetchSuccessful() {
                dialog.dismiss()

                Log.d("login_debug", "onUserFetchSuccessful: ")
                when (SharedPreferenceManager.user_mode) {
                    AppComponent.MODE_CLIENT -> findNavController().navigate(R.id.registration_to_client_activity)
                    AppComponent.MODE_SERVICE_PROVIDER -> findNavController().navigate(R.id.registration_to_service_activity)
                }
            }

            override fun onUserCreationFailed(exception: Exception) {
                dialog.dismiss()
                Log.d("login_debug", "onUserCreationFailed: ")
            }

        })

        Log.d("login_debug", "onLoginSuccessful:$login ")

    }

    override fun onLoginFailed() {
        dialog.dismiss()
    }


    private fun StartTimer() {
        countDownTimer = object : CountDownTimer(timeInMillSec, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeInMillSec = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                resend_code_btn.isEnabled = true
                timer_txt.text = null
                timer_txt.visibility = View.GONE
                goto_login_btn.isEnabled = true
            }
        }.start()
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
        timer_txt.text = timeLeftText
    }

    private fun resetCodeFields() {
        timeInMillSec = 120000
        verificationCode = arrayOfNulls(6)
        editText.setText(null)
        editText2.text = null
        editText3.text = null
        editText4.text = null
        editText5.text = null
        editText.setEnabled(true)
        editText.requestFocus()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
    }



}