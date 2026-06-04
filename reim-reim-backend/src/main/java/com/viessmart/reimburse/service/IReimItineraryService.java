package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.dto.ReimItineraryDTO;
import com.viessmart.reimburse.entity.ReimItinerary;

import com.viessmart.reimburse.vo.ReimItineraryVO;

import java.util.List;


/**
 * <p>
 * 补录行程 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IReimItineraryService extends IService<ReimItinerary> {


    /**
     * 添加补录行程
     * @param itineraryDTO
     * @return 创建后的行程对象（包含itineraryUid）
     */
    ReimItinerary addReimItinerary(ReimItineraryDTO itineraryDTO);



    /**
     * 删除补录行程
     * @param itineraryDTO
     */
    void deleteItinerary(ReimItineraryDTO itineraryDTO);

    /**
     * 复制补录行程
     * @param itineraryDTO
     */
    void copyItinerary(ReimItineraryDTO itineraryDTO);

    /**
     * 查询补录行程
     * @param formUid
     * @return
     */
    List<ReimItineraryVO> listItinerary(long formUid);


    /**
     * 更新补录行程
     * @param itineraryDTO
     */
    void updateItinerary(ReimItineraryDTO itineraryDTO);
}
