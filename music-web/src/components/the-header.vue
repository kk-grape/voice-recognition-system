<template>
  <header class="fancy-header">
    <!-- 背景装饰 -->
    <div class="header-bg-decor">
      <div class="bg-shape bg-shape-1"></div>
      <div class="bg-shape bg-shape-2"></div>
      <div class="bg-shape bg-shape-3"></div>
    </div>

    <div class="header-container">
      <!-- Logo -->
      <div class="fancy-logo">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" class="logo-svg">
            <path
              d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"
              fill="currentColor"
              opacity="0.9"
            />
            <path
              d="M19 10v2a7 7 0 0 1-14 0v-2"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
            <line
              x1="12"
              y1="19"
              x2="12"
              y2="23"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
            <line
              x1="8"
              y1="23"
              x2="16"
              y2="23"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
            />
          </svg>
        </div>
        <div class="logo-text-group">
          <span class="logo-title">智能语音识别</span>
          <span class="logo-subtitle">Smart Voice Platform</span>
        </div>
      </div>

      <!-- Navigation Menu -->
      <nav class="fancy-nav">
        <ul class="nav-list">
          <li class="nav-item" :class="{ active: isActive('/home') }">
            <router-link to="/home" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                  <polyline points="9 22 9 12 15 12 15 22" />
                </svg>
              </span>
              <span class="nav-text">首页</span>
            </router-link>
          </li>
          <li class="nav-item" :class="{ active: isActive('/home/filetrans') }">
            <router-link to="/home/filetrans" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path
                    d="M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z"
                  />
                  <path d="M19 10v2a7 7 0 0 1-14 0v-2" />
                  <line x1="12" y1="19" x2="12" y2="23" />
                </svg>
              </span>
              <span class="nav-text">语音识别</span>
            </router-link>
          </li>
          <li
            class="nav-item"
            :class="{ active: isActive('/home/voice-assistant') }"
          >
            <router-link to="/home/voice-assistant" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
                  />
                </svg>
              </span>
              <span class="nav-text">语音助手</span>
            </router-link>
          </li>
          <li
            class="nav-item"
            :class="{ active: isActive('/home/fileformat') }"
          >
            <router-link to="/home/fileformat" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <polyline points="23 4 23 10 17 10" />
                  <polyline points="1 20 1 14 7 14" />
                  <path
                    d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"
                  />
                </svg>
              </span>
              <span class="nav-text">音频转换</span>
            </router-link>
          </li>
          <li class="nav-item" :class="{ active: isActive('/home/help') }">
            <router-link to="/home/help" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <circle cx="12" cy="12" r="10" />
                  <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3" />
                  <line x1="12" y1="17" x2="12.01" y2="17" />
                </svg>
              </span>
              <span class="nav-text">帮助文档</span>
            </router-link>
          </li>
          <li class="nav-item" :class="{ active: isActive('/home/member') }">
            <router-link to="/home/member" class="nav-link">
              <span class="nav-icon">
                <svg
                  viewBox="0 0 24 24"
                  fill="none"
                  stroke="currentColor"
                  stroke-width="2"
                >
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </span>
              <span class="nav-text">个人中心</span>
            </router-link>
          </li>
        </ul>
      </nav>

      <!-- User Info -->
      <div class="fancy-user">
        <div class="user-welcome" v-if="member?.name">
          <span class="welcome-icon">
            <svg
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
          </span>
          <span class="welcome-text">嗨，{{ member.name }}</span>
        </div>
        <router-link to="/login" class="user-logout">
          <svg
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            class="logout-icon"
          >
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
            <polyline points="16 17 21 12 16 7" />
            <line x1="21" y1="12" x2="9" y2="12" />
          </svg>
          <span class="logout-text">退出</span>
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { onUnmounted } from "vue";
import store from "../store/index.js";
import { useRouter } from "vue-router";

const router = useRouter();
const member = store.state.member || {};

const isActive = (path) => {
  const current = router.currentRoute.value.path;
  if (path === "/home") {
    return current === "/home" || current === "/home/welcome";
  }
  return current === path;
};

const heart = () => {};
let interval = setInterval(heart, 30000);

onUnmounted(() => clearInterval(interval));
</script>

<style scoped>
/* 头部容器 */
.fancy-header {
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 12px 0;
  overflow: hidden;
}

/* 背景装饰 */
.header-bg-decor {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.5;
}

.bg-shape-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ffadc0 50%, #ffd6d9 100%);
  top: -100px;
  right: 3%;
  animation: floatBg1 12s ease-in-out infinite;
}

.bg-shape-2 {
  width: 500px;
  height: 300px;
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 50%, #ffb8ba 100%);
  top: -50px;
  left: 0%;
  animation: floatBg2 15s ease-in-out infinite;
}

.bg-shape-3 {
  width: 1000px;
  height: 1000px;
  background: linear-gradient(135deg, #ffcacd 0%, #ffe8eb 50%, #fff5f6 100%);
  top: -150px;
  left: 35%;
  animation: floatBg3 10s ease-in-out infinite;
}

@keyframes floatBg1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-20px, 20px) scale(1.1);
  }
}

