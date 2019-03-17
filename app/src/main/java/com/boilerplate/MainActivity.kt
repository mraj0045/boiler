package com.boilerplate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Convert Object to HashMap
        val post = PostQuery.init().setName("Mohan").setAge(26)
        Log.e("Map Data", post.toMap().toString())

        //Convert HashMap to Object
        val map = hashMapOf("name" to "Superman", "age" to "30")
        Log.e("Object Data", PostQuery.init().toObject(map).toString())

    }
}
