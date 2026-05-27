<script setup lang="js">
import { useRoute, useRouter } from 'vue-router'
const route = useRoute()
const router = useRouter()

import {
  CirclePlus,
  Delete,
  Edit,
  CopyDocument,
  WarnTriangleFilled,

  formData,
  activeNames,
  currentDate,
  compOption,
  deptOption,
  personOption,
  typeOption,
  cityOptions,
  tripList,
  helpList,
  rules,
  formRef,

  tripDialog,
  tripForm,
  helpDialog,
  helpCalendar,

  totalHelpAmount,
  totalAmount,
  totalMoney,
  mealMoney,
  transMoney,
  comMoney,

  mealSelected,
  transSelected,
  comSelected,
  selectAll,

  clear,
  checkAmount,
  setTrip,
  editTrip,
  copyTrip,
  deleteTrip,
  saveTrip,
  editSubsidy,
  saveHelp,
  toggleRowSelect,
  toggleMealColumn,
  toggleTransportColumn,
  toggleCommColumn,
  toggleSelectAll,
  getType,
  close,
  submit,

  initRoute,
  initRouter
} from '@/FormScript.js'

// 初始化路由（修复报错关键）
initRoute(route)
initRouter(router)
</script>

<template>
  <div class="formPage">
    <div class="form-container">
      <div class="title">
        <h1 style="font-size: 20px; color: black">差旅费用报销单</h1>
        <span style="font-size: 14px; color: #666666; position: absolute; right: 5%; top: 60%">
          提单日期&nbsp;&nbsp;&nbsp;{{ currentDate }}
        </span>
      </div>

      <el-form ref="formRef" :model="formData" :rules="rules" class="main">
        <el-collapse v-model="activeNames">
          <el-collapse-item name="1">
            <template #title>
              <div class="topic">基本信息</div>
            </template>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="报销标题" prop="title">
                  <el-input v-model="formData.title" placeholder="请输入" type="textarea" maxlength="500" style="width: 1000px" />
                </el-form-item>
              </el-col>
              <el-col :span="5">
                <el-form-item label="报销人" prop="reimburserId">
                  <el-select v-model="formData.reimburserId" placeholder="请选择报销人" style="width: 100%">
                    <el-option v-for="item in personOption" :key="item.reimburserId" :label="item.reimburserName" :value="item.reimburserId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item label="报销部门" prop="deptId">
                  <el-select v-model="formData.deptId" placeholder="请选择部门" style="width: 100%">
                    <el-option v-for="item in deptOption" :key="item.reimDepartmentId" :label="item.reimDepartmentName" :value="item.reimDepartmentId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="费用归属公司" prop="companyId">
                  <el-select v-model="formData.companyId" placeholder="请选择公司" style="width: 395px">
                    <el-option v-for="item in compOption" :key="item.reimCompanyId" :label="item.reimCompanyName" :value="item.reimCompanyId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="业务类型" prop="bizTypeId">
                  <el-cascader v-model="formData.bizTypeId" :options="typeOption" placeholder="请选择业务类型" style="width: 400px" :show-all-levels="false" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="出差事由" prop="reason">
                  <el-input v-model="formData.reason" type="textarea" :rows="3" placeholder="请输入出差事由" maxlength="500" style="width: 1000px" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-collapse-item>
        </el-collapse>

        <el-collapse v-model="activeNames">
          <el-collapse-item name="2">
            <template #title>
                <div class="topic">
                  <span id="topic">补录行程</span>
                  <el-link
                    :icon="CirclePlus"
                    type="primary"
                    size="small"
                    @click.stop="setTrip"
                  >
                    补录行程
                  </el-link>
                </div>
              </template>
            <el-table :data="tripList" border stripe style="width: 100%">
              <el-table-column type="index" label="序号" width="60"/>
              <el-table-column prop="personName" label="出行人" width="120"/>
              <el-table-column prop="tripDate" label="出差日期" width="250"/>
              <el-table-column prop="tripRoute" label="行程" width="140"/>
              <el-table-column prop="tripDesc" label="行程说明" />
              <el-table-column label="操作" width="140">
                <template #default="scope">
                  <el-button :icon="Edit" size="small" @click="editTrip(scope.row, scope.$index)" circle ></el-button>
                  <el-button :icon="CopyDocument" size="small" @click="copyTrip(scope.row)" circle></el-button>
                  <el-button :icon="Delete" size="small" @click="deleteTrip(scope.$index)" circle></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>

        <el-collapse v-model="activeNames">
          <el-collapse-item name="3">
            <template #title>
              <div class="topic">补助信息
                <el-tooltip content="1、请根据实际出差日期选择补助&#10;2、出差期间当日有用餐安排的请自行核减当日餐补&#10;3、出差期间当日有用车的，请自行核减当日交补" placement="top">
                  <span style="margin-left: 60px; color: #fa7720; display: inline-block; width: 500px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; vertical-align: middle; font-size: 14px">
                    <el-text class="mx-1" style="margin-right: 10px">
                      {{totalMoney}}&nbsp;&nbsp;（{{helpList[0]?.travelerName || ''}}：{{helpList[0]?.subsidyDays || ''}}天）
                    </el-text>
                    <el-icon><WarnTriangleFilled /></el-icon>
                    1、请根据实际出差日期选择补助2、出差期间当日有用餐安排的请自行核减当日餐补3、出差期间当日有用车的，请自行核减当日交补
                  </span>
                </el-tooltip>
              </div>
            </template>
            <el-table :data="helpList" border stripe style="width: 100%">
              <el-table-column type="index" label="序号" width="60"/>
              <el-table-column prop="travelerName" label="出行人" width="120"/>
              <el-table-column prop="reimSubsidyDate" label="出差日期" width="250"/>
              <el-table-column prop="subsidyDays" label="补助天数" />
              <el-table-column prop="subsidyCity" label="行程" />
              <el-table-column prop="subsidyCityName" label="补助城市" />
              <el-table-column prop="applyAmount" label="标准金额" />
              <el-table-column prop="subsidyAmount" label="补助金额" />
              <el-table-column label="操作" width="100">
                <template #default="scope">
                  <el-button :icon="Edit" size="small" @click="editSubsidy(scope.row, scope.$index)" circle ></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>

        <el-collapse v-model="activeNames">
          <el-collapse-item name="4">
            <template #title>
              <div class="topic">费用合计</div>
            </template>
            <div class="moneyTable">
              <el-form-item label="补助总金额">
                <el-input :value="totalMoney" disabled style="width: 150px" />
              </el-form-item>
              <el-form-item label="餐费补助">
                <el-input :value="mealMoney" disabled style="width: 150px" />
              </el-form-item>
              <el-form-item label="交通补助">
                <el-input :value="transMoney" disabled style="width: 150px" />
              </el-form-item>
              <el-form-item label="通讯补助">
                <el-input :value="comMoney" disabled style="width: 150px" />
              </el-form-item>
            </div>
          </el-collapse-item>
        </el-collapse>

        <el-collapse v-model="activeNames">
          <el-collapse-item name="5">
              <template #title>
                <div class="topic">
                  <span id="topic">备注信息</span>
                  <el-link
                    :icon="Delete"
                    type="primary"
                    size="small"
                    @click.stop="clear"
                  >
                    删除备注
                  </el-link>
                </div>
              </template>
            <el-input v-model="formData.remark" type="textarea" :rows="4" maxlength="1000" placeholder="请输入备注信息" style="width: 100%" />
          </el-collapse-item>
        </el-collapse>
      </el-form>

      <div class="form-footer">
        <el-button @click="close">关闭</el-button>
        <el-button type="primary" @click="submit">提交</el-button>
      </div>

      <el-dialog v-model="tripDialog" title="补录行程" width="600px" :close-on-click-modal="false">
        <el-form :model="tripForm" label-width="100px">
          <el-form-item prop="travelerId" label="出行人" required>
            <el-select v-model="tripForm.personId" disabled placeholder="请选择">
              <el-option v-for="item in personOption" :key="item.reimburserId" :label="item.reimburserName" :value="item.reimburserId" />
            </el-select>
          </el-form-item>
          <el-form-item label="出发城市" required>
            <el-select v-model="tripForm.startCity" placeholder="请选择">
              <el-option v-for="item in cityOptions" :key="item.cityNo" :label="item.cityName" :value="item.cityNo" />
            </el-select>
          </el-form-item>
          <el-form-item label="到达城市" required>
            <el-select v-model="tripForm.endCity" placeholder="请选择">
              <el-option v-for="item in cityOptions" :key="item.cityNo" :label="item.cityName" :value="item.cityNo" />
            </el-select>
          </el-form-item>
          <el-form-item label="出发日期" required>
            <el-date-picker v-model="tripForm.startDate" type="datetime" placeholder="选择日期" />
          </el-form-item>
          <el-form-item label="到达日期" required>
            <el-date-picker v-model="tripForm.endDate" type="datetime" placeholder="选择日期" :disabled="!tripForm.startDate" />
          </el-form-item>
          <el-form-item prop="description" label="行程说明">
            <el-input v-model="tripForm.tripDesc" type="textarea" :rows="3" maxlength="500" placeholder="请输入行程说明" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="tripDialog = false">取消</el-button>
          <el-button type="primary" @click="saveTrip">保存</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="helpDialog" title="补助日历" width="1200px" :close-on-click-modal="false">
        <div style="display: flex; gap: 20px;">
          <div style="width: 280px; background: #f8f9fa; border-radius: 8px; padding: 20px;">
            <div style="margin-bottom: 20px;">
              <div style="font-weight: bold; margin-bottom: 10px;">出差类型</div>
              <div style="color: #fa7720;">{{ getType(formData.bizTypeId) || '请选择业务类型' }}</div>
            </div>
            <div style="margin-bottom: 20px;">
              <div style="font-weight: bold; margin-bottom: 10px;">出差日期</div>
              <div style="font-size: 12px; color: #666;">开始日期</div>
              <div>{{ helpCalendar.length > 0 ? helpCalendar[0].date : '' }}</div>
              <div style="font-size: 12px; color: #666; margin-top: 10px;">结束日期</div>
              <div>{{ helpCalendar.length > 0 ? helpCalendar[helpCalendar.length - 1].date : '' }}</div>
            </div>
            <div style="margin-bottom: 20px;">
              <div style="font-weight: bold; margin-bottom: 10px;">行程天数</div>
              <div>{{ helpCalendar.length }}天</div>
            </div>
            <div style="border-top: 1px solid #e8e8e8; padding-top: 15px;">
              <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                <span style="color: #666;">标准总额</span>
                <span style="color: #fa7720;">CNY&nbsp;{{ totalAmount }}</span>
              </div>
              <div style="display: flex; justify-content: space-between;">
                <span style="color: #666;">补助金额</span>
                <span style="color: #fa7720;">CNY&nbsp;{{ totalHelpAmount }}</span>
              </div>
            </div>
          </div>
          <div style="flex: 1;">
            <div style="display: flex; justify-content: flex-end; margin-bottom: 10px;">
              <el-checkbox v-model="selectAll" @change="toggleSelectAll">全选</el-checkbox>
            </div>
            <el-table :data="helpCalendar" border style="width: 100%;" :cell-style="{ textAlign: 'center', verticalAlign: 'middle' }" :header-cell-style="{ textAlign: 'center' }">
              <el-table-column label="出差日期" width="170">
                <template #default="scope">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <div style="display: flex; align-items: center; gap: 8px;">
                      <el-checkbox v-model="scope.row.allSelected" @change="toggleRowSelect(scope.row)" />
                      <span>{{ scope.row.date }}</span>
                    </div>
                    <div style="font-size: 12px; color: #999;">{{ scope.row.weekday }}</div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="cityName" label="补助城市" width="140" />
              <el-table-column label="餐费补助" width="180">
                <template #header>
                  <div style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                    <el-checkbox v-model="mealSelected" @change="toggleMealColumn" />
                    <span>餐费补助</span>
                  </div>
                </template>
                <template #default="scope">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <div style="color: #fa7720; font-size: 12px;">CNY&nbsp;{{ scope.row.mealStandard.toFixed(2) }}/天</div>
                    <div style="display: flex; align-items: center; gap: 5px;">
                      <el-checkbox v-model="scope.row.mealSelected" :disabled="!scope.row.allSelected" />
                      <el-input v-model="scope.row.mealAmount" type="text" :disabled="!scope.row.allSelected || !scope.row.mealSelected" :class="{ 'amount-error': scope.row.mealAmountError }" style="width: 80px; font-size: 12px;" placeholder="0.00" @input="checkAmount(scope.row, 'mealAmount', scope.row.mealStandard)" />
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="交通补助" width="180">
                <template #header>
                  <div style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                    <el-checkbox v-model="transSelected" @change="toggleTransportColumn" />
                    <span>交通补助</span>
                  </div>
                </template>
                <template #default="scope">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <div style="color: #fa7720; font-size: 12px;">CNY&nbsp;{{ scope.row.transportStandard.toFixed(2) }}/天</div>
                    <div style="display: flex; align-items: center; gap: 5px;">
                      <el-checkbox v-model="scope.row.transportSelected" :disabled="!scope.row.allSelected" />
                      <el-input v-model="scope.row.transAmount" type="text" :disabled="!scope.row.allSelected || !scope.row.transportSelected" :class="{ 'amount-error': scope.row.transAmountError }" style="width: 80px; font-size: 12px;" placeholder="0.00" @input="checkAmount(scope.row, 'transAmount', scope.row.transportStandard)" />
                    </div>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="通讯补助" width="180">
                <template #header>
                  <div style="display: flex; align-items: center; justify-content: center; gap: 5px;">
                    <el-checkbox v-model="comSelected" @change="toggleCommColumn" />
                    <span>通讯补助</span>
                  </div>
                </template>
                <template #default="scope">
                  <div style="display: flex; flex-direction: column; align-items: center; gap: 4px;">
                    <div style="color: #fa7720; font-size: 12px;">CNY&nbsp;{{ scope.row.commStandard.toFixed(2) }}/天</div>
                    <div style="display: flex; align-items: center; gap: 5px;">
                      <el-checkbox v-model="scope.row.commSelected" :disabled="!scope.row.allSelected" />
                      <el-input v-model="scope.row.commAmount" type="text" :disabled="!scope.row.allSelected || !scope.row.commSelected" :class="{ 'amount-error': scope.row.commAmountError }" style="width: 80px; font-size: 12px;" placeholder="0.00" @input="checkAmount(scope.row, 'commAmount', scope.row.commStandard)" />
                    </div>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
        <div style="
            position: fixed;
            bottom: 20px;
            left: 50%;
            transform: translateX(-50%);
            z-index: 9999;
            background: white;
            padding: 10px 30px;
            box-shadow: 0 -2px 10px rgba(0,0,0,0.1);
            border-radius: 8px;
        ">
          <el-button @click="helpDialog = false" size="default">关闭</el-button>
          <el-button type="primary" @click="saveHelp" size="default">提交</el-button>
        </div>

      </el-dialog>
    </div>
  </div>
</template>

<style src="@/FormStyle.css" scoped></style>

















