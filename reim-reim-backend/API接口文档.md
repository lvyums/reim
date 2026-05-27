# 报销管理系统 API 接口文档

## 1. 基础数据管理

### 1.1 获取费用归属公司列表

**接口描述：** 获取所有费用归属公司信息

**请求方式：** GET

**接口路径：** `/api/companies`

**请求参数：** 无

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<CompanyVO> | 公司列表 |

**CompanyVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| companyId | Long | 公司ID |
| companyName | String | 公司名称 |
| companyCode | String | 公司编码 |

---

### 1.2 获取部门列表

**接口描述：** 获取所有部门信息

**请求方式：** GET

**接口路径：** `/api/departments`

**请求参数：** 无

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<DepartmentVO> | 部门列表 |

**DepartmentVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| departmentId | Long | 部门ID |
| departmentName | String | 部门名称 |
| departmentNo | String | 部门编号 |
| parentId | Long | 父级部门ID |

---

### 1.3 获取员工列表

**接口描述：** 获取所有员工信息

**请求方式：** GET

**接口路径：** `/api/employees`

**请求参数：** 无

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<EmployeeVO> | 员工列表 |

**EmployeeVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| reimburserId | Long | 员工ID |
| reimburserName | String | 员工姓名 |
| reimburserNo | String | 员工工号 |
| departmentId | Long | 所属部门ID |

---

### 1.4 获取业务类型树形结构

**接口描述：** 获取业务类型树形结构数据

**请求方式：** GET

**接口路径：** `/api/business-types/tree`

**请求参数：** 无

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<BusinessTypeTreeVO> | 业务类型树形列表 |

**BusinessTypeTreeVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| businessTypeId | Long | 业务类型ID |
| businessTypeName | String | 业务类型名称 |
| businessTypeCode | String | 业务类型编码 |
| parentId | Long | 父级业务类型ID |
| children | List<BusinessTypeTreeVO> | 子节点列表 |

---

### 1.5 获取城市列表

**接口描述：** 获取所有城市信息

**请求方式：** GET

**接口路径：** `/api/cities`

**请求参数：** 无

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<CityVO> | 城市列表 |

**CityVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| cityId | Long | 城市ID |
| cityName | String | 城市名称 |
| cityCode | String | 城市编码 |
| provinceName | String | 省份名称 |

---

## 2. 报销单管理

### 2.1 分页查询报销单列表

**接口描述：** 根据条件分页查询报销单列表

**请求方式：** POST

**接口路径：** `/api/reim-forms/page`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| page | Integer | Y | 页码，默认1 |
| size | Integer | Y | 每页大小，默认10 |
| orderNo | String | N | 报销单号（模糊查询） |
| title | String | N | 报销标题（模糊查询） |
| reason | String | N | 出差事由（模糊查询） |
| companyId | String | N | 费用归属公司ID |
| departmentId | String | N | 报销部门ID |
| reimburserId | String | N | 报销人ID |
| businessTypeId | String | N | 业务类型ID |
| status | Integer | N | 单据状态：1-未提交 2-已提交 3-已删除 4-已作废 |
| startTime | LocalDateTime | N | 创建开始时间 |
| endTime | LocalDateTime | N | 创建结束时间 |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | PageResult<ReimFormVO> | 分页结果 |

**PageResult 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| total | Long | 总记录数 |
| pages | Integer | 总页数 |
| current | Integer | 当前页码 |
| size | Integer | 每页大小 |
| records | List<ReimFormVO> | 数据列表 |

**ReimFormVO 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| formUid | Long | 报销单业务主键 |
| orderNo | String | 报销单号 |
| title | String | 报销标题 |
| reason | String | 出差事由 |
| reimburserId | String | 报销人ID |
| reimburserName | String | 报销人姓名 |
| reimburserNo | String | 报销人工号 |
| departmentId | String | 报销部门ID |
| departmentName | String | 部门名称 |
| departmentNo | String | 部门编号 |
| companyId | String | 费用归属公司ID |
| companyName | String | 公司名称 |
| businessTypeId | String | 业务类型ID |
| businessTypeName | String | 业务类型名称 |
| status | Integer | 状态：1-未提交 2-已提交 3-已删除 4-已作废 |
| statusDesc | String | 状态描述 |
| mealAllowanceTotal | Integer | 餐费补助合计(分) |
| transportAllowanceTotal | Integer | 交通补助合计(分) |
| communicationAllowanceTotal | Integer | 通讯补助合计(分) |
| allowanceTotal | Integer | 补助总金额(分) |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |
| itineraries | List<ReimItinerary> | 行程列表 |
| subsidies | List<ReimSubsidy> | 补助列表 |

---

### 2.2 查询报销单详情

**接口描述：** 根据报销单ID查询详细信息

**请求方式：** GET

**接口路径：** `/api/reim-forms/{formUid}`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formUid | Long | Y | 报销单ID（路径参数） |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | ReimFormVO | 报销单详情 |

---

### 2.3 保存报销单

**接口描述：** 新增或更新报销单（包含行程和补助信息）

**请求方式：** POST

**接口路径：** `/api/reim-forms/save`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formUid | Long | N | 报销单ID（更新时必填） |
| orderNo | String | N | 报销单号（系统自动生成） |
| title | String | Y | 报销标题 |
| reason | String | Y | 出差事由 |
| reimburserId | String | Y | 报销人ID |
| departmentId | String | Y | 报销部门ID |
| companyId | String | Y | 费用归属公司ID |
| businessTypeId | String | Y | 业务类型ID |
| status | Integer | Y | 状态：1-未提交 2-已提交 |
| itineraries | List<ReimItineraryDTO> | Y | 行程列表 |
| subsidies | List<ReimSubsidyDTO> | Y | 补助列表 |

