import request from '@/utils/request'

// 查询补助日历
export function getSubsidyCalendar(subsidyUid) {
  return request({
    url: `/api/subsidyCalendar/${subsidyUid}`,
    method: 'get'
  })
}

// 保存补助日历
export function saveSubsidyCalendar(subsidyUid, data) {
  return request({
    url: `/api/subsidyCalendar/${subsidyUid}`,
    method: 'put',
    data
  })
}