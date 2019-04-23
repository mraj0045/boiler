package com.boiler

interface TypeConverter<T> {
    fun format(t: T?): String
    fun parse(value: String?): T?
}