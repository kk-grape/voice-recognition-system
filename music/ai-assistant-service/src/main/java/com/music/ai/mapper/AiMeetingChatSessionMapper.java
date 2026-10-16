package com.music.ai.mapper;

import com.music.ai.domain.AiMeetingChatSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiMeetingChatSessionMapper {

    int insert(AiMeetingChatSession record);

    AiMeetingChatSession selectByAsrSessionId(@Param("asrSessionId") Long asrSessionId);

    int updateLastMessage(@Param("sessionId") Long sessionId,
                          @Param("lastMessage") String lastMessage,
                          @Param("lastMessageAt") java.util.Date lastMessageAt,
                          @Param("messageCount") Integer messageCount);

    int updateStatus(@Param("sessionId") Long sessionId, @Param("status") Integer status);
}
