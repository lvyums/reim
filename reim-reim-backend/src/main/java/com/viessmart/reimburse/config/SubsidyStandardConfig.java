package com.viessmart.reimburse.config;

import org.springframework.stereotype.Component;

/**
 * 设定补助标准
 */
@Component
public class SubsidyStandardConfig {
    // 一线：餐补100，交通40，通讯40（单位：分）
    private static final int[] FIRST = {10000, 4000, 4000};
    // 二线：餐补80，交通40，通讯40
    private static final int[] SECOND = {8000, 4000, 4000};
    // 三线：餐补50，交通40，通讯40
    private static final int[] THIRD = {5000, 4000, 4000};

    public int[] getStandardByCityType(Integer cityType) {
        if (cityType == null) {
            return THIRD;
        }
        return switch (cityType) {
            case 1 -> FIRST;
            case 2 -> SECOND;
            default -> THIRD;
        };
    }

}
