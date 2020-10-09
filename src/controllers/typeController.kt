package com.environmentService.controllers

import com.environmentService.HelloService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

@KtorExperimentalLocationsAPI
@Location("/type/{name}")
data class Type(val name: String) {
    @Location("/edit")
    data class Edit(val type: Type)

    @Location("/list/{page}")
    data class List(val type: Type, val page: Int)
}

@KtorExperimentalLocationsAPI
fun Route.type() {
    val service: HelloService by inject()

    get<Type.Edit> {
        call.respondText("Inside $it, ${service.sayHello()}")
    }

    get<Type.List> {
        call.respondText("Inside $it, ${service.sayHello()}")
    }
}