// src/api/base.js
import request from '@/utils/request'

/**
 * 获取公司列表
 */
export function getCompanyList() {
  return request({
    url: '/api/companies',
    method: 'get'
  })
}

/**
 * 获取部门列表
 */
export function getDepartmentList() {
  return request({
    url: '/api/departments',
    method: 'get'
  })
}

/**
 * 获取员工列表
 */
export function getEmployeeList() {
  return request({
    url: '/api/employees',
    method: 'get'
  })
}

/**
 * 获取业务类型树
 */
export function getBusinessTypeTree() {
  return request({
    url: '/api/business-types/tree',
    method: 'get'
  })
}

/**
 * 获取城市列表
 */
export function getCityList() {
  return request({
    url: '/api/cities',
    method: 'get'
  })
}