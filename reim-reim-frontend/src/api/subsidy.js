import request from '@/utils/request'; // 你的 axios 封装

/**
 * 1. 根据 formUid 获取补助列表
 * @param {number} formUid - 报销单UID
 */
export function list(formUid) {
  return request({
    url: '/api/reimSubsidy/list',
    method: 'get',
    params: { formUid }
  });
}

/**
 * 2. 新增补助
 * @param {object} data - 补助实体
 */
export function addReimSubsidy(data) {
  return request({
    url: '/api/reimSubsidy/add',
    method: 'post',
    data
  });
}

/**
 * 3. 修改补助
 * @param {object} data - 补助实体（必须带主键）
 */
export function updateReimSubsidy(data) {
  return request({
    url: '/api/reimSubsidy/update',
    method: 'post',
    data
  });
}

/**
 * 4. 删除补助
 * @param {object} data - 必须传 subsidyUid
 */
export function deleteReimSubsidy(data) {
  return request({
    url: '/api/reimSubsidy/delete',
    method: 'post',
    data
  });
}