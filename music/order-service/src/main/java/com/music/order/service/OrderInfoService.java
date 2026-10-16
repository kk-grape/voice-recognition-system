package com.music.order.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.music.common.alipay.AliPayService;
import com.music.common.context.LoginMemberContext;
import com.music.common.enums.OrderInfoChannelEnum;
import com.music.common.req.OrderInfoPayReq;
import com.music.order.callback.AfterPayService;
import com.music.order.domain.OrderInfo;
import com.music.order.domain.OrderInfoExample;
import com.music.order.enums.OrderInfoStatusEnum;
import com.music.order.mapper.OrderInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderInfoService {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private AfterPayService afterPayService;

    /**
     * 仅负责创建订单，不负责拉起支付。
     * 返回订单号，方便上层再去调用支付模块。
     */
    public String createOrder(OrderInfoPayReq req) {
        Date now = new Date();

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setId(IdUtil.getSnowflakeNextId());

        String orderNo = genOrderNo();
        orderInfo.setOrderNo(orderNo);

        orderInfo.setOrderAt(now);
        orderInfo.setOrderType(req.getOrderType());
        orderInfo.setInfo(req.getInfo());
        orderInfo.setMemberId(LoginMemberContext.getId());
        orderInfo.setAmount(req.getAmount());
        orderInfo.setPayAt(now);
        orderInfo.setChannel(req.getChannel());
        orderInfo.setChannelAt(null);
        orderInfo.setOrderStatus(OrderInfoStatusEnum.I.getCode());
        orderInfo.setOrderDesc(req.getDesc());
        orderInfo.setCreatedAt(now);
        orderInfo.setUpdatedAt(now);
        orderInfoMapper.insert(orderInfo);

        return orderNo;
    }

    public String genOrderNo() {
        String no = DateUtil.format(new Date(), "yyyyMMddHHmmssSSS");
        int random = (int) (Math.random() * 900 + 100);
        no = no + random;
        return no;
    }

    /**
     * 查询本地订单状态并返回
     * 
     * @param orderNo
     * @return
     */
    public String queryOrderStatus(String orderNo) {
        OrderInfo orderInfo = this.selectByOrderNo(orderNo);

        // 全链路查询
        /*
         * 如果不想要循环依赖的话，就直接返回当前的订单状态，下面这一步操作就不需要了，
         * 或者查询已经支付成功的话，就只在这里改变订单的状态。如果也想让其他支付之后的功能进行的话，
         * 后面可以继续开发一个新的service来做，不使用AfterPayService这个类
         */
        if (OrderInfoStatusEnum.I.getCode().equals(orderInfo.getOrderStatus())) {
            if (OrderInfoChannelEnum.ALIPAY.getCode().equals(orderInfo.getChannel())) {
                AlipayTradeQueryResponse response = aliPayService.query(orderNo);
                String tradeStatus = response.getTradeStatus();
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    String sendPayDate = response.getSendPayDate();
                    Date date = DateUtil.parse(sendPayDate, "yyyy-MM-dd HH:mm:ss");
                    afterPayService.afterPaySuccess(orderNo, date);
                    return OrderInfoStatusEnum.S.getCode();
                }
            }
        }
        return orderInfo.getOrderStatus();
    }

    public OrderInfo selectByOrderNo(String orderNo) {
        OrderInfoExample orderInfoExample = new OrderInfoExample();
        OrderInfoExample.Criteria criteria = orderInfoExample.createCriteria();
        criteria.andOrderNoEqualTo(orderNo);
        List<OrderInfo> list = orderInfoMapper.selectByExample(orderInfoExample);
        if (CollectionUtils.isEmpty(list)) {
            return new OrderInfo();
        }
        return list.get(0);
    }

    /**
     * 支付成功后，将订单更新成S，按状态更新，从I改成S
     * 
     * @param orderNo
     * @param channelTime
     */
    public int afterPaySuccess(String orderNo, Date channelTime) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderStatus(OrderInfoStatusEnum.S.getCode());
        orderInfo.setChannelAt(channelTime);
        orderInfo.setUpdatedAt(new Date());

        OrderInfoExample orderInfoExample = new OrderInfoExample();
        OrderInfoExample.Criteria criteria = orderInfoExample.createCriteria();
        criteria.andOrderNoEqualTo(orderNo).andOrderStatusEqualTo(OrderInfoStatusEnum.I.getCode());
        return orderInfoMapper.updateByExampleSelective(orderInfo, orderInfoExample);
    }
}
