<template>
  <div class="home-container">
    <!-- 流动背景装饰 -->
    <div class="bg-decoration">
      <div class="flow-blob blob-1"></div>
      <div class="flow-blob blob-2"></div>
      <div class="flow-blob blob-3"></div>
      <div class="flow-blob blob-4"></div>
      <div class="flow-particle particle-1"></div>
      <div class="flow-particle particle-2"></div>
      <div class="flow-particle particle-3"></div>
      <div class="flow-particle particle-4"></div>
      <div class="flow-particle particle-5"></div>
    </div>

    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-wrapper">
        <!-- 左侧内容 -->
        <div class="hero-content">
          <div class="hero-badge">
            <span class="badge-icon">🎙️</span>
            <span>新一代智能语音平台</span>
          </div>
          <h1 class="hero-title">
            <span class="title-line">智能语音识别</span>
            <span class="title-line accent">&amp; 音频处理平台</span>
          </h1>
          <p class="hero-desc">
            一站式完成语音转写、音频转换与批量文件处理，
            <br />为学习、科研与办公场景提供稳定可靠的语音解决方案。
          </p>
          <div class="hero-buttons">
            <router-link to="/home/filetrans" class="primary-btn">
              <span class="btn-icon">▶</span>
              立即体验语音识别
            </router-link>
            <router-link to="/home/help" class="secondary-btn">
              <span class="btn-icon">📖</span>
              查看使用指南
            </router-link>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-number">98.2%</span>
              <span class="stat-label">识别准确率</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">10s</span>
              <span class="stat-label">平均处理时长</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-number">50+</span>
              <span class="stat-label">支持格式</span>
            </div>
          </div>
        </div>

        <!-- 右侧可视化 -->
        <div class="hero-visual">
          <div class="visual-card main-card">
            <div class="card-header">
              <div class="card-dots">
                <span class="dot"></span>
                <span class="dot"></span>
                <span class="dot"></span>
              </div>
              <span class="card-title">实时语音识别</span>
            </div>
            <div class="wave-container">
              <div class="wave-bar wave-1"></div>
              <div class="wave-bar wave-2"></div>
              <div class="wave-bar wave-3"></div>
              <div class="wave-bar wave-4"></div>
              <div class="wave-bar wave-5"></div>
              <div class="wave-bar wave-6"></div>
              <div class="wave-bar wave-7"></div>
              <div class="wave-bar wave-8"></div>
              <div class="wave-bar wave-9"></div>
              <div class="wave-bar wave-10"></div>
              <div class="wave-bar wave-11"></div>
              <div class="wave-bar wave-12"></div>
              <div class="wave-bar wave-13"></div>
              <div class="wave-bar wave-14"></div>
              <div class="wave-bar wave-15"></div>
              <div class="wave-bar wave-16"></div>
            </div>
            <div class="card-footer">
              <div class="status-indicator">
                <span class="status-dot"></span>
                <span>识别中...</span>
              </div>
              <span class="time">02:34</span>
            </div>
          </div>
          <div class="visual-card small-card card-1">
            <div class="mini-icon">📊</div>
            <div class="mini-info">
              <span class="mini-value">1,234</span>
              <span class="mini-label">今日识别</span>
            </div>
          </div>
          <div class="visual-card small-card card-2">
            <div class="mini-icon">✨</div>
            <div class="mini-info">
              <span class="mini-value">99.8%</span>
              <span class="mini-label">准确率</span>
            </div>
          </div>
          <div class="floating-element elem-1">🎵</div>
          <div class="floating-element elem-2">🔊</div>
          <div class="floating-element elem-3">📁</div>
        </div>
      </div>
    </section>

    <!-- 装饰分隔带 -->
    <div class="section-divider">
      <div class="divider-line"></div>
      <div class="divider-icon">⬇</div>
      <div class="divider-line"></div>
    </div>

    <!-- 功能介绍 -->
    <section class="section features-section">
      <div class="section-header">
        <span class="section-badge">
          <span class="badge-dot"></span>
          核心能力
        </span>
        <h2>围绕语音任务的一站式工具</h2>
        <p>从录音文件到结构化文本，再到格式转换，完整覆盖你的语音处理链路。</p>
      </div>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <SoundOutlined />
          </div>
          <div class="feature-number">01</div>
          <h3>高精度语音识别</h3>
          <p>支持普通话及多种口音，针对长语音和噪声场景进行了优化。</p>
          <div class="feature-line"></div>
        </div>
        <div class="feature-card featured">
          <div class="feature-icon">
            <AudioOutlined />
          </div>
          <div class="feature-number">02</div>
          <h3>多格式音频转换</h3>
          <p>一键在 MP3、WAV、M4A 等常见格式间转换，兼顾音质与体积。</p>
          <div class="feature-line"></div>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <FileSyncOutlined />
          </div>
          <div class="feature-number">03</div>
          <h3>批量文件处理</h3>
          <p>
            支持多文件队列处理与任务管理，适合课程录音、访谈整理等高频场景。
          </p>
          <div class="feature-line"></div>
        </div>
      </div>
    </section>

    <!-- 快速链接 -->
    <section class="section quick-links">
      <div class="section-header">
        <span class="section-badge">
          <span class="badge-dot"></span>
          快速开始
        </span>
        <h2>常用功能直达</h2>
      </div>
      <div class="links-grid">
        <router-link to="/home/filetrans" class="link-card">
          <div class="link-icon">
            <SoundOutlined />
          </div>
          <div class="link-content">
            <span class="link-title">语音识别</span>
            <small>录音转文字，支持长音频</small>
          </div>
          <div class="link-arrow">→</div>
        </router-link>
        <router-link to="/home/fileformat" class="link-card">
          <div class="link-icon">
            <AudioOutlined />
          </div>
          <div class="link-content">
            <span class="link-title">音频转换</span>
            <small>多种格式自由切换</small>
          </div>
          <div class="link-arrow">→</div>
        </router-link>
        <router-link to="/home/help" class="link-card">
          <div class="link-icon">
            <QuestionCircleOutlined />
          </div>
          <div class="link-content">
            <span class="link-title">使用帮助</span>
            <small>上手指南与常见问题</small>
          </div>
          <div class="link-arrow">→</div>
        </router-link>
        <router-link to="/home/voice-assistant" class="link-card">
          <div class="link-icon">
            <CustomerServiceOutlined />
          </div>
          <div class="link-content">
            <span class="link-title">语音助手</span>
            <small>AI智能对话小助手</small>
          </div>
          <div class="link-arrow">→</div>
        </router-link>
      </div>
    </section>

    <!-- 底部装饰 -->
    <div class="footer-decoration">
      <div class="footer-wave"></div>
    </div>
  </div>
