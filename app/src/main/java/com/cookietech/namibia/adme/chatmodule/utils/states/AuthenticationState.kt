package com.cookietech.namibia.adme.chatmodule.utils.states

sealed class AuthenticationState {
    class Authenticated(val userId: String) : AuthenticationState()
    object Unauthenticated : AuthenticationState()
    object InvalidAuthentication : AuthenticationState()
}