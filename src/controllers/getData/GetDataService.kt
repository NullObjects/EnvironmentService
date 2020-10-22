package com.environmentService.controllers.getData

import com.environmentService.models.getData.DataClassEnum
import com.environmentService.models.getData.Device
import com.environmentService.models.getData.Environment
import com.environmentService.utils.druid.IDruid
import org.ktorm.database.Database
import org.ktorm.dsl.between
import org.ktorm.entity.filter
import org.ktorm.entity.first
import org.ktorm.entity.map
import org.ktorm.entity.sortedByDescending
import java.time.LocalDateTime

class GetDataService(druid: IDruid) : IGetData {
    /**
     * 数据库连接
     */
    private val database = druid.getDataSource()
        ?.let {
            try {
                Database.connect(it)
            } catch (ex: Exception) {
                null
            }
        }

    override fun getData(dataClass: String) =
        try {
            database ?: throw Exception("Connect failed")
            when (DataClassEnum.valueOf(dataClass.toUpperCase())) {
                //获取不同类型信息
                DataClassEnum.ENVIRONMENT -> database.Environment.sortedByDescending { it.recordTime }.first()
                    .properties.mapValues {
                        //替换时间为字符串
                        if (it.value is LocalDateTime)
                            it.value.toString()
                        else
                            it.value
                    }
                DataClassEnum.DEVICE -> database.Device.sortedByDescending { it.recordTime }.first()
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
                //获取不同类型信息
                DataClassEnum.ENVIRONMENT -> database.Environment.filter { it.recordTime.between(startTime..endTime) }
                    .map {
                        it.properties.mapValues { entry ->
                            //替换时间为字符串
                            if (entry.value is LocalDateTime)
                                entry.value.toString()
                            else
                                entry.value
                        }
                    }
                DataClassEnum.DEVICE -> database.Device.filter { it.recordTime.between(startTime..endTime) }
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