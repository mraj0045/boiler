package com.boiler

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Entry(
    /**
     * Key value to be used for the Map. By default it will the field name.
     */
    val key: String = ""
)
