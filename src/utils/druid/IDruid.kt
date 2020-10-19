package com.environmentService.utils.druid

import javax.sql.DataSource

interface IDruid {
    /**
     * 获取连接异常信息
     */
    var exception: Exception?

    /**
     * 从连接池获取连接
     * @return datasource 数据源
     */
    fun getDataSource(): DataSource?
}