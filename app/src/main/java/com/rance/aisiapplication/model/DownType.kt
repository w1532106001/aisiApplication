package com.rance.aisiapplication.model

enum class DownType(value: String) {
    NONE("未下载"),
    NOT_INITIATED("未启动"),
    FAIL("下载失败"),
    SUCCESS("下载成功")
}