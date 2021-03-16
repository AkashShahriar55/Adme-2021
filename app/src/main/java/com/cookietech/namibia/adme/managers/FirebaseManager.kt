package com.cookietech.namibia.adme.managers

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class FirebaseManager {
    val mDataBase: FirebaseFirestore = FirebaseFirestore.getInstance()
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mStorage: FirebaseStorage = FirebaseStorage.getInstance()
    val mFunctions: FirebaseFunctions = FirebaseFunctions.getInstance()

}