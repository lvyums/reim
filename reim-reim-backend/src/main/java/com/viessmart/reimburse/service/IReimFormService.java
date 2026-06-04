package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.dto.ReimFormQueryDTO;
import com.viessmart.reimburse.dto.ReimFormSaveDTO;
import com.viessmart.reimburse.entity.ReimForm;
import com.viessmart.reimburse.vo.ReimFormVO;


/**
 * <p>
 * 报销单主表（金额单位：分） 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IReimFormService extends IService<ReimForm> {

    /**
     * 报销单分页列表查询
     * @param page
     * @param   queryDTO
     * @return
     */
    Page<ReimFormVO> pageList(Page<ReimForm> page, ReimFormQueryDTO queryDTO);

    /**
     * 报销单详情
     * @param formUid
     * @return
     */
    ReimFormVO getFormDetail(Long formUid);

    /**
     * 保存/编辑
     * @param formUid
     * @param dto
     * @return
     */
    ReimFormVO updateForm(Long formUid, ReimFormSaveDTO dto);

    /**
     * 删除（未提交才能删）
     * @param formUid
     */
    void deleteForm(Long formUid);

    /**
     * 提交（未提交 → 已提交）
     * @param formUid
     */
    void submitForm(Long formUid);

    /**
     * 作废（已提交 → 已作废）
     * @param formUid
     */
    void cancelForm(Long formUid);


    /**
     * 新增空白报销单
     * @return
     */
    ReimFormVO createEmptyForm(ReimFormSaveDTO dto);

    /**
     * 撤回报销单
     * @param formUid
     */
    void withdrawForm(Long formUid);

    /**
     * 重新计算报销单的补助合计（从剩余未删除的补助日历汇总）
     * @param formUid 报销单ID
     */
    void recalculateFormTotals(Long formUid);
}
