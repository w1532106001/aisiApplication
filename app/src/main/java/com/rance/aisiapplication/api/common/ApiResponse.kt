package com.smartmicky.android.data.api.common

/**
 * Created by hechuangju on 2017/7/18.
 */
class ApiResponse<T> {
    public var code: Int = 0
    public var message: String = ""
    public var data: T? = null

    public fun isSucceed() = code == 0
}