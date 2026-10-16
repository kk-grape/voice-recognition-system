package com.music.order.mapper;

import com.music.order.admin.resp.OrderCountResp;
import com.music.order.admin.resp.OrderSumResp;
import com.music.order.domain.OrderInfo;
import com.music.order.domain.OrderInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderInfoMapper {
    long countByExample(OrderInfoExample example);

    int deleteByExample(OrderInfoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OrderInfo record);

    int insertSelective(OrderInfo record);

    List<OrderInfo> selectByExample(OrderInfoExample example);

    OrderInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);

    int updateByExample(@Param("record") OrderInfo record, @Param("example") OrderInfoExample example);

    int updateByPrimaryKeySelective(OrderInfo record);

    int updateByPrimaryKey(OrderInfo record);

    /**
     * 按订单状态统计数量
     */
    List<OrderCountResp> countByStatus();

    /**
     * 统计订单总金额
     */
    OrderSumResp sumAmount();
}
