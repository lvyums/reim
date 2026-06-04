import request from '@/utils/request'

/**
 * 1. 根据报销单ID查询行程列表
 * @param {number} formUid
 */
export function getItineraryList(formUid) {
  return request({
    url: '/api/itineraries/list',
    method: 'get',
    params: { formUid }
  })
}

/**
 * 2. 新增补录行程
 * @param {object} data
 */
export function addItinerary(data) {
  return request({
    url: '/api/itineraries/add',
    method: 'post',
    data
  })
}

/**
 * 3. 修改行程
 * @param {object} data
 */
export function updateItinerary(data) {
  return request({
    url: '/api/itineraries/update',
    method: 'post',
    data
  })
}

/**
 * 4. 删除行程
 * @param {object} data  { itineraryUid }
 */
export function deleteItinerary(data) {
  return request({
    url: '/api/itineraries/delete',
    method: 'post',
    data
  })
}

/**
 * 5. 复制行程
 * @param {object} data
 */
export function copyItinerary(data) {
  return request({
    url: '/api/itineraries/copy',
    method: 'post',
    data
  })
}

/**
 * 6. 根据ID查询行程
 * @param {number} itineraryUid
 */
export function getItineraryById(itineraryUid) {
  return request({
    url: '/api/itineraries/getById',
    method: 'get',
    params: { itineraryUid }
  })
}