package com.viessmart.reimburse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 报销单保存参数
 */
@Data
public class ReimFormSaveDTO {
    /**
     * 报销标题
     */
    @NotBlank(message = "报销标题不能为空")
    @Size(max = 200, message = "报销标题不能超过200个字符")
    private String title;

    /**
     * 出差事由
     */
    @Size(max = 500, message = "出差事由不能超过500个字符")
    private String reason;

    /**
     * 报销人ID
     */
    private String reimburserId;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 公司ID
     */
    private String companyId;

    /**
     * 业务类型ID
     */
    private String businessTypeId;

    /**
     * 备注
     */
    @Size(max = 1000, message = "备注不能超过1000个字符")
    private String remark;
}
