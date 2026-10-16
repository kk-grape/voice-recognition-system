package com.music.nls.business.member.service;

import com.music.nls.business.member.domain.MemberPackage;
import com.music.nls.business.member.domain.MemberPackageExample;
import com.music.nls.business.member.mapper.MemberPackageMapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;

@Service
public class MemberPackageService {

    @Resource
    private MemberPackageMapper memberPackageMapper;

    /**
     * 获取所有可用的会员套餐
     * @return 会员套餐列表
     */
    public List<MemberPackage> getAllAvailablePackages() {
        MemberPackageExample example = new MemberPackageExample();
        example.createCriteria().andStatusEqualTo(1); // 1表示启用状态
        example.setOrderByClause("level asc"); // 按等级升序排列
        return memberPackageMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 根据ID获取会员套餐详情
     * @param id 套餐ID
     * @return 会员套餐详情
     */
    public MemberPackage getPackageById(Long id) {
        return memberPackageMapper.selectByPrimaryKey(id);
    }
}
