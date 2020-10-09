package com.environmentService

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HelloRepository {
    val getHello = "Ktor & Koin Time:${
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())
    }"
}

interface HelloService {
    fun sayHello(): String
}

class HelloServiceImpl(private val helloRepository: HelloRepository) : HelloService {
    override fun sayHello() = "Hello ${helloRepository.getHello} !"
}