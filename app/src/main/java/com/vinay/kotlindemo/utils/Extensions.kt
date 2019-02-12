package com.vinay.kotlindemo.utils

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import java.text.DecimalFormat
import com.vinay.kotlindemo.BuildConfig


val VERBOSE = Log.VERBOSE
val DEBUG = Log.DEBUG
val INFO = Log.INFO
val WARN = Log.WARN
val ERROR = Log.ERROR
val WTF = Log.ASSERT

private val TAG = "LogHelper"

fun log(level: Int, msg: String?, throwable: Throwable?) {
    if (BuildConfig.DEBUG) {
        val elements = Throwable().stackTrace
        var callerClassName = "?"
        var callerMethodName = "?"
        var callerLineNumber = "?"
        if (elements.size >= 4) {
            callerClassName = elements[3].className
            callerClassName = callerClassName.substring(callerClassName.lastIndexOf('.') + 1)
            if (callerClassName.indexOf("$") > 0) {
                callerClassName = callerClassName.substring(0, callerClassName.indexOf("$"))
            }
            callerMethodName = elements[3].methodName
            callerMethodName = callerMethodName.substring(callerMethodName.lastIndexOf('_') + 1)
            if (callerMethodName == "<init>") {
                callerMethodName = callerClassName
            }
            callerLineNumber = elements[3].lineNumber.toString()
        }

        val stack =
            "[" + callerClassName + "." + callerMethodName + "():" + callerLineNumber + "]" + if (TextUtils.isEmpty(
                    msg
                )
            ) "" else " "

        when (level) {
            VERBOSE -> android.util.Log.v(TAG, stack + msg!!, throwable)
            DEBUG -> android.util.Log.d(TAG, stack + msg!!, throwable)
            INFO -> android.util.Log.i(TAG, stack + msg!!, throwable)
            WARN -> android.util.Log.w(TAG, stack + msg!!, throwable)
            ERROR -> android.util.Log.e(TAG, stack + msg!!, throwable)
            WTF -> android.util.Log.wtf(TAG, stack + msg!!, throwable)
            else -> {
            }
        }
    }
}

fun Any.logd(tag: String = "TAG", throwable: Throwable? = null) {
    log(DEBUG, this.toString(), throwable)
}

fun Any.loge(tag: String = "", throwable: Throwable? = null) {
    log(ERROR, this.toString(), throwable)
}

fun Any.logv(tag: String = "", throwable: Throwable? = null) {
    log(VERBOSE, this.toString(), throwable)
}

fun Any.logw(tag: String = "", throwable: Throwable? = null) {
    log(WARN, this.toString(), throwable)
}

fun Any.logwtf(tag: String = "", throwable: Throwable? = null) {
    log(WTF, this.toString(), throwable)
}

fun Any.logi(tag: String = "", throwable: Throwable? = null) {
    log(INFO, this.toString(), throwable)
}

fun Float.toFormat(): String = DecimalFormat("###.00").format(this)

fun JsonObject.jsonString(key: String): String =
    if (this.get(key) != null && JsonNull.INSTANCE != this.get(key)) this.get(key).asString else ""

fun JsonObject.jsonInt(key: String): Int =
    if (this.get(key) != null && JsonNull.INSTANCE != this.get(key)) this.get(key).asInt else 0

fun JsonObject.jsonFloat(key: String): Float =
    if (this.get(key) != null && JsonNull.INSTANCE != this.get(key)) this.get(key).asFloat else 0.0f

fun JsonElement.jsonArray(key: String): JsonArray =
    if (this is JsonObject && this.asJsonObject.get(key) != null && this.asJsonObject.get(key) != JsonNull.INSTANCE) this.asJsonObject.get(
        key
    ).asJsonArray else JsonArray()

fun JsonElement.jsonString(key: String): String =
    if (this is JsonObject && this.asJsonObject.get(key) != null && this.asJsonObject.get(key) != JsonNull.INSTANCE) this.asJsonObject.get(
        key
    ).asString else ""

fun JsonElement.jsonInt(key: String): Int =
    if (this is JsonObject && this.asJsonObject.get(key) != null && this.asJsonObject.get(key) != JsonNull.INSTANCE) this.asJsonObject.get(
        key
    ).asInt else 0

fun String.toast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun Any.jsonString(): String = if (this == JsonNull.INSTANCE) "" else (this as JsonElement).asString