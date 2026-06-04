package com.viessmart.reimburse.controller;

import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.dto.ReimItineraryDTO;
import com.viessmart.reimburse.entity.ReimItinerary;
import com.viessmart.reimburse.service.IReimItineraryService;
import com.viessmart.reimburse.vo.ReimItineraryVO;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 补录行程管理
 */
@RestController
@Slf4j
@RequestMapping("/api/itineraries")
public class ReimItineraryController {

    @Autowired
    private IReimItineraryService iReimItineraryService;

    /**
     * 添加行程
     */
    @RequestMapping("/add")
    public Result<?> addItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        try {
            ReimItinerary itinerary = iReimItineraryService.addReimItinerary(itineraryDTO);
            return Result.success(itinerary);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 查询行程列表
     */
    @RequestMapping("/list")
    public Result<List<ReimItineraryVO>> listItineraries(@RequestParam Long formUid) {
        return Result.success(iReimItineraryService.listItinerary(formUid));
    }

    /**
     * 修改行程
     */
    @RequestMapping("/update")
    public Object updateItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.updateItinerary(itineraryDTO);
        return Result.success("success");
    }

    /**
     * 删除行程
     */
    @RequestMapping("/delete")
    public Object deleteItineraries(@RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.deleteItinerary(itineraryDTO);
        return Result.success("success");
    }

    /**
     * 复制行程
     */
    @RequestMapping("/copy")
    public Object copyItineraries(@RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.copyItinerary(itineraryDTO);
        log.info("复制行程成功");
        return Result.success("success");
    }

    /**
     * 根据ID查询行程
     */
    @GetMapping("/getById")
    public Result<?> getById(@RequestParam Long itineraryUid) {
        return Result.success(iReimItineraryService.getById(itineraryUid));
    }
}
