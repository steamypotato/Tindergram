package com.example.soen341

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_upload_image.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

//Inherit HomeActivity because I dont want to change my layout.
class ImageActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

         if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
            {
                println("Permission denied")
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),MY_PERMISSIONS_READ_EXTERNAL_STORAGE)

            }
            else{
                println("Permission already granted")
                pickImageFromGallery()
            }

    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    fun createImageDataFromURI(uri : Uri?) : ByteArray?{
        var imageData : ByteArray? = null
        val inputStream = contentResolver.openInputStream(uri!!)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
            println("Image in bytes is : " + imageData)
        }
        return imageData
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
           var uri = data?.data;
            val imageData : ByteArray? = createImageDataFromURI(uri)
            userImage_imageview.setImageURI(uri)
            userImage_post.setOnClickListener {
                RequestHandler.getInstance(this).saveImageToServer(imageData, userImage_comment.text.toString(),this)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        Toast.makeText(this,"image discarded",Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)

    }
    //this will take care of the case when the user is prompted for permission. In essence, it will really only
    //happen once unless permissions are explicitely reset
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        //this is Kotlin fancy way of doing switch statements. For now we are only checking if the generated request code
        //from this handler function is equal to the user defined request code we wrote into earlier
        when(requestCode){
            MY_PERMISSIONS_READ_EXTERNAL_STORAGE -> {
                //if grant results is empty, it means that the permission request was cancelled.
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                } else{
                    //Toast just open a little window on the app where you can write stuff
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
        //Here by default
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object{
        private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 0
        private val IMAGE_PICK_CODE = 0
    }


}


