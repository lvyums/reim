
import { ref, reactive, computed } from 'vue'
import { CirclePlus, Delete, Edit, CopyDocument, WarnTriangleFilled } from '@element-plus/icons-vue'
import { ElMessageBox, ElMessage } from 'element-plus'

// 🔥 引入你统一的报销单接口
import { getReimFormDetail, submitReimForm, updateReimForm } from '@/api/reimForm.js'
import { getCompanyList, getDepartmentList, getEmployeeList, getBusinessTypeTree, getCityList } from '@/api/base.js'
import {
  getItineraryList,
  addItinerary,
  updateItinerary,
  deleteItinerary,
} from '@/api/itinerary.js'

import { list } from '@/api/subsidy.js'
import { getSubsidyCalendar, saveSubsidyCalendar } from '@/api/calendar.js'

export let router = null
let formUid = null

export function initRouter(routerInstance) {
  router = routerInstance
}

export function initRoute(routeInstance) {
  formUid = routeInstance.query?.formUid
  loadAllData()
}

const compOption = ref([])
const deptOption = ref([])
const personOption = ref([])
const typeOption = ref([])

async function loadDictList() {
  try {
    const c = await getCompanyList()
    compOption.value = c.data.data || []
    const d = await getDepartmentList()
    deptOption.value = d.data.data || []
    const e = await getEmployeeList()
    personOption.value = e.data.data || []
    const t = await getBusinessTypeTree()
    const rawTree = t.data.data || []
    const cities = await getCityList()
    cityOptions.value = cities.data.data || []
    const transCascader = (arr) => {
      return arr.map(item => ({
        value: item.businessTypeId,
        label: item.businessTypeName,
        children: item.children ? transCascader(item.children) : []
      }))
    }
    typeOption.value = transCascader(rawTree)
  } catch (err) {
    // 拦截器已统一处理错误提示
  }
}

function getFullTypeIdPath(tree, targetId) {
  let result = []
  const dfs = (nodes, path) => {
    for (let node of nodes) {
      let newPath = [...path, node.value]
      if (node.value === targetId) {
        result = newPath
        return true
      }
      if (node.children && node.children.length) {
        if (dfs(node.children, newPath)) return true
      }
    }
    return false
  }
  dfs(tree, [])
  return result
}

const formStatus = ref(1)
const allowanceTotal = ref(0)
const mealAllowanceTotal = ref(0)
const transportAllowanceTotal = ref(0)
const communicationAllowanceTotal = ref(0)

async function loadFormData() {
  if (!formUid) return
  const res = await getReimFormDetail(formUid)
  const data = res.data.data

  // 后端默认值不显示，保持表单为空
  formData.title = (data.title && data.title !== '未命名报销单') ? data.title : ''
  formData.reason = data.reason || ''
  formData.remark = data.remark || ''
  formData.reimburserId = (data.reimburserId && data.reimburserId !== '1') ? data.reimburserId : ''
  formData.deptId = (data.departmentId && data.departmentId !== '1') ? data.departmentId : ''
  formData.companyId = (data.companyId && data.companyId !== '1') ? data.companyId : ''
  formStatus.value = data.status || 1

  mealAllowanceTotal.value = (data.mealAllowanceTotal || 0) / 100
  transportAllowanceTotal.value = (data.transportAllowanceTotal || 0) / 100
  communicationAllowanceTotal.value = (data.communicationAllowanceTotal || 0) / 100
  allowanceTotal.value = (data.allowanceTotal || 0) / 100

  const leafBizId = data.business_type_id || data.businessTypeId
  if (leafBizId && leafBizId !== '1') {
    const fullPath = getFullTypeIdPath(typeOption.value, leafBizId)
    setTimeout(() => {
      formData.bizTypeId = fullPath || [leafBizId]
    }, 10)
  }
}

