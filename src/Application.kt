package com.environmentService

import com.environmentService.controllers.data
import com.environmentService.controllers.getData.GetDataService
import com.environmentService.controllers.getData.IGetData
import com.environmentService.utils.druid.DruidUtil
import com.environmentService.utils.druid.IDruid
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.routing.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.ext.Koin

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
    val appModule = module {
        single<IDruid> { DruidUtil() }
        scope(named("REQUEST_SCOPE")) {
            scoped<IGetData> { GetDataService(get()) }
        }
    }
    install(Koin) {
        modules(appModule)
    }

    //加载Location路由
    routing {
        data()
    }
}