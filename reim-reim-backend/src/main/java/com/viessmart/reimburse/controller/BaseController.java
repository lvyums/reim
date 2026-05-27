package com.viessmart.reimburse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viessmart.reimburse.entity.*;
import com.viessmart.reimburse.service.*;
import com.viessmart.reimburse.common.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
/**
 * 基础数据接口
 */
public class BaseController {
    @Resource
    private IReimCompanyService companyService;

    @Resource
    private IReimDepartmentService departmentService;

    @Resource
    private IReimEmployeeService employeeService;

    @Resource
    private IBaseBusinessTypeService businessTypeService;

    @Resource
    private IBaseCityService cityService;

    /**
     * 查询公司列表
     * @return
     */
    @GetMapping("/api/companies")
    public Result<List<ReimCompany>> getCompanyList() {
        return Result.success(companyService.list());
    }

    /**
     * 查询部门列表
     * @return
     */
    @GetMapping("/api/departments")
    public Result<List<ReimDepartment>> getDepartmentList() {
        return Result.success(departmentService.list());
    }

    /**
     * 查询员工列表
     * @return
     */
    @GetMapping("/api/employees")
    public Result<List<ReimEmployee>> getEmployeeList() {
        return Result.success(employeeService.list());
    }

    /**
     * 查询业务类型树
     * @return
     */
    @GetMapping("/api/business-types/tree")
    public Result<List<BaseBusinessType>> getBusinessTypeTree() {
        return Result.success(businessTypeService.getTree());
    }

    /**
     * 查询城市列表
     * @return
     */
    @GetMapping("/api/cities")
    public Result<List<BaseCity>> getCityList() {
        return Result.success(cityService.list());
    }

}
