<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
      <div class="circle circle-4"></div>
    </div>

    <!-- 装饰图标 -->
    <div class="music-notes">
      <span class="note note-1">♪</span>
      <span class="note note-2">♫</span>
      <span class="note note-3">♪</span>
      <span class="note note-4">♫</span>
    </div>

    <div class="login-container">
      <!-- 左侧品牌区域 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-logo">
            <img src="/images/logo.png" alt="KK智能语音" />
          </div>
          <h1 class="brand-title">KK智能语音</h1>
          <p class="brand-subtitle">智能语音管理系统</p>
          <div class="brand-features">
            <div class="feature-item">
              <span class="feature-icon">🎤</span>
              <span>语音识别</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">📝</span>
              <span>字幕生成</span>
            </div>
            <div class="feature-item">
              <span class="feature-icon">🤖</span>
              <span>AI助手</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="form-section">
        <div class="form-card">
          <div class="form-header">
            <h2>管理员登录</h2>
            <p>欢迎回来，请登录您的账户</p>
          </div>

          <a-form
            :model="loginUser"
            name="basic"
            :wrapper-col="{ span: 24 }"
            @finish="login"
            class="login-form"
          >
            <a-form-item
              name="loginName"
              :rules="[{ required: true, message: '请输入登录名' }]"
            >
              <a-input
                v-model:value="loginUser.loginName"
                placeholder="请输入登录名"
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <UserOutlined class="input-icon" />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item
              name="password"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <a-input-password
                v-model:value="loginUser.password"
                placeholder="请输入密码"
                size="large"
                class="custom-input"
              >
                <template #prefix>
                  <LockOutlined class="input-icon" />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item
              name="imageCode"
              :rules="[
                {
                  required: true,
                  message: '请输入图片验证码',
                  trigger: 'blur',
                },
              ]"
            >
              <a-input
                v-model:value="loginUser.imageCode"
                placeholder="请输入图片验证码"
                size="large"
                class="custom-input captcha-input"
              >
                <template #prefix>
                  <SafetyOutlined class="input-icon" />
                </template>
                <template #suffix>
                  <img
                    v-show="!!imageCodeSrc"
                    :src="imageCodeSrc"
                    alt="验证码"
                    class="captcha-img"
                    @click="loadImageCode()"
                  />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item class="submit-item">
              <a-button
                type="primary"
                html-type="submit"
                size="large"
                block
                class="submit-btn"
                :loading="loading"
              >
                登 录
              </a-button>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import store from "../store/index.js";
import { UserOutlined, LockOutlined, SafetyOutlined } from "@ant-design/icons-vue";
import { adminLogin } from "../api/admin.js";

let router = useRouter();
const loading = ref(false);

const loginUser = ref({
  loginName: "",
  password: "",
  imageCode: "",
});

// 图形验证码Token
const imageCodeToken = ref();
// 图形验证码图片地址
const imageCodeSrc = ref();

/**
 * 加载图形验证码
 */
const loadImageCode = () => {
  loginUser.value.imageCode = "";
  imageCodeToken.value = Math.random().toString(36).substring(2, 10);
  imageCodeSrc.value =
    import.meta.env.VITE_SERVER +
    "/admin/kaptcha/image-code/" +
    imageCodeToken.value;
};

// 页面加载时自动获取验证码
loadImageCode();

/**
 * 执行登录操作
 */
