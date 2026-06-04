package com.viessmart.reimburse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 业务类型
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("base_business_type")
public class BaseBusinessType implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    /**
     * 业务类型业务主键
     */
    private String businessTypeId;

    /**
     * 业务类型编号
     */
    private String businessTypeNo;

    /**
     * 业务类型名称
     */
    private String businessTypeName;

    /**
     * 是否有下级节点 0:无 1:有
     */
    private Integer thereSubordinateNode;

    /**
     * 上级业务类型业务主键
     */
    private String superiorId;

    /**
     * 层级 1-一级 2-二级 3-三级
     */
    private Integer level;

    /**
     * 状态 1-启用 0-禁用
     */
    private Integer status;

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

    /**
     * 业务类型的树形结构
     */
    @TableField(exist = false)
    private List<BaseBusinessType> children;

    public List<BaseBusinessType> getChildren() {
        return children;
    }

    public void setChildren(List<BaseBusinessType> baseBusinessTypes) {
        this.children = baseBusinessTypes;
    }
}
