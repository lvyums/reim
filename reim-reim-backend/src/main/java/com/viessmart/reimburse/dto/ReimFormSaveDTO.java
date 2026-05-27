package com.viessmart.reimburse.dto;

import lombok.Data;

@Data
public class ReimFormSaveDTO {
    // 标题
    private String title;
    // 出差理由
    private String reason;
    // 报销人
    private String reimburserId;
    // 部门
    private String departmentId;
    // 公司
    private String companyId;
    // 业务类型
    private String businessTypeId;
    // 备注
    private String remark;

}
