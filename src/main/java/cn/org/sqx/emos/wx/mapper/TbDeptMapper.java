package cn.org.sqx.emos.wx.mapper;

import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @auther: sqx
 * @Date: 2023-02-12
 */

@Mapper
public interface TbDeptMapper {

    //查询部门
    public ArrayList<HashMap> searchDeptMembers(String keyword);



}
