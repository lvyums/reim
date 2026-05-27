/*
import { ref, reactive} from 'vue'
import { EditPen, Delete, More } from '@element-plus/icons-vue'


//查询板块
const search = reactive({
  number: '',// 报销单号
  title: '',// 标题
  reason: '',//事由
  company: '',// 费用归属公司
  department: '',//报销部门
  person: '',// 报销人
  type: ''// 业务类型
})


//业务类型树形选择
const options = [
  {
    value: 'activity',
    label: '员工差旅活动',
    children: [
      {
        value: 'domestic',
        label: '境内出差',
        children: [
          {
            value: 'project',
            label: '项目出差',
          },
          {
            value: 'market',
            label: '市场拓展出差',
          },
        ],
      },
      {
        value: 'overseas',
        label: '境外出差',
        children: [
          {
            value: 'investigation',
            label: '国外考察',
          },
          {
            value: 'maintain',
            label: '售后维护出差',
          },
        ],
      },
    ],
  },
  {
    value: 'resource',
    label: '人力资源',
    children: [
      {
        value: 'train',
        label: '个人团队培训',
      },
      {
        value: 'meeting',
        label: '招聘会',
      },
    ],
  },
  {
    value: 'welfare',
    label: '员工福利',
    children: [
      {
        value: 'trip',
        label: '员工旅游',
      },
      {
        value: 'build',
        label: '员工团建',
      },
      {
        value: 'test',
        label: '员工体检',
      },
    ],
  },
]


// 表格数据
const tableData = reactive([
  {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  },
  {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }, {
    number: 'RCBX20260423004',
    status: '未提交',
    person: '张三[202101497]',
    department: '[CS001]测试部',
    company: '胜意科技武汉分公司',
    type: '日常出差',
    title: '5月北京出差',
    reason: '项目交付',
    money: '1200.00',
    time: '2026-05-09 10:23:12'
  }
])


//删除
const clear = () => {
  search.number = ''
  search.title = ''
  search.reason = ''
  search.company = ''
  search.department = ''
  search.person = ''
  search.type = ''
}

// 分页
const pageNum = ref(1)
const pageSize = ref(10)
// 当前页的信息
const pageData = () => {
  const start = (pageNum.value-1) * pageSize.value // 本页的第一条数据
  const end = start + pageSize.value // 本页的最后一条数据
  return tableData.slice(start, end) // 返回本页的为所有数据
}



export {
  search,
  options,
  tableData,
  EditPen,
  Delete,
  More,
  clear,
  pageNum,
  pageSize,
  pageData
}

*/
// 这个文件现在只需要导出图标即可
import { EditPen, Delete, More } from '@element-plus/icons-vue'

export {
  EditPen,
  Delete,
  More
}