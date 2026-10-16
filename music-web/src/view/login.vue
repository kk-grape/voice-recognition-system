<template>
  <div class="login">
    <!-- 背景装饰元素 -->
    <div class="background-decorations">
      <div class="decoration decoration-1"></div>
      <div class="decoration decoration-2"></div>
      <div class="decoration decoration-3"></div>
      <div class="decoration decoration-4"></div>
      <div class="decoration decoration-5"></div>
      <!-- 音乐符号装饰 -->
      <div class="music-note music-note-1"></div>
      <div class="music-note music-note-2"></div>
      <div class="music-note music-note-3"></div>
      <div class="music-note music-note-4"></div>
      <!-- 有生命力的音符和乐器装饰 -->
      <div class="living-music living-instrument-4"></div>
      <div class="living-music living-instrument-2"></div>
      <div class="living-music living-instrument-1"></div>
      <div class="living-music living-instrument-3"></div>
    </div>

    <a-row type="flex" justify="center">
      <a-col
        class="main"
        style="width: 380px; min-width: 380px; max-width: 380px"
      >
        <!-- 动漫风格标题 -->
        <div class="title">
          <span class="title-text">KK智能语音</span>
          <div class="title-icon"></div>
        </div>

        <a-form
          :model="loginMember"
          name="basic"
          :wrapper-col="{ span: 24 }"
          @finish="login"
          class="login-form"
        >
          <div class="form-container">
            <a-form-item
              name="mobile"
              class="form-item"
              :rules="[{ required: true, message: '请输入手机号' }]"
            >
              <a-input
                v-model:value="loginMember.mobile"
                placeholder="手机号"
                size="large"
              >
                <template #prefix>
                  <MobileOutlined style="margin-left: 15px; color: #8ad7f7" />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item
              name="password"
              class="form-item"
              :rules="[{ required: true, message: '请输入密码' }]"
            >
              <a-input-password
                v-model:value="loginMember.password"
                placeholder="密码"
                size="large"
              >
                <template #prefix>
                  <LockOutlined style="margin-left: 15px; color: #8ad7f7" />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item
              name="imageCode"
              class="form-item"
              :rules="[
                {
                  required: true,
                  message: '请输入验证码',
                  trigger: 'blur',
                },
              ]"
            >
              <div class="captcha-container">
                <a-input
                  v-model:value="loginMember.imageCode"
                  placeholder="验证码"
                  size="large"
                  style="flex: 1; margin-right: 10px"
                >
                  <template #prefix>
                    <SafetyOutlined style="margin-left: 15px; color: #8ad7f7" />
                  </template>
                </a-input>
                <img
                  v-show="!!imageCodeSrc"
                  :src="imageCodeSrc"
                  alt="验证码"
                  v-on:click="loadImageCode()"
                  class="captcha-img"
                />
              </div>
            </a-form-item>

            <a-form-item class="form-item">
              <a-button
                type="primary"
                block
                html-type="submit"
                class="login-btn"
                size="large"
              >
                <span class="btn-text">登&nbsp;录</span>
              </a-button>
            </a-form-item>
          </div>
        </a-form>
        <p class="footer">
          <router-link to="/register" class="footer-link">我要注册</router-link
          >&nbsp;&nbsp;
          <router-link class="pull-right footer-link" to="/reset"
            >忘记密码</router-link
          >
        </p>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import { useRouter } from "vue-router";
import store from "../store/index.js";

let router = useRouter();

const loginMember = ref({
  mobile: "",
  password: "",
  imageCode: "",
});

//登录
const login = (values) => {
  console.log("开始登录:", values);
  axios
    .post("/web/member/login", {
      mobile: loginMember.value.mobile,
      password: hexMd5Key(loginMember.value.password),
      imageCode: loginMember.value.imageCode,
      imageCodeToken: imageCodeToken.value,
    })
    .then((response) => {
      console.log("登录响应:", response);
      console.log("响应数据:", response.data);
      let data = response.data;
      if (data && data.success) {
        message.success("登录成功！");
        store.commit("setMember", data.content);
        router.push("/home");
      } else {
        message.error(data?.message || "登录失败，请重试");
        refreshImageCode();
      }
    })
    .catch((error) => {
      console.error("登录错误:", error);
      console.error("错误响应:", error.response);
      if (error.response && error.response.data) {
        const errorData = error.response.data;
        message.error(errorData.message || "登录失败，请重试");
      } else {
        message.error("网络错误，请检查网络连接");
      }
      refreshImageCode();
    });
};

