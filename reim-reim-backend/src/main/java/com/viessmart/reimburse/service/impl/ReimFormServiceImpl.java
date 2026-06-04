package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.common.FormStatusEnum;
import com.viessmart.reimburse.dto.ReimFormQueryDTO;
import com.viessmart.reimburse.dto.ReimFormSaveDTO;
import com.viessmart.reimburse.entity.*;
import com.viessmart.reimburse.mapper.ReimFormMapper;
import com.viessmart.reimburse.service.*;
import com.viessmart.reimburse.tools.FormIdContext;
import com.viessmart.reimburse.vo.ReimFormVO;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 报销单主表（金额单位：分） 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class ReimFormServiceImpl extends ServiceImpl<ReimFormMapper, ReimForm> implements IReimFormService {

    @Resource
    private IReimEmployeeService employeeService;

    @Resource
    private IReimDepartmentService departmentService;

    @Resource
    private IReimCompanyService companyService;

    @Resource
    private IBaseBusinessTypeService businessTypeService;

    @Resource
    private IReimItineraryService itineraryService;

    @Resource
    private IReimSubsidyService subsidyService;

    // 日志服务
    @Autowired
    private IReimStatusLogService reimStatusLogService;

    // 并行查询线程池
    @jakarta.annotation.Resource(name = "reimQueryExecutor")
    private Executor reimQueryExecutor;



    /**
     * 报销单分页列表查询
     *
     * @param page
     * @param queryDTO
     * @return
     */
    @Override
    public Page<ReimFormVO> pageList(Page<ReimForm> page, ReimFormQueryDTO queryDTO) {
        // 1. 构建 MP 分页查询条件
        LambdaQueryWrapper<ReimForm> qw = new LambdaQueryWrapper<>();

        // 2. 拼接多条件查询
        qw.eq(ReimForm::getDeleted, 0); // 只查未删除

        // 模糊查询
        qw.like(queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().isEmpty(), ReimForm::getOrderNo, queryDTO.getOrderNo());
        qw.like(queryDTO.getTitle() != null && !queryDTO.getTitle().isEmpty(), ReimForm::getTitle, queryDTO.getTitle());
        qw.like(queryDTO.getReason() != null && !queryDTO.getReason().isEmpty(), ReimForm::getReason, queryDTO.getReason());

        // 精确查询
        qw.eq(queryDTO.getCompanyId() != null && !queryDTO.getCompanyId().isEmpty(), ReimForm::getCompanyId, queryDTO.getCompanyId());
        qw.eq(queryDTO.getDepartmentId() != null && !queryDTO.getDepartmentId().isEmpty(), ReimForm::getDepartmentId, queryDTO.getDepartmentId());
        qw.eq(queryDTO.getReimburserId() != null && !queryDTO.getReimburserId().isEmpty(), ReimForm::getReimburserId, queryDTO.getReimburserId());
        qw.eq(queryDTO.getBusinessTypeId() != null && !queryDTO.getBusinessTypeId().isEmpty(), ReimForm::getBusinessTypeId, queryDTO.getBusinessTypeId());
        qw.eq(queryDTO.getStatus() != null, ReimForm::getStatus, queryDTO.getStatus());

        // 时间范围
        qw.ge(queryDTO.getStartTime() != null, ReimForm::getCreateTime, queryDTO.getStartTime());
        qw.le(queryDTO.getEndTime() != null, ReimForm::getCreateTime, queryDTO.getEndTime());

        // 排序
        qw.orderByDesc(ReimForm::getCreateTime);

        // 3. MP 分页查询
        baseMapper.selectPage(page, qw);


        List<ReimFormVO> voList = new ArrayList<>();
       /* if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            voList = page.getRecords().stream().map(this::convertVO).toList();
        }*/


        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            // ====================== 批量查询优化：CompletableFuture 并行查询解决 N+1 问题 ======================
            List<ReimForm> forms = page.getRecords();

            // 1. 收集所有不重复的 ID
            Set<String> reimburserIds = forms.stream()
                    .map(ReimForm::getReimburserId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<String> departmentIds = forms.stream()
                    .map(ReimForm::getDepartmentId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<String> companyIds = forms.stream()
                    .map(ReimForm::getCompanyId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<String> businessTypeIds = forms.stream()
                    .map(ReimForm::getBusinessTypeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 2. CompletableFuture 并行查询4个关联表
            CompletableFuture<Map<String, ReimEmployee>> empFuture = CompletableFuture.supplyAsync(() -> {
                if (reimburserIds.isEmpty()) return new HashMap<>();
                LambdaQueryWrapper<ReimEmployee> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ReimEmployee::getReimburserId, reimburserIds);
                return employeeService.list(wrapper).stream()
                        .collect(Collectors.toMap(ReimEmployee::getReimburserId, Function.identity(), (a, b) -> a));
            }, reimQueryExecutor);

            CompletableFuture<Map<String, ReimDepartment>> deptFuture = CompletableFuture.supplyAsync(() -> {
                if (departmentIds.isEmpty()) return new HashMap<>();
                LambdaQueryWrapper<ReimDepartment> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ReimDepartment::getReimDepartmentId, departmentIds);
                return departmentService.list(wrapper).stream()
                        .collect(Collectors.toMap(ReimDepartment::getReimDepartmentId, Function.identity(), (a, b) -> a));
            }, reimQueryExecutor);

            CompletableFuture<Map<String, ReimCompany>> compFuture = CompletableFuture.supplyAsync(() -> {
                if (companyIds.isEmpty()) return new HashMap<>();
                LambdaQueryWrapper<ReimCompany> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(ReimCompany::getReimCompanyId, companyIds);
                return companyService.list(wrapper).stream()
                        .collect(Collectors.toMap(ReimCompany::getReimCompanyId, Function.identity(), (a, b) -> a));
            }, reimQueryExecutor);

            CompletableFuture<Map<String, BaseBusinessType>> btFuture = CompletableFuture.supplyAsync(() -> {
                if (businessTypeIds.isEmpty()) return new HashMap<>();
                LambdaQueryWrapper<BaseBusinessType> wrapper = new LambdaQueryWrapper<>();
                wrapper.in(BaseBusinessType::getBusinessTypeId, businessTypeIds);
                return businessTypeService.list(wrapper).stream()
                        .collect(Collectors.toMap(BaseBusinessType::getBusinessTypeId, Function.identity(), (a, b) -> a));
            }, reimQueryExecutor);

            // 3. 等待全部并行查询完成
            CompletableFuture.allOf(empFuture, deptFuture, compFuture, btFuture).join();

            // 4. 获取并行查询结果
            Map<String, ReimEmployee> finalEmployeeMap = empFuture.join();
            Map<String, ReimDepartment> finalDepartmentMap = deptFuture.join();
            Map<String, ReimCompany> finalCompanyMap = compFuture.join();
            Map<String, BaseBusinessType> finalBusinessTypeMap = btFuture.join();

            voList = forms.stream()
                    .map(form -> convertVO(form, finalEmployeeMap, finalDepartmentMap, finalCompanyMap, finalBusinessTypeMap))
                    .toList();
        }

        // ✅ 安全封装 Page
        Page<ReimFormVO> voPage = new Page<>();
        voPage.setCurrent(page.getCurrent());
        voPage.setSize(page.getSize());
        voPage.setTotal(page.getTotal());
        voPage.setPages(page.getPages());
        voPage.setRecords(voList);


        return voPage;
    }
    /**
     * 转换 VO（批量查询场景，从 Map 缓存中获取关联数据）
     *
     * @param form 报销单实体
     * @param employeeMap 员工 Map（key: reimburserId）
     * @param departmentMap 部门 Map（key: reimDepartmentId）
     * @param companyMap 公司 Map（key: reimCompanyId）
     * @param businessTypeMap 业务类型 Map（key: businessTypeId）
     * @return ReimFormVO
     */
    private ReimFormVO convertVO(ReimForm form,
                                 Map<String, ReimEmployee> employeeMap,
                                 Map<String, ReimDepartment> departmentMap,
                                 Map<String, ReimCompany> companyMap,
                                 Map<String, BaseBusinessType> businessTypeMap) {
        ReimFormVO vo = new ReimFormVO();
        BeanUtils.copyProperties(form, vo);
        vo.setStatusDesc(getStatusText(form.getStatus()));
        vo.setFormUid(form.getFormUid());

        // ====================== 从 Map 缓存获取关联数据 ======================
        // 报销人信息
        if (form.getReimburserId() != null) {
            ReimEmployee employee = employeeMap.get(form.getReimburserId());
            vo.setReimburserNo(employee != null ? employee.getReimburserNo() : "");
            vo.setReimburserName(employee != null ? employee.getReimburserName() : "");
        }

        // 部门信息
        if (form.getDepartmentId() != null) {
            ReimDepartment dept = departmentMap.get(form.getDepartmentId());
            vo.setDepartmentNo(dept != null ? dept.getReimDepartmentNo() : "");
            vo.setDepartmentName(dept != null ? dept.getReimDepartmentName() : "");
        }

        // 公司信息
        if (form.getCompanyId() != null) {
            ReimCompany company = companyMap.get(form.getCompanyId());
            vo.setCompanyId(company != null ? company.getReimCompanyId() : "");
            vo.setCompanyName(company != null ? company.getReimCompanyName() : "");
        }

        // 业务类型信息
        if (form.getBusinessTypeId() != null) {
            BaseBusinessType businessType = businessTypeMap.get(form.getBusinessTypeId());
            vo.setBusinessTypeId(businessType != null ? businessType.getBusinessTypeId() : "");
            vo.setBusinessTypeName(businessType != null ? businessType.getBusinessTypeName() : "");
        }

        return vo;
    }
    /**
     * 查询详细报告单
     * @param formUid
     * @return
     */
    @Override
    public ReimFormVO getFormDetail(Long formUid) {
        //查询主表
        ReimForm form = getById(formUid);
        if (form == null) {
            throw new RuntimeException("报销单不存在");
        }
        ReimFormVO vo = convertVO(form);

        // 添加全局报销单ID
        FormIdContext.setForm(formUid, form.getReimburserId());
        // 3. 查询行程列表
        LambdaQueryWrapper<ReimItinerary> itineraryQuery = new LambdaQueryWrapper<>();
        itineraryQuery.eq(ReimItinerary::getFormId, formUid);
        itineraryQuery.eq(ReimItinerary::getDeleted, 0);
        List<ReimItinerary> itineraries = itineraryService.list(itineraryQuery);
        vo.setItineraries(itineraries);

        // 4. 查询补助列表
        LambdaQueryWrapper<ReimSubsidy> subsidyQuery = new LambdaQueryWrapper<>();
        subsidyQuery.eq(ReimSubsidy::getFormId, formUid);
        subsidyQuery.eq(ReimSubsidy::getDeleted, 0);
        List<ReimSubsidy> subsidies = subsidyService.list(subsidyQuery);
        vo.setSubsidies(subsidies);

        return vo;
    }

    /**
     * 编辑
     * @param formUid
     * @param dto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReimFormVO updateForm(Long formUid, ReimFormSaveDTO dto) {
        //判断单据状态是否允许编辑
        ReimForm form = getById(formUid);
        if (form == null) throw new RuntimeException("单据不存在");
        if (form.getStatus() != FormStatusEnum.DRAFT.getCode())
            throw new RuntimeException("只能编辑未提交单据");
        BeanUtils.copyProperties(dto, form);
        boolean success = updateById(form);
        if (!success) {
            throw new RuntimeException("操作冲突，请刷新后重试");
        }
        return getFormDetail(formUid);

    }

    /**
     * 删除（未提交才能删）
     * @param formUid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteForm(Long formUid) {
        ReimForm form = getById(formUid);
        if (form == null) throw new RuntimeException("单据不存在");
        if (form.getStatus() != FormStatusEnum.DRAFT.getCode())
            throw new RuntimeException("只能删除未提交单据");
        form.setStatus(FormStatusEnum.DELETED.getCode());
        form.setDeleted(1);
        form.setUpdateTime(LocalDateTime.now());
        boolean success = updateById(form);
        if (!success) {
            throw new RuntimeException("操作冲突，请刷新后重试");
        }

        reimStatusLogService.addLog(
                formUid,
                FormStatusEnum.DRAFT.getCode(),
                FormStatusEnum.DELETED.getCode(),
                form.getReimburserId(),
                "删除报销单"
        );
        FormIdContext.remove();
    }

    /**
     * 提交（未提交 → 已提交）
     * @param formUid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitForm(Long formUid) {
        ReimForm form = getById(formUid);
        if (form == null) throw new RuntimeException("单据不存在");
        if (form.getStatus() != FormStatusEnum.DRAFT.getCode())
            throw new RuntimeException("只能提交未提交单据");

        form.setStatus(FormStatusEnum.SUBMITTED.getCode());
        form.setUpdateTime(LocalDateTime.now());
        boolean success = updateById(form);
        if (!success) {
            throw new RuntimeException("操作冲突，请刷新后重试");
        }

        // 记录日志
        reimStatusLogService.addLog(
                formUid,
                FormStatusEnum.DRAFT.getCode(),
                FormStatusEnum.SUBMITTED.getCode(),
                form.getReimburserId(),
                "提交报销单"
        );
        FormIdContext.remove();
    }

    /**
     * 作废（已提交 → 已作废）
     * @param formUid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelForm(Long formUid) {
        ReimForm form = getById(formUid);
        if (form == null) throw new RuntimeException("单据不存在");
        if (form.getStatus() != FormStatusEnum.SUBMITTED.getCode())
            throw new RuntimeException("只能作废已提交的单据");

        form.setStatus(FormStatusEnum.CANCELED.getCode());
        form.setDeleted(1);
        form.setUpdateTime(LocalDateTime.now());
        boolean success = updateById(form);
        if (!success) {
            throw new RuntimeException("操作冲突，请刷新后重试");
        }

        reimStatusLogService.addLog(
                formUid,
                FormStatusEnum.SUBMITTED.getCode(),
                FormStatusEnum.CANCELED.getCode(),
                form.getReimburserId(),
                "作废报销单"
        );
        FormIdContext.remove();
    }

    /**
     * 撤回报销单（已提交 → 未提交）
     * @param formUid
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdrawForm(Long formUid) {
        ReimForm form = getById(formUid);
        if (form == null) throw new RuntimeException("报销单不存在");

        // 仅 已提交 可撤回
        if (form.getStatus() != FormStatusEnum.SUBMITTED.getCode()) {
            throw new RuntimeException("只有已提交的报销单才能撤回");
        }

        form.setStatus(FormStatusEnum.DRAFT.getCode());
        form.setUpdateTime(LocalDateTime.now());
        boolean success = updateById(form);
        if (!success) {
            throw new RuntimeException("操作冲突，请刷新后重试");
        }

        reimStatusLogService.addLog(
                formUid,
                FormStatusEnum.SUBMITTED.getCode(),
                FormStatusEnum.DRAFT.getCode(),
                form.getReimburserId(),
                "撤回报销单"
        );
        FormIdContext.remove();
    }

    private ReimFormVO convertVO(ReimForm form) {
        ReimFormVO vo = new ReimFormVO();
        BeanUtils.copyProperties(form, vo);
        vo.setStatusDesc(getStatusText(form.getStatus()));
        vo.setFormUid(form.getFormUid());

        // ====================== 根据ID查询名称 ======================
        // 报销人姓名
        if (form.getReimburserId() != null) {
            // 根据 reimburserId 字段查询，不是根据主键 id
            LambdaQueryWrapper<ReimEmployee> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReimEmployee::getReimburserId, form.getReimburserId());
            ReimEmployee employee = employeeService.getOne(wrapper);
            vo.setReimburserNo(employee != null ? employee.getReimburserNo() : "");
            vo.setReimburserName(employee != null ? employee.getReimburserName() : "");
        }
        // 部门名称
        if (form.getDepartmentId() != null) {
            LambdaQueryWrapper<ReimDepartment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReimDepartment::getReimDepartmentId, form.getDepartmentId());
            ReimDepartment dept = departmentService.getOne(wrapper);
            vo.setDepartmentNo(dept != null ? dept.getReimDepartmentNo() : "");
            vo.setDepartmentName(dept != null ? dept.getReimDepartmentName() : "");
        }

        // 公司名称
        if (form.getCompanyId() != null) {
            LambdaQueryWrapper<ReimCompany> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ReimCompany::getReimCompanyId, form.getCompanyId());
            ReimCompany company = companyService.getOne(wrapper);
            vo.setCompanyId(company != null ? company.getReimCompanyId() : "");
            vo.setCompanyName(company != null ? company.getReimCompanyName() : "");
        }

        // 业务类型名称
        if (form.getBusinessTypeId() != null) {
            LambdaQueryWrapper<BaseBusinessType> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BaseBusinessType::getBusinessTypeId, form.getBusinessTypeId());
            BaseBusinessType businessType = businessTypeService.getOne(wrapper);
            vo.setBusinessTypeId(businessType != null ? businessType.getBusinessTypeId() : "");
            vo.setBusinessTypeName(businessType != null ? businessType.getBusinessTypeName() : "");
        }

        return vo;
    }
    /**
     * 状态文字转换
     */
    private String getStatusText(Integer status) {
        return FormStatusEnum.getDescByCode(status);
    }

    /**
     * 新增空白报销单
     */
    @Override
    public ReimFormVO createEmptyForm(ReimFormSaveDTO dto){
        //long now = System.currentTimeMillis();
        ReimForm form = new ReimForm();
        //form.setFormUid(now);
        //加当前的年月日
        form.setOrderNo("RE" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        // =========== 必须传的字段，全部手动设置！===========
        form.setTitle("未命名报销单");  // 解决 title 报错
        form.setReimburserId("1");     // 解决 reimburser_id 报错（给个默认值）
        form.setDepartmentId("1");           // 部门默认值
        form.setCompanyId("1");        // 公司默认值
        form.setBusinessTypeId("1");        // 业务类型默认值
        form.setStatus(1);            // 未提交
        form.setStatus(FormStatusEnum.DRAFT.getCode());
        form.setMealAllowanceTotal(0);
        form.setTransportAllowanceTotal(0);
        form.setCommunicationAllowanceTotal(0);
        form.setAllowanceTotal(0);
        form.setDeleted(0);
        form.setCreateTime(LocalDateTime.now());
        form.setUpdateTime(LocalDateTime.now());

        save(form);
        //获取全局报销单ID
        FormIdContext.setForm(form.getFormUid(), form.getReimburserId());

        return convertVO(form);

    }

}
