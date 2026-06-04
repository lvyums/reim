package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.entity.BaseCity;
import com.viessmart.reimburse.mapper.BaseCityMapper;
import com.viessmart.reimburse.service.IBaseCityService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 城市 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class BaseCityServiceImpl extends ServiceImpl<BaseCityMapper, BaseCity> implements IBaseCityService {

    /**
     * 查询城市名称
     * @param cityNo
     * @return
     */
    @Override
    public String getCityNameByCityNo(String cityNo) {
        if (cityNo == null ||cityNo.isBlank()){
            return cityNo;
        }
        BaseCity city = this.lambdaQuery()
                .eq(BaseCity::getCityNo, cityNo)
                .one();

        return city == null ? cityNo : city.getCityName();
    }

    /**
     * 查询城市类型
     * @param cityNo
     * @return
     */
    @Override
    public Integer getCityTypeByCityNo(String cityNo) {
        if (cityNo == null || cityNo.isBlank()) {
            return 3; // 空默认按三线城市
        }

        BaseCity city = lambdaQuery()
                .eq(BaseCity::getCityNo, cityNo)
                .one();

        return city == null ? 3 : city.getCityType();
    }
}
