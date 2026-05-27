package com.viessmart.reimburse.dto;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

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
public class ReimItineraryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,行程业务主键（原 id）
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    /**
     * 到达日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    /**
     * 行程说明
     */
    private String description;


}
