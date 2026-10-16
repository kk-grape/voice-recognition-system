# music-web - 用户端前端

智能语音识别系统的用户端 Web 应用，基于 Vue 3 + Vite + Ant Design Vue。

## 技术栈

- Vue 3.3（`<script setup>` 组合式 API）
- Vite 5
- Ant Design Vue 4.1
- Vue Router 4
- Vuex 4
- Axios
- music-metadata（音频元信息读取）

## 快速开始

```bash
# 安装依赖
npm install

# 开发启动（默认端口 3000）
npm run dev

# 生产构建
npm run build

# 预览构建产物
npm run preview
```

## 环境变量

`.env`：

```
VITE_SERVER=http://localhost:18000
```

`VITE_SERVER` 指向后端 API 网关地址，所有请求（含 WebSocket）均通过网关路由到对应微服务。

## 目录结构

```
src/
├── assets/            # 静态资源
├── components/        # 公共组件
├── router/            # 路由配置
├── store/             # Vuex 状态管理
├── view/               # 页面视图
│   └── home/
│       ├── welcome.vue            # 首页
│       ├── filetrans-upload.vue   # 录音文件上传
│       ├── filetrans.vue           # 录音转写记录
│       ├── filetrans-subtitle.vue # 转写字幕查看/导出
│       ├── fileformat.vue         # 音视频格式转换
│       ├── voice-assistant.vue   # 实时语音识别 / AI 助手
│       └── help.vue               # 使用帮助
└── main.js
```

## 主要功能

- 会员注册/登录（手机号 + 短信验证码）
- 录音文件上传与转写（付费，支付宝沙箱）
- 转写字幕在线查看与导出
- 音视频格式在线转换
- 实时语音识别与翻译（WebSocket）
- AI 会议纪要查看与对话
- 个人中心与订单查询

## 浏览器要求

支持现代浏览器（Chrome / Edge / Firefox 最新版）。实时语音识别需要麦克风权限与 HTTPS（localhost 除外）。
