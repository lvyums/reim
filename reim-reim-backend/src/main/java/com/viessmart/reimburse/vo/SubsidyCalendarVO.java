package com.viessmart.reimburse.vo;

import lombok.Data;

@Data
public class SubsidyCalendarVO {

    private Long calendarUid;
    /**
     * 具体日期
     */
    private String date;

    /**
     * 补助城市名称
     */
    private String cityName;

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

}
