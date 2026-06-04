
package com.viessmart.reimburse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 报销单主表（金额单位：分）
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reim_form")
public class ReimForm implements Serializable {

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
     * 报销部门ID（关联 reim_department.reim_department_id）
     */
    private String departmentId;

    /**
     * 费用归属公司ID（关联 reim_company.reim_company_id）
     */
    private String companyId;

    /**
     * 业务类型ID（关联 base_business_type.business_type_id）
     */
    private String businessTypeId;

    /**
     * 状态：1-未提交 2-已提交 3-已删除 4-已作废
     */
    private Integer status;

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
     * 备注
     */
    private String remark;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除 0-未删 1-已删
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
