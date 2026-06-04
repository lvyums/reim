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
    @PostMapping("/add")
    public Result<ReimItinerary> addItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        ReimItinerary itinerary = iReimItineraryService.addReimItinerary(itineraryDTO);
        return Result.success(itinerary);
    }

    /**
     * 查询行程列表
     */
    @GetMapping("/list")
    public Result<List<ReimItineraryVO>> listItineraries(@RequestParam Long formUid) {
        return Result.success(iReimItineraryService.listItinerary(formUid));
    }

    /**
     * 修改行程
     */
    @PutMapping("/update")
    public Result<String> updateItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.updateItinerary(itineraryDTO);
        return Result.success("修改成功");
    }

    /**
     * 删除行程
     */
    @DeleteMapping("/delete")
    public Result<String> deleteItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.deleteItinerary(itineraryDTO);
        return Result.success("删除成功");
    }

    /**
     * 复制行程
     */
    @PostMapping("/copy")
    public Result<String> copyItineraries(@Valid @RequestBody ReimItineraryDTO itineraryDTO) {
        iReimItineraryService.copyItinerary(itineraryDTO);
        log.info("复制行程成功");
        return Result.success("复制成功");
    }

    /**
     * 根据ID查询行程
     */
    @GetMapping("/getById")
    public Result<ReimItinerary> getById(@RequestParam Long itineraryUid) {
        return Result.success(iReimItineraryService.getById(itineraryUid));
    }
}
