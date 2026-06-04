package com.viessmart.reimburse.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.viessmart.reimburse.entity.BaseBusinessType;
import com.viessmart.reimburse.mapper.BaseBusinessTypeMapper;
import com.viessmart.reimburse.service.IBaseBusinessTypeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 业务类型 服务实现类
 * </p>
 *
 * @author author
 * @since 2026-05-12
 */
@Service
public class BaseBusinessTypeServiceImpl extends ServiceImpl<BaseBusinessTypeMapper, BaseBusinessType> implements IBaseBusinessTypeService {

    @Override
    public List<BaseBusinessType> getTree() {
        List<BaseBusinessType> allList = this.list();
        if (allList.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, BaseBusinessType> nodeMap = allList.stream()
                .collect(Collectors.toMap(BaseBusinessType::getBusinessTypeId, node -> node));

        List<BaseBusinessType> rootList = new ArrayList<>();
        for (BaseBusinessType node : allList) {
            String superiorId = node.getSuperiorId();
            if (superiorId == null || superiorId.isEmpty()) {
                rootList.add(node);
            } else {
                BaseBusinessType parent = nodeMap.get(superiorId);
                if (parent != null) {
                    // 动态初始化父节点的 children 列表
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        }
        return rootList;
    }
}