**ReimItineraryDTO 对象结构：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| itineraryUid | Long | N | 行程ID（更新时必填） |
| formId | Long | Y | 报销单ID |
| travelerId | String | Y | 出行人ID |
| departureCityNo | String | Y | 出发城市编码 |
| arrivalCityNo | String | Y | 到达城市编码 |
| departureDate | LocalDate | Y | 出发日期 |
| arrivalDate | LocalDate | Y | 到达日期 |
| description | String | N | 行程说明 |

**ReimSubsidyDTO 对象结构：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| subsidyUid | Long | N | 补助ID（更新时必填） |
| formId | Long | Y | 报销单ID |
| calendarDate | LocalDate | Y | 补助日期 |
| mealSelected | Integer | Y | 餐费选择：0-无 1-有 |
| mealActualAmount | Integer | Y | 餐费实际金额(分) |
| transportSelected | Integer | Y | 交通费选择：0-无 1-有 |
| transportActualAmount | Integer | Y | 交通费实际金额(分) |
| commSelected | Integer | Y | 通讯费选择：0-无 1-有 |
| commActualAmount | Integer | Y | 通讯费实际金额(分) |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | Long | 报销单ID |

---

### 2.4 删除报销单

**接口描述：** 逻辑删除报销单

**请求方式：** DELETE

**接口路径：** `/api/reim-forms/{formUid}`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formUid | Long | Y | 报销单ID（路径参数） |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | Boolean | 删除结果 |

---

### 2.5 提交报销单

**接口描述：** 提交报销单，将状态改为已提交

**请求方式：** PUT

**接口路径：** `/api/reim-forms/{formUid}/submit`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formUid | Long | Y | 报销单ID（路径参数） |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | Boolean | 提交结果 |

---

## 3. 行程管理

### 3.1 根据报销单ID查询行程列表

**接口描述：** 查询指定报销单的所有行程信息

**请求方式：** GET

**接口路径：** `/api/reim-itineraries/form/{formId}`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formId | Long | Y | 报销单ID（路径参数） |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<ReimItinerary> | 行程列表 |

**ReimItinerary 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| itineraryUid | Long | 行程ID |
| formId | Long | 报销单ID |
| travelerId | String | 出行人ID |
| departureCityNo | String | 出发城市编码 |
| arrivalCityNo | String | 到达城市编码 |
| departureDate | LocalDate | 出发日期 |
| arrivalDate | LocalDate | 到达日期 |
| description | String | 行程说明 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

## 4. 补助管理

### 4.1 根据报销单ID查询补助列表

**接口描述：** 查询指定报销单的所有补助信息

**请求方式：** GET

**接口路径：** `/api/reim-subsidies/form/{formId}`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| formId | Long | Y | 报销单ID（路径参数） |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | List<ReimSubsidy> | 补助列表 |

**ReimSubsidy 对象结构：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| subsidyUid | Long | 补助ID |
| formId | Long | 报销单ID |
| calendarDate | LocalDate | 补助日期 |
| mealSelected | Integer | 餐费选择：0-无 1-有 |
| mealActualAmount | Integer | 餐费实际金额(分) |
| transportSelected | Integer | 交通费选择：0-无 1-有 |
| transportActualAmount | Integer | 交通费实际金额(分) |
| commSelected | Integer | 通讯费选择：0-无 1-有 |
| commActualAmount | Integer | 通讯费实际金额(分) |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

## 5. 补助日历管理

### 5.1 批量保存补助日历明细

**接口描述：** 批量保存或更新补助日历明细信息

**请求方式：** POST

**接口路径：** `/api/subsidy-calendars/batch-save`

**请求参数：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| calendarList | List<CalendarItemDTO> | Y | 日历明细列表 |

**CalendarItemDTO 对象结构：**

| 字段名 | 类型 | 是否必填 | 说明 |
|--------|------|----------|------|
| calendarUid | Long | N | 日历ID（更新时必填） |
| mealSelected | Integer | Y | 餐费选择：0-无 1-有 |
| mealActualAmount | Integer | Y | 餐费实际金额(分) |
| transportSelected | Integer | Y | 交通费选择：0-无 1-有 |
| transportActualAmount | Integer | Y | 交通费实际金额(分) |
| commSelected | Integer | Y | 通讯费选择：0-无 1-有 |
| commActualAmount | Integer | Y | 通讯费实际金额(分) |

**响应参数：**

| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 响应码 |
| message | String | 响应消息 |
| data | Boolean | 保存结果 |

---

## 6. 通用响应结构

所有接口均返回统一的响应结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

**响应码说明：**

| 响应码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 7. 注意事项

1. **金额单位：** 所有金额字段单位为"分"，例如：10000 表示 100.00 元
2. **日期格式：** 日期字段格式为 `yyyy-MM-dd`，日期时间字段格式为 `yyyy-MM-dd HH:mm:ss`
3. **逻辑删除：** 删除操作为逻辑删除，不会物理删除数据
4. **状态枚举：**
   - 报销单状态：1-未提交 2-已提交 3-已删除 4-已作废
   - 补助选择：0-无 1-有
5. **分页参数：** 页码从1开始，默认每页10条
6. **权限控制：** 部分接口需要登录认证，请在请求头中携带 Token

---

## 8. 接口版本

- **版本号：** v1.0
- **更新日期：** 2026-05-24
- **维护团队：** 飞算AI编程助手
