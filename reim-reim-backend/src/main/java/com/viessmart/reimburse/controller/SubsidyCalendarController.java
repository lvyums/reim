package com.viessmart.reimburse.controller;

import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.dto.SubsidyCalendarSaveDTO;
import com.viessmart.reimburse.service.IReimSubsidyCalendarService;

import com.viessmart.reimburse.vo.SubsidyCalendarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subsidyCalendar")
public class SubsidyCalendarController {
    @Autowired
    private IReimSubsidyCalendarService subsidyCalendarService;

    /**
     * 3.1 查询补助日历
     */
    @GetMapping("/{subsidyUid}")
    public Result<List<SubsidyCalendarVO>> getCalendar(@PathVariable Long subsidyUid) {
        List<SubsidyCalendarVO> list = subsidyCalendarService.getCalendarBySubsidyUid(subsidyUid);
        return Result.success(list);
    }

    /**
     * 3.2 保存补助日历 + 自动重算补助总额
     */
    @PutMapping("/{subsidyUid}")
    public Result<?> saveCalendar(
            @PathVariable Long subsidyUid,
            @RequestBody SubsidyCalendarSaveDTO dto) {
        subsidyCalendarService.saveCalendarAndRecalculate(subsidyUid, dto);
        return Result.success("保存成功");
    }

}