// ----------- 图形验证码 --------------------
const imageCodeToken = ref();
const imageCodeSrc = ref();
/**
 * 加载图形验证码
 */
const loadImageCode = () => {
  loginMember.value.imageCode = "";
  imageCodeToken.value = Tool.uuid(8);
  imageCodeSrc.value =
    "http://localhost:18000/web/kaptcha/image-code/" +
    imageCodeToken.value;
};

//刷新图片验证码
const refreshImageCode = () => {
  loginMember.value.imageCode = "";
  loadImageCode();
};

loadImageCode();
</script>

<style scoped>
.pull-right {
  float: right;
}

/************* 登录/注册/忘记密码 ************/
.login,
.register,
.reset {
  padding-top: 8%;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background:
    linear-gradient(
      -45deg,
      rgba(168, 230, 207, 0.8),
      rgba(181, 234, 215, 0.8),
      rgba(199, 206, 234, 0.8),
      rgba(138, 215, 247, 0.8)
    ),
    url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20summer%20background%20with%20mint%20blue%20color%20and%20music%20notes&image_size=landscape_16_9");
  background-size: cover, cover;
  background-position: center, center;
  background-repeat: no-repeat, no-repeat;
  overflow: hidden;
}

/* 背景动画效果 */
.login {
  animation: backgroundPulse 10s ease-in-out infinite;
}

@keyframes backgroundPulse {
  0%,
  100% {
    background-size: 100% 100%;
  }
  50% {
    background-size: 105% 105%;
  }
}
.register {
  padding-top: 4%;
}
.login .ant-form-item,
.register .ant-form-item,
.reset .ant-form-item {
  margin-bottom: 5px !important;
}
.login .main,
.register .main,
.reset .main {
  padding: 25px 25px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 35px;
  box-shadow:
    0 8px 32px rgba(31, 38, 135, 0.15),
    inset 0 0 0 1px rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

/* 主容器发光效果 */
.login .main::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(138, 215, 247, 0.1),
    transparent
  );
  animation: mainGlow 6s linear infinite;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.login .main:hover::before {
  opacity: 1;
}

@keyframes mainGlow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.login .main:hover {
  transform: translateY(-8px);
  box-shadow:
    0 15px 45px rgba(31, 38, 135, 0.5),
    inset 0 0 0 1px rgba(255, 255, 255, 0.25);
  border-color: rgba(138, 215, 247, 0.6);
}
.login .title,
.register .title,
.reset .title {
  vertical-align: middle;
  font-size: 2rem;
  font-weight: 900;
  text-align: center;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  margin-bottom: 20px;
  letter-spacing: 2px;
  color: #4a89dc;
  transition: all 0.4s ease;
  position: relative;
  z-index: 1;
}

.login .title:hover {
  transform: scale(1.1);
  text-shadow:
    0 0 15px rgba(74, 137, 220, 0.6),
    0 0 30px rgba(74, 137, 220, 0.4);
}
.login .ant-input-affix-wrapper,
.register .ant-input-affix-wrapper,
.reset .ant-input-affix-wrapper {
  padding: 0 !important;
  border-radius: 24px !important;
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #8ad7f7, #4ecdc4, #4a89dc) border-box !important;
  transition: all 0.4s ease;
  height: 34px !important;
}

.login .ant-input-affix-wrapper input,
.register .ant-input-affix-wrapper input,
.reset .ant-input-affix-wrapper input {
  height: 100% !important;
  line-height: 36px !important;
  font-size: 16px !important;
  padding: 0 20px !important;
  background: transparent !important;
}

