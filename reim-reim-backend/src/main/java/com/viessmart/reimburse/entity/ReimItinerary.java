package com.viessmart.reimburse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 补录行程
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reim_itinerary")
public class ReimItinerary implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,行程业务主键（原 id）
     */
    @TableId(value = "itinerary_uid", type = IdType.AUTO)
    private Long itineraryUid;

    /**
     * 报销单业务主键（关联 reim_form.form_uid）
     */
    private Long  formId;

    /**
     * 出行人ID（关联 reim_employee.reimburser_id）
     */
    private String travelerId;

    /**
     * 出发城市编码
     */
    private String departureCityNo;

    /**
     * 到达城市编码
     */
    private String arrivalCityNo;

    /**
     * 出发日期
     */
    private LocalDate departureDate;

    /**
     * 到达日期
     */
    private LocalDate arrivalDate;

    /**
     * 行程说明
     */
    private String description;

    /**
     * 逻辑删除 0-未删 1-已删
     */
    @TableLogic(value = "0", delval = "1")
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
