package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.dto.SubsidyCalendarSaveDTO;
import com.viessmart.reimburse.entity.ReimSubsidyCalendar;
import com.viessmart.reimburse.vo.SubsidyCalendarVO;

import java.util.List;


/**
 * <p>
 * 补助日历明细（金额单位：分） 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IReimSubsidyCalendarService extends IService<ReimSubsidyCalendar> {

    /**
     * 根据补贴编号查询补贴日历
     * @param subsidyUid
     * @return
     */
    List<SubsidyCalendarVO> getCalendarBySubsidyUid(Long subsidyUid);

    /**
     * 保存补贴日历并重新计算
     * @param subsidyUid
     * @param dto
     */
    void saveCalendarAndRecalculate(Long subsidyUid, SubsidyCalendarSaveDTO dto);
}