</template>

<script setup>
import {
  SoundOutlined,
  AudioOutlined,
  FileSyncOutlined,
  QuestionCircleOutlined,
  CustomerServiceOutlined,
} from "@ant-design/icons-vue";
</script>

<style scoped>
/* 流动性背景装饰 */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* 流动的渐变圆形 */
.flow-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.35;
}

.blob-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ffc3d0 50%, #ffd6d9 100%);
  top: -150px;
  left: -100px;
  animation: blobFloat1 20s ease-in-out infinite;
}

.blob-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 50%, #ffb8ba 100%);
  top: 40%;
  right: -100px;
  animation: blobFloat2 25s ease-in-out infinite;
}

.blob-3 {
  width: 350px;
  height: 350px;
  background: linear-gradient(135deg, #ffd6d9 0%, #ffe8eb 50%, #fff5f6 100%);
  bottom: -80px;
  left: 15%;
  animation: blobFloat3 18s ease-in-out infinite;
}

.blob-4 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #ffadc0 0%, #ffd6d9 50%, #ffe8eb 100%);
  bottom: 30%;
  right: 5%;
  animation: blobFloat4 22s ease-in-out infinite;
}

/* 漂浮的小粒子 */
.flow-particle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
}

.particle-1 {
  width: 10px;
  height: 10px;
  background: linear-gradient(135deg, #ff9a9e, #ff758c);
  top: 12%;
  left: 8%;
  animation: particleFloat1 15s ease-in-out infinite;
}

.particle-2 {
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #ffadc0, #ff9a9e);
  top: 22%;
  right: 12%;
  animation: particleFloat2 12s ease-in-out infinite;
}

.particle-3 {
  width: 6px;
  height: 6px;
  background: linear-gradient(135deg, #ffd6d9, #ffc3d0);
  top: 55%;
  left: 5%;
  animation: particleFloat3 18s ease-in-out infinite;
}

.particle-4 {
  width: 12px;
  height: 12px;
  background: linear-gradient(135deg, #ffb8ba, #ff9a9e);
  top: 65%;
  right: 18%;
  animation: particleFloat4 14s ease-in-out infinite;
}

.particle-5 {
  width: 8px;
  height: 8px;
  background: linear-gradient(135deg, #ff758c, #ffadc0);
  top: 40%;
  left: 88%;
  animation: particleFloat5 16s ease-in-out infinite;
}

/* 流动动画 */
@keyframes blobFloat1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  25% {
    transform: translate(40px, 30px) scale(1.1) rotate(5deg);
  }
  50% {
    transform: translate(20px, -20px) scale(0.95) rotate(-3deg);
  }
  75% {
    transform: translate(-30px, 40px) scale(1.05) rotate(3deg);
  }
}

@keyframes blobFloat2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  33% {
    transform: translate(-30px, 40px) scale(1.08) rotate(-5deg);
  }
  66% {
    transform: translate(40px, -30px) scale(0.92) rotate(4deg);
  }
}

@keyframes blobFloat3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  40% {
    transform: translate(50px, -20px) scale(1.1) rotate(6deg);
  }
  80% {
    transform: translate(-20px, 30px) scale(0.95) rotate(-4deg);
  }
}

@keyframes blobFloat4 {
  0%,
  100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  30% {
    transform: translate(-25px, 35px) scale(1.05) rotate(-3deg);
  }
  60% {
    transform: translate(35px, -25px) scale(0.98) rotate(5deg);
  }
}

@keyframes particleFloat1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(80px, -60px) scale(1.5);
    opacity: 0.8;
  }
}

