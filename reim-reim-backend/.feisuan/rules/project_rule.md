
# 开发规范指南

本项目为报销系统后端服务，基于 Spring Boot 3.x 与 MyBatis-Plus 构建。为保证代码质量、可维护性、安全性与可扩展性，请在开发过程中严格遵循以下规范。

## 一、基础环境信息

- **操作系统**：Windows 11
- **工作目录**：`D:\javalearn\work\reim-reim-backend`
- **构建工具**：Maven
- **JDK 版本**：17.0.12
- **Spring Boot 版本**：3.2.12
- **核心框架**：MyBatis-Plus 3.5.11
- **代码作者**：zyj17
- **第一语言**：中文（所有注释、文档请使用中文）

## 二、项目目录结构

请严格遵循以下目录结构进行文件组织：

```text
reim-reim-backend
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── viessmart
    │   │           └── reimburse
    │   │               ├── common          # 公共常量、枚举、异常定义
    │   │               ├── config          # 配置类（MyBatisPlus配置、拦截器等）
    │   │               ├── controller      # 控制器层，处理HTTP请求
    │   │               ├── dto             # 数据传输对象（入参）
    │   │               ├── entity          # 数据库实体映射
    │   │               ├── interceptor     # 拦截器（如登录校验、权限校验）
    │   │               ├── mapper          # MyBatis-Plus Mapper接口
    │   │               ├── service         # 服务接口层
    │   │               │   └── impl        # 服务实现层
    │   │               ├── tools           # 工具类
    │   │               └── vo              # 视图展示对象（出参）
    │   └── resources
    │       ├── mapper                      # MyBatis XML映射文件
    │       └── sql                         # 初始化SQL脚本
    └── test
        └── java
            └── com
                └── viessmart
                    └── reimburse
```

## 三、技术栈与依赖规范

### 1. 核心依赖
- **ORM 框架**：MyBatis-Plus (`mybatis-plus-spring-boot3-starter` 3.5.11)
- **数据库驱动**：MySQL Connector/J
- **代码简化**：Lombok 1.18.36
- **SQL 解析**：MyBatis-Plus JSqlParser

### 2. MyBatis-Plus 配置规范
- **包扫描**：实体类包名为 `com.viessmart.reimburse.entity`
- **映射文件**：XML 文件存放于 `classpath:/mapper/**/*.xml`
- **全局配置**：
  - 主键生成策略：自增 (`id-type: auto`)
  - 逻辑删除字段：`deleted` (0:未删除, 1:已删除)
  - 下划线转驼峰：开启 (`map-underscore-to-camel-case: true`)
  - 缓存：关闭 (`cache-enabled: false`)

## 四、分层架构规范

| 层级        | 职责说明                         | 开发约束与注意事项                                               |
|-------------|----------------------------------|----------------------------------------------------------------|
| **Controller** | 处理 HTTP 请求与响应，定义 API 接口 | 保持轻量，仅负责参数接收、校验及结果返回，**不得包含业务逻辑** |
| **Service**    | 实现业务逻辑、事务管理与数据校验   | 接口与实现分离；必须通过 Mapper 层访问数据库；返回 DTO/VO 而非 Entity |
| **Mapper**     | 数据访问层（MyBatis-Plus）         | 继承 `BaseMapper<T>`；复杂查询使用 XML 或 Wrapper，避免 N+1 查询 |
| **Entity**     | 映射数据库表结构                   | 使用 `@TableName` 指定表名；使用 `@TableLogic` 标识逻辑删除字段；**严禁直接返回给前端** |
| **DTO/VO**     | 数据传输/视图展示对象              | Controller 与 Service 之间使用 DTO；Service 与前端之间使用 VO |

### 接口与实现分离
- 所有 Service 接口需放在 `service` 包下。
- 具体实现类需放在 `service.impl` 子包中，命名规则为 `接口名 + Impl`（如 `UserServiceImpl`）。

## 五、安全与性能规范

### 1. 输入校验
- 使用 `@Valid` 或 `@Validated` 进行参数校验。
- 使用 JSR-303 校验注解（如 `@NotBlank`, `@Size`, `@Email` 等）。
  - **注意**：Spring Boot 3.x 中校验注解位于 `jakarta.validation.constraints.*` 包下。

### 2. SQL 安全
- **禁止**手动拼接 SQL 字符串，防止 SQL 注入攻击。
- 使用 MyBatis-Plus 提供的 `QueryWrapper` 或 XML 中的 `<if>` 标签进行动态条件拼接。
- 敏感数据（如密码）在数据库中必须加密存储，严禁明文传输或展示。

