package com.music.ai.mapper;

import com.music.ai.admin.resp.ChatSessionCountResp;
import com.music.ai.domain.AiChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface AiChatSessionMapper {

    int insert(AiChatSession record);

    AiChatSession selectByUuid(@Param("sessionUuid") String sessionUuid);

    AiChatSession selectRecentByUserId(@Param("userId") Long userId);

    List<AiChatSession> selectListByUserId(@Param("userId") Long userId);

    int updateLastMessage(@Param("sessionId") Long sessionId,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastMessageAt") Date lastMessageAt,
                          @Param("messageCount") Integer messageCount);

    int updateStatus(@Param("sessionId") Long sessionId, @Param("status") Integer status);

    AiChatSession selectByPrimaryKey(@Param("id") Long id);

    List<AiChatSession> selectAllList();

    Long countAll();

    /**
     * 按状态统计会话数量
     */
    List<ChatSessionCountResp> countByStatus();

    /**
     * 条件查询会话列表
     */
    List<AiChatSession> selectListByCondition(@Param("userId") Long userId,
                                               @Param("status") Integer status,
                                               @Param("title") String title);
}
