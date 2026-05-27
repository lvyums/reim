package com.viessmart.reimburse.dto;
import lombok.Data;


@Data
public class PageRequest {
    /**
     * 页码,从1开始
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
