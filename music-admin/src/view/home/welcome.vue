<template>
  <div class="welcome-container">
    <!-- 装饰元素 -->
    <div class="decoration decoration-1"></div>
    <div class="decoration decoration-2"></div>
    <div class="decoration decoration-3"></div>

    <!-- 欢迎标题卡片 -->
    <div class="welcome-card">
      <div class="welcome-content">
        <div class="welcome-icon">
          <span class="icon-emojis">🎵</span>
        </div>
        <div class="welcome-text">
          <h1 class="welcome-title">欢迎回来，管理员</h1>
          <p class="welcome-subtitle">KK智能语音管理系统 - 让语音识别更简单</p>
        </div>
      </div>
    </div>

    <!-- 统计数据卡片 -->
    <a-row :gutter="[20, 20]" class="stat-row">
      <a-col :xs="12" :sm="8" :lg="6">
        <div class="stat-card stat-card-blue" @click="goTo('/home/member')">
          <div class="stat-icon">
            <TeamOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.memberCount || 0 }}</div>
            <div class="stat-label">会员总数</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :sm="8" :lg="6">
        <div class="stat-card stat-card-green" @click="goTo('/home/filetrans')">
          <div class="stat-icon">
            <AudioOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.filetransCount || 0 }}</div>
            <div class="stat-label">语音识别数</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :sm="8" :lg="6">
        <div class="stat-card stat-card-orange" @click="goTo('/home/order')">
          <div class="stat-icon">
            <DollarOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">¥{{ statistics.orderAmount || 0 }}</div>
            <div class="stat-label">订单总金额</div>
          </div>
        </div>
      </a-col>
      <a-col :xs="12" :sm="8" :lg="6">
        <div class="stat-card stat-card-purple" @click="goTo('/home/realtime')">
          <div class="stat-icon">
            <SoundOutlined />
          </div>
          <div class="stat-info">
            <div class="stat-value">
              {{ formatDuration(statistics.asrDuration) }}
            </div>
            <div class="stat-label">实时识别时长</div>
          </div>
        </div>
      </a-col>
    </a-row>

    <!-- 功能快捷入口 -->
    <div class="quick-cards">
      <div class="quick-card" @click="goTo('/home/filetrans')">
        <div class="card-icon card-icon-pink">
          <AudioOutlined />
        </div>
        <div class="card-info">
          <h3>语音识别</h3>
          <p>管理语音转字幕任务</p>
        </div>
        <div class="card-arrow">
          <RightOutlined />
        </div>
      </div>

      <div class="quick-card" @click="goTo('/home/member')">
        <div class="card-icon card-icon-yellow">
          <TeamOutlined />
        </div>
        <div class="card-info">
          <h3>会员管理</h3>
          <p>查看和管理会员信息</p>
        </div>
        <div class="card-arrow">
          <RightOutlined />
        </div>
      </div>

      <div class="quick-card" @click="goTo('/home/order')">
        <div class="card-icon card-icon-blue">
          <DollarOutlined />
        </div>
        <div class="card-info">
          <h3>订单管理</h3>
          <p>查看订单交易记录</p>
        </div>
        <div class="card-arrow">
          <RightOutlined />
        </div>
      </div>

      <div class="quick-card" @click="goTo('/home/ai-chat')">
        <div class="card-icon card-icon-purple">
          <RobotOutlined />
        </div>
        <div class="card-info">
          <h3>AI助手</h3>
          <p>用户与AI的对话记录</p>
        </div>
        <div class="card-arrow">
          <RightOutlined />
        </div>
      </div>
    </div>

    <!-- 底部信息卡片 -->
    <a-row :gutter="[20, 20]" class="info-row">
      <a-col :xs="24" :lg="12">
        <a-card class="info-card">
          <template #title>
            <span class="card-title-icon">💻</span> 系统信息
          </template>
          <a-descriptions :column="1" size="small" class="info-descriptions">
            <a-descriptions-item label="系统版本">
              <a-tag color="processing">v1.0.0</a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="前端框架"
              >Vue 3 + Vite</a-descriptions-item
            >
            <a-descriptions-item label="UI框架"
              >Ant Design Vue 4.x</a-descriptions-item
            >
            <a-descriptions-item label="当前时间">{{
              currentTime
            }}</a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card class="info-card">
          <template #title>
            <span class="card-title-icon">📖</span> 使用指南
          </template>
          <a-list size="small" :data-source="guideItems">
            <template #renderItem="{ item }">
              <a-list-item class="guide-item">
                <a-list-item-meta>
                  <template #title>
                    <span class="guide-title">{{ item.title }}</span>
                  </template>
                  <template #description>
                    <span class="guide-desc">{{ item.desc }}</span>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import {
  AudioOutlined,
  TeamOutlined,
  RobotOutlined,
  SoundOutlined,
  RightOutlined,
  DollarOutlined,
} from "@ant-design/icons-vue";
import { getMemberCount } from "../../api/member";
import { getFiletransCount } from "../../api/filetrans";
import { getOrderSumAmount } from "../../api/order";
import { getAsrSessionSumDuration } from "../../api/asr";

