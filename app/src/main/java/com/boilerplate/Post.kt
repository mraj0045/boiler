package com.boilerplate

import com.boiler.Entry
import com.boiler.MapBuilder

@MapBuilder
class Post(
    @Entry(key = "name1")
    val name: String?,
    @Entry(key = "age")
    val age: Int?
)