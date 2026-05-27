package com.viessmart.reimburse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 补助信息（金额单位：分）
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reim_subsidy")
public class ReimSubsidy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,补助信息业务主键（原 id）
     */
    @TableId(value = "subsidy_uid", type = IdType.AUTO)
    private Long subsidyUid;

    /**
     * 报销单业务主键（关联 reim_form.form_uid）
     */
    private Long  formId;

    /**
     * 行程业务主键（关联 reim_itinerary.itinerary_uid，一对一）
     */
    private Long  itineraryId;

    /**
     * 出行人ID
     */
    private String travelerId;

    /**
     * 补助开始日期
     */
    private LocalDate startDate;

    /**
     * 补助结束日期
     */
    private LocalDate endDate;

    /**
     * 补助城市编码
     */
    private String subsidyCityNo;

    /**
     * 申请金额(分)
     */
    private Integer applyAmount;

    /**
     * 实际补助(分)
     */
    private Integer subsidyAmount;

    /**
     * 逻辑删除
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
