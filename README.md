# Environment

### 框架
- Ktor Web框架
- Koin 依赖注入
- Ktorm ORM框架
- Druid(Ali) 数据库连接池

### 配置文件
```properties
# resources路径下druid.properties
# 数据库连接参数
url=jdbc:mysql://[ip]:[port]/[DBName]
username=***
password=***
# 连接池的参数
initialSize=10
maxActive=10
maxWait=2000
```

### 不再使用
- ktorm 数据类需要为interface，否则无法使用实体序列api
- 数据类为interface时，ktor返回序列化的数据对象不方便
- ktorm操作繁琐，仅保留用于学习
- 查看[EnvironmentApi--.NetCore](https://github.com/NullObjects/EnvironmentApi.git)