@keyframes particleFloat2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: translate(-60px, 80px) scale(1.3);
    opacity: 0.7;
  }
}

@keyframes particleFloat3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.3;
  }
  50% {
    transform: translate(100px, -40px) scale(1.4);
    opacity: 0.6;
  }
}

@keyframes particleFloat4 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(-40px, 100px) scale(1.6);
    opacity: 0.8;
  }
}

@keyframes particleFloat5 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: translate(-80px, -60px) scale(1.3);
    opacity: 0.7;
  }
}

.home-container {
  min-height: calc(100vh - 108px);
  background: linear-gradient(180deg, #fff5f7 0%, #ffffff 30%, #fff7fb 100%);
  position: relative;
  z-index: 1;
}

/* ========== 英雄区域 ========== */
.hero-section {
  padding: 60px 0 40px;
}

.hero-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
}

/* 左侧内容 */
.hero-content {
  animation: fadeInLeft 0.8s ease-out;
}

@keyframes fadeInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(
    135deg,
    rgba(255, 154, 158, 0.15),
    rgba(255, 117, 140, 0.1)
  );
  border: 1px solid rgba(255, 154, 158, 0.25);
  border-radius: 50px;
  margin-bottom: 24px;
  font-size: 13px;
  color: #ff5f7b;
}

.badge-icon {
  font-size: 16px;
}

.hero-title {
  font-size: 42px;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 20px;
  color: #333;
}

.title-line {
  display: block;
}

.title-line.accent {
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 50%, #ffadc0 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 16px;
  line-height: 1.8;
  color: #666;
  margin-bottom: 28px;
}

.hero-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 32px;
}

.primary-btn,
.secondary-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.primary-btn {
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 100%);
  color: #fff;
  box-shadow: 0 8px 24px rgba(255, 117, 140, 0.4);
}

.primary-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(255, 117, 140, 0.5);
}

.secondary-btn {
  background: #fff;
  color: #ff5f7b;
  border: 2px solid rgba(255, 154, 158, 0.3);
}

.secondary-btn:hover {
  background: rgba(255, 154, 158, 0.08);
  border-color: #ff9a9e;
  transform: translateY(-2px);
}

.btn-icon {
  font-size: 14px;
}

/* 数据统计 */
.hero-stats {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  border: 1px solid rgba(255, 154, 158, 0.15);
  backdrop-filter: blur(10px);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #ff5f7b;
}

