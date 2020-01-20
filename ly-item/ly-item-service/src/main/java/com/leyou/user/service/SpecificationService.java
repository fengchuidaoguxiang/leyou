package com.leyou.user.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.user.mapper.SpecGroupMapper;
import com.leyou.user.mapper.SpecParamMapper;
import com.leyou.user.pojo.SpecGroup;
import com.leyou.user.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper groupMapper;

    @Autowired
    private SpecParamMapper paramMapper;


    public List<SpecGroup> queryGroupByCid(Long cid) {
        // 查询条件
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        //  查询
        List<SpecGroup> list = groupMapper.select(specGroup);
        if(CollectionUtils.isEmpty(list)){
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);

        List<SpecParam> list = paramMapper.select(param);
        if(CollectionUtils.isEmpty(list)){
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        // 查询规格组
        List<SpecGroup> specGroups = queryGroupByCid(cid);
        // 查询当前分类下的参数
        List<SpecParam> specParams = queryParamList(null, cid, null);
        // 先把规格参数变成map, map的key是规格组id,map的值是组下的所有参数
        Map<Long, List<SpecParam>> map = new HashMap<>();

        for (SpecParam param : specParams) {
            if(!map.containsKey(param.getGroupId())){
                // 这个组id在map中不存在，新增一个list
                map.put(param.getGroupId(), new ArrayList<>());
            }
            map.get(param.getGroupId()).add(param);
        }

        // 填充param到group
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
