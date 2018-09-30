package com.seeyon.cmp.common.extentions

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.lang.reflect.Type

private val gson = Gson()

@Throws(JsonSyntaxException::class)
fun Any.toJson(): String = gson.toJson(this)

fun <T> String.toObj(clazz: Class<T>): T? =
        try {
            gson.fromJson(this, clazz)
        } catch (e: Exception) {
            null
        }

fun <T> String.toObj(type: Type): T? =
        try {
            gson.fromJson(this, type)
        } catch (e: Exception) {
            null
        }

fun <T> String.toObjArray(clz: Class<T>): ArrayList<T>? =
        try {
            val array = JsonParser().parse(this).asJsonArray
            array.mapTo(ArrayList()) {
                gson.fromJson(it, clz)
            }
        } catch (e: Exception) {
            null
        }

