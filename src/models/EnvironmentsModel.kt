package com.environmentService.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import java.time.LocalDateTime

interface Environment : Entity<Environment> {
    companion object : Entity.Factory<Environment>()
    val id:Int
    val temperature:Float
    val humidity:Float
    val recordTime:LocalDateTime
}

object Environments : Table<Environment>("Environment") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val temperature = float("Temperature").bindTo { it.temperature }
    val humidity = float("Humidity").bindTo { it.humidity }
    val recordTime = datetime("RecordTime").bindTo { it.recordTime }
}

val Database.Environements get()=this.sequenceOf(Environments)