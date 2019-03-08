package com.boiler

/**
 * Generates map code for the class annotated with this annotation.
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class MapBuilder(
    /** Generated file suffix. Format FileName+suffix*/
    val suffix: String = ""
)