const router = useRouter();

// 统计数据
const statistics = reactive({
  memberCount: 0,
  filetransCount: 0,
  orderAmount: 0,
  asrDuration: 0,
});

const currentTime = ref("");
let timer = null;

const guideItems = [
  { title: "会员管理", desc: "查看和管理系统会员信息，调整会员等级" },
  { title: "语音识别", desc: "管理语音转字幕任务，查看识别结果" },
  { title: "订单管理", desc: "查看订单交易记录，了解收入情况" },
  { title: "AI助手", desc: "查看用户与AI助手的对话记录" },
];

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return "0秒";
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const secs = seconds % 60;
  let result = "";
  if (hours > 0) result += hours + "时";
  if (minutes > 0) result += minutes + "分";
  if (secs > 0 || result === "") result += secs + "秒";
  return result;
};

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 并行加载所有统计数据
    const [memberRes, filetransRes, orderRes, asrRes] =
      await Promise.allSettled([
        getMemberCount(),
        getFiletransCount(),
        getOrderSumAmount(),
        getAsrSessionSumDuration(),
      ]);

    if (memberRes.status === "fulfilled") {
      statistics.memberCount = memberRes.value?.content ?? 0;
    }
    if (filetransRes.status === "fulfilled") {
      statistics.filetransCount = filetransRes.value?.content ?? 0;
    }
    if (orderRes.status === "fulfilled") {
      // content 可能是数字也可能是对象 { totalAmount: xxx }
      const orderContent = orderRes.value?.content;
      statistics.orderAmount = typeof orderContent === 'number' 
        ? orderContent 
        : (orderContent?.totalAmount ?? 0);
    }
    if (asrRes.status === "fulfilled") {
      statistics.asrDuration = asrRes.value?.content ?? 0;
    }
  } catch (err) {
    console.error("加载统计数据失败", err);
  }
};

// 更新当前时间
const updateTime = () => {
  const now = new Date();
  currentTime.value = now.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

// 路由跳转
const goTo = (path) => {
  router.push(path);
};

onMounted(() => {
  updateTime();
  timer = setInterval(updateTime, 1000);
  loadStatistics();
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.welcome-container {
  position: relative;
  padding: 0;
  overflow: hidden;
}

/* 装饰元素 */
.decoration {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.decoration-1 {
  width: 200px;
  height: 200px;
  background: linear-gradient(
    135deg,
    var(--primary-lighter),
    var(--primary-light)
  );
  top: -80px;
  right: -80px;
  opacity: 0.6;
}

.decoration-2 {
  width: 150px;
  height: 150px;
  background: linear-gradient(135deg, var(--accent-light), var(--accent-color));
  bottom: 100px;
  left: -60px;
  opacity: 0.4;
}

.decoration-3 {
  width: 100px;
  height: 100px;
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--primary-light)
  );
  top: 50%;
  right: 10%;
  opacity: 0.3;
}

/* 欢迎卡片 */
.welcome-card {
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--accent-color) 100%
  );
  border-radius: var(--radius-large);
  padding: 32px;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
  z-index: 1;
}

