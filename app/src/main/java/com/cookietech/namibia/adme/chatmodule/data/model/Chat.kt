package com.cookietech.namibia.adme.chatmodule.data.model

import com.cookietech.namibia.adme.chatmodule.data.source.firebase.MessageLiveData
import com.cookietech.namibia.adme.chatmodule.data.source.firebase.ReceiverLiveData
import com.google.firebase.firestore.Exclude

data class Chat(
    var chatId: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var isGroupChat: Boolean? = null
) {
    @get:Exclude
    var user: ReceiverLiveData? = null

    @get:Exclude
    var message: MessageLiveData? = null
}