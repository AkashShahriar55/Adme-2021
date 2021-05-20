package com.cookietech.namibia.adme.chatmodule.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.cookietech.namibia.adme.chatmodule.view.chatRoom.ChatFragment
import com.cookietech.namibia.adme.chatmodule.view.chatRooms.StartFragment
import com.artf.chatapp.view.searchUser.SearchFragment
import com.cookietech.namibia.adme.chatmodule.view.userProfile.UsernameFragment
import com.cookietech.namibia.adme.Application.AdmeApplication
import com.cookietech.namibia.adme.R
import com.cookietech.namibia.adme.chatmodule.utils.states.FragmentState
import com.cookietech.namibia.adme.databinding.ChatActivityMainBinding

class NavigationManager(activity: AppCompatActivity, val binding: ChatActivityMainBinding) {

    private val navOptions = NavOptions.Builder().setLaunchSingleTop(true).build()
    private val uriUsername = Uri.parse("atr:fragment_username")
    private val uriChat = Uri.parse("atr:fragment_chat")
    private val uriSearch = Uri.parse("atr:fragment_search")
    private val uriStart = Uri.parse("atr:fragment_start")

    object FragmentLabel {
        val CHAT = ChatFragment::class.java.simpleName
        val SEARCH = SearchFragment::class.java.simpleName
        val START = StartFragment::class.java.simpleName
        val USERNAME = UsernameFragment::class.java.simpleName
    }

    private val doNothing = Any()

    init {
        val navController = activity.findNavController(R.id.nav_host_fragment)
        Navigation.setViewNavController(binding.root, navController)
        addOnDestinationChangedListener(binding.root.findNavController())
    }

    fun onFragmentStateChange(fragmentState: FragmentState) = when (fragmentState) {
        FragmentState.USERNAME -> navigateTo(uriUsername, navOptions)
        FragmentState.START -> navigateTo(uriStart, navOptions)
        FragmentState.CHAT -> navigateTo(uriChat, navOptions)
        else -> throw Exception()
    }

    private fun addOnDestinationChangedListener(navController: NavController) {
        navController.addOnDestinationChangedListener(getOnDestinationChangedListener())
    }

    private fun getOnDestinationChangedListener(): NavController.OnDestinationChangedListener {
        return NavController.OnDestinationChangedListener { controller, destination, arguments ->
            onDestinationChanged(destination)
        }
    }

    private fun onDestinationChanged(destination: NavDestination) = when (destination.label) {
        FragmentLabel.CHAT -> doNothing
        else -> AdmeApplication.receiverId = null
    }

    private fun navigateTo(uri: Uri, navOptions: NavOptions) {
        binding.root.findNavController().navigate(uri, navOptions)
    }
}