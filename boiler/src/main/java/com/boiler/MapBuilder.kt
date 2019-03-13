package com.boiler

/**
 * Generates HashMap code for the fields in the class annotated with [MapBuilder].
 *
 * Coverts fields to [Map] & [Map] to Object class]
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class MapBuilder(
    /** Generated file suffix. Format FileName+suffix*/
    val suffix: String = ""
)
