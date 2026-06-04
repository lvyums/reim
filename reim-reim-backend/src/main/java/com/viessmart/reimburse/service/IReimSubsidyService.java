package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.vo.ReimSubsidyVO;

import java.util.List;


/**
 * <p>
 * 补助信息（金额单位：分） 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IReimSubsidyService extends IService<ReimSubsidy> {

    /**
     * 根据表单编号查询补贴信息
     * @param formUid
     * @return
     */
    List<ReimSubsidyVO> listReimSubsidy(Long formUid);
}
