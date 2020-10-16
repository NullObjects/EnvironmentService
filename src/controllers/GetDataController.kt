package com.environmentService.controllers

import com.environmentService.models.DataClassEnum
import com.environmentService.models.DataOptEnum
import com.environmentService.models.Device
import com.environmentService.models.Environment
import com.environmentService.utils.DruidUtil
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.between
import org.ktorm.entity.filter
import org.ktorm.entity.first
import org.ktorm.entity.map
import org.ktorm.entity.sortedByDescending
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 数据获取路由
 */
@KtorExperimentalLocationsAPI
@Location("/data/{dataClass}")
class GetDataController(val dataClass: String) {
    @Location("/{span}")
    data class Span(val parentRoute: GetDataController, val span: String = "last")

    @Location("/{start}/{end}")
    data class Time(val parentRoute: GetDataController, val start: String, val end: String)
}

/**
 * 数据获取路由
 */
@KtorExperimentalLocationsAPI
fun Route.data() {
    /**
     * 获取最新一条数据
     * @param database 数据所在数据库
     * @param dataClass 待获取数据类别
     * @return Map<String, Any?> 最新数据 或错误信息
     */
    fun getData(database: Database, dataClass: String) =
        try {
            when (DataClassEnum.valueOf(dataClass.toUpperCase())) {
                DataClassEnum.ENVIRONMENT -> database.Environment.sortedByDescending { it.recordTime }.first()
                    .properties.mapValues {
                        //替换时间为字符串
                        if (it.value is LocalDateTime)
                            it.value.toString()
                        else
                            it.value
                    }
                DataClassEnum.DEVICE -> database.Device.sortedByDescending { it.recordTime }.first()
                    .properties.mapValues {
                        //替换时间为字符串
                        if (it.value is LocalDateTime)
                            it.value.toString()
                        else
                            it.value
                    }
            }
        } catch (ex: Exception) {
            mapOf("getData failed" to ex.message)
        }

    /**
     * 获取一段时间内数据
     * @param database 数据所在数据库
     * @param dataClass 待获取数据类别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<Map<String, Any?>> 一段时间内数据列表 或错误信息
     */
    fun getData(database: Database, dataClass: String, startTime: LocalDateTime, endTime: LocalDateTime) =
        try {
            when (DataClassEnum.valueOf(dataClass.toUpperCase())) {
                DataClassEnum.ENVIRONMENT -> database.Environment.filter { it.recordTime.between(startTime..endTime) }
                    .map {
                        it.properties.mapValues { entry ->
                            //替换时间为字符串
                            if (entry.value is LocalDateTime)
                                entry.value.toString()
                            else
                                entry.value
                        }
                    }
                DataClassEnum.DEVICE -> database.Device.filter { it.recordTime.between(startTime..endTime) }
                    .map {
                        it.properties.mapValues { entry ->
                            //替换时间为字符串
                            if (entry.value is LocalDateTime)
                                entry.value.toString()
                            else
                                entry.value
                        }
                    }
            }
        } catch (ex: Exception) {
            listOf(mapOf("getData failed" to ex.message))
        }

    /**
     * 按时间段获取数据
     */
    get<GetDataController.Span> {
        val endTime = LocalDateTime.now()
        var startTime = endTime
        //根据时间段获取起始时间
        try {
            startTime = when (DataOptEnum.valueOf(it.span.toUpperCase())) {
                DataOptEnum.LATEST -> endTime
                DataOptEnum.HOUR -> endTime.minusHours(1)
                DataOptEnum.DAY -> endTime.minusDays(1)
                DataOptEnum.WEEK -> endTime.minusDays(7)
                DataOptEnum.MONTH -> endTime.minusMonths(1)
            }
        } catch (ex: IllegalArgumentException) {
            println("Info: TimeSpan does not meet specifications, return the latest data")
        }

        val database = DruidUtil.getDataSource()?.let { source -> Database.connect(source) }
        database?.let { db ->
            //根据时间选择获取数据
            if (startTime == endTime)
                call.respond(getData(db, it.parentRoute.dataClass))
            else
                call.respond(getData(db, it.parentRoute.dataClass, startTime, endTime))
        } ?: call.respondText("Connect failed")
    }

    /**
     * 按起止时间获取数据
     */
    get<GetDataController.Time> {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startTime = LocalDateTime.parse(it.start, dateTimeFormatter)
            val endTime = LocalDateTime.parse(it.end, dateTimeFormatter)

            val database = DruidUtil.getDataSource()?.let { source -> Database.connect(source) }
            database
                ?.let { db ->
                    call.respond(getData(db, it.parentRoute.dataClass, startTime, endTime))
                } ?: call.respondText("Connect failed")
        } catch (ex: DateTimeParseException) {
            call.respondText("DateTimeParse failed")
        }
    }
}