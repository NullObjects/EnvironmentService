package com.environmentService.models

import org.ktorm.schema.*

object Environments : Table<Nothing>("Environment") {
    val id = int("ID").primaryKey()
    val temperature = varchar("Temperature")
    val humidity = varchar("Humidity")
    val recordTime = varchar("RecordTime")
}