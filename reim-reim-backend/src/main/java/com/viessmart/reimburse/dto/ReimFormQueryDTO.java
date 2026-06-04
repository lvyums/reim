package com.viessmart.reimburse.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报销单分页查询参数
 */
@Data
public class ReimFormQueryDTO {

    //报销单号：模糊查询
    private String orderNo;

     //报销标题（模糊）
    private String title;

    //出差事由（模糊）
    private String reason;

    //"费用归属公司ID"
    private String companyId;

    //"报销部门ID"
    private String departmentId;

    //"报销人ID"
    private String reimburserId;

    //"业务类型ID"
    private String businessTypeId;

    //"单据状态 1-未提交 2-已提交 3-已删除 4-已作废"
    private Integer status;

    //"创建开始时间"
    private LocalDateTime startTime;

    //"创建结束时间"
    private LocalDateTime endTime;
}
