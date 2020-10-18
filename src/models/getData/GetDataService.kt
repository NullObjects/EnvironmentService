package com.environmentService.models.getData

import com.environmentService.utils.IDruid
import org.ktorm.database.Database
import org.ktorm.dsl.between
import org.ktorm.entity.filter
import org.ktorm.entity.first
import org.ktorm.entity.map
import org.ktorm.entity.sortedByDescending
import java.time.LocalDateTime

interface IGetData {
    /**
     * 获取最新一条数据
     * @param dataClass 待获取数据类别
     * @return Map<String, Any?> 最新数据 或错误信息
     */
    fun getData(dataClass: String): Map<String, Any?>

    /**
     * 获取一段时间内数据
     * @param dataClass 待获取数据类别
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return List<Map<String, Any?>> 一段时间内数据列表 或错误信息
     */
    fun getData(dataClass: String, startTime: LocalDateTime, endTime: LocalDateTime): List<Map<String, Any?>>
}

class GetDataService(druid: IDruid) : IGetData {
    private val database = druid.getDataSource()
        ?.let { Database.connect(it) }

    override fun getData(dataClass: String) =
        try {
            database ?: throw Exception("Connect failed")
            when (DataClassEnum.valueOf(dataClass.toUpperCase())) {
                DataClassEnum.ENVIRONMENT -> database.Environment.sortedByDescending { Environments.recordTime }.first()
                    .properties.mapValues {
                        //替换时间为字符串
                        if (it.value is LocalDateTime)
                            it.value.toString()
                        else
                            it.value
                    }
                DataClassEnum.DEVICE -> database.Device.sortedByDescending { Devices.recordTime }.first()
                    .properties.mapValues {
                        //替换时间为字符串
                        if (it.value is LocalDateTime)
                            it.value.toString()
                        else
                            it.value
                    }
            }
        } catch (ex: Exception) {
            println("WARN:>>>$ex")
            mapOf("getData failed" to ex.message)
        }

    override fun getData(dataClass: String, startTime: LocalDateTime, endTime: LocalDateTime) =
        try {
            database ?: throw Exception("Connect failed")
            when (DataClassEnum.valueOf(dataClass.toUpperCase())) {
                DataClassEnum.ENVIRONMENT -> database.Environment.filter { Environments.recordTime.between(startTime..endTime) }
                    .map {
                        it.properties.mapValues { entry ->
                            //替换时间为字符串
                            if (entry.value is LocalDateTime)
                                entry.value.toString()
                            else
                                entry.value
                        }
                    }
                DataClassEnum.DEVICE -> database.Device.filter { Devices.recordTime.between(startTime..endTime) }
                    .map {
                        it.properties.mapValues { entry ->
                            //替换时间为字符串
                            if (entry.value is LocalDateTime)
                                entry.value.toString()
                            else
                                entry.value
                        }
                    }
            }
        } catch (ex: Exception) {
            println("WARN:>>>$ex")
            listOf(mapOf("getData failed" to ex.message))
        }
}