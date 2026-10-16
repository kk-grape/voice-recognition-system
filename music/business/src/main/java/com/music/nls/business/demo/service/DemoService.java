package com.music.nls.business.demo.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.music.nls.business.demo.domain.Demo;
import com.music.nls.business.demo.domain.DemoExample;
import com.music.nls.business.common.exception.BusinessException;
import com.music.nls.business.common.exception.BusinessExceptionEnum;
import com.music.nls.business.demo.mapper.DemoMapper;
import com.music.nls.business.demo.mapper.DemoMapperCust;
import com.music.nls.business.demo.req.DemoQueryReq;
import com.music.nls.business.demo.resp.DemoQueryResp;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoService {

    @Resource
    private DemoMapperCust demoMapperCust;

    @Resource
    private DemoMapper demoMapper;
    public int count(){
        return demoMapperCust.count();
//        return (int) demoMapper.countByExample(null);
    }

    public List<DemoQueryResp> query(DemoQueryReq req) {
        String mobile = req.getMobile();
        DemoExample demoExample = new DemoExample();
        demoExample.setOrderByClause("id asc");
        DemoExample.Criteria criteria = demoExample.createCriteria();
//         if (mobile != null) {
//             criteria.andMobileEqualTo(mobile);
//         }
        if (StrUtil.isBlank(mobile)) {
            throw new BusinessException(BusinessExceptionEnum.DEMO_MOBILE_NOT_NULL);
        }
//        SmsCodeStatusEnum.USED.getCode();
        criteria.andMobileEqualTo(mobile);
        List<Demo> list = demoMapper.selectByExample(demoExample);
        return BeanUtil.copyToList(list,DemoQueryResp.class);//类型转换
    }
}
