package com.music.audio.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.SearchMediaResponse;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.music.api.client.OrderClient;
import com.music.audio.domain.Filetrans;
import com.music.audio.domain.FiletransExample;
import com.music.audio.enums.FiletransPayStatusEnum;
import com.music.audio.enums.FiletransStatusEnum;
import com.music.audio.mapper.FiletransMapper;
import com.music.audio.nls.NlsUtil;
import com.music.audio.req.FiletransPayReq;
import com.music.audio.util.VodUtil;
import com.music.common.alipay.AliPayService;
import com.music.common.alipay.AlipayInfoPayResp;
import com.music.common.context.LoginMemberContext;
import com.music.common.enums.OrderTypeEnum;
import com.music.common.exception.BusinessException;
import com.music.common.exception.BusinessExceptionEnum;
import com.music.common.req.FiletransQueryReq;
import com.music.common.req.OrderInfoPayReq;
import com.music.common.resp.FiletransQueryResp;
import com.music.common.resp.PageResp;
import io.seata.spring.annotation.GlobalTransactional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class FiletransService {

    @Resource
    private FiletransMapper filetransMapper;

    @Resource
    private FiletransSubtitleService filetransSubtitleService;

    @Resource
    private AliPayService aliPayService;

    @Resource
    private OrderClient orderClient;

    /**
     * 语音识别下单并拉起支付
     * 加上全局事务：本地语音识别记录 + 远程订单服务创建订单 要么都成功，要么都回滚
     *
     * 说明：
     * - Sentinel Feign 在触发 fallback 时，会把 fallback 中抛出的 RuntimeException 包装成
     * AssertionError
     * - 如果仅配置 rollbackFor = Exception.class，则 AssertionError 不会触发回滚，Seata 会提交事务
     * - 因此这里使用 rollbackFor = Throwable.class，确保包括 Error 在内的所有异常都会回滚
     */
    @GlobalTransactional(name = "filetrans-pay-tx", rollbackFor = Throwable.class)
    public AlipayInfoPayResp pay(FiletransPayReq req) throws Exception {
        Date now = new Date();

        // 获取视频信息
        GetVideoInfoResponse videoInfo = VodUtil.getVideoInfo(req.getVod());
        Float duration = videoInfo.getVideo().getDuration();
        log.info("视频：{}，时长：{}", req.getVod(), duration);
        int second = Math.round(duration);

        Filetrans filetrans = new Filetrans();

        long id = IdUtil.getSnowflakeNextId();
        filetrans.setId(id);
        filetrans.setMemberId(LoginMemberContext.getId());
        filetrans.setName(req.getName());
        filetrans.setSecond(second);
        filetrans.setAmount(req.getAmount());
        filetrans.setAudio(req.getAudio());
        filetrans.setFileSign(req.getFileSign());
        filetrans.setPayStatus(FiletransPayStatusEnum.I.getCode());
        filetrans.setStatus(FiletransStatusEnum.INIT.getCode());
        filetrans.setLang(req.getLang());
        filetrans.setVod(req.getVod());
        // filetrans.setTaskId();
        // filetrans.setTransStatusCode();
        // filetrans.setTransStatusText();
        // filetrans.setTransTime();
        // filetrans.setSolveTime();
        filetrans.setCreatedAt(now);
        filetrans.setUpdatedAt(now);

        filetransMapper.insert(filetrans);

        // 保存订单信息（仅创建订单，不拉起支付）
        OrderInfoPayReq orderInfoPayReq = new OrderInfoPayReq();
        orderInfoPayReq.setOrderType(OrderTypeEnum.FILETRANS_PAY.getCode());
        // 订单表的info保存语音识别表的id
        orderInfoPayReq.setInfo(String.valueOf(id));
        orderInfoPayReq.setAmount(req.getAmount());
        orderInfoPayReq.setChannel(req.getChannel());
        orderInfoPayReq.setDesc("语音识别付费");

        // 通过订单 API 远程创建订单，拿回订单号
        String orderNo = orderClient.createOrder(orderInfoPayReq);

        // 调用支付模块拉起支付宝，返回支付信息给前端
        AlipayInfoPayResp resp = new AlipayInfoPayResp();
        resp.setOrderNo(orderNo);
        AlipayTradePagePayResponse payResponse = aliPayService.pay("语音识别付费", orderNo, req.getAmount().toPlainString());
        resp.setChannelResult(payResponse.getBody());
        return resp;

    }

    /**
     * 支付成功后处理
     */
    public void afterPaySuccess(Long id) {
        Date now = new Date();
        Filetrans filetrans = new Filetrans();
        filetrans.setId(id);
        filetrans.setPayStatus(FiletransPayStatusEnum.S.getCode());
        filetrans.setStatus(FiletransStatusEnum.SUBTITLE_INIT.getCode());
        filetrans.setUpdatedAt(now);
        filetransMapper.updateByPrimaryKeySelective(filetrans);

        log.info("发起语音识别任务");
        Filetrans filetransDB = filetransMapper.selectByPrimaryKey(id);
        CommonResponse commonResponse = NlsUtil.trans(filetransDB.getAudio(), filetransDB.getLang());
        if (commonResponse.getHttpStatus() == 200) {
            JSONObject result = JSONObject.parseObject(commonResponse.getData());
            Integer statusCode = result.getInteger("StatusCode");
            String statusText = result.getString("StatusText");
            String taskId = result.getString("TaskId");
            if ("SUCCESS".equals(statusText)) {
                log.info("录音文件识别请求成功响应： " + result.toJSONString());
            } else {
                log.error("录音文件识别请求失败： " + result.toJSONString());
                throw new BusinessException(BusinessExceptionEnum.FILETRANS_TRANS_ERROR);
            }

            log.info("更新语音识别状态为：生成字幕中");
            Filetrans filetransAfterNls = new Filetrans();
            filetransAfterNls.setId(id);
            filetransAfterNls.setStatus(FiletransStatusEnum.SUBTITLE_PENDING.getCode());
            filetransAfterNls.setUpdatedAt(now);
            filetransAfterNls.setTaskId(taskId);
            filetransAfterNls.setTransTime(now);
            filetransAfterNls.setTransStatusCode(statusCode);
            filetransAfterNls.setTransStatusText(statusText);
            filetransMapper.updateByPrimaryKeySelective(filetransAfterNls);
        }
    }

    /**
     * 语音识别回调后处理
     */
    public void afterRecognition(JSONObject jsonResult) {
        Date now = new Date();
        String taskId = jsonResult.getString("TaskId");
        Integer statusCode = jsonResult.getInteger("StatusCode");
        String statusText = jsonResult.getString("StatusText");

        Filetrans filetrans = new Filetrans();
        filetrans.setUpdatedAt(now);
        filetrans.setTransStatusCode(statusCode);
        filetrans.setTransStatusText(statusText);

        if ("21050000".equals(statusCode.toString())) {
            filetrans.setSolveTime(new Date(jsonResult.getLong("SolveTime")));
            filetrans.setStatus(FiletransStatusEnum.SUBTITLE_SUCCESS.getCode());
        } else {
            filetrans.setStatus(FiletransStatusEnum.SUBTITLE_FAILURE.getCode());
        }

        FiletransExample filetransExample = new FiletransExample();
        filetransExample.createCriteria().andTaskIdEqualTo(taskId)
                .andStatusEqualTo(FiletransStatusEnum.SUBTITLE_PENDING.getCode());
        int i = filetransMapper.updateByExampleSelective(filetrans, filetransExample);

        // 保存字幕结果
        if (i == 0) {
            log.info("未更新到taskId={}，状态={}/{}，不保存字幕表", taskId, FiletransStatusEnum.SUBTITLE_PENDING.getCode(),
                    FiletransStatusEnum.SUBTITLE_PENDING.getDesc());
            return;
        }

        if ("21050000".equals(statusCode.toString())) {
            FiletransExample example = new FiletransExample();
            example.createCriteria().andTaskIdEqualTo(taskId);
            List<Filetrans> filetransList = filetransMapper.selectByExample(example);
            Filetrans filetransDB = filetransList.get(0);

            JSONObject result = jsonResult.getJSONObject("Result");

            filetransSubtitleService.saveSubtitle(filetransDB.getId(), result);
        }
    }

    public PageResp<FiletransQueryResp> query(FiletransQueryReq req) {
        FiletransExample filetransExample = new FiletransExample();
        FiletransExample.Criteria criteria = filetransExample.createCriteria();

        if (ObjectUtil.isNotNull(req.getMemberId())) {
            criteria.andMemberIdEqualTo(req.getMemberId());
        }
        if (StrUtil.isNotBlank(req.getLang())) {
            criteria.andLangEqualTo(req.getLang());
        }
        if (StrUtil.isNotBlank(req.getStatus())) {
            // 指定了状态，则按指定状态查询
            criteria.andStatusEqualTo(req.getStatus());
        } else {
            // 未指定状态时，默认排除“失效/已删除”的记录
            criteria.andStatusNotEqualTo(FiletransStatusEnum.DELETED.getCode());
        }
        if (StrUtil.isNotBlank(req.getName())) {
            criteria.andNameLike("%" + req.getName() + "%");
        }

        filetransExample.setOrderByClause("id desc");

        PageHelper.startPage(req.getPage(), req.getSize());
        List<Filetrans> filetransList = filetransMapper.selectByExample(filetransExample);
        // 构造分页返回信息
        PageResp<FiletransQueryResp> pageResp = new PageResp<>();
        // 获取分页信息，只需要总数
        PageInfo<Filetrans> pageInfo = new PageInfo<>(filetransList);
        pageResp.setTotal(pageInfo.getTotal());
        // 获取当前分页列表内容
        List<FiletransQueryResp> filetransQueryRespList = BeanUtil.copyToList(filetransList, FiletransQueryResp.class);
        pageResp.setList(filetransQueryRespList);

        return pageResp;
    }

    /**
     * 删除语音识别任务（逻辑删除）
     *
     * 说明：
     * - 使用 FiletransStatusEnum.DELETED 标记为“失效”
     * - 前端列表在未指定状态条件时会自动过滤掉该状态
     */
    public void delete(Long id) {
        Date now = new Date();
        Filetrans filetrans = new Filetrans();
        filetrans.setId(id);
        filetrans.setStatus(FiletransStatusEnum.DELETED.getCode());
        filetrans.setUpdatedAt(now);
        filetransMapper.updateByPrimaryKeySelective(filetrans);
    }

    /**
     * 删除早期视频
     * 
     * @return
     */
    public void deleteVodJob() {
        try {
            Date date = new Date();
            Date start = DateUtil.offsetDay(date, -135);
            String startStr = DateUtil.format(start, "yyyy-MM-dd") + "T00:00:00Z";
            Date end = DateUtil.offsetDay(date, -15);
            String endStr = DateUtil.format(end, "yyyy-MM-dd") + "T00:00:00Z";
            log.info("删除过期VOD，查询列表日期范围：" + startStr + ", " + endStr);
            SearchMediaResponse searchMediaResponse = VodUtil.searchByCreationTime(startStr, endStr);
            List<SearchMediaResponse.Media> mediaList = searchMediaResponse.getMediaList();
            if (!CollectionUtils.isEmpty(mediaList)) {
                for (SearchMediaResponse.Media media : mediaList) {
                    try {
                        VodUtil.deleteVideo(media.getMediaId());
                    } catch (Exception e) {
                        log.error("删除过期VOD异常：" + media.getMediaId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("删除过期VOD异常", e);
        }

    }
}
