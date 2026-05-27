package com.viessmart.reimburse.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReimStatusLogVO {
    /**
     * 自增主键,日志业务主键（原 id）
     */
    private Long logUid;

    /**
     * 报销单业务主键（关联 reim_form.form_uid）
     */
    private Long formId;

    /**
     * 原状态
     */
    private Integer fromStatus;

    /**
     * 新状态
     */
    private Integer toStatus;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作时间
     */
    private String operateTime;

    /**
     * 备注
     */
    private String remark;
}
