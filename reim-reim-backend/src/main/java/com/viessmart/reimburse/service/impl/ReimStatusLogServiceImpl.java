package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.entity.ReimStatusLog;
import com.viessmart.reimburse.mapper.ReimStatusLogMapper;
import com.viessmart.reimburse.service.IReimStatusLogService;
import com.viessmart.reimburse.vo.ReimStatusLogVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 报销单状态变更日志 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class ReimStatusLogServiceImpl extends ServiceImpl<ReimStatusLogMapper, ReimStatusLog> implements IReimStatusLogService {

    @Override
    public List<ReimStatusLog> getLogsByFormUid(Long formUid) {
        LambdaQueryWrapper<ReimStatusLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReimStatusLog::getFormId, formUid)
                .orderByDesc(ReimStatusLog::getOperateTime);
        return list(wrapper);
    }

    @Override
    public void addLog(Long formId, Integer fromStatus, Integer toStatus, String operatorId, String remark) {
        ReimStatusLog log = new ReimStatusLog();
        log.setFormId(formId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setOperatorId(operatorId);
        log.setOperateTime(LocalDateTime.now());
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        log.setUpdateTime(LocalDateTime.now());
        save(log);
    }

    @Override
    public List<ReimStatusLogVO> getLogVOsByFormUid(Long formUid) {
        List<ReimStatusLog> logs = getLogsByFormUid(formUid);

        List<ReimStatusLogVO> voList = logs.stream().map(log -> {
            ReimStatusLogVO vo = new ReimStatusLogVO();
            vo.setLogUid(log.getLogUid());
            vo.setFormId(log.getFormId());
            vo.setFromStatus(log.getFromStatus());
            vo.setToStatus(log.getToStatus());
            vo.setOperatorId(log.getOperatorId());
            vo.setOperateTime(log.getOperateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            vo.setRemark(log.getRemark());
            return vo;
        }).collect(Collectors.toList());

        return voList;
    }
}
