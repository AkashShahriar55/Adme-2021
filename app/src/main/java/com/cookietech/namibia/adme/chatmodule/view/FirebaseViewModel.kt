package com.cookietech.namibia.adme.chatmodule.view

import android.net.Uri
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cookietech.namibia.adme.chatmodule.data.model.Chat
import com.cookietech.namibia.adme.chatmodule.data.model.Message
import com.cookietech.namibia.adme.chatmodule.data.model.User
import com.cookietech.namibia.adme.chatmodule.data.repository.Repository
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomListLiveData
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomLiveData
import com.cookietech.namibia.adme.chatmodule.utils.extension.clear
import com.cookietech.namibia.adme.chatmodule.utils.states.AuthenticationState
import com.cookietech.namibia.adme.chatmodule.utils.states.FragmentState
import com.cookietech.namibia.adme.chatmodule.utils.states.NetworkState

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

//@OpenForTesting
class FirebaseViewModel @ViewModelInject constructor(
    private val repository: Repository
) : ViewModel() {

    private var isUsernameAvailableJob: Job? = null
    private var searchForUserJob: Job? = null
    private var getUserDataJob:Job? = null

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> = _userList

    private val _userSearchStatus = MutableLiveData<NetworkState>()
    val userSearchStatus: LiveData<NetworkState> = _userSearchStatus

    private val _pushImgStatus = MutableLiveData<NetworkState>()
    val pushImgStatus: LiveData<NetworkState> = _pushImgStatus

    private val _pushAudioStatus = MutableLiveData<NetworkState>()
    val pushAudioStatus: LiveData<NetworkState> = _pushAudioStatus

    private val _chatRoomList: ChatRoomListLiveData = repository.getChatRoomListLiveData()
    val chatRoomList: LiveData<List<Chat>> = _chatRoomList

    private val _msgList: ChatRoomLiveData = repository.getChatRoomLiveData()
    val msgList: LiveData<List<Message>> = _msgList

    private val _msgLength = MutableLiveData<Int>()
    val msgLength: LiveData<Int> = _msgLength

    private val _usernameStatus = MutableLiveData<NetworkState>()
    val usernameStatus: LiveData<NetworkState> = _usernameStatus

    private val _fragmentState = MutableLiveData<Pair<FragmentState, Boolean>>()
    val fragmentState: LiveData<Pair<FragmentState, Boolean>> = _fragmentState

    init {
        repository.fetchConfigMsgLength { _msgLength.value = it }
    }

    val user: LiveData<Pair<User?, AuthenticationState>> = repository.getUserLiveData()
    val authenticationState = Transformations.map(user) { userWithState ->
        Log.d("akash_chat_debug",
            ("observeAuthState: " + userWithState)
        )
        if (userWithState.second is AuthenticationState.Authenticated) {
            userWithState.first?.username ?: setFragmentState(FragmentState.USERNAME)
        }
        userWithState.second
    }

    fun setMsgList(msgList: List<Message>) {
        _msgList.value = msgList
    }

    fun setFragmentState(fragmentState: FragmentState, notify: Boolean = true) {
        _fragmentState.value = Pair(fragmentState, notify)
    }

    fun setReceiver(user: User?) {
        user?.let { _userData.value = it }
        user?.userId?.let { _msgList.receiverId = it }
    }

    fun onSignIn() {
    }

    fun onSignOut() {
        _msgList.clear()
        _chatRoomList.clear()
    }

    fun onSearchTextChange(newText: String) {
        searchForUserJob?.cancel()
        searchForUserJob = viewModelScope.launch {
            repository.searchForUser(newText) { networkState, userList ->
                _userList.value = userList
                _userSearchStatus.value = networkState
            }
        }
    }

    private val _userData = MutableLiveData<User>()
    val userData:LiveData<User> = _userData

    private val _userDataStatus = MutableLiveData<NetworkState>()
    val userDataStatus:LiveData<NetworkState> = _userDataStatus

    fun getUser(userId:String){

        getUserDataJob?.cancel()
        getUserDataJob = viewModelScope.launch {
            repository.getUser(userId){ networkState, user ->
                _userData.value  = user
                _userDataStatus.value = networkState
            }
        }
    }

    fun isUsernameAvailable(username: String) {
        isUsernameAvailableJob?.cancel()
        isUsernameAvailableJob = viewModelScope.launch {
            repository.isUsernameAvailable(username) {
                _usernameStatus.value = it
            }
        }
    }

    fun addUsername(username: String) {
        repository.addUsername(username) {

            _usernameStatus.value = it
            if (it == NetworkState.LOADED) setFragmentState(FragmentState.START)
        }
    }

    fun pushAudio(audioPath: String, audioDuration: Long) {
        _msgList.pushAudio(audioPath, audioDuration) {
            _pushAudioStatus.value = it
        }
    }

    fun pushMsg(msg: String) {
        _msgList.pushMsg(msg)
    }

    fun pushPicture(pictureUri: Uri) {
        _msgList.pushPicture(pictureUri) {
            _pushImgStatus.value = it
        }
    }
}