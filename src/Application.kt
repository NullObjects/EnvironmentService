package com.environmentService

import com.environmentService.controllers.*
import com.environmentService.models.*
import com.environmentService.models.Environments.id
import com.environmentService.utils.*
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.ext.Koin
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.filter
import org.ktorm.entity.toList

/**
 * 程序入口，启动netty服务器
 */
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

/**
 * 模块扩展方法
 * 在application.conf中加载
 */
@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        header("MyCustomHeader")
        allowCredentials = true
        anyHost() // Don't do this in production if possible. Try to limit it.
    }

    install(Authentication) {
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    //安装使用Koin 依赖注入
    val helloAppModule = module {
        scope(named("REQUEST_SCOPE")) {
            scoped { HelloRepository() }
        }
    }
    install(Koin) {
        modules(helloAppModule)
    }

//    DSL
//    DruidUtil.getDataSource()?.let {
//        for (row in Database.connect(it)
//            .from(Environments).select().limit(0, 20)) {
//            println(row[Environments.recordTime])
//        }
//    }

    //加载Location路由
    routing {
        get("/") {
            val database = DruidUtil.getDataSource()?.let { Database.connect(it).Environements }
            database?.let {
                call.respond(mapOf("count" to it.toList().count()))
            } ?: call.respondText("connect failed")
        }
        location()
        type()
    }
}