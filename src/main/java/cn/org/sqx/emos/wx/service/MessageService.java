package cn.org.sqx.emos.wx.service;

import cn.org.sqx.emos.wx.domain.MessageEntity;
import cn.org.sqx.emos.wx.domain.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @auther: sqx
 * @Date: 2023-02-08
 */
public interface MessageService {
    //插入主键值
    public String insertMessage(MessageEntity entity);

    //按照分页去查询消息
    public List<HashMap> searchMessageByPage(int userId, long start, int length);

    //根据Id查找数据
    public HashMap searchMessageById(String id);

    public String insertMessageRef(MessageRefEntity entity);

    //查询未读消息的数量
    public long searchUnreadCount(int userId);

    //查询新消息
    public long searchLastCount(int userId);

    //修改消息状态
    public long updateUnreadMessage(String id);

    //删除记录
    public long deleteMessageRefById(String id);

    //删除所有消息
    public long deleteUserMessageRef(int userId);

}
