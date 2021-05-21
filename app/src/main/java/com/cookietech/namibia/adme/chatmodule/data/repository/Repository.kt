package com.cookietech.namibia.adme.chatmodule.data.repository

import androidx.lifecycle.LiveData
import com.cookietech.namibia.adme.chatmodule.data.model.User
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomListLiveData
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ChatRoomLiveData
import com.cookietech.namibia.adme.chatmodule.utils.states.AuthenticationState
import com.cookietech.namibia.adme.chatmodule.utils.states.NetworkState

interface Repository {

    fun getChatRoomListLiveData(): ChatRoomListLiveData

    fun getChatRoomLiveData(): ChatRoomLiveData

    fun getUserLiveData(): LiveData<Pair<User?, AuthenticationState>>

    suspend fun isUsernameAvailable(
        username: String,
        callBack: (networkState: NetworkState) -> Unit
    )

    suspend fun searchForUser(
        username: String,
        callBack: (networkState: NetworkState, userList: MutableList<User>) -> Unit
    )

    suspend fun getUser(userId:String,callback:(networkState:NetworkState,user:User?)->Unit)

    fun addUsername(
        username: String,
        callBack: (usernameStatus: NetworkState) -> Unit
    )

    fun fetchConfigMsgLength(callBack: (msgLengh: Int) -> Unit)
}