.stat-label {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: linear-gradient(
    180deg,
    transparent,
    rgba(255, 154, 158, 0.3),
    transparent
  );
}

/* 右侧可视化 */
.hero-visual {
  position: relative;
  animation: fadeInRight 0.8s ease-out 0.2s both;
}

@keyframes fadeInRight {
  from {
    opacity: 0;
    transform: translateX(30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.main-card {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 20px;
  box-shadow: 0 20px 60px rgba(255, 154, 158, 0.15);
  border: 1px solid rgba(255, 154, 158, 0.2);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.card-dots {
  display: flex;
  gap: 6px;
}

.card-dots .dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #ff9a9e;
}

.card-dots .dot:nth-child(2) {
  background: #ffd6d9;
}
.card-dots .dot:nth-child(3) {
  background: #ffb8ba;
}

.card-title {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 波形动画 */
.wave-container {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 6px;
  height: 120px;
  padding: 0 10px;
  margin-bottom: 16px;
}

.wave-bar {
  width: 10px;
  background: linear-gradient(180deg, #ff758c, #ff9a9e, #ffd6d9);
  border-radius: 4px;
  animation: wavePulse 1.2s ease-in-out infinite;
}

.wave-1 {
  height: 60%;
  animation-delay: 0s;
}
.wave-2 {
  height: 80%;
  animation-delay: 0.1s;
}
.wave-3 {
  height: 100%;
  animation-delay: 0.2s;
}
.wave-4 {
  height: 70%;
  animation-delay: 0.3s;
}
.wave-5 {
  height: 90%;
  animation-delay: 0.4s;
}
.wave-6 {
  height: 50%;
  animation-delay: 0.5s;
}
.wave-7 {
  height: 75%;
  animation-delay: 0.6s;
}
.wave-8 {
  height: 55%;
  animation-delay: 0.7s;
}
.wave-9 {
  height: 85%;
  animation-delay: 0.8s;
}
.wave-10 {
  height: 65%;
  animation-delay: 0.9s;
}
.wave-11 {
  height: 95%;
  animation-delay: 1s;
}
.wave-12 {
  height: 72%;
  animation-delay: 1.1s;
}
.wave-13 {
  height: 88%;
  animation-delay: 1.2s;
}
.wave-14 {
  height: 58%;
  animation-delay: 1.3s;
}
.wave-15 {
  height: 78%;
  animation-delay: 1.4s;
}
.wave-16 {
  height: 62%;
  animation-delay: 1.5s;
}

@keyframes wavePulse {
  0%,
  100% {
    transform: scaleY(0.6);
  }
  50% {
    transform: scaleY(1);
  }
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid rgba(255, 154, 158, 0.15);
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #4caf50;
  border-radius: 50%;
  animation: blink 1.5s ease-in-out infinite;
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
}

.time {
  font-size: 13px;
  color: #999;
  font-family: monospace;
}

/* 小卡片 */
.small-card {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 18px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 154, 158, 0.15);
}

.card-1 {
  bottom: -30px;
  left: -30px;
}

.card-2 {
  top: 30px;
  right: -40px;
}

.mini-icon {
  font-size: 24px;
}

.mini-info {
  display: flex;
  flex-direction: column;
}

.mini-value {
  font-size: 18px;
  font-weight: 700;
  color: #ff5f7b;
}

.mini-label {
  font-size: 11px;
  color: #888;
}

/* 漂浮元素 */
.floating-element {
  position: absolute;
  font-size: 28px;
  animation: float 3s ease-in-out infinite;
}

.elem-1 {
  top: -20px;
  right: 20%;
  animation-delay: 0s;
}
.elem-2 {
  bottom: 40px;
  left: -20px;
  animation-delay: 1s;
}
.elem-3 {
  top: 50%;
  right: -30px;
  animation-delay: 2s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(10deg);
  }
}

/* ========== 分隔带 ========== */
.section-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  padding: 20px 0;
  margin: 20px 0;
}

.divider-line {
  width: 100px;
  height: 2px;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 154, 158, 0.4),
    transparent
  );
}

.divider-icon {
  font-size: 18px;
  color: #ff9a9e;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(8px);
  }
}

/* ========== 功能介绍 ========== */
.features-section {
  padding: 40px 0 60px;
}

