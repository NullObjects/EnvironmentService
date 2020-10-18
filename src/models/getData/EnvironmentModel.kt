package com.environmentService.models.getData

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.float
import org.ktorm.schema.int
import java.time.LocalDateTime

/**
 * 环境信息模型
 */
interface EnvironmentModel : Entity<EnvironmentModel> {
    companion object : Entity.Factory<EnvironmentModel>()

    val id: Int
    val temperature: Float
    val humidity: Float
    val recordTime: LocalDateTime
}

/**
 * 环境信息表对象
 */
object Environments : Table<EnvironmentModel>("Environment") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val temperature = float("Temperature").bindTo { it.temperature }
    val humidity = float("Humidity").bindTo { it.humidity }
    val recordTime = datetime("RecordTime").bindTo { it.recordTime }
}

/**
 * Database扩展属性(表)
 */
val Database.Environment get() = this.sequenceOf(Environments)