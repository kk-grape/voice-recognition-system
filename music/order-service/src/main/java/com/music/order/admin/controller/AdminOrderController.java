package com.music.order.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.common.enums.OrderInfoChannelEnum;
import com.music.common.enums.OrderTypeEnum;
import com.music.common.resp.CommonResp;
import com.music.common.resp.PageResp;
import com.music.order.admin.req.AdminOrderQueryReq;
import com.music.order.admin.resp.AdminOrderResp;
import com.music.order.admin.resp.OrderCountResp;
import com.music.order.admin.resp.OrderSumResp;
import com.music.order.domain.OrderInfo;
import com.music.order.domain.OrderInfoExample;
import com.music.order.enums.OrderInfoStatusEnum;
import com.music.order.mapper.OrderInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员-订单管理接口
 * 路径：/admin/order/**
 */
@Slf4j
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    /**
     * 查询订单列表（管理员版本）
     * - 不限制会员ID，可查看所有记录
     */
    @GetMapping("/list")
    public CommonResp<PageResp<AdminOrderResp>> list(AdminOrderQueryReq req) {
        OrderInfoExample example = new OrderInfoExample();
        OrderInfoExample.Criteria criteria = example.createCriteria();

        // 管理员版本：不限制会员ID，可查看所有记录
        if (req.getMemberId() != null) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        if (StringUtils.hasText(req.getOrderNo())) {
            criteria.andOrderNoLike("%" + req.getOrderNo() + "%");
        }
        if (StringUtils.hasText(req.getOrderStatus())) {
            criteria.andOrderStatusEqualTo(req.getOrderStatus());
        }
        if (StringUtils.hasText(req.getChannel())) {
            criteria.andChannelEqualTo(req.getChannel());
        }
        if (StringUtils.hasText(req.getOrderType())) {
            criteria.andOrderTypeEqualTo(req.getOrderType());
        }
        if (req.getStartDate() != null) {
            criteria.andCreatedAtGreaterThanOrEqualTo(req.getStartDate());
        }
        if (req.getEndDate() != null) {
            criteria.andCreatedAtLessThanOrEqualTo(req.getEndDate());
        }

        example.setOrderByClause("id desc");

        PageHelper.startPage(req.getPage(), req.getSize());
        List<OrderInfo> list = orderInfoMapper.selectByExample(example);

        PageResp<AdminOrderResp> pageResp = new PageResp<>();
        PageInfo<OrderInfo> pageInfo = new PageInfo<>(list);
        pageResp.setTotal(pageInfo.getTotal());

        // 复制属性并设置状态名称
        List<AdminOrderResp> respList = BeanUtil.copyToList(list, AdminOrderResp.class);
        for (AdminOrderResp resp : respList) {
            // 设置订单状态名称
            OrderInfoStatusEnum statusEnum = OrderInfoStatusEnum.getByCode(resp.getOrderStatus());
            if (statusEnum != null) {
                resp.setOrderStatusName(statusEnum.getDesc());
            }
            // 设置支付渠道名称
            OrderInfoChannelEnum channelEnum = OrderInfoChannelEnum.getByCode(resp.getChannel());
            if (channelEnum != null) {
                resp.setChannelName(channelEnum.getDesc());
            }
            // 设置订单类型名称
            OrderTypeEnum typeEnum = OrderTypeEnum.getByCode(resp.getOrderType());
            if (typeEnum != null) {
                resp.setOrderTypeName(typeEnum.getDesc());
            }
        }
        pageResp.setList(respList);

        return new CommonResp<>(pageResp);
    }

    /**
     * 查看订单详情
     */
    @GetMapping("/detail/{id}")
    public CommonResp<AdminOrderResp> detail(@PathVariable Long id) {
        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(id);
        if (orderInfo == null) {
            return new CommonResp<>();
        }
        AdminOrderResp resp = new AdminOrderResp();
        BeanUtil.copyProperties(orderInfo, resp);

        OrderInfoStatusEnum statusEnum = OrderInfoStatusEnum.getByCode(orderInfo.getOrderStatus());
        if (statusEnum != null) {
            resp.setOrderStatusName(statusEnum.getDesc());
        }
        OrderInfoChannelEnum channelEnum = OrderInfoChannelEnum.getByCode(orderInfo.getChannel());
        if (channelEnum != null) {
            resp.setChannelName(channelEnum.getDesc());
        }
        OrderTypeEnum typeEnum = OrderTypeEnum.getByCode(orderInfo.getOrderType());
        if (typeEnum != null) {
            resp.setOrderTypeName(typeEnum.getDesc());
        }

        return new CommonResp<>(resp);
    }

    /**
     * 根据订单号查看详情
     */
    @GetMapping("/detail-by-orderno/{orderNo}")
    public CommonResp<AdminOrderResp> detailByOrderNo(@PathVariable String orderNo) {
        OrderInfoExample example = new OrderInfoExample();
        example.createCriteria().andOrderNoEqualTo(orderNo);
        List<OrderInfo> list = orderInfoMapper.selectByExample(example);
        if (list.isEmpty()) {
            return new CommonResp<>();
        }
        OrderInfo orderInfo = list.get(0);
        AdminOrderResp resp = new AdminOrderResp();
        BeanUtil.copyProperties(orderInfo, resp);

        OrderInfoStatusEnum statusEnum = OrderInfoStatusEnum.getByCode(orderInfo.getOrderStatus());
        if (statusEnum != null) {
            resp.setOrderStatusName(statusEnum.getDesc());
        }
        OrderInfoChannelEnum channelEnum = OrderInfoChannelEnum.getByCode(orderInfo.getChannel());
        if (channelEnum != null) {
            resp.setChannelName(channelEnum.getDesc());
        }
        OrderTypeEnum typeEnum = OrderTypeEnum.getByCode(orderInfo.getOrderType());
        if (typeEnum != null) {
            resp.setOrderTypeName(typeEnum.getDesc());
        }

        return new CommonResp<>(resp);
    }

    /**
     * 统计订单总数
     */
    @GetMapping("/count")
    public CommonResp<Long> count() {
        long count = orderInfoMapper.countByExample(new OrderInfoExample());
        return new CommonResp<>(count);
    }

    /**
     * 按状态统计订单数量
     */
    @GetMapping("/count-by-status")
    public CommonResp<List<OrderCountResp>> countByStatus() {
        List<OrderCountResp> result = orderInfoMapper.countByStatus();
        return new CommonResp<>(result);
    }

    /**
     * 统计订单总金额
     */
    @GetMapping("/sum-amount")
    public CommonResp<OrderSumResp> sumAmount() {
        OrderSumResp resp = orderInfoMapper.sumAmount();
        return new CommonResp<>(resp);
    }
}
