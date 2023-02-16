package cn.org.sqx.emos.wx.mapper;

import cn.org.sqx.emos.wx.domain.SysConfig;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @auther: sqx
 * @Date: 2022-12-25
 */
@Mapper
@Repository //解决无法自动装配。找不到对应的bean
public interface SysConfigMapper {
    //查询常量
    public List<SysConfig> selectAllParam();
}