async function loadTripsAndSubsidies() {
  if (!formUid) {
    console.warn('formUid 为空，不加载行程/补助')
    return
  }
  try {
    const tripRes = await getItineraryList(formUid)
    const apiList = tripRes.data?.data || []
    tripList.value = apiList.map(item => {
      const person = personOption.value.find(p => p.reimburserId === item.travelerId)
      const tripDate = item.itineraryDate
        || (item.departureDate && item.arrivalDate ? `${item.departureDate} 至 ${item.arrivalDate}` : null)
        || '-'
      const tripRoute = item.itineraryCity
        || (item.departureCityNo && item.arrivalCityNo ? `${getCityName(item.departureCityNo)} → ${getCityName(item.arrivalCityNo)}` : null)
        || '-'
      return {
        personName: person?.reimburserName || '未知人员',
        tripDate,
        tripRoute,
        tripDesc: item.description || '',
        personId: item.travelerId,
        startCity: item.departureCityNo,
        endCity: item.arrivalCityNo,
        startDate: item.departureDate,
        endDate: item.arrivalDate,
        itineraryUid: item.itineraryUid
      }
    })

    const helpRes = await list(formUid)
    helpList.value = (helpRes.data.data || []).map(item => ({
      ...item,
      applyAmount: ((item.applyAmount || 0) / 100).toFixed(2),
      subsidyAmount: ((item.subsidyAmount || 0) / 100).toFixed(2)
    }))
  } catch (e) {
    tripList.value = []
    helpList.value = []
  }
}

async function loadSubsidies() {
  if (!formUid) return
  try {
    const helpRes = await list(formUid)
    helpList.value = (helpRes.data.data || []).map(item => ({
      ...item,
      applyAmount: ((item.applyAmount || 0) / 100).toFixed(2),
      subsidyAmount: ((item.subsidyAmount || 0) / 100).toFixed(2)
    }))
  } catch (e) {
    // 拦截器已统一处理错误提示
  }
}


async function loadAllData() {
  await loadDictList()
  if (formUid) {
    await new Promise(r => setTimeout(r, 20))
    await loadFormData()
    await loadTripsAndSubsidies()
  }
}

const activeNames = ref(['1', '2', '3', '4', '5'])
const mealSelected = ref(true)
const transSelected = ref(true)
const comSelected = ref(true)
const selectAll = ref(true)
const currentDate = new Date().toLocaleDateString('zh-CN')

const formData = reactive({
  title: '',
  reason: '',
  reimburserId: '',
  deptId: '',
  companyId: '',
  bizTypeId: [],
  remark: ''
})

