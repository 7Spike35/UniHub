# UniHub - 大学生交流平台

## 项目简介

UniHub是一个基于SpringBoot开发的大学生交流平台，旨在为大学生提供一个安全、便捷的交流环境。

## 技术栈

- **后端框架**: Spring Boot 3.5.3
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security
- **构建工具**: Maven
- **Java版本**: JDK 17

## 项目结构

```
src/main/java/com/unihub/unihub/
├── common/                    # 公共组件
│   ├── config/               # 配置类
│   ├── exception/            # 异常处理
│   │   └── GlobalExceptionHandler.java
│   ├── utils/                # 工具类
│   └── response/             # 统一响应格式
│       └── ApiResponse.java
├── user/                     # 用户管理模块
│   ├── controller/           # 控制器层
│   │   └── UserController.java
│   ├── service/              # 业务逻辑层
│   │   ├── UserService.java
│   │   └── impl/
│   │       └── UserServiceImpl.java
│   ├── repository/           # 数据访问层
│   │   └── UserRepository.java
│   ├── entity/               # 实体类
│   │   ├── User.java
│   │   ├── UserStatus.java
│   │   └── UserRole.java
│   ├── dto/                  # 数据传输对象
│   │   ├── UserRegisterDto.java
│   │   ├── UserLoginDto.java
│   │   └── UserUpdateDto.java
│   └── vo/                   # 视图对象
│       └── UserVo.java
├── security/                 # 安全模块
│   └── config/
│       └── SecurityConfig.java
└── UniHubApplication.java    # 启动类
```

## 功能模块

### 1. 用户管理模块 (已完成)

#### 核心功能
- **用户注册**: 支持用户名、邮箱、学号唯一性验证
- **用户登录**: 基于Spring Security的认证机制
- **用户信息管理**: 查看、更新个人信息
- **密码管理**: 修改密码、重置密码
- **用户状态管理**: 活跃、非活跃、封禁、待审核
- **用户角色管理**: 学生、教师、管理员、版主
- **用户搜索**: 支持多字段模糊搜索
- **用户统计**: 用户数量统计、状态分布统计

#### API接口

##### 用户注册
```
POST /api/users/register
Content-Type: application/json

{
  "username": "student001",
  "password": "123456",
  "confirmPassword": "123456",
  "email": "student001@example.com",
  "realName": "张三",
  "studentId": "2021001",
  "phoneNumber": "13800138000",
  "university": "清华大学",
  "major": "计算机科学与技术",
  "grade": "2021级"
}
```

##### 用户登录
```
POST /api/users/login
Content-Type: application/json

{
  "username": "student001",
  "password": "123456"
}
```

##### 获取用户信息
```
GET /api/users/{id}
```

##### 更新用户信息
```
PUT /api/users/{id}
Content-Type: application/json

{
  "realName": "张三",
  "phoneNumber": "13800138000",
  "university": "清华大学",
  "major": "计算机科学与技术",
  "grade": "2021级"
}
```

##### 用户列表查询
```
GET /api/users/list?status=ACTIVE&page=0&size=10&sortBy=id&sortDir=desc
```

##### 用户搜索
```
GET /api/users/search?status=ACTIVE&keyword=张三&page=0&size=10
```

### 2. 论坛模块 (待开发)
- 帖子发布、编辑、删除
- 评论系统
- 点赞、收藏功能
- 分类管理
- 热门帖子推荐

### 3. 私信模块 (待开发)
- 一对一私信
- 群组聊天
- 消息历史记录
- 在线状态显示

### 4. 管理模块 (待开发)
- 用户管理
- 内容审核
- 系统配置
- 数据统计

## 数据库设计

### 用户表 (users)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键ID |
| username | VARCHAR(50) | 用户名(唯一) |
| password | VARCHAR(100) | 密码(加密) |
| email | VARCHAR(100) | 邮箱(唯一) |
| real_name | VARCHAR(50) | 真实姓名 |
| student_id | VARCHAR(20) | 学号(唯一) |
| phone_number | VARCHAR(20) | 手机号 |
| avatar_url | VARCHAR(255) | 头像URL |
| university | VARCHAR(100) | 大学 |
| major | VARCHAR(100) | 专业 |
| grade | VARCHAR(20) | 年级 |
| user_status | ENUM | 用户状态 |
| user_role | ENUM | 用户角色 |
| created_time | DATETIME | 创建时间 |
| updated_time | DATETIME | 更新时间 |
| last_login_time | DATETIME | 最后登录时间 |

## 运行说明

### 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 启动步骤

1. **创建数据库**
```sql
CREATE DATABASE unihub_user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. **修改数据库配置**
编辑 `src/main/resources/application.properties` 文件，修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/unihub_user_db?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false
spring.datasource.username=你的数据库用户名
spring.datasource.password=你的数据库密码
```

3. **启动应用**
```bash
mvn spring-boot:run
```

4. **访问应用**
- 应用地址: http://localhost:8000
- API文档: 可通过Postman等工具测试API接口

## 开发规范

### 包结构规范
- 按功能模块分包
- 每层职责清晰，避免跨层调用
- 统一命名规范

### 代码规范
- 遵循Java编码规范
- 使用有意义的变量和方法名
- 添加必要的注释
- 统一异常处理

### API设计规范
- RESTful API设计
- 统一响应格式
- 合理的HTTP状态码
- 完整的参数验证

## 后续开发计划

1. **完善用户管理模块**
   - 添加JWT认证
   - 实现邮箱验证
   - 添加头像上传功能

2. **开发论坛模块**
   - 帖子管理
   - 评论系统
   - 分类管理

3. **开发私信模块**
   - 即时通讯
   - 消息管理

4. **开发管理模块**
   - 后台管理
   - 数据统计

5. **系统优化**
   - 性能优化
   - 安全加固
   - 日志完善

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

本项目采用MIT许可证。 