.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.section-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 6px 16px;
  background: linear-gradient(
    135deg,
    rgba(255, 154, 158, 0.12),
    rgba(255, 117, 140, 0.08)
  );
  border-radius: 50px;
  font-size: 13px;
  color: #ff5f7b;
  font-weight: 500;
  margin-bottom: 16px;
}

.badge-dot {
  width: 6px;
  height: 6px;
  background: #ff758c;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.6;
  }
}

.section-header h2 {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.section-header p {
  font-size: 15px;
  color: #777;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
}

.feature-card {
  position: relative;
  background: #fff;
  border-radius: 20px;
  padding: 32px 28px;
  text-align: center;
  transition: all 0.4s ease;
  border: 1px solid rgba(255, 154, 158, 0.12);
  overflow: hidden;
}

.feature-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ff9a9e, #ff758c);
  transform: scaleX(0);
  transition: transform 0.4s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(255, 154, 158, 0.15);
  border-color: rgba(255, 154, 158, 0.3);
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-card.featured {
  background: linear-gradient(135deg, #fff5f7, #fff);
  border-color: rgba(255, 154, 158, 0.25);
  transform: translateY(-8px);
  box-shadow: 0 16px 36px rgba(255, 154, 158, 0.18);
}

.feature-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
  box-shadow: 0 8px 20px rgba(255, 117, 140, 0.35);
}

.feature-number {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 48px;
  font-weight: 800;
  color: rgba(255, 154, 158, 0.08);
}

.feature-card h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.7;
}

.feature-line {
  width: 40px;
  height: 3px;
  background: linear-gradient(90deg, #ff9a9e, #ff758c);
  border-radius: 2px;
  margin: 20px auto 0;
}

/* ========== 快速链接 ========== */
.quick-links {
  padding: 20px 0 60px;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 32px;
}

.link-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px 22px;
  background: #fff;
  border-radius: 16px;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 154, 158, 0.12);
}

.link-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 28px rgba(255, 154, 158, 0.15);
  border-color: rgba(255, 154, 158, 0.3);
}

.link-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  color: #fff;
  flex-shrink: 0;
}

.link-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.link-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.link-content small {
  font-size: 12px;
  color: #888;
}

.link-arrow {
  font-size: 18px;
  color: #ccc;
  transition: all 0.3s ease;
}

.link-card:hover .link-arrow {
  color: #ff5f7b;
  transform: translateX(4px);
}

/* ========== 底部装饰 ========== */
.footer-decoration {
  position: relative;
  height: 120px;
  overflow: hidden;
}

.footer-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100%;
  background: linear-gradient(
    180deg,
    transparent 0%,
    rgba(255, 154, 158, 0.08) 100%
  );
}

.footer-wave::before {
  content: "";
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 60'%3E%3Cpath fill='%23fff' d='M0,60 L0,30 Q360,0 720,30 T1440,30 L1440,60 Z'/%3E%3C/svg%3E")
    repeat-x;
  background-size: 1440px 60px;
}

/* ========== 响应式 ========== */
@media (max-width: 1024px) {
  .hero-wrapper {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }

  .hero-content {
    order: 1;
  }

  .hero-visual {
    order: 0;
  }

  .hero-badge {
    margin: 0 auto 24px;
  }

  .hero-buttons {
    justify-content: center;
  }

  .hero-stats {
    justify-content: center;
    max-width: 400px;
    margin: 0 auto;
  }

  .features-grid {
    grid-template-columns: 1fr;
    max-width: 500px;
  }

  .feature-card.featured {
    transform: none;
  }

  .links-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-section {
    padding: 40px 0 30px;
  }

  .hero-wrapper {
    padding: 0 20px;
  }

  .hero-title {
    font-size: 32px;
  }

  .hero-desc {
    font-size: 14px;
  }

  .primary-btn,
  .secondary-btn {
    padding: 12px 24px;
    font-size: 14px;
  }

  .hero-stats {
    flex-direction: column;
    gap: 16px;
  }

  .stat-divider {
    width: 60px;
    height: 1px;
  }

  .small-card {
    display: none;
  }

  .features-grid,
  .links-grid {
    padding: 0 20px;
  }

  .section-header h2 {
    font-size: 26px;
  }

  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style>
