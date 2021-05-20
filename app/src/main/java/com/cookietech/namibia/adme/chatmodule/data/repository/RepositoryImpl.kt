package com.artf.chatapp.data.repository

import androidx.lifecycle.LiveData
import com.cookietech.namibia.adme.chatmodule.data.model.User
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomListLiveData
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomLiveData
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.FirebaseDaoImpl
import com.cookietech.namibia.adme.chatmodule.utils.states.AuthenticationState
import com.cookietech.namibia.adme.chatmodule.utils.states.NetworkState
import com.cookietech.namibia.adme.chatmodule.data.repository.Repository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val firebaseDaoImpl: FirebaseDaoImpl,
    private val ioDispatcher: CoroutineDispatcher
) : Repository {

    override fun getChatRoomListLiveData(): ChatRoomListLiveData {
        return firebaseDaoImpl.chatRoomListLiveData
    }

    override fun getChatRoomLiveData(): ChatRoomLiveData {
        return firebaseDaoImpl.chatRoomLiveData
    }

    override fun getUserLiveData(): LiveData<Pair<User?, AuthenticationState>> {
        return firebaseDaoImpl.userLiveData
    }

    override suspend fun isUsernameAvailable(
        username: String,
        callBack: (networkState: NetworkState) -> Unit
    ) {
        firebaseDaoImpl.isUsernameAvailable(username, callBack)
    }

    override suspend fun searchForUser(
        username: String,
        callBack: (networkState: NetworkState, userList: MutableList<User>) -> Unit
    ) {
        firebaseDaoImpl.searchForUser(username, callBack)
    }

    override fun addUsername(
        username: String,
        callBack: (usernameStatus: NetworkState) -> Unit
    ) {
        firebaseDaoImpl.addUsername(username, callBack)
    }

    override fun fetchConfigMsgLength(callBack: (msgLengh: Int) -> Unit) {
        firebaseDaoImpl.fetchConfigMsgLength(callBack)
    }
}