package com.boilerplate

import com.boiler.Entry
import com.boiler.Ignore
import com.boiler.MapBuilder

@MapBuilder(suffix = "query")
class Post(
    @Entry(key = "name1")
    val name: String?,
    @Entry(key = "age")
    val age: Int?,
    @Ignore
    val test: String? = ""
)