package com.viessmart.reimburse.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.viessmart.reimburse.entity.ReimForm;
import org.apache.ibatis.annotations.Mapper;


/**
 * <p>
 * 报销单主表（金额单位：分） Mapper 接口
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Mapper
public interface ReimFormMapper extends BaseMapper<ReimForm> {

}
