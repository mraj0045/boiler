package com.boiler

/** For using custom key value for the Field, Annotate the field with [Entry] annotation.
 *
 * Custom key value is optional.
 * */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Entry(
    /**
     * Key value to be used for the Map. By default it will the field name.
     */
    val key: String = ""
)
