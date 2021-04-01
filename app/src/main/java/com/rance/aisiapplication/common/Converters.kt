package com.rance.aisiapplication.common

import android.text.TextUtils
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rance.aisiapplication.model.DownType
import java.util.*
import java.util.concurrent.ConcurrentHashMap


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

    @TypeConverter
    fun downTypeToInt(downType: DownType): Int {
        return downType.ordinal
    }

    @TypeConverter
    fun intToDownType(int: Int): DownType {
        return DownType.values()[int]
    }

    @TypeConverter
    fun mapToString(map: ConcurrentHashMap<String, String>): String {
        return Gson().toJson(map)
    }

    @TypeConverter
    fun stringToMap(string: String?): ConcurrentHashMap<String, String> {
        return if (string.isNullOrEmpty()) {
            ConcurrentHashMap()
        } else {
            Gson().fromJson(string.toString(), object : TypeToken<ConcurrentHashMap<String, String>>() {}.type)
        }
    }
}