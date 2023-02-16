package cn.org.sqx.emos.wx.mapper;

import cn.org.sqx.emos.wx.domain.TbFaceModel;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @auther: sqx
 * @Date: 2023-01-02
 */
@Mapper
@Repository
public interface TbFaceModelMapper {
    //TODO 查询
    /**
     * 签到查询
     * @param userId    用户ID
     * @return  消息通知
     */
    public String searchFaceModel(int userId);

    //TODO 新增

    /**
     * 增加数据模型
     * @param tbFaceModel   人脸数据
     */
    public void insert(TbFaceModel tbFaceModel);

    //TODO 删除

    /**
     * 删除人脸模型
     * @param userId    用户ID
     * @return  删除的记录
     */
    public int deleteFaceModel(int userId);

}
