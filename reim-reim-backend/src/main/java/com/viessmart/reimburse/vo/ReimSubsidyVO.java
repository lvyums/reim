package com.viessmart.reimburse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.viessmart.reimburse.entity.ReimSubsidy;
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

public class ReimSubsidyVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,补助信息业务主键（原 id）
     */
    //@TableId(value = "subsidy_uid", type = IdType.AUTO)
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
     * 出行人姓名
     */
    private String travelerName;

    /**
     * 补助开始日期
     */
    private LocalDate startDate;

    /**
     * 补助结束日期
     */
    private LocalDate endDate;

    /**
     * 出差日期:开始日期-结束日期
     */
    private String  reimSubsidyDate;
    /**
     * 补助城市编码
     */
    private String subsidyCityNo;

    /**
     * 补助城市
     */
    private String subsidyCityName;

    /**
     * 补助天数
     */
    private Integer subsidyDays;

    /**
     * 补助行程
     */
    private String subsidyCity;

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