.welcome-card::before {
  content: "";
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-card::after {
  content: "";
  position: absolute;
  bottom: -30%;
  left: 10%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
}

.welcome-content {
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
  z-index: 1;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.icon-emojis {
  font-size: 40px;
}

.welcome-text {
  color: #fff;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.welcome-subtitle {
  font-size: 16px;
  opacity: 0.9;
}

/* 统计卡片 */
.stat-row {
  position: relative;
  z-index: 1;
  margin-bottom: 20px;
}

.stat-card {
  background: var(--bg-card);
  border-radius: var(--radius-medium);
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-card);
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: "";
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
}

.stat-card-blue::before {
  background: #1890ff;
}
.stat-card-green::before {
  background: #52c41a;
}
.stat-card-orange::before {
  background: #fa8c16;
}
.stat-card-purple::before {
  background: #722ed1;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card-blue .stat-icon {
  background: linear-gradient(135deg, #1890ff, #69c0ff);
}
.stat-card-green .stat-icon {
  background: linear-gradient(135deg, #52c41a, #95de64);
}
.stat-card-orange .stat-icon {
  background: linear-gradient(135deg, #fa8c16, #ffc53d);
}
.stat-card-purple .stat-icon {
  background: linear-gradient(135deg, #722ed1, #b37feb);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: var(--text-light);
  margin-top: 4px;
}

/* 快捷卡片 */
.quick-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;
  position: relative;
  z-index: 1;
}

@media (max-width: 1200px) {
  .quick-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .quick-cards {
    grid-template-columns: 1fr;
  }
}

.quick-card {
  background: var(--bg-card);
  border-radius: var(--radius-medium);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  transition: all var(--transition-normal);
  box-shadow: var(--shadow-card);
  position: relative;
  overflow: hidden;
}

.quick-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color));
  transform: scaleX(0);
  transition: transform var(--transition-normal);
}

.quick-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-card-hover);
}

.quick-card:hover::before {
  transform: scaleX(1);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  flex-shrink: 0;
}

.card-icon-pink {
  background: linear-gradient(135deg, var(--accent-color), var(--accent-light));
}

.card-icon-yellow {
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--primary-light)
  );
}

.card-icon-blue {
  background: linear-gradient(135deg, #1890ff, #69c0ff);
}

.card-icon-purple {
  background: linear-gradient(135deg, #9c27b0, #e1bee7);
}

.card-info {
  flex: 1;
}

.card-info h3 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.card-info p {
  font-size: 13px;
  color: var(--text-light);
}

.card-arrow {
  color: var(--text-light);
  font-size: 18px;
  transition: all var(--transition-normal);
}

.quick-card:hover .card-arrow {
  color: var(--primary-color);
  transform: translateX(4px);
}

/* 信息卡片 */
.info-row {
  position: relative;
  z-index: 1;
}

.info-card {
  height: 100%;
}

.info-card :deep(.ant-card-head) {
  background: linear-gradient(135deg, var(--primary-lighter), var(--bg-card));
}

.card-title-icon {
  margin-right: 8px;
}

.info-descriptions :deep(.ant-descriptions-item-label) {
  color: var(--text-secondary);
  font-weight: 500;
}

.guide-item {
  padding: 10px 0 !important;
}

.guide-title {
  font-weight: 600;
  color: var(--text-primary);
}

.guide-desc {
  color: var(--text-light);
  font-size: 13px;
}
</style>
