package com.rance.aisiapplication.common

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class Converters {
    @TypeConverter
    fun listToString(list: MutableList<String>?): String? =
        if (list == null || list.isEmpty()) "" else Gson().toJson(list)

    @TypeConverter
    fun stringToList(string: String?): MutableList<String>? {
        if (TextUtils.isEmpty(string)) {
            return mutableListOf()
        } else {
            return Gson().fromJson<MutableList<String>>(
                string,
                object : TypeToken<MutableList<String>>() {}.type
            )
        }
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}