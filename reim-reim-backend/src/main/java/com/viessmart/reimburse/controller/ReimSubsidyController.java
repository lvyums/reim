package com.viessmart.reimburse.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.service.IReimSubsidyService;
import com.viessmart.reimburse.vo.ReimSubsidyVO;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reimSubsidy")
public class ReimSubsidyController {


    @Resource
    private IReimSubsidyService ireimSubsidyService;

    /**
     * 根据 formUid 获取补助列表
     * @param formUid
     * @return
     */
    @GetMapping("/list")
    public Result<List<ReimSubsidyVO>> list(@RequestParam Long formUid) {

        return Result.success(ireimSubsidyService.listReimSubsidy(formUid));
    }

    /**
     * 新增补助
     * @param data
     * @return
     */
    @PostMapping("/add")
    public Result<ReimSubsidy > add(@RequestBody ReimSubsidy data) {
        ireimSubsidyService.save(data);
        return Result.success(data);
    }

    /**
     * 修改补助
     * @param data
     * @return
     */
    @PostMapping("/update")
    public Result<ReimSubsidy> update(@RequestBody ReimSubsidy data) {
        ireimSubsidyService.updateById(data);
        return Result.success(data);
    }

    /**
     * 删除补助
     * @param data
     * @return
     */
    @PostMapping("/delete")
    public Result<ReimSubsidy> delete(@RequestBody ReimSubsidy data) {
        ireimSubsidyService.removeById(data.getSubsidyUid());
        return Result.success(data);
    }
}
