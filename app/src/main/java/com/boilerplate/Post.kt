package com.boilerplate

import com.boiler.Entry
import com.boiler.Ignore
import com.boiler.MapBuilder

@MapBuilder(suffix = "query")
data class Post(
    @Entry(key = "name")
    val name: String?,
    @Entry(key = "age")
    val age: Int?,
    @Ignore
    val test: String? = "",
    val dob: String?
)