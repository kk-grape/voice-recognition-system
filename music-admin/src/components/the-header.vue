<template>
  <a-layout-header class="header">
    <!-- Logo 区域 -->
    <div class="header-logo">
      <img src="/images/logo.png" alt="KK智能语音" class="logo-img" />
      <span class="logo-text">KK智能语音</span>
    </div>

    <!-- 右侧用户信息 -->
    <div class="header-right">
      <div class="user-info">
        <a-avatar :size="40" class="user-avatar">
          <template #icon><UserOutlined /></template>
        </a-avatar>
        <div class="user-details">
          <span class="user-name">{{ user.loginName }}</span>
          <span class="user-role">管理员</span>
        </div>
      </div>
      <a-divider type="vertical" class="divider" />
      <a-button type="text" class="logout-btn" @click="handleLogout">
        <template #icon><LogoutOutlined /></template>
        <span>退出登录</span>
      </a-button>
    </div>

    <!-- 装饰元素 -->
    <div class="header-decoration header-decoration-1"></div>
    <div class="header-decoration header-decoration-2"></div>
    <div class="header-decoration header-decoration-3"></div>
  </a-layout-header>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import store from "../store/index.js";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons-vue";
import { message } from "ant-design-vue";

const router = useRouter();
const user = store.state.user;

const handleLogout = () => {
  store.commit("setUser", {});
  message.success("已退出登录");
  router.push("/login");
};
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  overflow: hidden;
  padding: 0 24px !important;
}

/* Logo 样式 */
.header-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 1;
}

.logo-img {
  width: 50px;
  height: 50px;
  object-fit: contain;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.15));
  animation: float 3s ease-in-out infinite;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  letter-spacing: 2px;
}

/* 右侧用户信息 */
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
  z-index: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50px;
  backdrop-filter: blur(10px);
}

.user-avatar {
  border: 2px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.user-details {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.user-name {
  color: #fff;
  font-weight: 600;
  font-size: 14px;
}

.user-role {
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}

.divider {
  background: rgba(255, 255, 255, 0.4) !important;
  margin: 0 8px !important;
}

.logout-btn {
  color: #fff !important;
  padding: 8px 16px;
  height: auto;
  border-radius: 50px;
  background: rgba(255, 255, 255, 0.15);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: scale(1.02);
}

/* 装饰元素 */
.header-decoration {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
  pointer-events: none;
}

.header-decoration-1 {
  width: 200px;
  height: 200px;
  background: #fff;
  top: -100px;
  right: 200px;
}

.header-decoration-2 {
  width: 150px;
  height: 150px;
  background: var(--accent-color);
  bottom: -75px;
  right: 100px;
}

.header-decoration-3 {
  width: 100px;
  height: 100px;
  background: var(--primary-light);
  top: -50px;
  left: 50%;
}

/* 动画 */
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-4px); }
}
</style>
