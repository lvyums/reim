package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.entity.BaseCity;


/**
 * <p>
 * 城市 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IBaseCityService extends IService<BaseCity> {
    // 根据城市编号获取城市名称
    String getCityNameByCityNo(String cityNo);
    // 根据城市编号获取城市类型（1/2/3线）
    Integer getCityTypeByCityNo(String cityNo);

}
