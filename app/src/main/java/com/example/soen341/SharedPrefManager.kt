package com.example.soen341

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import java.util.*

class SharedPrefManager constructor(context: Context) {
    private val PREF_NAME = "userSharedPref"
    private val KEY_USERNAME = "username"
    private val KEY_USER_ID = "user_id"
    private val KEY_USER_EMAIL = "user_email"
    private var imageQueue : Queue<ImageContainer>? = null

    val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    init{
        imageQueue = LinkedList<ImageContainer>()
    }
    companion object {
        @Volatile
        private var INSTANCE: SharedPrefManager? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SharedPrefManager(context).also {
                    INSTANCE = it
                }
            }
    }
    fun GetImageUri() : ImageContainer? {
             return imageQueue?.poll()
    }
    fun AddToImageQueue(container : ImageContainer) : Unit{
        imageQueue?.offer(container)
    }
    fun userLoginPref(id:Int, username:String, email:String) {
        val editor = sharedPref.edit()
        editor.putInt(KEY_USER_ID, id)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun isUserLoggedIn() : Boolean {
        return sharedPref.getString(KEY_USERNAME, null) != null
    }
    fun getUserUsername() : String? {
        return sharedPref.getString(KEY_USERNAME, null)
    }

    fun getUserEmail() : String? {
        return sharedPref.getString(KEY_USER_EMAIL, null)
    }

    fun getUserID() : Int {
        return sharedPref.getInt(KEY_USER_ID, 0)
    }

    fun setUserUsername(username:String) {
        val editor = sharedPref.edit()
        editor.putString(KEY_USERNAME, username)
        editor.apply()
    }

    fun setUserEmail(email:String) {
        val editor = sharedPref.edit()
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()
    }

    fun userLogoutPref() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }
}
class ImageContainer(var imageData: String, var likes: Int, var comments: String, var authorID : String, var imageID : Int)
