package com.viessmart.reimburse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.viessmart.reimburse.entity.ReimItinerary;
import com.viessmart.reimburse.entity.ReimSubsidy;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReimFormVO {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键 报销单业务主键
     */
    @TableId(value = "form_uid", type = IdType.AUTO)
    private Long formUid;

    /**
     * 报销单号
     */
    private String orderNo;

    /**
     * 报销标题
     */
    private String title;

    /**
     * 出差事由
     */
    private String reason;

    /**
     * 报销人ID（关联 reim_employee.reimburser_id）
     */
    private String reimburserId;

    /**
     * 报销人姓名
     */
    private String reimburserName;

    /**
     * 报销人工号
     */
    private String reimburserNo;

    /**
     * 报销部门ID（关联 reim_department.reim_department_id）
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门编号
     */
    private String departmentNo;

    /**
     * 费用归属公司ID（关联 reim_company.reim_company_id）
     */
    private String companyId;
    private String companyName;

    /**
     * 业务类型ID（关联 base_business_type.business_type_id）
     */
    private String businessTypeId;
    private String businessTypeName;

    /**
     * 状态：1-未提交 2-已提交 3-已删除 4-已作废
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 餐费补助合计(分)
     */
    private Integer mealAllowanceTotal;

    /**
     * 交通补助合计(分)
     */
    private Integer transportAllowanceTotal;

    /**
     * 通讯补助合计(分)
     */
    private Integer communicationAllowanceTotal;

    /**
     * 补助总金额(分)
     */
    private Integer allowanceTotal;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    // 生成 getter/setter
    // 行程列表
    @Setter
    @Getter
    private List<ReimItinerary> itineraries;

    // 补助列表
    @Setter
    @Getter
    private List<ReimSubsidy> subsidies;

}
