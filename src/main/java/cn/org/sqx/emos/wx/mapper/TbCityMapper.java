package cn.org.sqx.emos.wx.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @auther: sqx
 * @Date: 2023-01-02
 */
@Mapper
@Repository
public interface TbCityMapper {
    //查询城市编码
    public String searchCode(String city);
}
