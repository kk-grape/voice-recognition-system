package com.music.nls.business.member.mapper;

import com.music.nls.business.member.domain.MemberPackage;
import com.music.nls.business.member.domain.MemberPackageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberPackageMapper {
    long countByExample(MemberPackageExample example);

    int deleteByExample(MemberPackageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(MemberPackage record);

    int insertSelective(MemberPackage record);

    List<MemberPackage> selectByExampleWithBLOBs(MemberPackageExample example);

    List<MemberPackage> selectByExample(MemberPackageExample example);

    MemberPackage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") MemberPackage record, @Param("example") MemberPackageExample example);

    int updateByExampleWithBLOBs(@Param("record") MemberPackage record, @Param("example") MemberPackageExample example);

    int updateByExample(@Param("record") MemberPackage record, @Param("example") MemberPackageExample example);

    int updateByPrimaryKeySelective(MemberPackage record);

    int updateByPrimaryKeyWithBLOBs(MemberPackage record);

    int updateByPrimaryKey(MemberPackage record);
}