package com.cookietech.namibia.adme.managers

import android.app.Activity
import android.util.Log
import com.cookietech.namibia.adme.models.UserPOJO
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentSnapshot
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.TimeUnit

class LoginAndRegistrationManager() {



    fun checkIfAlreadyLoggedIn(callback: UserCreationCallback): Boolean {
        if(FirebaseManager.mFirebaseUser == null){
            Log.d("login_debug", "checkIfAlreadyLoggedIn: null")
        } else{
            Log.d("login_debug", "checkIfAlreadyLoggedIn: not null")
        }
        FirebaseManager.mFirebaseUser?.let {
            createOrFetchUser(it,callback)
            return true
        }
        return false
    }


    fun verifyPhoneNo(phoneNo: String, callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks, activity: Activity){
        try{
            val options = PhoneAuthOptions.newBuilder(FirebaseManager.mAuth)
                    .setPhoneNumber(phoneNo)       // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(activity)                 // Activity (for callback binding)
                    .setCallbacks(callback)          // OnVerificationStateChangedCallbacks
                    .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }catch (exception:FirebaseException){
            Log.d("akash_debug", "verifyPhoneNo: " + exception)
        }


    }

    fun signInWithPhoneAuthCredential(
            phoneAuthCredential: PhoneAuthCredential,
            onCompleteListener: OnCompleteListener<AuthResult>,
    ) {
        FirebaseManager.mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(onCompleteListener)
    }

    fun linkWithUser(
            phoneAuthCredential: PhoneAuthCredential,
            onCompleteListener: OnCompleteListener<AuthResult>,
    ) {
        FirebaseManager.mFirebaseUser?.linkWithCredential(phoneAuthCredential)?.addOnCompleteListener(onCompleteListener)
    }
    /** Firebase Authentication With Google**/
    fun firebaseAuthWithGoogle(idToken: String?): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        return FirebaseManager.mAuth.signInWithCredential(credential)
    }

    /** Firebase Authentication With Facebook**/
    fun firebaseAuthWithFacebook(token: AccessToken): Task<AuthResult> {
        Log.d("fb_login_debug", "handleFacebookAccessToken: called")
        val credential = FacebookAuthProvider.getCredential(token.token)
        return FirebaseManager.mAuth.signInWithCredential(credential)
    }

    fun createOrFetchUser(user: FirebaseUser , callback: UserCreationCallback) {
        val user_id = user.uid
        FirebaseManager.mUserRef.document(user_id).get().addOnCompleteListener { task->
            if (task.isSuccessful) {
                Log.d("user_creation", "createUser: successful")
                val document: DocumentSnapshot = task.getResult()
                if (document.exists()) {
                    FirebaseManager.currentUser = document.toObject(UserPOJO::class.java)
                    if(FirebaseManager.currentUser?.user_info_updated == true){
                        callback.onUserFetchSuccessful()
                    }else{
                        callback.onUserCreationSuccessful()
                    }

                } else {
                    // User hasn't created yet
                    // create new user in database
                    val name = user.displayName ?: "Adme User"
                    val email = user.email ?: "null"
                    val phone = user.phoneNumber ?: "null"
                    val registration_date = Timestamp.now()
                    val new_user = UserPOJO(user_id = user_id, user_name = name, email = email, phone = phone, registration_date = registration_date)
                    FirebaseManager.mUserRef.document(user.uid).set(new_user).addOnSuccessListener {
                        FirebaseManager.currentUser = new_user
                        callback.onUserCreationSuccessful()
                    }.addOnFailureListener {
                        Log.d("user_creation", "createUser: " + it.message)
                        callback.onUserCreationFailed(it)
                    }
                }
            } else {
                task.exception?.let { callback.onUserCreationFailed(it) }
            }
        }

    }

    fun updateUserInfo(user:UserPOJO
    ): Task<Void> {
        return FirebaseManager.mUserRef.document(user.user_id).set(user)
    }

    fun updateFCMToken(token: String?, uid: String) {
        val data = hashMapOf(
            "token" to token,
            "user_id" to uid
        )
        Log.d("FCM_debug", "updateFCMToken: data: $data")
        FirebaseManager.mFunctions.getHttpsCallable("updateFCMToken").call(data).addOnCompleteListener { task->
            if(task.isSuccessful){
                val jsonString = task.result.data.toString()
                Log.d("FCM_debug", "updateFCMToken result: ${task.result?.data.toString()}")
                //val jsonObject = JSONObject(jsonString)
                //Log.d("FCM_debug", "updateFCMToken: messgae: $jsonObject}")

                Log.d("z z", "updateFCMToken: task successful: " + task.exception)


            }else{
                Log.d("FCM_debug", "updateFCMToken: task not successful: " + task.exception)
            }
        }.addOnFailureListener {
            Log.d("FCM_debug", "updateFCMToken: Failure: " + it.message)

        }
    }


    interface UserCreationCallback{
        fun onUserCreationSuccessful()
        fun onUserFetchSuccessful()
        fun onUserCreationFailed(exception:Exception)
    }


}