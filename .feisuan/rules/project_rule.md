
# 开发规范指南

为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、项目环境与技术栈

### 1. 基础信息
- **作者**：zyj17
- **工作目录**：`D:\javalearn\work`
- **操作系统**：Windows 11
- **构建工具**：Maven
- **注释语言**：中文（用户第一语言）

### 2. 技术栈要求
- **主框架**：Spring Boot 3.2.12
- **语言版本**：JDK 17
- **数据库 ORM**：MyBatis-Plus 3.5.11 (兼容 Spring Boot 3.x)
- **数据库驱动**：MySQL Connector/J (runtime scope)
- **核心依赖**：
  - `spring-boot-starter-web`
  - `mybatis-plus-spring-boot3-starter`
  - `mybatis-plus-jsqlparser`
  - `lombok` (1.18.36)

### 3. 目录结构
项目采用前后端分离结构，后端核心代码位于 `reim-reim-backend` 模块。

```text
work
├── reim-reim-backend          # 后端服务模块
│   ├── .feisuan               # 内部工具/规则目录
│   └── src
│       ├── main
│       │   ├── java
│       │   │   └── com
│       │   │       └── viessmart
│       │   │           └── reimburse
│       │   │               ├── common       # 通用工具、异常处理、常量
│       │   │               ├── config       # 配置类（MyBatisPlus配置、Web配置等）
│       │   │               ├── controller   # 控制层，处理HTTP请求
│       │   │               ├── dto          # 数据传输对象
│       │   │               ├── entity       # 数据库实体映射
│       │   │               ├── interceptor  # 拦截器
│       │   │               ├── mapper       # MyBatis Mapper接口
│       │   │               ├── service      # 业务逻辑接口
│       │   │               │   └── impl     # 业务逻辑实现
│       │   │               ├── tools        # 第三方工具集成
│       │   │               └── vo           # 视图展示对象
│       │   └── resources
│       │       ├── mapper       # MyBatis XML映射文件
│       │       └── sql          # SQL脚本
│       └── test                 # 测试代码
└── reim-reim-frontend           # 前端模块 (Vue/React等)
    ├── public
    └── src
        ├── api                  # 接口请求封装
        ├── assets               # 静态资源
        ├── components           # 公共组件
        ├── router               # 路由配置
        ├── stores               # 状态管理
        ├── utils                # 前端工具函数
        └── views                # 页面视图
```

## 二、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 保持轻量，仅负责参数校验与结果封装；不得包含业务逻辑             |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 必须通过 Mapper 层访问数据库；返回 DTO 而非 Entity               |
| **Mapper**     | 数据库访问与持久化操作             | 继承 `BaseMapper<T>`；复杂查询使用 XML 或 Wrapper，避免 N+1 查询 |
| **Entity**     | 映射数据库表结构                   | 不得直接返回给前端；包名统一为 `entity`                          |

### 接口与实现分离

- 所有 Service 接口实现类需放在接口所在包下的 `impl` 子包中（如 `service.impl`）。

## 三、安全与性能规范

### 输入校验

- 使用 `@Valid` 与 JSR-303 校验注解（如 `@NotBlank`, `@Size` 等）。
  - 注意：Spring Boot 3.x 中校验注解位于 `jakarta.validation.constraints.*`。
- 禁止手动拼接 SQL 字符串，防止 SQL 注入攻击。

### 事务管理

- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，影响性能。

### 数据库规范 (MyBatis-Plus)

- 启用逻辑删除：实体类中需包含 `deleted` 字段，并在配置中设置 `logic-delete-field`。
- 驼峰转换：确保配置 `map-underscore-to-camel-case: true`。
- 分页插件：确保引入 `mybatis-plus-jsqlparser` 依赖以支持分页功能。

## 四、代码风格规范

### 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `ReimburseServiceImpl`|
| 方法/变量  | lowerCamelCase       | `saveReimburse()`     |
| 常量       | UPPER_SNAKE_CASE     | `MAX_REIMBURSE_AMOUNT`|

### 注释规范

- **所有类、方法、字段必须添加中文注释**。
- 类级别注释需说明该类的作用。
- 方法级别注释需说明功能、参数含义及返回值。
- 复杂逻辑或业务规则需添加行内注释。

### 类型命名规范（阿里巴巴风格）

| 后缀 | 用途说明                     | 示例         |
|------|------------------------------|--------------|
| DTO  | 数据传输对象                 | `ReimburseDTO`    |
| DO   | 数据库实体对象（或Entity）   | `ReimburseDO`     |
| BO   | 业务逻辑封装对象             | `ReimburseBO`     |
| VO   | 视图展示对象                 | `ReimburseVO`     |
| Query| 查询参数封装对象             | `ReimburseQuery`  |

### 实体类简化工具

- 使用 Lombok 注解替代手动编写 getter/setter/构造方法：
  - `@Data`
  - `@NoArgsConstructor`
  - `@AllArgsConstructor`
  - `@TableName` (MyBatis-Plus 注解，指定表名)

## 五、扩展性与日志规范

### 接口优先原则

- 所有业务逻辑通过接口定义（如 `ReimburseService`），具体实现放在 `impl` 包中（如 `ReimburseServiceImpl`）。

### 日志记录

- 使用 `@Slf4j` 注解代替 `System.out.println`。
- 关键业务节点、异常捕获处需记录日志，级别根据场景选择 `DEBUG`, `INFO`, `ERROR`。
- 配置文件中已设置 `com.viessmart.reimburse` 包日志级别为 `DEBUG`，生产环境建议调整为 `INFO`。

## 六、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
