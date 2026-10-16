<template>
  <a-layout-sider width="220" class="sider">
    <!-- Logo 区域 -->
    <div class="sider-logo">
      <img src="/images/logo.png" alt="KK智能语音" class="logo-img" />
      <div class="logo-text">
        <span class="logo-title">KK智能语音</span>
        <span class="logo-subtitle">管理系统</span>
      </div>
    </div>

    <!-- 导航菜单 -->
    <a-menu v-model:selectedKeys="selectedKeys" mode="inline" class="nav-menu">
      <a-menu-item key="/home/welcome" @click="navigate('/home/welcome')">
        <template #icon><HomeOutlined /></template>
        <span>首页概览</span>
      </a-menu-item>

      <a-menu-item key="/home/filetrans" @click="navigate('/home/filetrans')">
        <template #icon><AudioOutlined /></template>
        <span>语音识别</span>
      </a-menu-item>

      <a-menu-item key="/home/member" @click="navigate('/home/member')">
        <template #icon><TeamOutlined /></template>
        <span>会员管理</span>
      </a-menu-item>

      <a-menu-item key="/home/order" @click="navigate('/home/order')">
        <template #icon><DollarOutlined /></template>
        <span>订单管理</span>
      </a-menu-item>

      <a-menu-item key="/home/fileformat" @click="navigate('/home/fileformat')">
        <template #icon><FolderOpenOutlined /></template>
        <span>文件格式</span>
      </a-menu-item>

      <a-menu-item key="/home/ai-chat" @click="navigate('/home/ai-chat')">
        <template #icon><RobotOutlined /></template>
        <span>AI助手</span>
      </a-menu-item>

      <a-menu-item key="/home/realtime" @click="navigate('/home/realtime')">
        <template #icon><SoundOutlined /></template>
        <span>实时语音</span>
      </a-menu-item>

      <a-menu-item key="/home/help" @click="navigate('/home/help')">
        <template #icon><QuestionCircleOutlined /></template>
        <span>帮助文档</span>
      </a-menu-item>
    </a-menu>

    <!-- 底部装饰 -->
    <div class="sider-footer">
      <div class="footer-decoration">
        <span class="footer-icon">♪</span>
        <span class="footer-icon">♫</span>
        <span class="footer-icon">♪</span>
      </div>
      <span class="footer-text">享受智能语音服务</span>
    </div>
  </a-layout-sider>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRouter } from "vue-router";
import {
  HomeOutlined,
  AudioOutlined,
  TeamOutlined,
  FolderOpenOutlined,
  RobotOutlined,
  SoundOutlined,
  QuestionCircleOutlined,
  DollarOutlined,
} from "@ant-design/icons-vue";

const selectedKeys = ref(["/home/welcome"]);
const router = useRouter();

const navigate = (path) => {
  router.push(path);
};

watch(
  () => router.currentRoute.value.path,
  (newValue) => {
    selectedKeys.value = [newValue];
  },
  { immediate: true },
);
</script>

<style scoped>
.sider {
  background: var(--bg-card) !important;
  box-shadow: 4px 0 20px var(--shadow-color);
  display: flex;
  flex-direction: column;
  border-radius: 0 var(--radius-large) var(--radius-large) 0;
  overflow: hidden;
}

/* Logo 样式 */
.sider-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  background: linear-gradient(
    135deg,
    var(--primary-lighter) 0%,
    var(--bg-card) 100%
  );
  border-bottom: 2px dashed var(--border-color);
}

.logo-img {
  width: 45px;
  height: 45px;
  object-fit: contain;
  filter: drop-shadow(0 2px 6px var(--shadow-color));
  animation: float 3s ease-in-out infinite;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.logo-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.logo-subtitle {
  font-size: 11px;
  color: var(--text-light);
}

/* 菜单样式 */
.nav-menu {
  flex: 1;
  padding: 16px 0;
  background: transparent !important;
}

.nav-menu :deep(.ant-menu-item) {
  margin: 4px 12px !important;
  padding-left: 20px !important;
  height: 48px !important;
  line-height: 48px !important;
  border-radius: var(--radius-medium) !important;
  font-weight: 500;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
}

.nav-menu :deep(.ant-menu-item:hover) {
  background: var(--primary-lighter) !important;
}

.nav-menu :deep(.ant-menu-item-selected) {
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--accent-color) 100%
  ) !important;
  color: #fff !important;
  box-shadow: 0 4px 15px var(--shadow-color);
}

.nav-menu :deep(.ant-menu-item-selected:hover) {
  background: linear-gradient(
    135deg,
    var(--primary-dark) 0%,
    var(--accent-dark) 100%
  ) !important;
}

.nav-menu :deep(.ant-menu-item .anticon) {
  font-size: 18px !important;
}

/* 底部装饰 */
.sider-footer {
  padding: 20px;
  background: linear-gradient(
    180deg,
    var(--bg-card) 0%,
    var(--primary-lighter) 100%
  );
  border-top: 2px dashed var(--border-color);
  text-align: center;
}

.footer-decoration {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.footer-icon {
  font-size: 18px;
  color: var(--primary-color);
  animation: bounce 2s ease-in-out infinite;
}

.footer-icon:nth-child(2) {
  animation-delay: 0.3s;
}

.footer-icon:nth-child(3) {
  animation-delay: 0.6s;
}

.footer-text {
  font-size: 12px;
  color: var(--text-light);
}

/* 动画 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}
</style>
