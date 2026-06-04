package com.viessmart.reimburse.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.viessmart.reimburse.common.Result;
import com.viessmart.reimburse.dto.ReimFormQueryDTO;
import com.viessmart.reimburse.dto.ReimFormSaveDTO;
import com.viessmart.reimburse.entity.ReimForm;
import com.viessmart.reimburse.service.IReimFormService;
import com.viessmart.reimburse.service.IReimStatusLogService;
import com.viessmart.reimburse.vo.ReimFormVO;
import com.viessmart.reimburse.vo.ReimStatusLogVO;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 报销单管理
 */
@RestController
@RequestMapping("/api/reimbursement")
public class ReimFormController {

    @Resource
    private IReimFormService reimFormService;

    @Resource
    private IReimStatusLogService reimStatusLogService;

    /**
     * 报销单分页列表查询
     */
    @GetMapping("/forms")
    public Result<Page<ReimFormVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            ReimFormQueryDTO reimFormQueryDTO
    ) {
        Page<ReimForm> pageParam = new Page<>(page, size);
        Page<ReimFormVO> voPage = reimFormService.pageList(pageParam, reimFormQueryDTO);
        return Result.success(voPage);
    }

    /**
     * 新增空白报销单
     */
    @PostMapping("/forms")
    public Result<ReimFormVO> createEmptyForm(@RequestBody ReimFormSaveDTO dto) {
        return Result.success(reimFormService.createEmptyForm(dto));
    }

    /**
     * 报销单详情查询（含行程+补助）
     */
    @GetMapping("/forms/{formUid}")
    public Result<ReimFormVO> getFormDetail(@PathVariable Long formUid) {
        return Result.success(reimFormService.getFormDetail(formUid));
    }

    /**
     * 保存/编辑报销单
     */
    @PutMapping("/forms/{formUid}")
    public Result<ReimFormVO> updateForm(
            @PathVariable Long formUid,
            @Valid @RequestBody ReimFormSaveDTO dto
    ) {
        return Result.success(reimFormService.updateForm(formUid, dto));
    }

    /**
     * 删除报销单
     */
    @DeleteMapping("/forms/{formUid}")
    public Result<String> deleteForm(@PathVariable Long formUid) {
        reimFormService.deleteForm(formUid);
        return Result.success("删除成功");
    }

    /**
     * 提交报销单
     */
    @PostMapping("/forms/{formUid}/submit")
    public Result<String> submitForm(@PathVariable Long formUid) {
        reimFormService.submitForm(formUid);
        return Result.success("提交成功");
    }

    /**
     * 作废报销单
     */
    @PostMapping("/forms/{formUid}/cancel")
    public Result<String> cancelForm(@PathVariable Long formUid) {
        reimFormService.cancelForm(formUid);
        return Result.success("作废成功");
    }

    /**
     * 撤回报销单（已提交 → 未提交）
     */
    @PostMapping("/forms/{formUid}/withdraw")
    public Result<String> withdrawForm(@PathVariable Long formUid) {
        reimFormService.withdrawForm(formUid);
        return Result.success("撤回成功");
    }

    /**
     * 查询报销单状态变更日志
     */
    @GetMapping("/forms/{formUid}/logs")
    public Result<List<ReimStatusLogVO>> getFormLogs(@PathVariable Long formUid) {
        List<ReimStatusLogVO> voList = reimStatusLogService.getLogVOsByFormUid(formUid);
        return Result.success(voList);
    }
}
