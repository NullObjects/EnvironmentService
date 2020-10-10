package com.environmentService.controllers

import com.environmentService.HelloService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.getKoin

@KtorExperimentalLocationsAPI
@Location("/location/{name}")
class MyLocation(val name: String, val arg1: Int = 42, val arg2: String = "default")

@KtorExperimentalLocationsAPI
fun Route.location() {
    get<MyLocation> {
        val scope = getKoin().createScope("requestScope", named("REQUEST_SCOPE"))
        val service: HelloService = scope.get()
        call.respondText(
            "Location: name=${it.name}, arg1=${it.arg1}, arg2=${it.arg2}, inf=${service.sayHello()}"
        )
        scope.close()
    }
}