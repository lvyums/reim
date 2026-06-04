// src/api/reim.js
import request from '@/utils/request'

/**
 * 报销单分页列表
 * @param {*} page 页码
 * @param {*} size 每页条数
 * @param {*} queryVO 查询条件
 * @returns
 */
export function getReimFormList(page, size, queryVO) {
  return request({
    url: '/api/reimbursement/forms',
    method: 'get',
    params: {
      page,
      size,
      ...queryVO
    }
  })
}

/**
 * 新增空白报销单
 * @returns
 */
export function createEmptyReimForm(dto) {
  return request({
    url: '/api/reimbursement/forms',
    method: 'post',
    data: dto
  })
}

/**
 * 获取报销单详情
 * @param {*} formUid
 * @returns
 */
export function getReimFormDetail(formUid) {
  return request({
    url: `/api/reimbursement/forms/${formUid}`,
    method: 'get'
  })
}

/**
 * 保存/编辑报销单
 * @param {*} formUid
 * @param {*} dto
 * @returns
 */
export function updateReimForm(formUid, dto) {
  return request({
    url: `/api/reimbursement/forms/${formUid}`,
    method: 'put',
    data: dto
  })
}

/**
 * 删除报销单
 * @param {*} formUid
 * @returns
 */
export function deleteReimForm(formUid) {
  return request({
    url: `/api/reimbursement/forms/${formUid}`,
    method: 'delete'
  })
}

/**
 * 提交报销单
 * @param {*} formUid
 * @returns
 */
export function submitReimForm(formUid) {
  return request({
    url: `/api/reimbursement/forms/${formUid}/submit`,
    method: 'post'
  })
}

/**
 * 作废报销单
 * @param {*} formUid
 * @returns
 */
export function cancelReimForm(formUid) {
  return request({
    url: `/api/reimbursement/forms/${formUid}/cancel`,
    method: 'post'
  })
}

/**
 * 撤回报销单
 * @param {*} formUid
 * @returns
 */
export function withdrawReimForm(formUid) {
  return request({
    url: `/api/reimbursement/forms/${formUid}/withdraw`,
    method: 'post'
  })
}