### 3. 事务管理
- `@Transactional` 注解仅用于 **Service 层**方法。
- 避免在循环中频繁提交事务，若涉及批量操作，建议分批处理或关闭自动提交后手动批量提交。

### 4. 日志记录
- 使用 `@Slf4j` 注解注入日志对象。
- 日志级别规范：
  - `ERROR`：系统错误、异常堆栈。
  - `WARN`：警告信息、非预期但可恢复的情况。
  - `INFO`：关键业务流程节点（如接口入口、出口、状态变更）。
  - `DEBUG`：详细调试信息（生产环境建议关闭或仅开启特定包）。
- 禁止使用 `System.out.println` 进行日志输出。

## 六、代码风格规范

### 1. 命名规范

| 类型       | 命名方式             | 示例                  |
|------------|----------------------|-----------------------|
| 类名       | UpperCamelCase       | `ReimbursementServiceImpl` |
| 方法/变量  | lowerCamelCase       | `getReimbursementList()` |
| 常量       | UPPER_SNAKE_CASE     | `MAX_REIMBURSE_AMOUNT`  |
| 包名       | 全小写               | `com.viessmart.reimburse` |

### 2. 注释规范
- **语言要求**：所有类、方法、字段必须添加 **中文** 注释。
- **类注释**：说明类的职责、作者、创建日期。
- **方法注释**：说明方法功能、参数含义、返回值、异常信息。
- **字段注释**：实体类字段需对应数据库字段含义，DTO/VO 字段需说明业务含义。
- **示例**：
  ```java
  /**
   * 报销服务实现类
   *
   * @author zyj17
   * @since 2026-05-24
   */
  @Service
  public class ReimbursementServiceImpl implements ReimbursementService {

      /**
       * 根据ID查询报销单详情
       *
       * @param id 报销单ID
       * @return 报销单详情VO
       */
      @Override
      public ReimbursementVO getDetail(Long id) {
          // ...
      }
  }
  ```

### 3. 类型命名规范

| 后缀 | 用途说明                     | 示例             | 存放包         |
|------|------------------------------|------------------|----------------|
| DTO  | 数据传输对象（通常用于接收前端参数） | `ReimbursementDTO` | `dto`          |
| VO   | 视图展示对象（通常用于返回前端数据） | `ReimbursementVO`  | `vo`           |
| Entity | 数据库实体对象               | `Reimbursement`    | `entity`       |
| Query| 查询参数封装对象             | `ReimbursementQuery` | `dto` 或单独 `query` |

### 4. 实体类简化工具
- 使用 Lombok 注解替代手动编写 getter/setter/toString 等方法：
  - `@Data`：包含 getter, setter, toString, equals, hashCode
  - `@NoArgsConstructor`：无参构造
  - `@AllArgsConstructor`：全参构造
  - `@Builder`：构建者模式（可选，视团队习惯而定）

## 七、扩展性与日志规范

### 1. 接口优先原则
- 所有业务逻辑通过接口定义（如 `ReimbursementService`），具体实现放在 `impl` 包中（如 `ReimbursementServiceImpl`）。
- 便于后续单元测试 Mock 及未来功能扩展。

### 2. 拦截器规范
- 登录校验、权限校验等通用逻辑应放在 `interceptor` 包下。
- 实现 `HandlerInterceptor` 接口，并在 `WebMvcConfigurer` 中注册。

## 八、编码原则总结

| 原则       | 说明                                       |
|------------|--------------------------------------------|
| **SOLID**  | 高内聚、低耦合，增强可维护性与可扩展性     |
| **DRY**    | 避免重复代码，提高复用性                   |
| **KISS**   | 保持代码简洁易懂                           |
| **YAGNI**  | 不实现当前不需要的功能                     |
| **OWASP**  | 防范常见安全漏洞，如 SQL 注入、XSS 等      |
| **Chinese**| 所有注释、文档、日志描述使用中文           |

## 九、其他注意事项

1. **配置文件安全**：`application.yml` 中的数据库密码等敏感信息在提交代码前务必脱敏或使用环境变量注入，严禁硬编码提交到版本控制系统。
2. **依赖版本**：所有第三方依赖版本应统一管理，建议通过 Maven 的 `<properties>` 标签定义版本号。
3. **代码提交**：提交信息应清晰描述修改内容，遵循约定式提交规范（如 `feat:`, `fix:`, `docs:` 等）。
