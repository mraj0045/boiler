package com.boiler

/** Ignores the field if annotated.*/
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
annotation class Ignore