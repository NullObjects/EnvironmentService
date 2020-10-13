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
import java.time.LocalTime

/**
 * environment路由
 */
@KtorExperimentalLocationsAPI
@Location("/environment")
class EnvironmentController() {
    @Location("/{span}")
    data class Span(val env: EnvironmentController, val span: String = "last")

    @Location("/{start}&&{end}")
    data class Time(val env: EnvironmentController, val start: LocalTime, val end: LocalTime)
}

/**
 * environment路由
 */
@KtorExperimentalLocationsAPI
fun Route.environment() {
    /**
     * 按时间段获取数据
     */
    get<EnvironmentController.Span> {
        var startTime = LocalDateTime.now()
        val endTime = LocalDateTime.now()
        //根据时间段获取起始时间
        startTime = when (it.span) {
            "hour" -> endTime.minusHours(1)
            "day" -> endTime.minusDays(1)
            "week" -> endTime.minusDays(7)
            "month" -> endTime.minusMonths(1)
            else -> endTime
        }
        val table = DruidUtil.getDataSource()?.let { source -> Database.connect(source).Environment }
        if (startTime != endTime) {
            //起止时间不同，返回一段时间内的数据
            table?.let { tab ->
                val envList = mutableListOf<Map<String, Any>>()
                tab.filter { items -> items.recordTime.between(startTime..endTime) }
                    .forEach { item ->
                        envList.add(
                            mapOf(
                                "id" to item.id,
                                "temperature" to item.temperature,
                                "humidity" to item.humidity,
                                "recordTime" to item.recordTime.toString()
                            )
                        )
                    }
                call.respond(envList)
            } ?: call.respondText("get data failed")
        } else {
            //起止时间相同，返回最新一条数据
            table?.let { tab ->
                val env = tab.sortedByDescending { items -> items.id }.first()
                call.respond(
                    mapOf(
                        "id" to env.id,
                        "temperature" to env.temperature,
                        "humidity" to env.humidity,
                        "recordTime" to env.recordTime.toString()
                    )
                )
            } ?: call.respondText("get data failed")
        }

//    get<Environment.Time> {
//
//    }
    }
}