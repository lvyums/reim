package com.viessmart.reimburse.vo;

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
 * 补录行程
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)

public class ReimItineraryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,行程业务主键（原 id）
     */
    //@TableId(value = "itinerary_uid", type = IdType.AUTO)
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
     * 行程：出发城市-到达城市
     */
    private String itineraryCity;

    /**
     * 出发日期
     */
    private LocalDate departureDate;

    /**
     * 到达日期
     */
    private LocalDate arrivalDate;

    /**
     * 出差日期：出发日期-到达日期
     */
    private String itineraryDate;
    /**
     * 行程说明
     */
    private String description;


}
