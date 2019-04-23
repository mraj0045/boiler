package com.boiler

class IgnoreConverter : TypeConverter<String> {

    override fun format(t: String?): String {
        return t ?: ""
    }

    override fun parse(value: String?): String? {
        return value
    }

}