@keyframes floatBg2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(15px, -15px) scale(0.95);
  }
}

@keyframes floatBg3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(10px, 10px) scale(1.05);
  }
}

/* 主容器 */
.header-container {
  position: relative;
  z-index: 1;
  max-width: 1300px;
  margin: 0 auto;
  padding: 3px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 100px;
  box-shadow:
    0 4px 30px rgba(255, 154, 158, 0.15),
    0 1px 2px rgba(0, 0, 0, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.8);
}

/* Logo 区域 */
.fancy-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo-icon {
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 50%, #ffadc0 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow:
    0 6px 20px rgba(255, 117, 140, 0.4),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  position: relative;
  overflow: hidden;
}

.logo-icon::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.3) 0%,
    transparent 60%
  );
  animation: logoShine 3s ease-in-out infinite;
}

@keyframes logoShine {
  0%,
  100% {
    opacity: 0.5;
    transform: translate(0, 0);
  }
  50% {
    opacity: 1;
    transform: translate(20%, 20%);
  }
}

.logo-svg {
  width: 24px;
  height: 24px;
  color: #fff;
  position: relative;
  z-index: 1;
}

.logo-text-group {
  display: flex;
  flex-direction: column;
  line-height: 1.3;
}

.logo-title {
  font-size: 17px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-subtitle {
  font-size: 10px;
  color: #999;
  letter-spacing: 0.5px;
}

/* 导航菜单 */
.fancy-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-list {
  display: inline-flex;
  list-style: none;
  padding: 6px;
  margin: 0;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 999px;
  box-shadow:
    0 4px 20px rgba(255, 154, 158, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 1);
  border: 2px solid rgba(255, 154, 158, 0.25);
}

.nav-item {
  margin: 0 2px;
}

.nav-link {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 999px;
  text-decoration: none;
  color: #888;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-icon {
  width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
  color: #bbb;
}

.nav-icon svg {
  width: 100%;
  height: 100%;
  stroke: currentColor;
  stroke-width: 2;
}

.nav-link:hover {
  color: #ff758c;
}

.nav-link:hover .nav-icon {
  transform: scale(1.1);
  color: #ff758c;
}

/* 选中状态 - 明显样式 */
.nav-item.active .nav-link {
  color: #fff;
  font-weight: 600;
  background: linear-gradient(135deg, #ff5f7b 0%, #ff6b8a 50%, #ff758c 100%);
  box-shadow:
    0 4px 16px rgba(255, 95, 123, 0.5),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.nav-item.active .nav-link::before {
  content: "";
  position: absolute;
  inset: -3px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff5f7b, #ff758c);
  z-index: -2;
  opacity: 0.6;
}

.nav-item.active .nav-link::after {
  content: "";
  position: absolute;
  inset: -1px;
  border-radius: 999px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  pointer-events: none;
}

.nav-item.active .nav-icon {
  color: #fff;
}

.nav-item.active .nav-icon svg {
  stroke: #fff;
}

.nav-text {
  white-space: nowrap;
}

/* 用户信息 */
.fancy-user {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.user-welcome {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 999px;
  background: linear-gradient(135deg, #fff5f6 0%, #fff 100%);
  border: 1px solid rgba(255, 154, 158, 0.15);
}

.welcome-icon {
  width: 20px;
  height: 20px;
  color: #ff9a9e;
}

.welcome-icon svg {
  width: 100%;
  height: 100%;
}

.welcome-text {
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.user-logout {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 999px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  color: #fff;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(255, 117, 140, 0.3);
  position: relative;
  overflow: hidden;
}

.user-logout::before {
  content: "";
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transition: left 0.5s ease;
}

.user-logout:hover::before {
  left: 100%;
}

.user-logout:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 117, 140, 0.4);
}

.logout-icon {
  width: 16px;
  height: 16px;
}

.logout-text {
  white-space: nowrap;
}

/* 响应式 */
@media (max-width: 1100px) {
  .header-container {
    gap: 20px;
    padding: 0 20px;
  }

  .nav-link {
    padding: 8px 12px;
    font-size: 12px;
  }

  .nav-icon {
    display: none;
  }
}

@media (max-width: 900px) {
  .header-container {
    flex-wrap: wrap;
    justify-content: center;
    padding: 16px 20px;
  }

  .fancy-logo {
    width: 100%;
    justify-content: center;
    margin-bottom: 12px;
  }

  .fancy-nav {
    order: 3;
    width: 100%;
  }

  .fancy-user {
    order: 2;
  }

  .nav-list {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }
}

@media (max-width: 600px) {
  .logo-text-group {
    display: none;
  }

  .nav-list {
    overflow-x: auto;
    justify-content: flex-start;
    -webkit-overflow-scrolling: touch;
  }

  .nav-item {
    flex-shrink: 0;
  }

  .user-welcome {
    padding: 6px 12px;
  }

  .welcome-text {
    display: none;
  }

  .logout-text {
    display: none;
  }

  .user-logout {
    padding: 10px;
  }
}
</style>
