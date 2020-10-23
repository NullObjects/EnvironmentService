# Environment
- [查看.NETCore版](https://github.com/NullObjects/EnvironmentApi)

### 框架
- Ktor Web框架
- Koin 依赖注入
- Ktorm ORM框架
- Druid(Ali) 数据库连接池

### 配置文件
- resources路径下druid.properties
```properties
# 数据库连接参数
url=jdbc:mysql://[ip]:[port]/[DBName]?serverTimezone=GMT%2B8&useTimezone=true
username=***
password=***
# 连接池的参数
initialSize=5
maxActive=10
maxWait=2000
```
