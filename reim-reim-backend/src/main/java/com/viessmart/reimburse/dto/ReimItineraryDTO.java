package com.viessmart.reimburse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 补录行程参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ReimItineraryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 行程ID（更新/删除时必填）
     */
    private Long itineraryUid;

    /**
     * 报销单ID
     */
    @NotNull(message = "报销单ID不能为空")
    private Long formId;

    /**
     * 出行人ID
     */
    private String travelerId;

    /**
     * 出发城市编码
     */
    @NotNull(message = "出发城市不能为空")
    private String departureCityNo;

    /**
     * 到达城市编码
     */
    @NotNull(message = "到达城市不能为空")
    private String arrivalCityNo;

    /**
     * 出发日期
     */
    @NotNull(message = "出发日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate departureDate;

    /**
     * 到达日期
     */
    @NotNull(message = "到达日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate arrivalDate;

    /**
     * 行程说明
     */
    @Size(max = 500, message = "行程说明不能超过500个字符")
    private String description;
}
