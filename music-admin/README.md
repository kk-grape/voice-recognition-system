# music-admin - 管理后台前端

智能语音识别系统的管理后台，基于 Vue 3 + Vite + Ant Design Vue。

## 技术栈

- Vue 3.3
- Vite 5
- Ant Design Vue 4.1
- Vue Router 4
- Vuex 4
- Axios

## 快速开始

```bash
# 安装依赖
npm install

# 开发启动（固定端口 3001）
npm run dev-admin

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

## 默认账号

| 账号 | 密码 |
|------|------|
| test | a111111 |

> 该账号由后端 `kk-admin` 库 `user` 表初始化，密码经 MD5 加盐双重加密。

## 目录结构

```
src/
├── assets/
├── components/
├── router/
├── store/
├── view/              # 管理页面
│   ├── welcome.vue            # 工作台
│   ├── member.vue             # 会员管理
│   ├── statistics.vue         # 数据统计
│   ├── filetrans.vue          # 录音转写记录
│   ├── fileformat.vue         # 格式转换记录
│   ├── voice-assistant.vue    # AI 助手记录
│   └── help.vue               # 帮助
└── main.js
```

## 主要功能

- 管理员登录（Token 鉴权）
- 会员信息查询与管理
- 订单与支付记录查看
- 录音转写记录与字幕管理
- 格式转换任务监控
- 实时识别 / AI 助手使用情况统计
- 数据看板