const login = async (values) => {
  loading.value = true;
  console.log("开始登录:", values);

  try {
    // 调用后端真实登录接口
    // 使用hexMd5Key加密（会自动拼接盐值!@#$*&^%nls）
    const resp = await adminLogin({
      loginName: loginUser.value.loginName,
      password: hexMd5Key(loginUser.value.password),
      imageCode: loginUser.value.imageCode,
      imageCodeToken: imageCodeToken.value,
    });

    // 将后端返回的真实token存储到Vuex
    store.commit("setUser", {
      id: resp.content.id,
      loginName: resp.content.loginName,
      token: resp.content.token,
    });

    message.success("登录成功！");
    router.push("/home/welcome");
  } catch (error) {
    console.error("登录失败:", error);
    loadImageCode();
    message.error(error.message || "登录失败，请检查账号密码和验证码");
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #fff8e7 0%, #ffe8d6 50%, #ffd6ba 100%);
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--primary-light)
  );
  top: -150px;
  left: -150px;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, var(--accent-color), var(--accent-light));
  bottom: -100px;
  right: -100px;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: var(--primary-color);
  top: 30%;
  right: 10%;
}

.circle-4 {
  width: 150px;
  height: 150px;
  background: var(--accent-color);
  bottom: 20%;
  left: 5%;
}

/* 音乐符号装饰 */
.music-notes {
  position: absolute;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.note {
  position: absolute;
  font-size: 40px;
  color: var(--primary-color);
  opacity: 0.2;
  animation: float 4s ease-in-out infinite;
}

.note-1 {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
}
.note-2 {
  top: 25%;
  right: 15%;
  animation-delay: 1s;
}
.note-3 {
  bottom: 30%;
  left: 8%;
  animation-delay: 2s;
}
.note-4 {
  bottom: 20%;
  right: 10%;
  animation-delay: 0.5s;
}

/* 主容器 */
.login-container {
  display: flex;
  width: 900px;
  max-width: 95%;
  background: var(--bg-card);
  border-radius: var(--radius-large);
  box-shadow: 0 20px 60px rgba(255, 179, 71, 0.3);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* 左侧品牌区域 */
.brand-section {
  flex: 1;
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--accent-color) 100%
  );
  padding: 60px 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-section::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(
    circle,
    rgba(255, 255, 255, 0.1) 0%,
    transparent 60%
  );
}

.brand-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 1;
}

.brand-logo {
  width: 120px;
  height: 120px;
  margin: 0 auto 24px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.brand-logo img {
  width: 80px;
  height: 80px;
  object-fit: contain;
}

.brand-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
}

.brand-features {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.feature-icon {
  font-size: 32px;
}

.feature-item span:last-child {
  font-size: 14px;
  opacity: 0.9;
}

/* 右侧表单区域 */
.form-section {
  flex: 1;
  padding: 60px 50px;
  display: flex;
  align-items: center;
}

.form-card {
  width: 100%;
  max-width: 360px;
  margin: 0 auto;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.form-header p {
  color: var(--text-light);
  font-size: 14px;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.custom-input {
  height: 48px !important;
  border-radius: var(--radius-medium) !important;
  font-size: 15px;
}

.input-icon {
  color: var(--primary-color) !important;
  font-size: 18px !important;
}

.captcha-img {
  width: 100px;
  height: 32px;
  cursor: pointer;
  border-radius: 4px;
  transition: transform var(--transition-fast);
}

.captcha-img:hover {
  transform: scale(1.05);
}

.submit-item {
  margin-top: 8px;
}

.submit-btn {
  height: 52px !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  border-radius: var(--radius-medium) !important;
  background: linear-gradient(
    135deg,
    var(--primary-color) 0%,
    var(--primary-dark) 100%
  ) !important;
  border: none !important;
  box-shadow: 0 6px 20px rgba(255, 179, 71, 0.4) !important;
  transition: all var(--transition-normal) !important;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(255, 179, 71, 0.5) !important;
}

/* 动画 */
@keyframes float {
  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-15px) rotate(10deg);
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    width: 95%;
  }

  .brand-section {
    padding: 40px 30px;
  }

  .brand-logo {
    width: 80px;
    height: 80px;
  }

  .brand-logo img {
    width: 50px;
    height: 50px;
  }

  .brand-title {
    font-size: 24px;
  }

  .brand-features {
    gap: 16px;
  }

  .feature-icon {
    font-size: 24px;
  }

  .form-section {
    padding: 40px 30px;
  }
}
</style>
