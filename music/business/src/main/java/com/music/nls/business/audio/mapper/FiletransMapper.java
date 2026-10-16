package com.music.nls.business.audio.mapper;

import com.music.nls.business.audio.domain.Filetrans;
import com.music.nls.business.audio.domain.FiletransExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FiletransMapper {
    long countByExample(FiletransExample example);

    int deleteByExample(FiletransExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Filetrans record);

    int insertSelective(Filetrans record);

    List<Filetrans> selectByExample(FiletransExample example);

    Filetrans selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") Filetrans record, @Param("example") FiletransExample example);

    int updateByExample(@Param("record") Filetrans record, @Param("example") FiletransExample example);

    int updateByPrimaryKeySelective(Filetrans record);

    int updateByPrimaryKey(Filetrans record);
}