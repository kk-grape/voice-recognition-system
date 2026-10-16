package com.music.ai.mapper;

import com.music.ai.domain.AiChatMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiChatMessageMapper {

    int insert(AiChatMessage record);

    List<AiChatMessage> selectBySessionId(@Param("sessionId") Long sessionId);

    List<AiChatMessage> selectBySessionUuid(@Param("sessionUuid") String sessionUuid);
}
