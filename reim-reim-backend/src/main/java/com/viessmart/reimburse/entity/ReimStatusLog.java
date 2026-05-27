package com.viessmart.reimburse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 报销单状态变更日志
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("reim_status_log")
public class ReimStatusLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键,日志业务主键（原 id）
     */
    @TableId(value = "log_uid", type = IdType.AUTO)
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
    private LocalDateTime operateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
