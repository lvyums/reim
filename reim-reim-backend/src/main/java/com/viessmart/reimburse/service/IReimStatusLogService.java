package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.entity.ReimStatusLog;
import com.viessmart.reimburse.vo.ReimStatusLogVO;

import java.util.List;


/**
 * <p>
 * 报销单状态变更日志 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IReimStatusLogService extends IService<ReimStatusLog> {

    /**
     * 根据表单编号查询状态变更日志
     * @param formUid
     * @return
     */
    List<ReimStatusLog> getLogsByFormUid(Long formUid);

    /**
     * 添加状态变更日志
     * @param formId
     * @param fromStatus
     * @param toStatus
     * @param operatorId
     * @param remark
     */
    void addLog(Long formId, Integer fromStatus, Integer toStatus, String operatorId, String remark);

    /**
     * 根据表单编号查询状态变更日志（返回VO列表）
     * @param formUid
     * @return
     */
    List<ReimStatusLogVO> getLogVOsByFormUid(Long formUid);

}
