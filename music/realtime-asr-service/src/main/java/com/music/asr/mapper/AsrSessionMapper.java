package com.music.asr.mapper;

import com.music.asr.admin.resp.SessionCountResp;
import com.music.asr.domain.AsrSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AsrSessionMapper {

    int insert(AsrSession record);

    int updateByPrimaryKey(AsrSession record);

    AsrSession selectByPrimaryKey(@Param("id") Long id);

    List<AsrSession> selectByMemberId(@Param("memberId") Long memberId);

    Long countAll();

    /**
     * 按状态统计会话数量
     */
    List<SessionCountResp> countByStatus();

    /**
     * 条件查询会话列表
     */
    List<AsrSession> selectListByCondition(@Param("memberId") Long memberId,
                                           @Param("status") String status,
                                           @Param("sourceLang") String sourceLang,
                                           @Param("targetLang") String targetLang);

    /**
     * 统计总时长
     */
    Long sumDuration();
}
