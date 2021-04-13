package com.cookietech.namibia.adme.architecture.client.home

import com.cookietech.namibia.adme.managers.FirebaseManager
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class HomeRepository {

    fun fetchCategories(): Task<QuerySnapshot> {
        return FirebaseManager.mCategoryReference.get()
    }
}