package com.cookietech.namibia.adme.managers

import com.cookietech.namibia.adme.models.UserPOJO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage

object FirebaseManager {
    private const val USER_COLLECTION_ID = "Adme_User"

    val mDataBase: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val mFunctions: FirebaseFunctions = FirebaseFunctions.getInstance()
    var mUserRef: CollectionReference
    var currentUser:UserPOJO? = null
    var mFirebaseUser:FirebaseUser? = null

    init {
        mUserRef = mDataBase.collection(USER_COLLECTION_ID)
        mFirebaseUser = mAuth.currentUser
    }

}