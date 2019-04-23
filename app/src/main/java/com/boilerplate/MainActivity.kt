package com.boilerplate

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Convert Object to HashMap
        val post = PostQuery.init()
            .setName("Mohan")
            .setAge(26)
            .setDob(Calendar.getInstance().apply {
                set(Calendar.YEAR, 1993)
                set(Calendar.MONTH, Calendar.FEBRUARY)
                set(Calendar.DAY_OF_MONTH, 8)
                set(Calendar.HOUR, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.time)
        Log.e("Map Data", post.toMap().toString())

        //Convert HashMap to Object
        val map = hashMapOf("name" to "Superman", "age" to "30", "dob" to "08-02-1993")
        Log.e("Object Data", PostQuery.init().toObject(map).toString())

    }
}
