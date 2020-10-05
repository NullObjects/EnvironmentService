package com.environmentService.controllers

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

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
    get<Type.Edit> {
        call.respondText("Inside $it")
    }

    get<Type.List> {
        call.respondText("Inside $it")
    }
}