.login .ant-input-affix-wrapper:hover,
.register .ant-input-affix-wrapper:hover,
.reset .ant-input-affix-wrapper:hover {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #4a89dc, #8ad7f7, #4ecdc4) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

.login .ant-input-affix-wrapper:focus-within {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #4ecdc4, #8ad7f7, #4a89dc) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}
.login .ant-input-affix-wrapper > input.ant-input,
.register .ant-input-affix-wrapper > input.ant-input,
.reset .ant-input-affix-wrapper > input.ant-input {
  padding: 12px 11px !important;
  font-size: 16px;
}

/* 优化密码输入框的显示/隐藏按钮 */
.login .ant-input-password-icon,
.register .ant-input-password-icon,
.reset .ant-input-password-icon {
  margin-right: 15px !important;
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: all 0.3s ease !important;
}

.login .ant-input-password-icon:hover,
.register .ant-input-password-icon:hover,
.reset .ant-input-password-icon:hover {
  transform: scale(1.1) !important;
}

/* 自定义密码显示/隐藏按钮图片 */
.login .ant-input-password-icon.anticon-eye,
.register .ant-input-password-icon.anticon-eye,
.reset .ant-input-password-icon.anticon-eye {
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20eye%20icon%20in%20mint%20blue%20color%20with%20sparkles%20on%20transparent%20background&image_size=square");
  background-size: contain;
  background-repeat: no-repeat;
  color: transparent !important;
}

.login .ant-input-password-icon.anticon-eye-invisible,
.register .ant-input-password-icon.anticon-eye-invisible,
.reset .ant-input-password-icon.anticon-eye-invisible {
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20closed%20eye%20icon%20in%20mint%20blue%20color%20with%20sparkles%20on%20transparent%20background&image_size=square");
  background-size: contain;
  background-repeat: no-repeat;
  color: transparent !important;
}
.login .form-item,
.register .form-item,
.reset .form-item {
  margin-top: 15px;
}

/* 表单容器样式 */
.form-container {
  padding: 15px;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 25px;
  box-shadow:
    0 8px 32px rgba(31, 38, 135, 0.37),
    inset 0 0 0 1px rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.18);
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
}

.form-container::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(138, 215, 247, 0.1),
    transparent
  );
  animation: formGlow 6s linear infinite;
  opacity: 0;
  transition: opacity 0.4s ease;
}

.form-container:hover {
  box-shadow:
    0 12px 40px rgba(31, 38, 135, 0.5),
    inset 0 0 0 1px rgba(255, 255, 255, 0.25);
  border-color: rgba(138, 215, 247, 0.4);
  transform: translateY(-3px);
}

.form-container:hover::before {
  opacity: 1;
}

@keyframes formGlow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

/* 验证码容器样式 */
.captcha-container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.captcha-img {
  width: 100px;
  height: 36px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.4s ease;
  border: 2px solid transparent;
  background:
    linear-gradient(rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.8))
      padding-box,
    linear-gradient(45deg, #8ad7f7, #4ecdc4) border-box;
  box-shadow: 0 2px 10px rgba(138, 215, 247, 0.3);
}

.captcha-img:hover {
  transform: scale(1.05) rotate(2deg);
  border: 2px solid transparent;
  background:
    linear-gradient(rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.8))
      padding-box,
    linear-gradient(45deg, #4ecdc4, #8ad7f7) border-box;
  box-shadow: 0 4px 15px rgba(138, 215, 247, 0.4);
}

/* 输入框高度调整和圆角样式 */
.login .ant-input-affix-wrapper,
.register .ant-input-affix-wrapper,
.reset .ant-input-affix-wrapper {
  height: 48px !important;
  border-radius: 24px !important;
}

.login .ant-input-affix-wrapper input,
.register .ant-input-affix-wrapper input,
.reset .ant-input-affix-wrapper input {
  height: 100% !important;
  line-height: 48px !important;
  font-size: 16px !important;
  padding: 0 20px !important;
}

/* 移除输入框的横线特效 */
.login .ant-input-affix-wrapper:hover,
.register .ant-input-affix-wrapper:hover,
.reset .ant-input-affix-wrapper:hover {
  box-shadow: none !important;
}

