package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.entity.BaseCity;
import com.viessmart.reimburse.entity.ReimEmployee;
import com.viessmart.reimburse.entity.ReimItinerary;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.mapper.BaseCityMapper;
import com.viessmart.reimburse.mapper.ReimEmployeeMapper;
import com.viessmart.reimburse.mapper.ReimSubsidyMapper;
import com.viessmart.reimburse.service.IReimItineraryService;
import com.viessmart.reimburse.service.IReimSubsidyService;
import com.viessmart.reimburse.vo.ReimSubsidyVO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 补助信息（金额单位：分） 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class ReimSubsidyServiceImpl extends ServiceImpl<ReimSubsidyMapper, ReimSubsidy> implements IReimSubsidyService {

    @Resource
    private BaseCityMapper baseCityMapper;

    @Resource
    private ReimEmployeeMapper employeeMapper;

    // 注入 行程 Service
    @Resource
    private IReimItineraryService itineraryService;
    @Override
    public List<ReimSubsidyVO> listReimSubsidy(Long formUid) {
        // 1. 查询补助表
        LambdaQueryWrapper<ReimSubsidy> qw = new LambdaQueryWrapper<>();
        qw.eq(ReimSubsidy::getFormId, formUid);
        qw.eq(ReimSubsidy::getDeleted, 0);

        List<ReimSubsidy> list = list(qw);
        if (list.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量查询优化：收集所有需要查询的 ID

        // 收集所有出行人ID
        Set<String> travelerIds = list.stream()
                .map(ReimSubsidy::getTravelerId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());

        // 收集所有行程ID
        Set<Long> itineraryIds = list.stream()
                .map(ReimSubsidy::getItineraryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 收集所有城市编号（补贴城市 + 行程的出发/到达城市）
        Set<String> subsidyCityNos = list.stream()
                .map(ReimSubsidy::getSubsidyCityNo)
                .filter(cityNo -> cityNo != null && !cityNo.isBlank())
                .collect(Collectors.toSet());

        // 批量查询员工信息
        Map<String, String> travelerNameMap;
        if (!travelerIds.isEmpty()) {
            List<ReimEmployee> employees = employeeMapper.selectList(
                    new LambdaQueryWrapper<ReimEmployee>()
                            .in(ReimEmployee::getReimburserId, travelerIds)
            );
            travelerNameMap = employees.stream()
                    .collect(Collectors.toMap(
                            ReimEmployee::getReimburserId,
                            ReimEmployee::getReimburserName,
                            (a, b) -> a
                    ));
        } else {
            travelerNameMap = Collections.emptyMap();
        }

        // 批量查询行程信息
        Map<Long, ReimItinerary> itineraryMap;
        if (!itineraryIds.isEmpty()) {
            List<ReimItinerary> itineraries = itineraryService.listByIds(itineraryIds);
            itineraryMap = itineraries.stream()
                    .collect(Collectors.toMap(
                            ReimItinerary::getItineraryUid,
                            Function.identity(),
                            (a, b) -> a
                    ));

            // 收集行程中的城市编号
            Set<String> itineraryCityNos = itineraries.stream()
                    .flatMap(itinerary -> Stream.of(
                            itinerary.getDepartureCityNo(),
                            itinerary.getArrivalCityNo()
                    ))
                    .filter(cityNo -> cityNo != null && !cityNo.isBlank())
                    .collect(Collectors.toSet());
            subsidyCityNos.addAll(itineraryCityNos);
        } else {
            itineraryMap = Collections.emptyMap();
        }


        // 批量查询城市信息
        Map<String, String> cityNameMap;
        if (!subsidyCityNos.isEmpty()) {
            List<BaseCity> cities = baseCityMapper.selectList(
                    new LambdaQueryWrapper<BaseCity>()
                            .in(BaseCity::getCityNo, subsidyCityNos)
            );
            cityNameMap = cities.stream()
                    .collect(Collectors.toMap(
                            BaseCity::getCityNo,
                            BaseCity::getCityName,
                            (a, b) -> a
                    ));
        } else {
            cityNameMap = Collections.emptyMap();
        }


        List<ReimSubsidyVO> voList = new ArrayList<>();

        for (ReimSubsidy subsidy : list) {
            ReimSubsidyVO vo = new ReimSubsidyVO();
            vo.setSubsidyUid(subsidy.getSubsidyUid());
            vo.setFormId(subsidy.getFormId());
            vo.setItineraryId(subsidy.getItineraryId());
            vo.setTravelerName(getNameFromMap(subsidy.getTravelerId(), travelerNameMap));
            vo.setTravelerId(subsidy.getTravelerId());
            vo.setStartDate(subsidy.getStartDate());
            vo.setEndDate(subsidy.getEndDate());
            vo.setReimSubsidyDate(subsidy.getStartDate() + " ~ " + subsidy.getEndDate());
            vo.setSubsidyCityNo(subsidy.getSubsidyCityNo());
            vo.setSubsidyCityName(getCityNameFromMap(subsidy.getSubsidyCityNo(), cityNameMap));
            vo.setSubsidyDays((int) (subsidy.getEndDate().toEpochDay() - subsidy.getStartDate().toEpochDay() + 1));
            vo.setApplyAmount(subsidy.getApplyAmount());
            vo.setSubsidyAmount(subsidy.getSubsidyAmount());

            // 从缓存获取行程信息
            Long itineraryId = subsidy.getItineraryId();
            ReimItinerary itinerary = itineraryMap.get(itineraryId);

            if (itinerary != null) {
                // 从缓存获取城市名称
                String departureCity = getCityNameFromMap(itinerary.getDepartureCityNo(), cityNameMap);
                String arrivalCity = getCityNameFromMap(itinerary.getArrivalCityNo(), cityNameMap);
                vo.setSubsidyCity(departureCity + " - " + arrivalCity);
            } else {
                vo.setSubsidyCity("未知行程");
            }
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 从缓存获取员工姓名
     */
    private String getNameFromMap(String travelerId, Map<String, String> travelerNameMap) {
        if (travelerId == null || travelerId.isBlank()) {
            return "未知姓名";
        }
        return travelerNameMap.getOrDefault(travelerId, travelerId);
    }

    /**
     * 从缓存获取城市名称
     */
    private String getCityNameFromMap(String cityNo, Map<String, String> cityNameMap) {
        if (cityNo == null || cityNo.isBlank()) {
            return "未知城市";
        }
        return cityNameMap.getOrDefault(cityNo, cityNo);
    }




    /**
     * 根据行程ID查询补录行程（单条查询用）
     */
    private ReimItinerary getItineraryById(Long itineraryId) {
        if (itineraryId == null) return null;
        return itineraryService.getById(itineraryId);
    }

    /**
     * 从数据库城市字典表获取城市名称（单条查询用）
     */
    private String getCityName(String cityNo) {
        if (cityNo == null || cityNo.trim().isEmpty()) {
            return "未知城市";
        }
        LambdaQueryWrapper<BaseCity> qw = new LambdaQueryWrapper<>();
        qw.eq(BaseCity::getCityNo, cityNo);
        BaseCity city = baseCityMapper.selectOne(qw);
        return city != null ? city.getCityName() : cityNo;
    }

    /**
     * 根据姓名编码查名称（单条查询用）
     */
    private String getTravelerName(String travelerId) {
        if (travelerId == null || travelerId.trim().isEmpty()) {
            return "未知姓名";
        }
        LambdaQueryWrapper<ReimEmployee> qw = new LambdaQueryWrapper<>();
        qw.eq(ReimEmployee::getReimburserId, travelerId);
        ReimEmployee employee = employeeMapper.selectOne(qw);
        return employee != null ? employee.getReimburserName() : travelerId;
    }

}
