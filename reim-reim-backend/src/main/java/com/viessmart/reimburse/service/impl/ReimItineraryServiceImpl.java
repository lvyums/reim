package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.config.SubsidyStandardConfig;
import com.viessmart.reimburse.dto.ReimItineraryDTO;
import com.viessmart.reimburse.entity.ReimForm;
import com.viessmart.reimburse.entity.ReimItinerary;
import com.viessmart.reimburse.entity.ReimSubsidy;
import com.viessmart.reimburse.entity.ReimSubsidyCalendar;
import com.viessmart.reimburse.mapper.ReimItineraryMapper;
import com.viessmart.reimburse.service.*;
import com.viessmart.reimburse.vo.ReimItineraryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 补录行程 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
@Slf4j
public class ReimItineraryServiceImpl extends ServiceImpl<ReimItineraryMapper, ReimItinerary> implements IReimItineraryService {

    @Autowired
    private IReimSubsidyService iReimSubsidyService;

    @Autowired
    private IReimSubsidyCalendarService iReimSubsidyCalendarService;

    @Autowired
    private IBaseCityService baseCityService;

    @Autowired
    private SubsidyStandardConfig subsidyStandardConfig;

    @Autowired
    private IReimFormService iReimFormService;

    /**
     * 增加补录行程 --添加budget_subsidy补助信息表 --》增加reim_subsidy_calendar补助日历明细表
     *
     * @param itineraryDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReimItinerary addReimItinerary(ReimItineraryDTO itineraryDTO) {
        //从全局变量中获取报销单Id
        //Long formUid = FormIdContext.getFormUid();
        //System.out.println("===== 1. addReimItinerary 开始，参数：" + itineraryDTO);
        try{
            if (itineraryDTO.getFormId() == null) {
                    throw new RuntimeException("请先选择报销单！");
            }

            // 出行人ID：优先用前端传来的，否则从报销单表查询
            String travelerId = itineraryDTO.getTravelerId();
            if (travelerId == null || travelerId.isEmpty()) {
                ReimForm reimForm = iReimFormService.getById(itineraryDTO.getFormId());
                if (reimForm == null) {
                    throw new RuntimeException("报销单不存在！");
                }
                travelerId = reimForm.getReimburserId();
            }
            if (travelerId == null || travelerId.isEmpty()) {
                throw new RuntimeException("报销单未设置报销人，无法新增行程");
            }

            LocalDate departure = itineraryDTO.getDepartureDate();
            LocalDate arrival = itineraryDTO.getArrivalDate();

            if (departure.isAfter(arrival)) {
                throw new RuntimeException("出发日期不能晚于到达日期！");
            }
            boolean exists = lambdaQuery()
                        .eq(ReimItinerary::getFormId, itineraryDTO.getFormId())
                        .eq(ReimItinerary::getTravelerId, travelerId)
                        .le(ReimItinerary::getDepartureDate, arrival)
                        .ge(ReimItinerary::getArrivalDate, departure)
                        .exists();

            if (exists) {
                throw new RuntimeException("该时间段已有行程，不允许重复添加！");
            }

                ReimItinerary reimItinerary = new ReimItinerary();
                reimItinerary.setFormId(itineraryDTO.getFormId());
                reimItinerary.setTravelerId(travelerId);
                reimItinerary.setDepartureCityNo(itineraryDTO.getDepartureCityNo());
                reimItinerary.setArrivalCityNo(itineraryDTO.getArrivalCityNo());
                reimItinerary.setDepartureDate(departure);
                reimItinerary.setArrivalDate(arrival);
                reimItinerary.setDeleted(0);
                reimItinerary.setDescription(itineraryDTO.getDescription());
                reimItinerary.setCreateTime(LocalDateTime.now());
                reimItinerary.setUpdateTime(LocalDateTime.now());
                //System.out.println("=== 2. 通过存在性检查，准备保存行程 ===");
                save(reimItinerary);
                //System.out.println("=== 3. 行程保存成功，itineraryUid=" + reimItinerary.getItineraryUid());

                //添加补助信息
                ReimSubsidy reimSubsidy = new ReimSubsidy();
                reimSubsidy.setFormId(itineraryDTO.getFormId());
                reimSubsidy.setItineraryId(reimItinerary.getItineraryUid());
                reimSubsidy.setTravelerId(travelerId);
                reimSubsidy.setStartDate(departure);
                reimSubsidy.setEndDate(arrival);
                reimSubsidy.setSubsidyCityNo(itineraryDTO.getArrivalCityNo());
                reimSubsidy.setApplyAmount(0);
                reimSubsidy.setSubsidyAmount(0);
                reimSubsidy.setDeleted(0);
                reimSubsidy.setCreateTime(LocalDateTime.now());
                reimSubsidy.setUpdateTime(LocalDateTime.now());

                iReimSubsidyService.save(reimSubsidy);
                //添加补助日历明细(从开始日期到结束日期)
                //System.out.println("=== 4. 补助保存成功，subsidyUid=" + reimSubsidy.getSubsidyUid());

                List<ReimSubsidyCalendar> calendarList = new ArrayList<>();

                // 从出发日期开始，一直循环到到达日期
                LocalDate currentDate = departure;
                String cityNo = itineraryDTO.getDepartureCityNo();
                //System.out.println("=== 城市编号：" + cityNo);
                // 1. 从数据库查城市类型
                Integer cityType = baseCityService.getCityTypeByCityNo(cityNo);
                //System.out.println("=== 城市类型：" + cityType);
                if (cityType == null) {
                    throw new RuntimeException("城市编号 " + cityNo + " 无效，无法获取补助标准");
                }
                // 2. 获取对应补助标准
                int[] standard = subsidyStandardConfig.getStandardByCityType(cityType);
                if (standard == null || standard.length < 3) {
                    throw new RuntimeException("补助标准配置错误");
                }

                int standardMeal =0;

                // 循环生成每一天
                while (!currentDate.isAfter(arrival)) {
                    ReimSubsidyCalendar calendar = new ReimSubsidyCalendar();
                    calendar.setSubsidyId(reimSubsidy.getSubsidyUid());
                    calendar.setDate(currentDate);
                    calendar.setCityNo(cityNo);

                    calendar.setMealSelected(1);
                    calendar.setMealStandardAmount(standard[0]); // 餐补标准
                    calendar.setMealActualAmount(standard[0]);

                    calendar.setTransportSelected(1);
                    calendar.setTransportStandardAmount(standard[1]); // 交通标准
                    calendar.setTransportActualAmount(standard[1]);

                    calendar.setCommSelected(1);
                    calendar.setCommStandardAmount(standard[2]); // 通讯标准
                    calendar.setCommActualAmount(standard[2]);

                    calendar.setDeleted(0);
                    calendar.setCreateTime(LocalDateTime.now());
                    calendar.setUpdateTime(LocalDateTime.now());

                    standardMeal += standard[0]+standard[1]+standard[2];
                    calendarList.add(calendar);
                    currentDate = currentDate.plusDays(1);
                }

                reimSubsidy.setApplyAmount(standardMeal);
                iReimSubsidyService.updateById(reimSubsidy);


                iReimSubsidyCalendarService.saveBatch(calendarList);
                //System.out.println("===== 11. 全部保存成功 =====");
                //System.out.println("=== 5. 日历保存成功，数量：" + calendarList.size());
                
                // 返回创建后的行程对象（包含itineraryUid）
                return reimItinerary;
            }catch(Exception e) {
                //System.err.println("===== 新增行程失败，事务回滚 =====");
                e.printStackTrace();
                // 抛出异常 → 让 Controller 捕获 → 前端提示错误
                throw new RuntimeException(e.getMessage());
            }
    }



    /**
     * 删除补录行程
     *
     * @param itineraryDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItinerary(ReimItineraryDTO itineraryDTO) {
        Long itineraryUid = itineraryDTO.getItineraryUid();

        // 1. 删除行程
        this.lambdaUpdate()
                .eq(ReimItinerary::getItineraryUid, itineraryUid)
                .set(ReimItinerary::getDeleted, 1)
                .update();

        // 2. 查询对应的补助
        ReimSubsidy subsidy = iReimSubsidyService.getOne(
                new LambdaQueryWrapper<ReimSubsidy>()
                        .eq(ReimSubsidy::getItineraryId, itineraryUid)
                        .eq(ReimSubsidy::getDeleted, 0)
        );

        if (subsidy == null) return;

        Long subsidyId = subsidy.getSubsidyUid();
        Long formId = subsidy.getFormId();

        // 3. 删除补助日历
        iReimSubsidyCalendarService.lambdaUpdate()
                .eq(ReimSubsidyCalendar::getSubsidyId, subsidyId)
                .set(ReimSubsidyCalendar::getDeleted, 1)
                .update();

        // 4. 删除补助
        iReimSubsidyService.lambdaUpdate()
                .eq(ReimSubsidy::getSubsidyUid, subsidyId)
                .set(ReimSubsidy::getDeleted, 1)
                .update();

        // 5. 重新计算报销单总金额（仅统计剩余未删除的补助）
        recalculateFormTotals(formId);
    }

    /**
     * 重新计算报销单的补助合计（从剩余未删除的补助日历汇总）
     */
    private void recalculateFormTotals(Long formUid) {
        // 查询该报销单下所有未删除的补助
        List<ReimSubsidy> remaining = iReimSubsidyService.lambdaQuery()
                .eq(ReimSubsidy::getFormId, formUid)
                .eq(ReimSubsidy::getDeleted, 0)
                .list();

        int mealTotal = 0, transportTotal = 0, commTotal = 0;
        for (ReimSubsidy sub : remaining) {
            List<ReimSubsidyCalendar> calendars = iReimSubsidyCalendarService.lambdaQuery()
                    .eq(ReimSubsidyCalendar::getSubsidyId, sub.getSubsidyUid())
                    .eq(ReimSubsidyCalendar::getDeleted, 0)
                    .list();
            for (ReimSubsidyCalendar cal : calendars) {
                if (cal.getMealSelected() == 1) mealTotal += cal.getMealActualAmount();
                if (cal.getTransportSelected() == 1) transportTotal += cal.getTransportActualAmount();
                if (cal.getCommSelected() == 1) commTotal += cal.getCommActualAmount();
            }
        }

        ReimForm form = new ReimForm();
        form.setFormUid(formUid);
        form.setMealAllowanceTotal(mealTotal);
        form.setTransportAllowanceTotal(transportTotal);
        form.setCommunicationAllowanceTotal(commTotal);
        form.setAllowanceTotal(mealTotal + transportTotal + commTotal);
        iReimFormService.updateById(form);
    }

