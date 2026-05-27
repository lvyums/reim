package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.dto.SubsidyCalendarSaveDTO;
import com.viessmart.reimburse.entity.ReimForm;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.entity.ReimSubsidyCalendar;
import com.viessmart.reimburse.mapper.ReimFormMapper;
import com.viessmart.reimburse.mapper.ReimSubsidyCalendarMapper;
import com.viessmart.reimburse.mapper.ReimSubsidyMapper;
import com.viessmart.reimburse.service.IBaseCityService;
import com.viessmart.reimburse.service.IReimSubsidyCalendarService;
import com.viessmart.reimburse.vo.SubsidyCalendarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 补助日历明细（金额单位：分） 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class ReimSubsidyCalendarServiceImpl extends ServiceImpl<ReimSubsidyCalendarMapper, ReimSubsidyCalendar> implements IReimSubsidyCalendarService {

    @Autowired
    private ReimSubsidyCalendarMapper reimSubsidyCalendarMapper;
    @Autowired
    private IBaseCityService baseCityService;
    @Autowired
    private ReimSubsidyMapper reimSubsidyMapper;
    @Autowired
    private ReimFormMapper reimFormMapper;

    /**
     * 查询补助日历
     * @param subsidyUid
     * @return
     */
    @Override
    public List<SubsidyCalendarVO> getCalendarBySubsidyUid(Long subsidyUid) {
        List<ReimSubsidyCalendar> list = lambdaQuery()
                .eq(ReimSubsidyCalendar::getSubsidyId,subsidyUid)
                .eq(ReimSubsidyCalendar::getDeleted, 0)
                .orderByAsc(ReimSubsidyCalendar::getDate)
                .list();

        List<SubsidyCalendarVO> voList = new ArrayList<>();
        for (ReimSubsidyCalendar calendar : list) {
            SubsidyCalendarVO vo = new SubsidyCalendarVO();
            vo.setCalendarUid(calendar.getCalendarUid());
            vo.setDate(calendar.getDate().toString());
            vo.setCityName(baseCityService.getCityNameByCityNo(calendar.getCityNo()));

            vo.setMealSelected(calendar.getMealSelected());
            vo.setMealStandardAmount(calendar.getMealStandardAmount());
            vo.setMealActualAmount(calendar.getMealActualAmount());

            vo.setTransportSelected(calendar.getTransportSelected());
            vo.setTransportStandardAmount(calendar.getTransportStandardAmount());
            vo.setTransportActualAmount(calendar.getTransportActualAmount());

            vo.setCommSelected(calendar.getCommSelected());
            vo.setCommStandardAmount(calendar.getCommStandardAmount());
            vo.setCommActualAmount(calendar.getCommActualAmount());

            voList.add(vo);
        }
        return voList;
    }


    /**
     * 保存补助日历+重算金额
     * @param subsidyUid
     * @param dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveCalendarAndRecalculate(Long subsidyUid, SubsidyCalendarSaveDTO dto) {
        List<SubsidyCalendarSaveDTO.CalendarItemDTO> calendarList = dto.getCalendarList();
        if (calendarList == null || calendarList.isEmpty()) {
            throw new RuntimeException("日历明细不能为空");
        }

        // 1. 批量更新每一天
        for (SubsidyCalendarSaveDTO.CalendarItemDTO item : calendarList) {
            ReimSubsidyCalendar calendar = getById(item.getCalendarUid());
            if (calendar == null) continue;

            // 校验每项金额不超过标准金额且不小于0
            if (item.getMealActualAmount() != null) {
                if (item.getMealActualAmount() < 0 || item.getMealActualAmount() > calendar.getMealStandardAmount()) {
                    throw new RuntimeException("餐补金额不能小于0且不能超过标准金额(¥" + (calendar.getMealStandardAmount() / 100.0) + ")");
                }
            }
            if (item.getTransportActualAmount() != null) {
                if (item.getTransportActualAmount() < 0 || item.getTransportActualAmount() > calendar.getTransportStandardAmount()) {
                    throw new RuntimeException("交通补助金额不能小于0且不能超过标准金额(¥" + (calendar.getTransportStandardAmount() / 100.0) + ")");
                }
            }
            if (item.getCommActualAmount() != null) {
                if (item.getCommActualAmount() < 0 || item.getCommActualAmount() > calendar.getCommStandardAmount()) {
                    throw new RuntimeException("通讯补助金额不能小于0且不能超过标准金额(¥" + (calendar.getCommStandardAmount() / 100.0) + ")");
                }
            }

            calendar.setMealSelected(item.getMealSelected());
            calendar.setMealActualAmount(item.getMealActualAmount());
            calendar.setTransportSelected(item.getTransportSelected());
            calendar.setTransportActualAmount(item.getTransportActualAmount());
            calendar.setCommSelected(item.getCommSelected());
            calendar.setCommActualAmount(item.getCommActualAmount());

            updateById(calendar);
        }

        // 2. 重新计算当前补助总金额
        recalculateSubsidyTotal(subsidyUid);
    }

    /**
     * 重算补助总额
     */
    private void recalculateSubsidyTotal(Long subsidyUid) {
        List<ReimSubsidyCalendar> list = lambdaQuery()
                .eq(ReimSubsidyCalendar::getSubsidyId, subsidyUid)
                .eq(ReimSubsidyCalendar::getDeleted, 0)
                .list();

        int mealTotal = 0;
        int transportTotal = 0;
        int commTotal = 0;

        for (ReimSubsidyCalendar calendar : list) {
            if (calendar.getMealSelected() == 1) {
                mealTotal += calendar.getMealActualAmount();
            }
            if (calendar.getTransportSelected() == 1) {
                transportTotal += calendar.getTransportActualAmount();
            }
            if (calendar.getCommSelected() == 1) {
                commTotal += calendar.getCommActualAmount();
            }
        }

        int total = mealTotal + transportTotal + commTotal;

        // 3. 更新补助主表
        ReimSubsidy subsidy = reimSubsidyMapper.selectById(subsidyUid);
        if (subsidy == null) return;
        subsidy.setSubsidyAmount(total);
        //subsidy.setApplyAmount(total);
        reimSubsidyMapper.updateById(subsidy);

        // 4. 同步更新报销单主表金额
        updateFormTotals(subsidy.getFormId());
    }

    /**
     * 重新计算报销单主表的补助合计
     */
    private void updateFormTotals(Long formUid) {
        List<ReimSubsidy> subsidies = reimSubsidyMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ReimSubsidy>()
                        .eq(ReimSubsidy::getFormId, formUid)
                        .eq(ReimSubsidy::getDeleted, 0)
        );
        int mealTotal = 0, transportTotal = 0, commTotal = 0;
        for (ReimSubsidy sub : subsidies) {
            // 查询该补助下的日历明细，按分类汇总
            List<ReimSubsidyCalendar> calendars = lambdaQuery()
                    .eq(ReimSubsidyCalendar::getSubsidyId, sub.getSubsidyUid())
                    .eq(ReimSubsidyCalendar::getDeleted, 0)
                    .list();
            for (ReimSubsidyCalendar cal : calendars) {
                if (cal.getMealSelected() == 1) mealTotal += cal.getMealActualAmount();
                if (cal.getTransportSelected() == 1) transportTotal += cal.getTransportActualAmount();
                if (cal.getCommSelected() == 1) commTotal += cal.getCommActualAmount();
            }
        }
        ReimForm form = new ReimForm();
        form.setFormUid(formUid);
        form.setMealAllowanceTotal(mealTotal);
        form.setTransportAllowanceTotal(transportTotal);
        form.setCommunicationAllowanceTotal(commTotal);
        form.setAllowanceTotal(mealTotal + transportTotal + commTotal);
        reimFormMapper.updateById(form);
    }
}
