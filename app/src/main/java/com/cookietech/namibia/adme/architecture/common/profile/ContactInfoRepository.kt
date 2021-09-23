package com.cookietech.namibia.adme.architecture.common.profile

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.facebook.AccessToken
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider

class ContactInfoRepository {
    fun firebaseAuthWithFacebook(token: AccessToken): Task<AuthResult>? {
        val credential = FacebookAuthProvider.getCredential(token.token)
        return FirebaseManager.mFirebaseUser?.linkWithCredential(credential)

    }

    fun firebaseAuthWithGoogle(idToken: String?): Task<AuthResult>? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return FirebaseManager.mFirebaseUser?.linkWithCredential(credential)
    }
}