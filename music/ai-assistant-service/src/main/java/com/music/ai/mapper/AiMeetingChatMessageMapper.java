package com.music.ai.mapper;

import com.music.ai.domain.AiMeetingChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiMeetingChatMessageMapper {

    int insert(AiMeetingChatMessage record);

    List<AiMeetingChatMessage> selectBySessionId(@Param("sessionId") Long sessionId);
}
