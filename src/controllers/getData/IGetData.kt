package com.environmentService.controllers.getData

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