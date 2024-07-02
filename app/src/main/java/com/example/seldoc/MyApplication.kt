package com.example.seldoc

import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MyApplication : MultiDexApplication() {

    companion object {
        val auth: FirebaseAuth by lazy {
            FirebaseAuth.getInstance()
        }
        var email: String? = null
        var sname: String? = null
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage

        fun checkAuth(): Boolean {
            val currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                currentUser.isEmailVerified
            } ?: false
        }
    }

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
    }
}