.login .ant-input-affix-wrapper:focus-within,
.register .ant-input-affix-wrapper:focus-within,
.reset .ant-input-affix-wrapper:focus-within {
  box-shadow: none !important;
}
.login .login-btn {
  margin-top: 20px;
  margin-bottom: 15px;
  background: linear-gradient(45deg, #4a89dc, #8ad7f7, #4ecdc4) !important;
  background-size: 200% 200% !important;
  border: none !important;
  border-radius: 24px !important;
  padding: 12px !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(138, 215, 247, 0.4);
  transition: all 0.4s ease;
  animation: buttonGradient 3s ease-in-out infinite;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

/* 按钮渐变动画 */
@keyframes buttonGradient {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}
.login .login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(138, 215, 247, 0.6);
}
.login .footer {
  margin-top: 20px;
  margin-bottom: 0;
}
.login .footer a,
.register .footer a,
.reset .footer a {
  color: #4a89dc;
  font-weight: 500;
  transition: all 0.3s ease;
}
.login .footer a:hover {
  color: #8ad7f7;
  text-decoration: underline;
}

/* 背景装饰元素 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  animation: float 8s cubic-bezier(0.68, -0.55, 0.265, 1.55) infinite;
  box-shadow: 0 8px 32px rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s ease;
}

.decoration-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 20%;
  animation-delay: 0s;
  background: rgba(138, 215, 247, 0.4);
}

.decoration-2 {
  width: 120px;
  height: 120px;
  top: 50%;
  right: 20%;
  animation-delay: 2s;
  background: rgba(168, 230, 207, 0.4);
}

.decoration-3 {
  width: 70px;
  height: 70px;
  top: 25%;
  right: 25%;
  animation-delay: 4s;
  background: rgba(199, 206, 234, 0.4);
}

.decoration-4 {
  width: 100px;
  height: 100px;
  bottom: 30%;
  left: 25%;
  animation-delay: 6s;
  background: rgba(78, 205, 196, 0.3);
}

.decoration-5 {
  width: 50px;
  height: 50px;
  top: 65%;
  left: 50%;
  animation-delay: 1s;
  background: rgba(68, 138, 230, 0.3);
}

/* 浮动动画 */
@keyframes float {
  0% {
    transform: translate(0px, 0px) rotate(0deg) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(30px, -30px) rotate(90deg) scale(1.1);
    opacity: 0.8;
  }
  50% {
    transform: translate(60px, 0px) rotate(180deg) scale(1.2);
    opacity: 1;
  }
  75% {
    transform: translate(30px, 30px) rotate(270deg) scale(1.1);
    opacity: 0.8;
  }
  100% {
    transform: translate(0px, 0px) rotate(360deg) scale(1);
    opacity: 0.6;
  }
}

/* 背景装饰元素悬停动画 */
.decoration:hover {
  animation: decorationPop 0.5s ease-in-out;
}

@keyframes decorationPop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
    box-shadow: 0 0 30px rgba(138, 215, 247, 0.8);
  }
  100% {
    transform: scale(1);
  }
}

/* 标题样式 */
.title {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 35px;
  padding: 15px 0;
}

.title-text {
  position: relative;
  z-index: 2;
}

.title-icon {
  display: none;
}

/* 标题发光动画 */
@keyframes titleGlow {
  from {
    text-shadow:
      3px 3px 6px rgba(0, 0, 0, 0.1),
      0 0 10px rgba(138, 215, 247, 0.5);
  }
  to {
    text-shadow:
      3px 3px 6px rgba(0, 0, 0, 0.1),
      0 0 20px rgba(138, 215, 247, 0.8),
      0 0 30px rgba(138, 215, 247, 0.5);
  }
}

/* 标题浮动动画 */
@keyframes titleFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

/* 验证码图片样式 */
.captcha-img {
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid #a8e6cf;
}

.captcha-img:hover {
  transform: scale(1.05);
  box-shadow: 0 0 15px rgba(78, 205, 196, 0.5);
}

/* 按钮样式增强 */
.login-btn {
  position: relative;
  overflow: hidden;
}

.btn-icon {
  display: inline-block;
  width: 20px;
  height: 20px;
  margin-left: 10px;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20arrow%20icon%20in%20mint%20blue%20color%20with%20sparkles&image_size=square");
  background-size: contain;
  background-repeat: no-repeat;
  vertical-align: middle;
  animation: pulse 2s ease-in-out infinite;
}

/* 弹跳动画 */
@keyframes bounce {
  0%,
  100% {
    transform: translateY(-50%) scale(1);
  }
  50% {
    transform: translateY(-60%) scale(1.1);
  }
}

/* 脉冲动画 */
@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* 页脚链接样式 */
.footer-link {
  position: relative;
  transition: all 0.3s ease;
}

