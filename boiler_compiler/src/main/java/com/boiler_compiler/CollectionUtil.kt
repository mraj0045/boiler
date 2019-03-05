package com.boiler_compiler

import com.squareup.kotlinpoet.ClassName

/** Returns [ClassName] instance of kotlin [Map]*/
internal fun mapClass() = ClassName("kotlin.collections", "Map")

/** Returns [ClassName] instance of kotlin [HashMap]*/
internal fun hashMapClass() = ClassName("kotlin.collections", "HashMap")

/** Returns [ClassName] instance of kotlin [List]*/
internal fun listClass() = ClassName("kotlin.collections", "List")