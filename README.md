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