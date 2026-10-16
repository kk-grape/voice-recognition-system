package com.music.audio.mapper;

import com.music.audio.domain.FiletransSubtitle;
import com.music.audio.domain.FiletransSubtitleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FiletransSubtitleMapper {
    long countByExample(FiletransSubtitleExample example);

    int deleteByExample(FiletransSubtitleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(FiletransSubtitle record);

    int insertSelective(FiletransSubtitle record);

    List<FiletransSubtitle> selectByExample(FiletransSubtitleExample example);

    FiletransSubtitle selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") FiletransSubtitle record, @Param("example") FiletransSubtitleExample example);

    int updateByExample(@Param("record") FiletransSubtitle record, @Param("example") FiletransSubtitleExample example);

    int updateByPrimaryKeySelective(FiletransSubtitle record);

    int updateByPrimaryKey(FiletransSubtitle record);
}