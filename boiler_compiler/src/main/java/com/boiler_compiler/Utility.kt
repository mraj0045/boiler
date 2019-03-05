package com.boiler_compiler

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element


/** Returns the [TypeName] of the element
 * @param element [Element] instance */
internal fun getTypeName(element: Element): TypeName {
    return when {
        //String
        element.asType().asTypeName().isString() -> String::class.asTypeName().copy(nullable = true)
        //Integer
        element.asType().asTypeName().isInt() -> Int::class.asTypeName().copy(nullable = true)
        //Float
        element.asType().asTypeName().isFloat() -> Float::class.asTypeName().copy(nullable = true)
        //Double
        element.asType().asTypeName().isDouble() -> Double::class.asTypeName().copy(nullable = true)
        //Boolean
        element.asType().asTypeName().isBoolean() -> Boolean::class.asTypeName().copy(nullable = true)
        else -> element.asType().asTypeName().copy(nullable = true)
    }
}


internal fun TypeName.isString(): Boolean = toString().contains("String", true)

internal fun TypeName.isInt(): Boolean = toString().contains("Integer", true) || toString().contains("int", true)

internal fun TypeName.isFloat(): Boolean = toString().contains("Float", true) || toString().contains("float", true)

internal fun TypeName.isDouble(): Boolean = toString().contains("Double", true) || toString().contains("double", true)

internal fun TypeName.isBoolean(): Boolean =
    toString().contains("Boolean", true) || toString().contains("boolean", true)