const rules = {
  title: [{ required: true, message: '请输入报销标题', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入出差事由', trigger: 'blur' }],
  reimburserId: [{ required: true, message: '请选择报销人', trigger: 'change' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  companyId: [{ required: true, message: '请选择公司', trigger: 'change' }],
  bizTypeId: [{ required: true, message: '请选择业务类型', trigger: 'change' }]
}

const cityOptions = ref([])

const tripList = ref([])
const helpList = ref([])
const tripDialog = ref(false)
const helpDialog = ref(false)
const helpCalendar = ref([])
const currentSubsidyIndex = ref(-1)
const currentSubsidyUid = ref('')

const tripForm = reactive({
  personId: '', startCity: '', endCity: '', startDate: '', endDate: '', tripDesc: '', editingIndex: -1
})

const getType = (ids) => {
  if (!ids || !ids.length || !typeOption.value.length) return ''
  const find = (nodes, id) => {
    for (const n of nodes) {
      if (n.value === id) return n
      if (n.children) {
        const r = find(n.children, id)
        if (r) return r
      }
    }
    return null
  }
  const node = find(typeOption.value, ids[ids.length - 1])
  return node?.label || ''
}

const setTrip = () => {
  Object.keys(tripForm).forEach(k => k !== 'editingIndex' && (tripForm[k] = ''))
  tripForm.editingIndex = -1
  tripForm.personId = formData.reimburserId
  tripDialog.value = true
}

const editTrip = (row, i) => {
  Object.assign(tripForm, row)
  tripForm.personId = formData.reimburserId
  tripForm.editingIndex = i
  tripDialog.value = true
}

const getCityName = (cityNo) => {
  const city = cityOptions.value.find(c => c.cityNo === cityNo)
  return city ? city.cityName : cityNo
}
  const saveTrip = async () => {
    try {
      const formatDate = (date) => {
        if (!date) return ''
        const d = new Date(date)
        const y = d.getFullYear()
        const m = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        return `${y}-${m}-${day}`
      }

      const params = {
        formId: formUid,
        travelerId: formData.reimburserId,
        departureCityNo: tripForm.startCity,
        arrivalCityNo: tripForm.endCity,
        departureDate: formatDate(tripForm.startDate),
        arrivalDate: formatDate(tripForm.endDate),
        description: tripForm.tripDesc || ''
      }

      if (tripForm.editingIndex >= 0) {
        params.itineraryUid = tripList.value[tripForm.editingIndex].itineraryUid
        await updateItinerary(params)
        const index = tripForm.editingIndex
        tripList.value[index] = {
          ...tripList.value[index],
          personName: personOption.value.find(p => p.reimburserId === params.travelerId)?.reimburserName || '未知人员',
          tripDate: `${params.departureDate} 至 ${params.arrivalDate}`,
          tripRoute: `${getCityName(params.departureCityNo)} → ${getCityName(params.arrivalCityNo)}`,
          tripDesc: params.description,
          startCity: params.departureCityNo,
          endCity: params.arrivalCityNo,
          startDate: params.departureDate,
          endDate: params.arrivalDate,
        }
      } else {
        const overlap = tripList.value.some(t =>
          t.personId === params.travelerId &&
          t.startDate <= params.arrivalDate &&
          params.departureDate <= t.endDate
        )
        if (overlap) {
          ElMessage.warning('该时间段已有行程，不允许重复添加！')
          return
        }
 
        const res = await addItinerary(params)
        const itineraryUid = res.data?.data?.itineraryUid
        tripList.value.push({
          personName: personOption.value.find(p => p.reimburserId === params.travelerId)?.reimburserName || '未知人员',
          tripDate: `${params.departureDate} 至 ${params.arrivalDate}`,
          tripRoute: `${getCityName(params.departureCityNo)} → ${getCityName(params.arrivalCityNo)}`,
          tripDesc: params.description,
          personId: params.travelerId,
          startCity: params.departureCityNo,
          endCity: params.arrivalCityNo,
          startDate: params.departureDate,
          endDate: params.arrivalDate,
          itineraryUid: itineraryUid,
        })
      }

      await loadSubsidies()
      await refreshTotals()
      tripDialog.value = false
      ElMessage.success('保存成功')
    } catch (err) {
      tripDialog.value = false
    }
  }

const deleteTrip = async (i) => {
  try {
    await ElMessageBox.confirm('确定删除该行程？', '提示')
    const row = tripList.value[i]
    await deleteItinerary({ itineraryUid: row.itineraryUid })
    tripList.value.splice(i, 1)
    await loadSubsidies()
    await refreshTotals()
    ElMessage.success('删除成功')
  } catch (e) {
    if (e === 'cancel') return
    await loadTripsAndSubsidies()
    await refreshTotals()
  }
}

const copyTrip = (row) => {
  if (!row) {
    ElMessage.error('行程数据异常')
    return
  }
  Object.assign(tripForm, {
    personId: formData.reimburserId,
    startCity: row.startCity || '',
    endCity: row.endCity || '',
    startDate: '',
    endDate: '',
    tripDesc: row.tripDesc || '',
    editingIndex: -1
  })
  tripDialog.value = true
}

const deleteInfo = async (i) => {
  try {
    await ElMessageBox.confirm('确定删除该补助？', '提示')
    helpList.value.splice(i, 1)
  } catch (e) {
    if (e === 'cancel') return
  }
}

const editSubsidy = async (row, i) => {
  try {
    currentSubsidyIndex.value = i
    currentSubsidyUid.value = row.subsidyUid

    if (!row.subsidyUid) {
      ElMessage.error('补助数据异常，缺少 subsidyUid')
      return
    }

    const res = await getSubsidyCalendar(row.subsidyUid)
    let calendarData = []
    if (res.data && Array.isArray(res.data.data)) {
      calendarData = res.data.data
    } else if (res.data && Array.isArray(res.data)) {
      calendarData = res.data
    } else if (Array.isArray(res)) {
      calendarData = res
    }

    helpCalendar.value = calendarData.map(item => ({
      calendarUid: item.calendarUid,
      date: item.date,
      cityName: item.cityName,
      mealStandard: (item.mealStandardAmount || 0) / 100,
      transportStandard: (item.transportStandardAmount || 0) / 100,
      commStandard: (item.commStandardAmount || 0) / 100,
      mealAmount: ((item.mealActualAmount ?? 0) / 100).toFixed(2),
      transAmount: ((item.transportActualAmount ?? 0) / 100).toFixed(2),
      commAmount: ((item.commActualAmount ?? 0) / 100).toFixed(2),
      mealSelected: item.mealSelected === 1,
      transportSelected: item.transportSelected === 1,
      commSelected: item.commSelected === 1,
      allSelected: (item.mealSelected === 1 && item.transportSelected === 1)
    }))

    helpDialog.value = true
  } catch (err) {
    helpCalendar.value = []
    helpDialog.value = true
  }
}

const toggleRowSelect = (row) => {
  row.mealSelected = row.allSelected
  row.transportSelected = row.allSelected
  row.commSelected = row.allSelected
}
const toggleMealColumn = () => {
  helpCalendar.value.forEach(r => r.allSelected && (r.mealSelected = mealSelected.value))
}
const toggleTransportColumn = () => {
  helpCalendar.value.forEach(r => r.allSelected && (r.transportSelected = transSelected.value))
}
const toggleCommColumn = () => {
  helpCalendar.value.forEach(r => r.allSelected && (r.commSelected = comSelected.value))
}
const toggleSelectAll = () => {
  mealSelected.value = selectAll.value
  transSelected.value = selectAll.value
  comSelected.value = selectAll.value
  helpCalendar.value.forEach(r => {
    r.allSelected = selectAll.value
    r.mealSelected = selectAll.value
    r.transportSelected = selectAll.value
    r.commSelected = selectAll.value
  })
}
async function refreshTotals() {
  if (!formUid) return
  try {
    const res = await getReimFormDetail(formUid)
    const data = res.data.data
    mealAllowanceTotal.value = (data.mealAllowanceTotal || 0) / 100
    transportAllowanceTotal.value = (data.transportAllowanceTotal || 0) / 100
    communicationAllowanceTotal.value = (data.communicationAllowanceTotal || 0) / 100
    allowanceTotal.value = (data.allowanceTotal || 0) / 100
  } catch (e) {
    // 拦截器已统一处理错误提示
  }
}

const saveHelp = async () => {
  if (hasAmountError(helpCalendar.value)) {
    ElMessage.error('存在金额超出标准金额，请修正后再提交')
    return
  }
  const calendarList = helpCalendar.value.map(item => ({
    calendarUid: item.calendarUid,
    mealSelected: item.mealSelected ? 1 : 0,
    mealActualAmount: Number(item.mealAmount || 0) * 100,
    transportSelected: item.transportSelected ? 1 : 0,
    transportActualAmount: Number(item.transAmount || 0) * 100,
    commSelected: item.commSelected ? 1 : 0,
    commActualAmount: Number(item.commAmount || 0) * 100
  }))

  const postData = { calendarList }

  try {
    await saveSubsidyCalendar(currentSubsidyUid.value, postData)
    ElMessage.success('保存成功')
    helpDialog.value = false
    await loadSubsidies()
    await refreshTotals()   // 只刷新金额，不动表单
    //await loadTripsAndSubsidies()
  } catch (e) {
    helpDialog.value = false
  }
}

const checkAmount = (row, field, max) => {
  const val = parseFloat(row[field])
  row[field + 'Error'] = !isNaN(val) && val > max
}

const hasAmountError = (rows) => {
  return rows.some(r => r.mealAmountError || r.transAmountError || r.commAmountError)
}

const clear = () => formData.remark = ''

const totalMoney = computed(() => (allowanceTotal.value).toFixed(2))
const mealMoney = computed(() => (mealAllowanceTotal.value).toFixed(2))
const transMoney = computed(() => (transportAllowanceTotal.value).toFixed(2))
const comMoney = computed(() => (communicationAllowanceTotal.value).toFixed(2))

const totalHelpAmount = computed(() => {
  if (!Array.isArray(helpCalendar.value)) return '0.00'
  return helpCalendar.value.reduce((s, r) => {
    const meal = r.mealSelected ? +r.mealAmount : 0
    const trans = r.transportSelected ? +r.transAmount : 0
    const comm = r.commSelected ? +r.commAmount : 0
    return s + meal + trans + comm
  }, 0).toFixed(2)
})

const totalAmount = computed(() => {
  if (!Array.isArray(helpCalendar.value)) return '0.00'
  return helpCalendar.value.reduce((s, r) => {
    const meal = r.mealStandard
    const trans = r.transportStandard
    const comm = +r.commStandard
    return s + meal + trans + comm
  }, 0).toFixed(2)
})

const formRef = ref(null)

const close = async () => {
  if (formStatus.value === 1) {
    try {
      await formRef.value.validate()

      const dto = {
        title: formData.title,
        reason: formData.reason,
        reimburserId: formData.reimburserId,
        departmentId: formData.deptId,
        companyId: formData.companyId,
        businessTypeId: Array.isArray(formData.bizTypeId) ? formData.bizTypeId[formData.bizTypeId.length - 1] : formData.bizTypeId,
        remark: formData.remark,
        itineraryList: tripList.value,
        subsidyList: helpList.value
      }
      await updateReimForm(formUid, dto)
      ElMessage.success('保存成功')
    } catch (e) {
      // 拦截器已统一处理错误提示
      return
    }
  }
  router.push('/')
}

const submit = async () => {
  if (formStatus.value !== 1) {
    ElMessage.warning('只能提交未提交单据')
    return
  }
  try {
    await formRef.value.validate()

    if (!tripList.value.length) {
      ElMessage.warning('请添加行程')
      return
    }
    if (!helpList.value.length) {
      ElMessage.warning('请添加补助')
      return
    }

    const dto = {
      title: formData.title,
      reason: formData.reason,
      reimburserId: formData.reimburserId,
      departmentId: formData.deptId,
      companyId: formData.companyId,
      businessTypeId: Array.isArray(formData.bizTypeId) ? formData.bizTypeId[formData.bizTypeId.length - 1] : formData.bizTypeId,
      remark: formData.remark
    }
    await updateReimForm(formUid, dto)
    await submitReimForm(formUid)
    ElMessage.success('提交成功')
    router.push('/')
  } catch (e) {
    // 拦截器已统一处理错误提示
  }
}

export {
  CirclePlus, Delete, Edit, CopyDocument, WarnTriangleFilled,
  formData, formStatus, activeNames, currentDate, compOption, deptOption, personOption, typeOption, cityOptions,
  tripList, helpList, rules, formRef,
  tripDialog, tripForm, helpDialog, helpCalendar,
  totalHelpAmount, totalAmount, totalMoney, mealMoney, transMoney, comMoney,
  mealSelected, transSelected, comSelected, selectAll,
  clear, checkAmount, hasAmountError, setTrip, editTrip, copyTrip, deleteTrip, saveTrip, deleteInfo, editSubsidy, saveHelp,
  toggleRowSelect, toggleMealColumn, toggleTransportColumn, toggleCommColumn, toggleSelectAll,
  getType, close, submit, loadSubsidies
}