    /**
     * 复制补录行程复制行程
     * @param itineraryDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void  copyItinerary(ReimItineraryDTO itineraryDTO) {
        // 1. 前端传过来的是【被复制的行程ID】
        Long sourceId = itineraryDTO.getItineraryUid();
        if (sourceId == null) {
            throw new RuntimeException("请选择要复制的行程");
        }

        // 2. 查询原数据
        ReimItinerary source = getById(sourceId);
        if (source == null) {
            throw new RuntimeException("行程不存在");
        }


        itineraryDTO.setDepartureCityNo(source.getDepartureCityNo());
        itineraryDTO.setArrivalCityNo(source.getArrivalCityNo());
        itineraryDTO.setDepartureDate(source.getDepartureDate());
        itineraryDTO.setArrivalDate(source.getArrivalDate());
        itineraryDTO.setDescription(source.getDescription());

        addReimItinerary(itineraryDTO);
    }

    @Override
    public List<ReimItineraryVO> listItinerary(long formUid) {
        //更具报销单Id和报销人Id查询（不展示已经被逻辑删除的）
        //根据formUid获得reimburserId\
        ReimForm reimForm = iReimFormService.getById(formUid) ;
        if (reimForm == null) {
            throw new RuntimeException("报销单不存在");
        }
        String reimburserId = reimForm.getReimburserId();

        if (reimburserId == null) {
            throw new RuntimeException("报销单未找到报销人信息！");
        }

        LambdaQueryWrapper<ReimItinerary> lambdaQuery = new LambdaQueryWrapper<>();
        lambdaQuery.eq(ReimItinerary::getFormId, formUid)
                .eq(ReimItinerary::getTravelerId, reimburserId)
                .eq(ReimItinerary::getDeleted, 0)
                .orderByAsc(ReimItinerary::getDepartureDate);
        List<ReimItinerary> itineraryList = list(lambdaQuery);

        // ========== 优化：批量查询城市信息，避免 N+1 问题 ==========
        // 1. 收集所有不重复的城市编号
        Set<String> cityNos = itineraryList.stream()
                .flatMap(itinerary -> java.util.stream.Stream.of(
                        itinerary.getDepartureCityNo(),
                        itinerary.getArrivalCityNo()
                ))
                .filter(cityNo -> cityNo != null && !cityNo.isBlank())
                .collect(Collectors.toSet());

        // 2. 批量查询城市信息，构建 Map 缓存
        Map<String, String> cityNameMap;
        if (!cityNos.isEmpty()) {
            List<com.viessmart.reimburse.entity.BaseCity> cities = baseCityService.lambdaQuery()
                    .in(com.viessmart.reimburse.entity.BaseCity::getCityNo, cityNos)
                    .list();
            cityNameMap = cities.stream()
                    .collect(Collectors.toMap(
                            com.viessmart.reimburse.entity.BaseCity::getCityNo,
                            com.viessmart.reimburse.entity.BaseCity::getCityName,
                            (a, b) -> a  // 处理重复 key
                    ));
        } else {
            cityNameMap = java.util.Collections.emptyMap();
        }

        List<ReimItineraryVO> volist = new ArrayList<>();
        for (ReimItinerary itinerary : itineraryList) {
            ReimItineraryVO vo = new ReimItineraryVO();
            // 一一赋值（你VO里有什么字段就拷什么）
            vo.setItineraryUid(itinerary.getItineraryUid());
            vo.setFormId(itinerary.getFormId());
            vo.setTravelerId(itinerary.getTravelerId());
            vo.setDepartureCityNo(itinerary.getDepartureCityNo());
            vo.setArrivalCityNo(itinerary.getArrivalCityNo());

            // 通过 Map 缓存获取城市名称，避免重复查询
            String departureCityName = getCityNameFromMap(itinerary.getDepartureCityNo(), cityNameMap);
            String arrivalCityName = getCityNameFromMap(itinerary.getArrivalCityNo(), cityNameMap);
            vo.setItineraryCity(departureCityName + "至" + arrivalCityName);


            //通过NO得到name
            //vo.setItineraryCity(baseCityService.getCityNameByCityNo(itinerary.getDepartureCityNo())+"至"+baseCityService.getCityNameByCityNo(itinerary.getArrivalCityNo()));

            vo.setDepartureDate(itinerary.getDepartureDate());
            vo.setArrivalDate(itinerary.getArrivalDate());
            vo.setItineraryDate(itinerary.getDepartureDate() + "至" + itinerary.getArrivalDate());
            vo.setDescription(itinerary.getDescription());
            // 有其他字段继续 set...
            volist.add(vo);
        }
        return volist;
    }


    /**
     * 从 Map 缓存中获取城市名称
     * @param cityNo 城市编号
     * @param cityNameMap 城市名称缓存
     * @return 城市名称，如果不存在则返回原城市编号
     */
    private String getCityNameFromMap(String cityNo, Map<String, String> cityNameMap) {
        if (cityNo == null || cityNo.isBlank()) {
            return cityNo;
        }
        return cityNameMap.getOrDefault(cityNo, cityNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItinerary(ReimItineraryDTO itineraryDTO) {
        //System.out.println("===== 1. 方法开始 =====");
        Long itineraryUid = itineraryDTO.getItineraryUid();
        if (itineraryUid == null) {
            log.debug("行程ID不能为空") ;
            throw new RuntimeException("行程ID不能为空");

        }

        LocalDate departure = itineraryDTO.getDepartureDate();
        LocalDate arrival = itineraryDTO.getArrivalDate();

        if (departure.isAfter(arrival)) {
            log.debug("出发日期不能晚于到达日期");
            throw new RuntimeException("出发日期不能晚于到达日期");
        }
        //System.out.println("===== 2. 参数校验通过 =====");
        // ====================== 1. 更新行程信息 ======================
        ReimItinerary itinerary = new ReimItinerary();
        itinerary.setFormId(itineraryDTO.getFormId());
        itinerary.setTravelerId(itineraryDTO.getTravelerId());
        itinerary.setItineraryUid(itineraryUid);
        itinerary.setDepartureCityNo(itineraryDTO.getDepartureCityNo());
        itinerary.setArrivalCityNo(itineraryDTO.getArrivalCityNo());
        itinerary.setDepartureDate(departure);
        itinerary.setArrivalDate(arrival);
        itinerary.setDeleted(0);
        itinerary.setDescription(itineraryDTO.getDescription());
        //itinerary.setCreateTime(LocalDateTime.now());
        itinerary.setUpdateTime(LocalDateTime.now());
        updateById(itinerary);
        log.debug("行程信息更新成功");
        //System.out.println("更新行程完成，准备查询旧补助");

        //System.out.println("===== 5. 开始删除旧日历和补助 =====");
        // ====================== 2. 真删除旧补助 + 旧日历 ======================
        ReimSubsidy oldSubsidy = iReimSubsidyService.getOne(
                new QueryWrapper<ReimSubsidy>().eq("itinerary_id", itineraryUid)
        );
        log.debug("旧补助信息删除成功");

        if (oldSubsidy != null) {
            try{
            Long subsidyId = oldSubsidy.getSubsidyUid();

            // 真删除所有日历
            iReimSubsidyCalendarService.remove(
                    new QueryWrapper<ReimSubsidyCalendar>().eq("subsidy_id", subsidyId)
            );

            // 真删除补助
            iReimSubsidyService.removeById(subsidyId);
            log.debug("旧日历信息删除成功");
            //System.out.println("===== 6. 删除完成 =====");
        } catch (Exception e) {
            //System.out.println("===== 删除旧数据时发生异常 =====");
                e.printStackTrace();  // 强制打印堆栈
                throw e;  // 继续抛出，让事务回滚
            }
        }
        //System.out.println("===== 6. 删除完成 =====");
        try{
        // ====================== 3. 全新生成补助 ======================
        ReimSubsidy newSubsidy = new ReimSubsidy();
        newSubsidy.setItineraryId(itineraryUid);
        newSubsidy.setFormId(itineraryDTO.getFormId());
        newSubsidy.setTravelerId(itineraryDTO.getTravelerId());
        newSubsidy.setStartDate(itineraryDTO.getDepartureDate());
        newSubsidy.setEndDate(itineraryDTO.getArrivalDate());
        newSubsidy.setSubsidyCityNo(itineraryDTO.getDepartureCityNo());
        newSubsidy.setDeleted(0);
        newSubsidy.setCreateTime(LocalDateTime.now());
        newSubsidy.setUpdateTime(LocalDateTime.now());
        iReimSubsidyService.save(newSubsidy);
        log.debug("新补助信息生成成功");

        // ====================== 4. 全新生成日历 ======================
        List<ReimSubsidyCalendar> calendarList = new ArrayList<>();
        LocalDate currentDate = departure;

        String cityNo = itineraryDTO.getDepartureCityNo();
        if (cityNo == null || cityNo.isBlank()) {
            throw new RuntimeException("出发城市不能为空");
        }
        Integer cityType = baseCityService.getCityTypeByCityNo(cityNo);
        if (cityType == null) {
            throw new RuntimeException("城市编号无效，无法获取补助标准");
        }
        int[] standard = subsidyStandardConfig.getStandardByCityType(cityType);
        //记录标准金额
        int standardAmount =0;
        while (!currentDate.isAfter(arrival)) {
            ReimSubsidyCalendar calendar = new ReimSubsidyCalendar();
            calendar.setSubsidyId(newSubsidy.getSubsidyUid());
            calendar.setDate(currentDate);
            calendar.setCityNo(itineraryDTO.getDepartureCityNo());

            calendar.setMealSelected(1);
            calendar.setMealStandardAmount(standard[0]);
            calendar.setMealActualAmount(standard[0]);

            calendar.setTransportSelected(1);
            calendar.setTransportStandardAmount(standard[1]);
            calendar.setTransportActualAmount(standard[1]);

            calendar.setCommSelected(1);
            calendar.setCommStandardAmount(standard[2]);
            calendar.setCommActualAmount(standard[2]);

            calendar.setDeleted(0);
            calendar.setCreateTime(LocalDateTime.now());
            calendar.setUpdateTime(LocalDateTime.now());
            standardAmount+=standard[0]+standard[1]+standard[2];
            calendarList.add(calendar);
            currentDate = currentDate.plusDays(1);
        }

        //iReimSubsidyService.save(newSubsidy);
            // 更新补助的申请金额（总标准金额）需要除以100，保留两位小数
         newSubsidy.setApplyAmount(standardAmount);
         iReimSubsidyService.updateById(newSubsidy);  // 或再次保存，但建议更新
        iReimSubsidyCalendarService.saveBatch(calendarList);
        log.debug("新日历信息生成成功");
        } catch (Exception e) {
            //System.out.println("===== 捕获到异常: " + e.getMessage() + " =====");
            log.error("生成新日历信息时出错", e);
            throw new RuntimeException(e);
        }
        //System.out.println("===== 10. 方法正常结束 =====");
    }

}