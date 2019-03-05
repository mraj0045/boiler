package com.boiler_compiler

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element


/** Returns the [TypeName] of the element
 * @param element [Element] instance */
fun getTypeName(element: Element): TypeName {
    return if (element.asType().asTypeName().toString().contains("String", true)) {
        String::class.asTypeName().copy(nullable = true)
    } else {
        element.asType().asTypeName().copy(nullable = true)
    }
}