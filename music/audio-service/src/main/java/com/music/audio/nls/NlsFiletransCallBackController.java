package com.music.audio.nls;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.music.audio.service.FiletransService;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
public class NlsFiletransCallBackController {

    @Resource
    private FiletransService filetransService;

    // 以4开头的状态码是客户端错误。
    private static final Pattern PATTERN_CLIENT_ERR = Pattern.compile("4105[0-9]*");
    // 以5开头的状态码是服务端错误。
    private static final Pattern PATTERN_SERVER_ERR = Pattern.compile("5105[0-9]*");
    // 必须是post方式
    @PostMapping("/filetrans/callback")
    public void GetResult(HttpServletRequest request) {
        ServletInputStream in = null;
        String result = null;
        try {
            in = request.getInputStream();
            
            // 正确读取流数据，处理 ContentLength 未知或读取不完整的情况
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            byte[] data = baos.toByteArray();
            
            // 使用 UTF-8 编码转换为字符串
            result = new String(data, StandardCharsets.UTF_8);
            log.info("录音转换回调结果: {}", result);
            
            // 验证 JSON 字符串是否完整
            if (result.trim().isEmpty()) {
                log.error("录音转换回调结果为空！");
                return;
            }
            
            JSONObject jsonResult = JSONObject.parseObject(result);
            String taskId = jsonResult.getString("TaskId");
            String statusCode = jsonResult.getString("StatusCode");
            String statusText = jsonResult.getString("StatusText");
            // 解析并输出相关结果内容。
            log.info("录音转换回调结果: TaskId：{}，StatusCode：{}，StatusText：{}", taskId, statusCode, statusText);
            Matcher matcherClient = PATTERN_CLIENT_ERR.matcher(jsonResult.getString("StatusCode"));
            Matcher matcherServer = PATTERN_SERVER_ERR.matcher(jsonResult.getString("StatusCode"));

            // 不管成功还是失败，都应该更新状态
            //语音识别后的处理
            filetransService.afterRecognition(jsonResult);

            // 以2开头状态码为正常状态码，回调方式正常状态只返回“21050000”。
            if("21050000".equals(statusCode)) {

                log.info("录音文件识别成功！taskId：{}", taskId);

                // System.out.println("RequestTime: " + jsonResult.getString("RequestTime"));
                // System.out.println("SolveTime: " + jsonResult.getString("SolveTime"));
                // System.out.println("BizDuration: " + jsonResult.getString("BizDuration"));
                // System.out.println("Result.Sentences.size: " +
                //     jsonResult.getJSONObject("Result").getJSONArray("Sentences").size());
                // for (int i = 0; i < jsonResult.getJSONObject("Result").getJSONArray("Sentences").size(); i++) {
                //     System.out.println("Result.Sentences[" + i + "].BeginTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("BeginTime"));
                //     System.out.println("Result.Sentences[" + i + "].EndTime: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EndTime"));
                //     System.out.println("Result.Sentences[" + i + "].SilenceDuration: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SilenceDuration"));
                //     System.out.println("Result.Sentences[" + i + "].Text: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("Text"));
                //     System.out.println("Result.Sentences[" + i + "].ChannelId: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("ChannelId"));
                //     System.out.println("Result.Sentences[" + i + "].SpeechRate: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("SpeechRate"));
                //     System.out.println("Result.Sentences[" + i + "].EmotionValue: " +
                //         jsonResult.getJSONObject("Result").getJSONArray("Sentences").getJSONObject(i).getString("EmotionValue"));
                // }
            }
            else if(matcherClient.matches()) {
                log.error("录音文件识别失败！状态码以4开头表示客户端错误：{}, {}", statusCode, statusText);
            }
            else if(matcherServer.matches()) {
                log.error("录音文件识别失败！状态码以5开头表示服务端错误：{}, {}", statusCode, statusText);
            }
            else {
                log.error("录音文件识别失败！状态码：{}, {}", statusCode, statusText);
            }
        } catch (JSONException e) {
            log.error("录音转换回调 JSON 解析异常！可能是 JSON 格式错误或数据不完整。", e);
            log.error("原始数据长度: {}", request.getContentLength());
            if (result != null) {
                log.error("原始数据内容（前1000字符）: {}", 
                    result.length() > 1000 ? result.substring(0, 1000) : result);
                log.error("原始数据内容（后1000字符）: {}", 
                    result.length() > 1000 ? result.substring(result.length() - 1000) : result);
                log.error("原始数据总长度: {} 字符", result.length());
                // 检查 JSON 字符串是否被截断
                int openBraces = (int) result.chars().filter(ch -> ch == '{').count();
                int closeBraces = (int) result.chars().filter(ch -> ch == '}').count();
                log.error("JSON 括号统计 - 左括号: {}, 右括号: {}", openBraces, closeBraces);
            } else {
                log.error("无法获取原始数据，可能在读取流时发生异常");
            }
        } catch (IOException e) {
            log.error("录音转换回调处理异常！！", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warn("关闭输入流异常", e);
                }
            }
        }
    }
}
