# mybig-event

基于 Spring Boot 的事件管理系统，提供用户管理、文章发布、分类管理、文件上传等功能，采用现代化的 Java 技术栈构建，支持高效开发和部署。

## 技术栈

- **核心框架**：Spring Boot 3.1.3
- **数据访问**：MyBatis 3.0.0、PageHelper 1.4.7
- **数据库**：MySQL 8.0.33
- **缓存**：Redis
- **认证授权**：JWT（java-jwt 4.4.0）
- **文件存储**：阿里云 OSS（aliyun-sdk-oss 3.15.1）
- **数据校验**：Spring Boot Starter Validation
- **工具类**：Lombok、MD5 加密
- **构建工具**：Maven
- **开发工具**：IntelliJ IDEA
- **CI/CD**：GitHub Actions

## 功能模块

### 1. 用户管理模块

- 用户注册与登录（支持 JWT 认证）
- 个人信息查询与修改
- 头像上传（对接阿里云 OSS）
- 密码修改与安全验证

### 2. 文章管理模块

- 文章发布与编辑
- 文章分类与状态管理（已发布 / 草稿）
- 文章列表分页查询
- 支持按分类和状态筛选

### 3. 分类管理模块

- 分类的增删改查
- 分类数据校验

### 4. 文件上传模块

- 基于阿里云 OSS 的文件存储
- 支持图片上传并返回访问 URL

## 项目结构

```plaintext
mybig-event/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── itheima/
│   │   │       ├── controller/      # 控制器层
│   │   │       ├── service/         # 服务层
│   │   │       ├── mapper/          # 数据访问层
│   │   │       ├── pojo/            # 实体类
│   │   │       └── utils/           # 工具类
│   │   └── resources/
│   │       ├── application.yml      # 全局配置
│   │       └── com/itheima/mapper/  # MyBatis 映射文件
│   └── test/                        # 测试代码
├── .github/workflows/               # GitHub Actions 配置
├── .idea/                           # IDEA 项目配置
├── pom.xml                          # Maven 依赖配置
└── .gitignore                       # Git 忽略文件配置
```

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 5.0+
- Maven 3.6+

### 配置步骤

1. **克隆仓库**

   ```bash
   git clone https://github.com/foorgange/mybig-event.git
   cd mybig-event
   ```

2. **配置数据库、Redis 与 OSS**

   所有敏感配置均通过**环境变量**注入，`application.yml` 中只保留带默认值的占位符，
   仓库内不存放任何真实凭证。

   | 环境变量 | 说明 | 默认值 |
   |---|---|---|
   | `MYSQL_URL` | MySQL 连接串 | `jdbc:mysql://localhost:3306/big_event` |
   | `MYSQL_USERNAME` | 数据库用户名 | `root` |
   | `MYSQL_PASSWORD` | 数据库密码 | 空 |
   | `REDIS_HOST` | Redis 主机 | `localhost` |
   | `REDIS_PORT` | Redis 端口 | `6379` |
   | `REDIS_PASSWORD` | Redis 密码 | 空 |
   | `OSS_ENDPOINT` | OSS 地域节点 | `https://oss-cn-beijing.aliyuncs.com` |
   | `OSS_ACCESS_KEY_ID` | OSS AccessKeyId | 无（必填） |
   | `OSS_ACCESS_KEY_SECRET` | OSS AccessKeySecret | 无（必填） |
   | `OSS_BUCKET` | OSS Bucket 名称 | `big-event` |

   本地开发可在启动前导出这些变量，例如：

   ```bash
   export OSS_ACCESS_KEY_ID=你的AccessKeyId
   export OSS_ACCESS_KEY_SECRET=你的AccessKeySecret
   export MYSQL_PASSWORD=你的数据库密码
   ```

4. **创建数据库**

   在 MySQL 中创建数据库 `big_event`，并根据实体类创建对应表结构（可通过 MyBatis 逆向工程生成）。

5. **构建与运行**

   ```bash
   mvn clean package
   java -jar target/mybig-event-1.0-SNAPSHOT.jar
   ```

   服务将在 `http://localhost:9090` 启动。

## API 示例

### 用户注册

```bash
POST /user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "testpass123"
}
```

### 文章发布

```bash
POST /article
Content-Type: application/json
Authorization: Bearer {your-jwt-token}

{
  "title": "测试文章",
  "content": "这是一篇测试文章",
  "coverImg": "https://example.com/img.jpg",
  "state": "已发布",
  "categoryId": 1
}
```

## 开发指南

1. **代码规范**：遵循阿里巴巴 Java 开发手册
2. **提交规范**：使用语义化提交信息（如 `feat: 新增文章查询接口`）
3. **测试要求**：核心功能需编写单元测试
4. **分支管理**：使用 `master` 作为主分支，功能开发使用 feature 分支

## 自动化构建

项目配置了 GitHub Actions 自动化构建流程，当代码推送到 `master` 分支或创建 Pull Request 时，将自动执行以下操作：

1. 检出代码并配置 JDK 17
2. 执行 `mvn -B package`，编译主代码与测试代码
3. 运行单元测试（测试不依赖外部 MySQL / Redis 服务，可在 CI 环境独立执行）
4. 生成依赖报告（该步骤失败不阻塞构建）

## 许可证

本项目未附带 LICENSE 文件，如未另行声明则默认保留所有权利。
