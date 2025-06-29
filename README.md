# UniHub - 大学学习生活管理平台

UniHub是一个基于Spring Boot + Vue.js的大学学习生活管理平台，提供用户认证、用户管理、个人资料管理等功能。

## 功能特性

### 用户认证系统
- ✅ 用户注册（包含完整的个人信息）
- ✅ 用户登录
- ✅ 基于角色的权限控制
- ✅ 密码修改功能

### 用户管理（管理员功能）
- ✅ 用户列表查看
- ✅ 用户信息编辑
- ✅ 用户状态管理
- ✅ 用户搜索功能
- ✅ 批量操作

### 个人资料管理
- ✅ 个人资料查看和编辑
- ✅ 密码修改
- ✅ 头像显示

### 系统主页
- ✅ 欢迎页面
- ✅ 统计数据展示
- ✅ 快速操作入口
- ✅ 响应式设计

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- Lombok
- Maven

### 前端
- Vue.js 3
- HTML5 + CSS3
- Font Awesome 图标
- 响应式设计

## 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.6+

### 2. 启动应用
```bash
# 克隆项目
git clone <repository-url>
cd UniHub

# 编译并启动
mvn spring-boot:run
```

### 3. 访问应用
- 应用地址: http://localhost:8080
- 登录页面: http://localhost:8080/login
- 系统主页: http://localhost:8080/dashboard
- 用户管理: http://localhost:8080/user-management

### 4. 默认用户
系统预置了以下测试用户：

#### 管理员用户
- 用户名: admin
- 密码: admin123
- 角色: ADMIN

#### 普通用户
- 用户名: user1
- 密码: user123
- 角色: USER

## 页面说明

### 登录注册页面 (/login)
- 支持用户注册和登录
- 表单验证和错误提示
- 响应式设计，支持移动端

### 系统主页 (/dashboard)
- 欢迎信息和统计数据
- 基于角色的导航菜单
- 个人资料管理
- 快速操作入口

### 用户管理页面 (/user-management)
- 用户列表展示
- 用户信息编辑
- 用户状态管理
- 搜索和筛选功能

## 角色权限

### 管理员 (ADMIN)
- 访问用户管理功能
- 管理所有用户信息
- 修改用户状态
- 查看系统统计数据

### 普通用户 (USER)
- 查看和编辑个人资料
- 修改个人密码
- 访问基本功能

## 数据库

项目使用H2内存数据库，启动时会自动创建表结构和初始化数据。

### 主要数据表
- `users`: 用户信息表
- 包含用户基本信息、认证信息、角色状态等

## 开发说明

### 项目结构
```
src/
├── main/
│   ├── java/
│   │   └── com/unihub/unihub/
│   │       ├── common/          # 公共组件
│   │       ├── controller/      # 控制器
│   │       ├── user/           # 用户模块
│   │       └── UniHubApplication.java
│   └── resources/
│       ├── static/             # 静态资源
│       │   ├── css/           # 样式文件
│   │       └── js/            # JavaScript文件
│   ├── templates/         # 页面模板
│   └── application.properties
```

### API接口
- `POST /api/users/register` - 用户注册
- `POST /api/users/login` - 用户登录
- `GET /api/users/profile` - 获取用户资料
- `PUT /api/users/{id}` - 更新用户信息
- `POST /api/users/change-password` - 修改密码
- `GET /api/users` - 获取所有用户
- `GET /api/dashboard/stats` - 获取统计数据

## 部署说明

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package

# 运行
java -jar target/unihub-0.0.1-SNAPSHOT.jar
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请提交 Issue 或联系开发团队。 