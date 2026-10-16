package com.music.asr.mapper;

import com.music.asr.domain.AsrSubtitle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AsrSubtitleMapper {

    int insert(AsrSubtitle record);

    List<AsrSubtitle> selectBySessionId(@Param("sessionId") Long sessionId);
}

