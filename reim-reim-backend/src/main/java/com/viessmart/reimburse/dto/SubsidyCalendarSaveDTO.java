package com.viessmart.reimburse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 补助日历保存参数
 */
@Data
public class SubsidyCalendarSaveDTO {

    /**
     * 日历明细列表
     */
    @NotEmpty(message = "日历明细不能为空")
    @Valid
    private List<CalendarItemDTO> calendarList;

    /**
     * 单条日历明细
     */
    @Data
    public static class CalendarItemDTO {

        /**
         * 日历ID
         */
        @NotNull(message = "日历ID不能为空")
        private Long calendarUid;

        /**
         * 餐补是否勾选 0-否 1-是
         */
        private Integer mealSelected;

        /**
         * 餐补实际金额（分）
         */
        private Integer mealActualAmount;

        /**
         * 交补是否勾选
         */
        private Integer transportSelected;

        /**
         * 交补实际金额（分）
         */
        private Integer transportActualAmount;

        /**
         * 通补是否勾选
         */
        private Integer commSelected;

        /**
         * 通补实际金额（分）
         */
        private Integer commActualAmount;
    }
}
