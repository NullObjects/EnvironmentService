package com.environmentService.controllers

import com.environmentService.models.EnvironmentDB
import com.environmentService.utils.DruidUtil
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.ktorm.database.Database
import org.ktorm.dsl.between
import org.ktorm.entity.*
import java.time.*

@KtorExperimentalLocationsAPI
@Location("/environment")
class Environment() {
    @Location("/{span}")
    data class Span(val env: Environment, val span: String = "last")

    @Location("/{start}&&{end}")
    data class Time(val env: Environment, val start: LocalTime, val end: LocalTime)
}

/**
 * environment路由
 */
@KtorExperimentalLocationsAPI
fun Route.environment() {
    /**
     * 按时间段获取数据
     */
    get<Environment.Span> {
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
        val database = DruidUtil.getDataSource()?.let { x -> Database.connect(x).EnvironmentDB }
        if (startTime != endTime) {
            database?.let { x ->
                val env = x.filter { y -> y.recordTime.between(startTime..endTime) }
                val envList = mutableListOf<Map<String, Any>>()
                env.forEach { z ->
                    envList.add(
                        mapOf(
                            "id" to z.id,
                            "temperature" to z.temperature,
                            "humidity" to z.humidity,
                            "recordTime" to z.recordTime.toString()
                        )
                    )
                }
                call.respond(envList)
            }
        } else {
            database?.let { x ->
                val env = x.sortedByDescending { y -> y.id }.first()
                call.respond(
                    mapOf(
                        "id" to env.id,
                        "temperature" to env.temperature,
                        "humidity" to env.humidity,
                        "recordTime" to env.recordTime.toString()
                    )
                )
            }
        }

//    get<Environment.Time> {
//
//    }
    }
}