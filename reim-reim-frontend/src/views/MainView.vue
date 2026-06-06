<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  EditPen, Delete, More,
  Check, Refresh, CircleClose
} from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage, ElTooltip } from 'element-plus'

// 👉 只使用你封装的 API，无任何 axios 硬编码
import {
  getReimFormList,
  createEmptyReimForm,
  deleteReimForm,
  submitReimForm,
  cancelReimForm,
  withdrawReimForm
} from '@/api/reimForm'

import {
  getCompanyList,
  getDepartmentList,
  getEmployeeList,
  getBusinessTypeTree
} from '@/api/base'

// 路由
const router = useRouter()

// 查询条件
const search = reactive({
  orderNo: '',
  title: '',
  reason: '',
  companyId: '',
  departmentId: '',
  reimburserId: '',
  businessTypeId: ''
})

// 表格 + 分页
const tableData = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const submittingId = ref(null) // 正在操作的行ID，用于按钮 loading

// 下拉框数据
const companyList = ref([])
const deptList = ref([])
const userList = ref([])
const businessTypeList = ref([])

onMounted(() => {
  loadList()
  loadDictData()
})

// 加载下拉框数据（全用封装接口）
async function loadDictData() {
  try {
    const coms = await getCompanyList()
    companyList.value = coms.data.data || []

    const depts = await getDepartmentList()
    deptList.value = depts.data.data || []

    const users = await getEmployeeList()
    userList.value = users.data.data || []

    const types = await getBusinessTypeTree()
    businessTypeList.value = types.data.data || []
  } catch (e) {
    // 拦截器已统一处理错误提示
  }
}

// 加载列表（全用封装接口）
async function loadList() {
  try {
    const res = await getReimFormList(pageNum.value, pageSize.value, search)
    tableData.value = res.data.data.records || []
    total.value = res.data.data.total || 0
  } catch (err) {
    // 拦截器已统一处理错误提示
  }
}

