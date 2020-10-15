package com.environmentService.controllers

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
import org.ktorm.entity.forEach
import org.ktorm.entity.sortedByDescending
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * environment路由
 */
@KtorExperimentalLocationsAPI
@Location("/environment")
class EnvironmentController() {
    @Location("/{span}")
    data class Span(val env: EnvironmentController, val span: String = "last")

    @Location("/{start}/{end}")
    data class Time(val env: EnvironmentController, val start: String, val end: String)
}

/**
 * environment路由
 */
@KtorExperimentalLocationsAPI
fun Route.environment() {
    /**
     * 获取最新一条数据
     * @param database 数据所在数据库
     * @return Map<String, Any> 最新数据 或错误信息
     */
    fun getData(database: Database) =
        try {
            val env = database.Environment.sortedByDescending { it.recordTime }.first()
            mapOf(
                "id" to env.id,
                "temperature" to env.temperature,
                "humidity" to env.humidity,
                "recordTime" to env.recordTime.toString()
            )
        } catch (ex: Exception) {
            mapOf("getData failed" to ex.message)
        }

    /**
     * 获取一段时间内数据
     * @param database 数据所在数据库
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return MutableList<Map<String, Any>> 一段时间内数据列表 或错误信息
     */
    fun getData(database: Database, startTime: LocalDateTime, endTime: LocalDateTime) =
        try {
            val envList = mutableListOf<Map<String, Any>>()
            database.Environment.filter { it.recordTime.between(startTime..endTime) }
                .forEach {
                    envList.add(
                        mapOf(
                            "id" to it.id,
                            "temperature" to it.temperature,
                            "humidity" to it.humidity,
                            "recordTime" to it.recordTime.toString()
                        )
                    )
                }
            envList
        } catch (ex: Exception) {
            mutableListOf(mapOf("getData failed" to ex.message))
        }

    /**
     * 按时间段获取数据
     */
    get<EnvironmentController.Span> {
        val endTime = LocalDateTime.now()
        //根据时间段获取起始时间
        val startTime = when (it.span) {
            "hour" -> endTime.minusHours(1)
            "day" -> endTime.minusDays(1)
            "week" -> endTime.minusDays(7)
            "month" -> endTime.minusMonths(1)
            else -> endTime
        }

        val database = DruidUtil.getDataSource()?.let { source -> Database.connect(source) }
        database?.let {
            //根据时间选择获取数据
            if (startTime == endTime)
                call.respond(getData(database))
            else
                call.respond(getData(database, startTime, endTime))
        } ?: call.respondText("Connect failed")
    }

    /**
     * 按起止时间获取数据
     */
    get<EnvironmentController.Time> {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startTime = LocalDateTime.parse(it.start, dateTimeFormatter)
            val endTime = LocalDateTime.parse(it.end, dateTimeFormatter)

            val database = DruidUtil.getDataSource()?.let { source -> Database.connect(source) }
            database
                ?.let { call.respond(getData(database, startTime, endTime)) }
                ?: call.respondText("Connect failed")
        } catch (ex: DateTimeParseException) {
            call.respondText("DateTimeParse failed")
        }
    }
}