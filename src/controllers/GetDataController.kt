package com.environmentService.controllers

import com.environmentService.models.getData.DataOptEnum
import com.environmentService.models.getData.IGetData
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent
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
            println("WARN:>>>$ex")
        }

        val scope = KoinJavaComponent.getKoin().createScope("requestScope", named("REQUEST_SCOPE"))
        val service: IGetData = scope.get()
        //根据时间选择获取数据
        if (startTime == endTime)
            call.respond(service.getData(it.parentRoute.dataClass))
        else
            call.respond(service.getData(it.parentRoute.dataClass, startTime, endTime))
        scope.close()
    }

    /**
     * 按起止时间获取数据
     */
    get<GetDataController.Time> {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startTime = LocalDateTime.parse(it.start, dateTimeFormatter)
            val endTime = LocalDateTime.parse(it.end, dateTimeFormatter)

            val scope = KoinJavaComponent.getKoin().createScope("requestScope", named("REQUEST_SCOPE"))
            val service: IGetData = scope.get()
            //根据时间选择获取数据
            call.respond(service.getData(it.parentRoute.dataClass, startTime, endTime))
            scope.close()
        } catch (ex: DateTimeParseException) {
            println("WARN:>>>$ex")
            call.respondText("DateTimeParse failed")
        }
    }
}