// 确认提示
async function confirmTip(msg) {
  return ElMessageBox.confirm(msg, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
}

// 编辑
function handleEdit(row) {
  router.push(`/form?formUid=${row.formUid}`)
}


// 新增
async function handleAdd() {
  try {
    // 1. 传默认标题，解决数据库报错
    const res = await createEmptyReimForm({
      title: "未命名报销单"
    })

    // 2. 安全获取 formUid
    const formUid = res?.data?.data?.formUid
    if (!formUid) {
      ElMessage.error('创建失败：未获取到单据ID')
      return
    }

    ElMessage.success('创建成功')
    router.push(`/form?formUid=${formUid}`)

  } catch (e) {
    // 拦截器已统一处理错误提示
  }
}


// 提交
async function handleSubmit(row) {
  try {
    await confirmTip('确定提交该报销单吗？')
    submittingId.value = row.formUid
    await submitReimForm(row.formUid)
    ElMessage.success('提交成功')
    loadList()
  } catch (e) {
    // 拦截器已统一处理错误提示
  } finally {
    submittingId.value = null
  }
}

// 撤回
async function handleRecall(row) {
  try {
    await confirmTip('确定撤回该报销单吗？')
    submittingId.value = row.formUid
    await withdrawReimForm(row.formUid)
    ElMessage.success('撤回成功')
    loadList()
  } catch (e) {
    // 拦截器已统一处理错误提示
  } finally {
    submittingId.value = null
  }
}

// 删除
async function handleDelete(row) {
  try {
    await confirmTip('此操作将永久删除单据，确定继续？')
    submittingId.value = row.formUid
    await deleteReimForm(row.formUid)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 拦截器已统一处理错误提示
  } finally {
    submittingId.value = null
  }
}

// 作废
async function handleCancel(row) {
  try {
    await confirmTip('确定作废该报销单吗？')
    submittingId.value = row.formUid
    await cancelReimForm(row.formUid)
    ElMessage.success('作废成功')
    loadList()
  } catch (e) {
    // 拦截器已统一处理错误提示
  } finally {
    submittingId.value = null
  }
}

// 更多操作
function handleMoreCommand(cmd, row) {
  if (cmd === 'recall') handleRecall(row)
  if (cmd === 'delete') handleDelete(row)
  if (cmd === 'cancel') handleCancel(row)
}

// 搜索
function onSearch() {
  pageNum.value = 1
  loadList()
}

// 清空
function clear() {
  Object.assign(search, {
    orderNo: '', title: '', reason: '',
    companyId: '', departmentId: '', reimburserId: '', businessTypeId: ''
  })
  loadList()
}
</script>

<template>
  <div class="div">
    <div class="query">
      <div style="margin-bottom: 20px;">
        <el-form :inline="true" :model="search">
          <el-form-item label="报销单号" style="margin-right: 90px;margin-left: 140px;">
            <el-input v-model="search.orderNo" placeholder="请输入" style="width: 240px;" />
          </el-form-item>
          <el-form-item label="标题" style="margin-right: 90px;">
            <el-input v-model="search.title" placeholder="请输入" style="width: 240px;" />
          </el-form-item>
          <el-form-item label="事由" style="margin-right: 90px;">
            <el-input v-model="search.reason" placeholder="请输入" style="width: 240px;" />
          </el-form-item>

          <el-form-item label="费用归属公司">
            <el-select v-model="search.companyId" placeholder="请选择" style="width: 240px;">
              <el-option v-for="item in companyList" :key="item.reimCompanyId" :label="item.reimCompanyName" :value="item.reimCompanyId" />
            </el-select>
          </el-form-item>

          <el-form-item label="报销部门" style="margin-right: 76px;margin-left: 140px;">
            <el-select v-model="search.departmentId" placeholder="请选择" style="width: 240px;">
              <el-option v-for="item in deptList" :key="item.reimDepartmentId" :label="item.reimDepartmentName" :value="item.reimDepartmentId" />
            </el-select>
          </el-form-item>

          <el-form-item label="报销人" style="margin-right: 62px;">
            <el-select v-model="search.reimburserId" placeholder="请选择" style="width: 240px;">
              <el-option v-for="item in userList" :key="item.reimburserId" :label="item.reimburserName" :value="item.reimburserId" />
            </el-select>
          </el-form-item>

          <el-form-item label="业务类型" style="margin-right: 220px;">
            <el-cascader
              v-model="search.businessTypeId"
              :options="businessTypeList"
              :props="{
                value: 'businessTypeId',
                label: 'businessTypeName',
                children: 'children',
                emitPath: false
              }"
              placeholder="请选择"
              style="width: 240px"
              :show-all-levels="false"
              clearable
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="onSearch">搜索</el-button>
            <el-button @click="clear">清除</el-button>
            <el-button type="success" @click="handleAdd">新增</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <div class="table">
      <el-table :data="tableData" border stripe>
        <el-table-column type="index" width="50" />
        <el-table-column type="selection" width="50" />

        <el-table-column label="操作" width="125">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top">
              <el-button :icon="EditPen" size="small" circle @click="handleEdit(scope.row)" />
            </el-tooltip>

            <el-tooltip content="提交" placement="top">
              <el-button :icon="Check" size="small" circle type="primary"
                :loading="submittingId === scope.row.formUid"
                :disabled="submittingId === scope.row.formUid"
                @click="handleSubmit(scope.row)" />
            </el-tooltip>

            <el-dropdown @command="(c) => handleMoreCommand(c, scope.row)">
              <span>
                <el-tooltip content="更多操作" placement="top" >
                  <el-button :icon="More" size="small" circle style="margin-left: 12px"/>
                </el-tooltip>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="recall">
                    <el-icon><Refresh /></el-icon>
                    <span style="margin-left: 4px;">撤回</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="delete">
                    <el-icon><Delete /></el-icon>
                    <span style="margin-left: 4px;">删除</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="cancel">
                    <el-icon><CircleClose /></el-icon>
                    <span style="margin-left: 4px;">作废</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>

<!--        <el-table-column label="报销单号" prop="orderNo" />-->
        <!-- 👉 点击报销单号跳转详情 -->
        <el-table-column label="报销单号" prop="orderNo" width="180px">
          <template #default="scope">
            <span
              style="color: #1677ff; cursor: pointer"
              @click="router.push(`/form?formUid=${scope.row.formUid}`)"
            >
              {{ scope.row.orderNo }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="单据状态" prop="statusDesc" width="90px">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : scope.row.status === 2 ? 'primary' : scope.row.status === 3 ? 'warning' : 'danger'">
              {{ scope.row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="报销人" prop="reimburserName" width="70px"/>
        <el-table-column label="报销部门" prop="departmentName" width="130px"/>
        <el-table-column label="费用归属公司" prop="companyName" />
        <el-table-column label="业务类型" prop="businessTypeName" />
<!--        <el-table-column label="报销标题" prop="title" />-->

        <!-- 👉 点击报销标题跳转详情 -->
        <el-table-column label="报销标题" prop="title">
          <template #default="scope">
            <span
              style="color: #1677ff; cursor: pointer"
              @click="router.push(`/form?formUid=${scope.row.formUid}`)"
            >
              {{ scope.row.title }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="报销事由" prop="reason" />
<!--        <el-table-column label="补助金额" prop="allowanceTotal" />-->
        <!-- 新的 ✅ -->
        <el-table-column label="补助金额" width="100px" align="right">
          <template #default="scope">
            {{ ((scope.row.allowanceTotal || 0) / 100).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" />
      </el-table>
    </div>

    <div class="page">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10,20,30]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadList"
        @size-change="loadList"
      />
    </div>
  </div>
</template>

<style src="@/Style.css"></style>