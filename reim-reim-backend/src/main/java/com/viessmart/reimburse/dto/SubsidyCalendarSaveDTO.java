package com.viessmart.reimburse.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubsidyCalendarSaveDTO {
    private List<CalendarItemDTO> calendarList;

    @Data
    public static class CalendarItemDTO {
        private Long calendarUid;
        private Integer mealSelected;
        private Integer mealActualAmount;
        private Integer transportSelected;
        private Integer transportActualAmount;
        private Integer commSelected;
        private Integer commActualAmount;
    }
}
