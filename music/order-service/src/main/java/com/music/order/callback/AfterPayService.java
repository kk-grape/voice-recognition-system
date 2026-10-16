package com.music.order.callback;


import com.music.api.client.AudioClient;
import com.music.common.enums.OrderTypeEnum;
import com.music.order.domain.OrderInfo;
import com.music.order.service.OrderInfoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Slf4j
@Service
public class AfterPayService {

    @Resource
    private OrderInfoService orderInfoService;

    @Resource
    private AudioClient audioClient;

    @Transactional
    public void afterPaySuccess(String orderNo, Date channelTime) {
        log.info("执行支付成功动作开始，orderNo={}，channelTime={}", orderNo, channelTime);

        // 1. 校验订单是否存在
        OrderInfo orderInfo = orderInfoService.selectByOrderNo(orderNo);
        if (orderInfo == null || orderInfo.getId() == null) {
            log.error("支付回调处理失败，订单不存在，orderNo={}", orderNo);
            return;
        }

        // 2. 将订单状态从 I 更新为 S（幂等保护：只允许从初始状态更新）
        log.info("更新订单信息开始，orderNo={}", orderNo);
        int updateCount = orderInfoService.afterPaySuccess(orderNo, channelTime);
        if (updateCount == 0) {
            // 说明订单当前状态不是 I，有可能已经被处理过或状态异常
            log.warn("支付成功处理被跳过，订单状态非初始，orderNo={}，当前状态={}", orderNo, orderInfo.getOrderStatus());
            return;
        }

        // 3. 按订单类型执行对应的业务后置处理
        String orderType = orderInfo.getOrderType();
        if (OrderTypeEnum.FILETRANS_PAY.getCode().equals(orderType)) {
            // 将语音识别记录更新成已支付并发起识别任务
            log.info("语音识别单次付费成功，准备更新语音识别表状态，orderNo={}，info={}", orderNo, orderInfo.getInfo());
            try {
                Long filetransId = Long.valueOf(orderInfo.getInfo());
                // 通过音频服务 API 远程调用，更新语音识别状态并发起识别任务
                audioClient.afterPaySuccess(filetransId);
                log.info("语音识别表状态更新成功，filetransId={}", filetransId);
            } catch (NumberFormatException e) {
                log.error("订单信息字段 info 不是有效的语音识别ID，orderNo={}，info={}", orderNo, orderInfo.getInfo(), e);
            } catch (Exception e) {
                // 这里记录错误，避免影响整个事务回滚时看不清问题
                log.error("执行语音识别支付后置逻辑异常，orderNo={}，info={}", orderNo, orderInfo.getInfo(), e);
                throw e;
            }
        } else {
            log.info("当前订单类型无需额外业务处理，orderNo={}，orderType={}", orderNo, orderType);
        }

        log.info("执行支付成功动作结束，orderNo={}", orderNo);
    }
}
