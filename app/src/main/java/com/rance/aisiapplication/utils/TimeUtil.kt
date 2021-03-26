package com.rance.aisiapplication.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hechuangju on 2018/08/10
 */
object TimeUtil {
    fun getTimeString(serviceTime: String?): String? {
        try {
            serviceTime?.isNotEmpty()?.let {
                val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
                val format = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                return format.format(data)
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    fun getTimeDividerString(serviceTime: String?): String? {
        try {
            serviceTime?.isNotEmpty()?.let {
                val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
                val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
                return format.format(data)
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    fun getTimeNoTString(serviceTime: String?): String? {
        try {
            serviceTime?.isNotEmpty()?.let {
                val data = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(serviceTime)
                val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
                return format.format(data)
            }
        } catch (e: Exception) {
            return ""
        }
        return ""
    }

    fun getTimeNoTStringDate(serviceTime: String?): Long {
        try {
            serviceTime?.isNotEmpty()?.let {
                val data = SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(serviceTime)
                return data.time
            }
        } catch (e: Exception) {
            return 0
        }
        return 0
    }

    fun getTimeTStringDate(serviceTime: String?): Long {
        try {
            serviceTime?.isNotEmpty()?.let {
                val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
                return data.time
            }
        } catch (e: Exception) {
            return 0
        }
        return 0
    }

    fun getDayTimeString(serviceTime: String?): String? {
        serviceTime?.isNotEmpty()?.let {
            val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
            val format = SimpleDateFormat("yyyy.MM.dd")
            return format.format(data)
        }
        return ""
    }

    fun getDayTimeString2(serviceTime: String?): String? {
        serviceTime?.isNotEmpty()?.let {
            val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
            val format = SimpleDateFormat("yyyy-MM-dd")
            return format.format(data)
        }
        return ""
    }

    fun getTimeTString(data: Date): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(data)
    }

    fun getDayTimeShortString(serviceTime: String?): String? {
        serviceTime?.isNotEmpty()?.let {
            try {
                val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
                val format = SimpleDateFormat("MM.dd")
                return format.format(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun getTimeShortString(serviceTime: String?): String? {
        serviceTime?.isNotEmpty()?.let {
            try {
                val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
                val format = SimpleDateFormat("MM-dd HH:mm")
                return format.format(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }

    fun getTodayChineseString(serviceTime: String?): String? {
        serviceTime?.isNotEmpty()?.let {
            val data = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(serviceTime)
            val format = SimpleDateFormat("yyyy年MM月dd日")
            return format.format(data)
        }
        return ""
    }

    fun getTodayShortString(): String {
        val simpleFormat = SimpleDateFormat("MM.dd")
        return simpleFormat.format(Date(System.currentTimeMillis()))
    }

    fun getTodayString(): String {
        val simpleFormat = SimpleDateFormat("yyyyMMdd")
        return simpleFormat.format(Date(System.currentTimeMillis()))
    }

    private val SDF = SimpleDateFormat(
        "yyyyMMdd"
    )

    /**
     * 最近1周获取起止日期
     *
     * @param date   需要参照的日期
     * @param option 0 开始日期；1 结束日期
     * @param k      0 包含本周 1 不包含本周
     * @return String
     */
    private fun getFromToDate(date: Date, option: Int, k: Int): String? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        var dayOfWeek = calendar[Calendar.DAY_OF_WEEK] - 1
        //判断是否为星期天,外国定义星期天是他们的第一天
        if (dayOfWeek <= 0) {
            dayOfWeek = 7
        }
        val offset = if (0 == option) 1 - dayOfWeek else 7 - dayOfWeek
        val amount = if (0 == option) offset - k * 7 else offset - k * 7
        calendar.add(Calendar.DATE, amount)
        var format: String? = null
        try {
            format = SDF.format(calendar.time)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return format
    }

    private fun getFromToDate(date: Date, option: Int, k: Int, sdf: SimpleDateFormat): String? {
        val calendar = Calendar.getInstance()
        calendar.time = date
        var dayOfWeek = calendar[Calendar.DAY_OF_WEEK] - 1
        //判断是否为星期天,外国定义星期天是他们的第一天
        if (dayOfWeek <= 0) {
            dayOfWeek = 7
        }
        val offset = if (0 == option) 1 - dayOfWeek else 7 - dayOfWeek
        val amount = if (0 == option) offset - k * 7 else offset - k * 7
        calendar.add(Calendar.DATE, amount)
        var format: String? = null
        try {
            format = sdf.format(calendar.time)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return format
    }

    /**
     * 根据当前日期获得本周的日期区间（本周周一和周日日期）
     *
     * @return String
     */
    fun getThisWeekTimeInterval(): String? {
        val beginDate = getFromToDate(Date(), 0, 0)
        val endDate = getFromToDate(Date(), 1, 0)
        return "$beginDate-$endDate"
    }

    fun getThisWeekTimeIntervalSimple(): String {
        val sdf = SimpleDateFormat(
            "MM.dd"
        )
        val beginDate = getFromToDate(Date(), 0, 0, sdf)
        val endDate = getFromToDate(Date(), 1, 0, sdf)
        return "$beginDate-$endDate"
    }

    /**
     * 根据当前日期获得上周的日期区间（上周周一和周日日期）
     *
     * @return String
     */
    fun getLastWeekTimeInterval(): String? {
        val beginDate = getFromToDate(Date(), 0, 1)
        val endDate = getFromToDate(Date(), 1, 1)
        return "$beginDate-$endDate"
    }

    /**
     * HH:mm:ss
     */
    fun getCurrentTimeString(): String {
        val simpleFormat = SimpleDateFormat("HH:mm:ss")
        return simpleFormat.format(Date(System.currentTimeMillis()))
    }
}

class TimeUtilJava {
    fun getTodayString(): String {
        val simpleFormat = SimpleDateFormat("yyyyMMdd")
        return simpleFormat.format(Date(System.currentTimeMillis()))
    }
}