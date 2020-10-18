package com.environmentService.utils

import com.alibaba.druid.pool.DruidDataSourceFactory
import java.util.*
import javax.sql.DataSource

interface IDruid {
    /**
     * 从连接池获取连接
     * @return datasource 数据源
     */
    fun getDataSource(): DataSource?
}

/**
 * Ali Druid连接池工具类
 */
class DruidUtil : IDruid {
    private var dataSource: DataSource? = null

    init {
        try {
            //创建Properties对象
            val properties = Properties()
            //将配置文件转换成字节输入流
            val stream = DruidUtil::class.java.classLoader.getResourceAsStream("druid.properties")
            //使用properties对象加载is
            properties.load(stream)
            //druid底层是使用的工厂设计模式，去加载配置文件，创建DruidDataSource对象
            dataSource = DruidDataSourceFactory.createDataSource(properties)
        } catch (ex:Exception) {
            ex.printStackTrace()
        }
    }

    override fun getDataSource(): DataSource? {
        return dataSource
    }
}