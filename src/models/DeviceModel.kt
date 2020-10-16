package com.environmentService.models

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.datetime
import org.ktorm.schema.float
import org.ktorm.schema.int
import java.time.LocalDateTime

/**
 * 设备信息模型
 */
interface DeviceModel : Entity<DeviceModel> {
    companion object : Entity.Factory<DeviceModel>()

    val id: Int
    val cpuTemperature: Float
    val cpuOccupancyRate: Float
    val ramOccupancyRate: Float
    val sdcardOccupancyRate: Float
    val hddOccupancyRate: Float
    val recordTime: LocalDateTime
}

/**
 * 设备信息表对象
 */
object Devices : Table<DeviceModel>("DeviceStatus") {
    val id = int("ID").primaryKey().bindTo { it.id }
    val cpuTemperature = float("CPUTemperature").bindTo { it.cpuTemperature }
    val cpuOccupancyRate = float("CPUOccupancyRate").bindTo { it.cpuOccupancyRate }
    val ramOccupancyRate = float("RAMOccupancyRate").bindTo { it.ramOccupancyRate }
    val sdcardOccupancyRate = float("SDCardOccupancyRate").bindTo { it.sdcardOccupancyRate }
    val hddOccupancyRate = float("HDDOccupancyRate").bindTo { it.hddOccupancyRate }
    val recordTime = datetime("RecordTime").bindTo { it.recordTime }
}

/**
 * Database扩展属性(表)
 */
val Database.Device get() = this.sequenceOf(Devices)