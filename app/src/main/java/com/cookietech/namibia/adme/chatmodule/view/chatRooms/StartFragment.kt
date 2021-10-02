package com.cookietech.namibia.adme.chatmodule.view.chatRooms

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cookietech.namibia.adme.Application.AppComponent
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.chatmodule.data.model.User

import com.cookietech.namibia.adme.chatmodule.view.FirebaseViewModel
import com.cookietech.namibia.adme.chatmodule.utils.states.FragmentState
import com.cookietech.namibia.adme.databinding.FragmentStartBinding
import com.cookietech.namibia.adme.managers.SharedPreferenceManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class StartFragment : Fragment() {

    private val firebaseVm: FirebaseViewModel by viewModels({ requireActivity() })


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentStartBinding.inflate(LayoutInflater.from(context))
        binding.lifecycleOwner = this
        binding.firebaseVm = firebaseVm
        firebaseVm.setFragmentState(FragmentState.START, false)

        binding.recyclerView.adapter = ChatListAdapter(getOnRoomClickListener())
        firebaseVm.setMsgList(mutableListOf())

        Log.d("chat_debug", "onCreateView: ")

        return binding.root
    }

    private fun getOnRoomClickListener(): ChatListAdapter.OnClickListener {
        return ChatListAdapter.OnClickListener { user ->
            firebaseVm.setReceiver(user.user?.value)
            if (SharedPreferenceManager.user_mode == AppComponent.MODE_SERVICE_PROVIDER) {
                findNavController().navigate(R.id.inbox_to_chat_provider)
            } else {
                findNavController().navigate(R.id.inbox_to_chat_provider)
            }

        }
    }
}