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
 * 补助日历明细（金额单位：分）
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reim_subsidy_calendar")
public class ReimSubsidyCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,日历明细业务主键（原 id）
     */
    @TableId(value = "calendar_uid", type = IdType.AUTO)
    private Long calendarUid;

    /**
     * 补助信息业务主键（关联 reim_subsidy.subsidy_uid）
     */
    private Long subsidyId;

    /**
     * 具体日期
     */
    private LocalDate date;

    /**
     * 补助城市编码
     */
    private String cityNo;

    /**
     * 餐补是否勾选
     */
    private Integer mealSelected;

    /**
     * 餐补标准(分)
     */
    private Integer mealStandardAmount;

    /**
     * 餐补实际(分)
     */
    private Integer mealActualAmount;

    /**
     * 交补是否勾选
     */
    private Integer transportSelected;

    /**
     * 交补标准(分)
     */
    private Integer transportStandardAmount;

    /**
     * 交补实际(分)
     */
    private Integer transportActualAmount;

    /**
     * 通补是否勾选
     */
    private Integer commSelected;

    /**
     * 通补标准(分)
     */
    private Integer commStandardAmount;

    /**
     * 通补实际(分)
     */
    private Integer commActualAmount;

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
