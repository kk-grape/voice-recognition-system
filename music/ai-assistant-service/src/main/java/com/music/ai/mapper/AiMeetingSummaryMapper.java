package com.music.ai.mapper;

import com.music.ai.domain.AiMeetingSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AiMeetingSummaryMapper {

    int insert(AiMeetingSummary record);

    AiMeetingSummary selectByAsrSessionId(@Param("asrSessionId") Long asrSessionId);

    int updateSummary(@Param("id") Long id,
                      @Param("summarySource") String summarySource,
                      @Param("summaryTarget") String summaryTarget,
                      @Param("keyPoints") String keyPoints,
                      @Param("actionItems") String actionItems,
                      @Param("decisions") String decisions,
                      @Param("keywords") String keywords,
                      @Param("totalTokens") Integer totalTokens);
}
