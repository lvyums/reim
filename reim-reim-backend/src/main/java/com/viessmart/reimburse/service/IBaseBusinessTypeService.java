package com.viessmart.reimburse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.viessmart.reimburse.entity.BaseBusinessType;

import java.util.List;


/**
 * <p>
 * 业务类型 服务类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
public interface IBaseBusinessTypeService extends IService<BaseBusinessType> {

    List<BaseBusinessType> getTree();
}
