package com.boilerplate

import com.boiler.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DateConverter : TypeConverter<Date> {

    private val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

    override fun format(t: Date?): String {
        return try {
            sdf.format(t)
        } catch (e: Exception) {
            ""
        }
    }

    override fun parse(value: String?): Date? {
        return try {
            sdf.parse(value)
        } catch (e: Exception) {
            null
        }
    }

}