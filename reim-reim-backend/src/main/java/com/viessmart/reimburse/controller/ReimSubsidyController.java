package com.viessmart.reimburse.controller;

import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.service.IReimSubsidyService;
import com.viessmart.reimburse.vo.ReimSubsidyVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 补助信息管理
 */
@RestController
@RequestMapping("/api/reimSubsidy")
public class ReimSubsidyController {

    @Resource
    private IReimSubsidyService ireimSubsidyService;

    /**
     * 根据 formUid 获取补助列表
     */
    @GetMapping("/list")
    public Result<List<ReimSubsidyVO>> list(@RequestParam Long formUid) {
        return Result.success(ireimSubsidyService.listReimSubsidy(formUid));
    }

    /**
     * 新增补助
     */
    @PostMapping("/add")
    public Result<ReimSubsidy> add(@Valid @RequestBody ReimSubsidy data) {
        ireimSubsidyService.save(data);
        return Result.success(data);
    }

    /**
     * 修改补助
     */
    @PutMapping("/update")
    public Result<ReimSubsidy> update(@Valid @RequestBody ReimSubsidy data) {
        ireimSubsidyService.updateById(data);
        return Result.success(data);
    }

    /**
     * 删除补助
     */
    @DeleteMapping("/delete")
    public Result<ReimSubsidy> delete(@Valid @RequestBody ReimSubsidy data) {
        ireimSubsidyService.removeById(data.getSubsidyUid());
        return Result.success(data);
    }
}
