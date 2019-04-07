package com.boiler_compiler

import com.squareup.kotlinpoet.ClassName

private const val COLLECTION_PACKAGE = "kotlin.collections"

/** Returns [ClassName] instance of kotlin [Map]*/
internal fun mapClass() = ClassName(COLLECTION_PACKAGE, "Map")

/** Returns [ClassName] instance of kotlin [HashMap]*/
internal fun hashMapClass() = ClassName(COLLECTION_PACKAGE, "HashMap")

/** Returns [ClassName] instance of kotlin [List]*/
internal fun listClass() = ClassName(COLLECTION_PACKAGE, "List")