.footer-link::after {
  content: "";
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: #4ecdc4;
  transition: width 0.3s ease;
}

.footer-link:hover::after {
  width: 100%;
}

/* 音乐符号装饰 */
.music-note {
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  animation: musicFloat 15s ease-in-out infinite;
  backdrop-filter: blur(10px);
  border-radius: 50%;
  padding: 10px;
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 20px rgba(138, 215, 247, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s ease;
}

.music-note:hover {
  transform: scale(1.1) rotate(15deg);
  box-shadow: 0 6px 25px rgba(138, 215, 247, 0.5);
  transition: all 0.4s ease;
}

.music-note-1 {
  width: 80px;
  height: 80px;
  top: 25%;
  left: 25%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20music%20note%20in%20mint%20blue%20color%20with%20sparkles%20and%20glowing%20effect&image_size=square");
  animation-delay: 0s;
}

.music-note-2 {
  width: 70px;
  height: 70px;
  top: 45%;
  right: 25%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20music%20note%20in%20light%20blue%20color%20with%20sparkles%20and%20glowing%20effect&image_size=square");
  animation-delay: 1s;
}

.music-note-3 {
  width: 75px;
  height: 75px;
  bottom: 35%;
  left: 35%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20music%20note%20in%20mint%20green%20color%20with%20sparkles%20and%20glowing%20effect&image_size=square");
  animation-delay: 2s;
}

.music-note-4 {
  width: 65px;
  height: 65px;
  bottom: 25%;
  right: 30%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20anime%20music%20note%20in%20light%20purple%20color%20with%20sparkles%20and%20glowing%20effect&image_size=square");
  animation-delay: 3s;
}

/* 音乐符号浮动动画 */
@keyframes musicFloat {
  0% {
    transform: translate(0px, 0px) rotate(0deg) scale(1);
    opacity: 0.6;
  }
  15% {
    transform: translate(30px, -40px) rotate(60deg) scale(1.1);
    opacity: 0.7;
  }
  30% {
    transform: translate(60px, -20px) rotate(120deg) scale(1.2);
    opacity: 0.8;
  }
  45% {
    transform: translate(40px, 30px) rotate(180deg) scale(1.1);
    opacity: 0.9;
  }
  60% {
    transform: translate(70px, 10px) rotate(240deg) scale(1.3);
    opacity: 1;
  }
  75% {
    transform: translate(50px, -30px) rotate(300deg) scale(1.2);
    opacity: 0.8;
  }
  90% {
    transform: translate(20px, 20px) rotate(330deg) scale(1.1);
    opacity: 0.7;
  }
  100% {
    transform: translate(0px, 0px) rotate(360deg) scale(1);
    opacity: 0.6;
  }
}

/* 音乐主题按钮效果 */
.login-btn:hover {
  background: linear-gradient(45deg, #4a89dc, #8ad7f7, #4ecdc4) !important;
  box-shadow:
    0 6px 20px rgba(138, 215, 247, 0.6),
    0 0 30px rgba(138, 215, 247, 0.3) !important;
}

/* 音乐波纹效果 */
.login-btn::before {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.3);
  transform: translate(-50%, -50%);
  transition: all 0.6s ease;
}

.login-btn:hover::before {
  width: 300px;
  height: 300px;
  opacity: 0;
}

/* 音乐风格表单边框 */
.ant-input-affix-wrapper {
  position: relative;
  overflow: hidden;
}

.ant-input-affix-wrapper::after {
  content: "";
  position: absolute;
  bottom: 0;
  left: -100%;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, #6bb9f0, #8ad7f7, #6bb9f0);
  transition: left 0.5s ease;
}

.ant-input-affix-wrapper:focus-within::after {
  left: 0;
}

/* 输入框聚焦动画 */
.ant-input-affix-wrapper:focus-within {
  animation: focusPulse 0.5s ease-in-out;
}

@keyframes focusPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(74, 137, 220, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(74, 137, 220, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(74, 137, 220, 0);
  }
}

/* 移除页面加载动画，保持背景静态显示 */
.login {
  /* 移除页面淡入动画，背景直接显示 */
}

/* 页面进入动画 */
.main {
  animation: mainSlideIn 1s ease-out 0.3s both;
}

@keyframes mainSlideIn {
  from {
    opacity: 0;
    transform: translateY(50px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 表单元素入场动画 */
.login-form {
  animation: formSlideIn 1s ease-out 0.5s both;
}

@keyframes formSlideIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 表单项目顺序动画 */
.form-item {
  animation: itemBounceIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  opacity: 0;
  animation-fill-mode: forwards;
}

.form-item:nth-child(1) {
  animation-delay: 0.3s;
}

.form-item:nth-child(2) {
  animation-delay: 0.5s;
}

.form-item:nth-child(3) {
  animation-delay: 0.7s;
}

.form-item:nth-child(4) {
  animation-delay: 0.9s;
}

@keyframes itemBounceIn {
  0% {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  60% {
    opacity: 1;
    transform: translateY(-10px) scale(1.05);
  }
  100% {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 按钮点击动画 */
.login-btn:active {
  animation: btnPop 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes btnPop {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.9);
    box-shadow: 0 0 20px rgba(138, 215, 247, 0.8);
  }
  100% {
    transform: scale(1);
    box-shadow: 0 4px 15px rgba(138, 215, 247, 0.4);
  }
}

/* 按钮悬停动画 */
.login-btn:hover {
  animation: btnHover 0.5s ease-in-out infinite alternate;
}

@keyframes btnHover {
  from {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(138, 215, 247, 0.6);
  }
  to {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(138, 215, 247, 0.8);
  }
}
.captcha-img:active {
  animation: captchaBounce 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes captchaBounce {
  0% {
    transform: scale(1) rotate(0deg);
  }
  30% {
    transform: scale(1.1) rotate(5deg);
  }
  60% {
    transform: scale(1.1) rotate(-5deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
  }
}

/* 页脚链接悬停动画 */
.footer-link:hover {
  animation: linkFloat 0.4s ease-in-out infinite alternate;
}

@keyframes linkFloat {
  from {
    transform: translateY(0);
    color: #4a89dc;
  }
  to {
    transform: translateY(-3px);
    color: #8ad7f7;
    text-shadow: 0 0 10px rgba(138, 215, 247, 0.7);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login {
    padding-top: 15%;
  }

  .login .main {
    width: 90%;
    margin: 0 auto;
    padding: 25px 15px;
  }

  .background-decorations {
    display: none;
  }

  .title {
    font-size: 1.5rem;
  }

  .title-icon {
    right: -30px;
    width: 25px;
    height: 25px;
  }

  .login .ant-input-affix-wrapper > input.ant-input {
    padding: 10px 11px !important;
    font-size: 14px;
  }

  .login .login-btn {
    padding: 10px !important;
    font-size: 16px !important;
  }

  .btn-icon {
    width: 16px;
    height: 16px;
  }
}

@media (max-width: 480px) {
  .login {
    padding-top: 20%;
  }

  .login .main {
    width: 95%;
    padding: 20px 12px;
  }

  .title {
    font-size: 1.3rem;
  }

  .title-icon {
    right: -25px;
    width: 20px;
    height: 20px;
  }

  .login .ant-input-affix-wrapper > input.ant-input {
    padding: 8px 11px !important;
    font-size: 13px;
  }

  .login .login-btn {
    padding: 8px !important;
    font-size: 14px !important;
  }

  .form-item {
    margin-top: 10px;
  }

  .footer {
    font-size: 12px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .login {
    padding-top: 10%;
  }

  .login .main {
    width: 70%;
    margin: 0 auto;
  }
}

/* 横屏模式适配 */
@media (orientation: landscape) and (max-height: 500px) {
  .login {
    padding-top: 5%;
  }

  .login .main {
    padding: 20px;
  }

  .form-item {
    margin-top: 10px;
  }

  .title {
    margin-bottom: 15px;
  }
}

/* 加载状态动画 */
.loading {
  position: relative;
  pointer-events: none;
}

.loading::after {
  content: "";
  position: absolute;
  top: 50%;
  right: 20px;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

@keyframes spin {
  to {
    transform: translateY(-50%) rotate(360deg);
  }
}

/* 增强的元素悬停效果 */
.main:hover .title {
  animation: titleHover 0.5s ease-in-out;
}

@keyframes titleHover {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

/* 表单验证反馈动画 */
.ant-form-item-has-error .ant-input-affix-wrapper {
  animation: errorShake 0.5s ease-in-out;
  border-color: #ff6b6b !important;
}

@keyframes errorShake {
  0%,
  100% {
    transform: translateX(0);
  }
  25% {
    transform: translateX(-5px);
  }
  75% {
    transform: translateX(5px);
  }
}

/* 成功状态动画 */
.success-animation {
  animation: successPop 0.5s ease-in-out;
}

@keyframes successPop {
  0% {
    transform: scale(0.8);
    opacity: 0;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

/* 页面滚动效果 */
@media (max-height: 600px) {
  .login {
    padding-top: 5%;
  }

  .main {
    padding: 20px 15px !important;
  }
}

/* 增强的背景装饰动画 */
.decoration:hover {
  animation: decorationHover 0.5s ease-in-out;
}

@keyframes decorationHover {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
  100% {
    transform: scale(1);
  }
}

/* 音乐符号悬停效果 */
.music-note:hover {
  animation: musicNoteHover 0.5s ease-in-out;
}

@keyframes musicNoteHover {
  0% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.2) rotate(180deg);
  }
  100% {
    transform: scale(1) rotate(360deg);
  }
}

/* 有生命力的音符和乐器装饰 */
.living-music {
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  backdrop-filter: blur(10px);
  border-radius: 50%;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(138, 215, 247, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: all 0.4s ease;
  overflow: hidden;
  animation: livingFloat 15s ease-in-out infinite;
}

.living-instrument-2 {
  width: 100px;
  height: 100px;
  top: 15%;
  left: 5%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20guitar%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20mint%20blue%20background%20matching%20web%20page%20theme&image_size=square");
  animation-delay: 0s;
}

.living-instrument-1 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 5%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20piano%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20light%20blue%20and%20mint%20green%20gradient%20background%20matching%20web%20page%20theme%20absolutely%20no%20border%20no%20outline%20no%20frame%20completely%20clean%20background%20soft%20colorful%20design&image_size=square");
  animation-delay: 2s;
}

.living-instrument-3 {
  width: 90px;
  height: 90px;
  bottom: 10%;
  left: 15%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20drum%20set%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20mint%20green%20and%20light%20blue%20gradient%20background%20matching%20web%20page%20theme%20no%20black%20border%20no%20black%20background%20clean%20design&image_size=square");
  animation-delay: 4s;
}

/* 登录表格右上角乐器装饰 */
.living-instrument-4 {
  width: 130px;
  height: 130px;
  top: 11%;
  right: 35%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20piano%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20mint%20blue%20and%20light%20green%20gradient%20background%20matching%20web%20page%20theme%20no%20border%20no%20outline%20clean%20design&image_size=square");
  animation-delay: 0s;
  z-index: -1;
  opacity: 0.7;
}

/* 有生命力的音乐元素浮动动画 */
@keyframes livingFloat {
  0% {
    transform: translate(0px, 0px) rotate(0deg) scale(1);
    opacity: 0.7;
  }
  20% {
    transform: translate(35px, -40px) rotate(10deg) scale(1.05);
    opacity: 0.8;
  }
  40% {
    transform: translate(65px, -20px) rotate(5deg) scale(1.1);
    opacity: 0.9;
  }
  60% {
    transform: translate(45px, 35px) rotate(-10deg) scale(1.15);
    opacity: 1;
  }
  80% {
    transform: translate(25px, 25px) rotate(-5deg) scale(1.05);
    opacity: 0.8;
  }
  100% {
    transform: translate(0px, 0px) rotate(0deg) scale(1);
    opacity: 0.7;
  }
}

/* 有生命力的音乐元素悬停效果 */
.living-music:hover {
  transform: scale(1.2) rotate(10deg);
  box-shadow: 0 12px 40px rgba(138, 215, 247, 0.5);
  border-color: rgba(138, 215, 247, 0.6);
  transition: all 0.4s ease;
}

/* 输入框文本动画 */
.ant-input {
  transition: all 0.3s ease;
}

.ant-input:focus {
  color: #4ecdc4 !important;
  font-weight: 500;
}

/* 页脚动画效果 */
.footer {
  animation: footerAppear 1s ease-out 1s both;
}

@keyframes footerAppear {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
