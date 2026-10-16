<template>
  <div class="register">
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
      <div class="music-note music-note-5"></div>
      <div class="music-note music-note-6"></div>
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
          <span class="title-text">我要注册</span>
        </div>

        <a-form
          :model="registerMember"
          name="basic"
          :wrapper-col="{ span: 24 }"
          @finish="register"
          class="login-form"
        >
          <div class="form-container">
            <a-form-item
              name="mobile"
              class="form-item"
              :rules="[
                { required: true, message: '请输入手机号', trigger: 'blur' },
                {
                  pattern: /^\d{11}$/,
                  message: '手机号为11位数字',
                  trigger: 'blur',
                },
              ]"
            >
              <a-input
                :value="registerMember.mobile"
                @update:value="(val) => (registerMember.mobile = val)"
                placeholder="手机号"
                size="large"
              >
                <template #prefix>
                  <MobileOutlined style="margin-left: 15px" />
                </template>
              </a-input>
            </a-form-item>

            <a-form-item
              name="imageCode"
              class="form-item"
              :rules="[
                {
                  required: true,
                  message: '请输入图片验证码',
                  trigger: 'blur',
                },
              ]"
            >
              <a-row :gutter="8">
                <a-col :span="16">
                  <a-input
                    :value="registerMember.imageCode"
                    @update:value="(val) => (registerMember.imageCode = val)"
                    placeholder="图片验证码"
                    class="image-code-input"
                  >
                    <template #prefix>
                      <SafetyOutlined style="margin-left: 15px" />
                    </template>
                  </a-input>
                </a-col>
                <a-col :span="8">
                  <img
                    v-show="!!imageCodeSrc"
                    :src="imageCodeSrc"
                    alt="验证码"
                    v-on:click="loadImageCode()"
                    class="image-code-img"
                  />
                </a-col>
              </a-row>
            </a-form-item>

            <a-form-item
              name="code"
              class="form-item"
              :rules="[
                {
                  required: true,
                  message: '请输入短信验证码',
                  trigger: 'blur',
                },
              ]"
            >
              <a-row :gutter="8">
                <a-col :span="16">
                  <a-input
                    :value="registerMember.code"
                    @update:value="(val) => (registerMember.code = val)"
                    placeholder="短信验证码"
                  >
                    <template #prefix>
                      <MessageOutlined style="margin-left: 15px" />
                    </template>
                  </a-input>
                </a-col>
                <a-col :span="8">
                  <a-button
                    type="primary"
                    @click="sendRegisterSmsCode"
                    :loading="sendBtnLoading"
                    class="sms-btn"
                  >
                    {{ sendText }}
                  </a-button>
                </a-col>
              </a-row>
            </a-form-item>

            <a-form-item
              name="passwordOri"
              class="form-item"
              :rules="[
                { required: true, message: '请输入密码', trigger: 'blur' },
                {
                  pattern: /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$/,
                  message: '密码包含数字和英文，长度6-20',
                  trigger: 'blur',
                },
              ]"
            >
              <a-input-password
                :value="registerMember.passwordOri"
                @update:value="(val) => (registerMember.passwordOri = val)"
                placeholder="密码"
                size="large"
              >
                <template #prefix>
                  <LockOutlined style="margin-left: 15px" />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item
              name="passwordConfirm"
              class="form-item"
              :rules="[{ required: true, message: '请输入确认密码' }]"
            >
              <a-input-password
                :value="registerMember.passwordConfirm"
                @update:value="(val) => (registerMember.passwordConfirm = val)"
                placeholder="确认密码"
                size="large"
              >
                <template #prefix>
                  <CheckCircleOutlined style="margin-left: 15px" />
                </template>
              </a-input-password>
            </a-form-item>

            <a-form-item class="form-item">
              <a-button
                type="primary"
                block
                html-type="submit"
                class="register-btn"
                size="large"
              >
                注&nbsp;册
              </a-button>
            </a-form-item>
          </div>
        </a-form>
        <p class="footer">
          <router-link to="/login" class="footer-link"
            ><ArrowLeftOutlined /> 返回登录</router-link
          >
        </p>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { message } from "ant-design-vue";
import axios from "axios";
import { ref } from "vue";
import { useRouter } from "vue-router";
import {
  MobileOutlined,
  SafetyOutlined,
  MessageOutlined,
  LockOutlined,
  CheckCircleOutlined,
  ArrowLeftOutlined,
} from "@ant-design/icons-vue";

let router = useRouter(); // 路由跳转

const registerMember = ref({
  mobile: "",
  imageCode: "",
  code: "",
  password: "",
  passwordOri: "",
  passwordConfirm: "",
});

// 注册方法
const register = (values) => {
  console.log("开始注册:", values);
  if (
    registerMember.value.passwordOri !== registerMember.value.passwordConfirm
  ) {
    message.error("密码和确认密码不一致!");
    return;
  }
  registerMember.value.password = registerMember.value.passwordOri;
  axios
    .post("/web/member/register", {
      mobile: registerMember.value.mobile,
      code: registerMember.value.code,
      password: hexMd5Key(registerMember.value.password),
    })
    .then((res) => {
      let data = res.data;
      if (data.success) {
        message.success("注册成功！");
        router.push("/login");
      } else {
        message.error(data.message);
        refreshImageCode(); // 刷新图片验证码
      }
    });
};

// ----------- 短信验证码 --------------------
const sendBtnLoading = ref(false); // 控制按钮加载状态和倒计时显示文本的变量
const sendText = ref("获取验证码");
const COUNTDOWN = 5;
let countdown = ref(COUNTDOWN);

// 倒计时方法
const setTime = () => {
  if (countdown.value === 0) {
    sendBtnLoading.value = false;
    sendText.value = "获取验证码";
    countdown.value = COUNTDOWN;
    return;
  } else {
    sendBtnLoading.value = true;
    sendText.value = "重新发送(" + countdown.value + ")";
    countdown.value--;
  }
  setTimeout(function () {
    setTime();
  }, 1000);
};

// 发送短信验证码方法
const sendRegisterSmsCode = () => {
  console.log("发送短信验证码:");
  // 手机号校验，必须为11位数字，第一位为1开头，第二位为3-9数字开头，其余位为0-9数字结尾
  if (
    !/^1[3-9]\d{9}$/.test(registerMember.value.mobile) ||
    registerMember.value.mobile === ""
  ) {
    message.error("请输入正确的手机号");
    return;
  }
  //如果验证码长度不为4位，则提示错误信息
  if (
    registerMember.value.imageCode === "" ||
    registerMember.value.imageCode.length !== 4
  ) {
    message.error("请查看图片验证码是否正确");
    return;
  }
  sendBtnLoading.value = true;
  axios
    .post("/web/sms-code/send-for-register", {
      mobile: registerMember.value.mobile,
      imageCode: registerMember.value.imageCode,
      imageCodeToken: imageCodeToken.value,
    })
    .then((res) => {
      let data = res.data;
      if (data.success) {
        setTime();
        message.success("验证码已发送，请注意查收");
      } else {
        sendBtnLoading.value = false;
        message.error(data.message);
        refreshImageCode(); // 刷新图片验证码
      }
    });
};

// ----------- 图形验证码 --------------------
const imageCodeToken = ref();
const imageCodeSrc = ref();
/**
 * 加载图形验证码
 */
const loadImageCode = () => {
  registerMember.value.imageCode = "";
  imageCodeToken.value = Tool.uuid(8);
  imageCodeSrc.value =
    "http://localhost:18000/web/kaptcha/image-code/" +
    imageCodeToken.value;
};

//刷新图片验证码
const refreshImageCode = () => {
  registerMember.value.imageCode = "";
  loadImageCode();
};

loadImageCode();
</script>

<style scoped>
/* 背景样式 */
.register {
  padding-top: 8%;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  background:
    linear-gradient(
      -45deg,
      rgba(255, 252, 232, 0.9),
      rgba(255, 248, 220, 0.9),
      rgba(255, 240, 196, 0.9),
      rgba(255, 235, 180, 0.9)
    ),
    url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20vibrant%20musical%20instrument%20spirits%20with%20big%20cute%20eyes%20and%20expressions%20floating%20in%20colorful%20landscape%20with%20green%20grass%20river%20bright%20rainbow%20flying%20music%20notes%20sparkles%20blue%20sky%20fluffy%20clouds%20no%20road%203d%20style%20energetic%20musical%20theme%20cream%20background&image_size=landscape_16_9");
  background-size: cover, cover;
  background-position: center, center;
  background-repeat: no-repeat, no-repeat;
  overflow: hidden;
  animation: backgroundPulse 8s ease-in-out infinite;
}

/* 背景装饰元素容器 */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

/* 装饰球样式 */
.decoration {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 240, 196, 0.6);
  box-shadow: 0 8px 32px rgba(255, 222, 173, 0.7);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 218, 185, 0.5);
  transition: all 0.4s ease;
}

.decoration-1 {
  width: 80px;
  height: 80px;
  top: 10%;
  left: 10%;
}

.decoration-2 {
  width: 60px;
  height: 60px;
  top: 20%;
  right: 15%;
}

.decoration-3 {
  width: 100px;
  height: 100px;
  bottom: 15%;
  left: 15%;
}

.decoration-4 {
  width: 70px;
  height: 70px;
  bottom: 25%;
  right: 10%;
}

.decoration-5 {
  width: 90px;
  height: 90px;
  top: 45%;
  left: 5%;
}

/* 装饰元素悬停动画 */
.decoration:hover {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 12px 40px rgba(255, 255, 255, 0.7);
}

/* 音乐符号装饰 */
.music-note {
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  backdrop-filter: blur(10px);
  border-radius: 50%;
  padding: 10px;
  background-color: rgba(255, 248, 220, 0.4);
  box-shadow: 0 4px 20px rgba(255, 222, 173, 0.5);
  border: 1px solid rgba(255, 218, 185, 0.4);
  transition: all 0.4s ease;
  animation: musicNoteFloat 8s ease-in-out infinite;
}

.music-note-1 {
  width: 50px;
  height: 50px;
  top: 15%;
  right: 20%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20soft%20cream%20yellow%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 0s;
}

.music-note-2 {
  width: 60px;
  height: 60px;
  bottom: 15%;
  left: 35%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20warm%20peach%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 1s;
}

.music-note-3 {
  width: 55px;
  height: 55px;
  top: 30%;
  left: 20%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20light%20cream%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 2s;
}

.music-note-4 {
  width: 65px;
  height: 65px;
  bottom: 25%;
  right: 30%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20pastel%20orange%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 3s;
}

/* 新增装饰元素 */
.music-note-5 {
  width: 45px;
  height: 45px;
  top: 50%;
  right: 15%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20soft%20yellow%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 4s;
}

.music-note-6 {
  width: 55px;
  height: 55px;
  bottom: 35%;
  left: 10%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=music%20note%20warm%20cream%20color%20matching%20cream%20theme%20clean%20design&image_size=square");
  animation-delay: 5s;
}

/* 音乐符号悬停动画 */
.music-note:hover {
  transform: scale(1.2) rotate(15deg);
  box-shadow: 0 8px 30px rgba(138, 215, 247, 0.5);
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

.living-instrument-1 {
  width: 100px;
  height: 100px;
  top: 20%;
  left: 10%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20guitar%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20cream%20yellow%20and%20pastel%20orange%20gradient%20background%20matching%20cream%20theme%20absolutely%20no%20border%20no%20outline%20no%20frame%20completely%20clean%20background%20soft%20colorful%20design&image_size=square");
  animation-delay: 0s;
}

.living-instrument-2 {
  width: 120px;
  height: 120px;
  bottom: 15%;
  right: 15%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20drum%20set%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20cream%20yellow%20and%20pastel%20orange%20gradient%20background%20matching%20cream%20theme%20absolutely%20no%20border%20no%20outline%20no%20frame%20completely%20clean%20background%20soft%20colorful%20design&image_size=square");
  animation-delay: 2s;
}

.living-instrument-3 {
  width: 90px;
  height: 90px;
  bottom: 10%;
  left: 15%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20drum%20set%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20cream%20yellow%20and%20pastel%20orange%20gradient%20background%20matching%20cream%20theme%20no%20black%20border%20no%20black%20background%20clean%20design&image_size=square");
  animation-delay: 4s;
}

.living-instrument-4 {
  width: 120px;
  height: 120px;
  top: 15%;
  right: 30%;
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20piano%20with%20big%20cute%20eyes%20and%20smile%20floating%20with%20sparkles%20on%20cream%20yellow%20and%20pastel%20orange%20gradient%20background%20matching%20cream%20theme%20no%20border%20no%20outline%20clean%20design&image_size=square");
  animation-delay: 0s;
  z-index: 1;
  opacity: 0.7;
}

/* 有生命力的音乐元素悬停动画 */
.living-music:hover {
  transform: scale(1.1) rotate(10deg);
  box-shadow: 0 12px 40px rgba(138, 215, 247, 0.5);
}

/* 登录表单容器 */
.register .main {
  padding: 25px 25px;
  background: rgba(255, 255, 255, 0.01);
  border-radius: 35px;
  box-shadow:
    0 8px 32px rgba(248, 215, 218, 0.3),
    0 0 0 1px rgba(245, 198, 203, 0.3),
    inset 0 0 15px rgba(248, 215, 218, 0.08);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(245, 198, 203, 0.3);
  transition: all 0.4s ease;
  position: relative;
  overflow: hidden;
  z-index: 10;
}

.register .main:hover {
  box-shadow:
    0 15px 60px rgba(248, 215, 218, 0.6),
    0 0 0 1px rgba(245, 198, 203, 0.6),
    0 0 30px rgba(248, 215, 218, 0.3),
    inset 0 0 20px rgba(248, 215, 218, 0.15);
  transform: translateY(-2px);
  animation: shadowPulse 2s ease-in-out infinite;
}

@keyframes shadowPulse {
  0%,
  100% {
    box-shadow:
      0 15px 60px rgba(248, 215, 218, 0.6),
      0 0 0 1px rgba(245, 198, 203, 0.6),
      0 0 30px rgba(248, 215, 218, 0.3),
      inset 0 0 20px rgba(248, 215, 218, 0.15);
  }
  50% {
    box-shadow:
      0 18px 70px rgba(248, 215, 218, 0.7),
      0 0 0 1px rgba(245, 198, 203, 0.7),
      0 0 40px rgba(248, 215, 218, 0.4),
      inset 0 0 25px rgba(248, 215, 218, 0.2);
  }
}

.register .main::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    135deg,
    rgba(255, 255, 255, 0.1) 0%,
    rgba(255, 255, 255, 0) 50%,
    rgba(255, 222, 173, 0.05) 100%
  );
  border-radius: 35px;
  pointer-events: none;
  z-index: 1;
}

/* 主容器发光效果 */
.register .main::after {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(248, 215, 218, 0.2),
    transparent
  );
  animation: mainGlow 6s linear infinite;
  opacity: 0;
  transition: opacity 0.4s ease;
  z-index: 0;
}

.register .main:hover::after {
  opacity: 1;
}

/* 标题样式 */
.register .title {
  text-align: center;
  margin-bottom: 25px;
  position: relative;
  padding: 10px 0;
  font-size: 2rem;
  font-weight: 900;
  text-shadow: 0 0 1px rgba(0, 0, 0, 0.1);
  font-family: "Segoe UI", "Helvetica Neue", Arial, sans-serif;
  letter-spacing: 2px;
  color: #f67280;
  transition: all 0.4s ease;
  position: relative;
  z-index: 1;
}

.register .title:hover {
  transform: scale(1.1);
  color: #f67280;
  text-shadow: 0 0 5px rgba(248, 215, 218, 0.5);
}

/* 移除标题图标 */

/* 表单样式 */
.register .login-form {
  position: relative;
  z-index: 1;
}

.register .form-container {
  position: relative;
  z-index: 1;
}

.register .ant-form-item {
  margin-bottom: 5px !important;
}

.register .form-item {
  margin-top: 15px;
  opacity: 0;
  animation: itemBounceIn 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  animation-fill-mode: forwards;
}

.register .form-item:nth-child(1) {
  animation-delay: 0.3s;
}

.register .form-item:nth-child(2) {
  animation-delay: 0.5s;
}

.register .form-item:nth-child(3) {
  animation-delay: 0.7s;
}

.register .form-item:nth-child(4) {
  animation-delay: 0.9s;
}

.register .form-item:nth-child(5) {
  animation-delay: 1.1s;
}

.register .form-item:nth-child(6) {
  animation-delay: 1.3s;
}

/* 输入框样式 */
.register .ant-input-affix-wrapper,
.register .ant-input-search {
  padding: 0 !important;
  border-radius: 24px !important;
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #f8b195, #f67280, #c06c84) border-box !important;
  transition: all 0.4s ease;
  height: 48px !important;
  position: relative;
  overflow: hidden;
}

.register .ant-input-affix-wrapper:hover,
.register .ant-input-search:hover {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #f67280, #f8b195, #c06c84) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

.register .ant-input-affix-wrapper:focus-within,
.register .ant-input-search:focus-within {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #c06c84, #f8b195, #f67280) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

/* 发送验证码按钮样式 */
.register .ant-input-search-button {
  background: transparent !important;
  border: none !important;
  color: #ffb380 !important;
  font-weight: 600 !important;
  padding: 0 15px !important;
  border-radius: 0 24px 24px 0 !important;
  transition: all 0.3s ease !important;
}

.register .ant-input-search-button:hover {
  background: rgba(255, 222, 173, 0.3) !important;
  color: #ffcc99 !important;
}

.register .ant-input-affix-wrapper input {
  height: 100% !important;
  line-height: 48px !important;
  font-size: 16px !important;
  padding: 0 20px !important;
  background: transparent !important;
}

.register .ant-input-affix-wrapper:hover,
.register .ant-input-search:hover {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #ffb380, #ffd7ba, #ffcc99) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

.register .ant-input-affix-wrapper:focus-within,
.register .ant-input-search:focus-within {
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #ffcc99, #ffd7ba, #ffb380) border-box !important;
  box-shadow: none !important;
  transition: all 0.3s ease !important;
}

.register .ant-input-affix-wrapper > input.ant-input {
  padding: 12px 11px !important;
  font-size: 16px;
}

/* 优化密码输入框的显示/隐藏按钮 */
.register .ant-input-password-icon {
  margin-right: 0 !important;
  margin-left: 5px !important;
  width: 30px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  transition: all 0.3s ease !important;
}

.register .ant-input-password-icon:hover {
  transform: scale(1.1) !important;
}

/* 自定义密码显示/隐藏按钮图片 */
.register .ant-input-password-icon.anticon-eye {
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20eye%20icon%20in%20mint%20blue%20color%20with%20sparkles%20on%20transparent%20background&image_size=square");
  background-size: contain;
  background-repeat: no-repeat;
  color: transparent !important;
}

.register .ant-input-password-icon.anticon-eye-invisible {
  background-image: url("https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20cartoon%20closed%20eye%20icon%20in%20mint%20blue%20color%20with%20sparkles%20on%20transparent%20background&image_size=square");
  background-size: contain;
  background-repeat: no-repeat;
  color: transparent !important;
}

/* 注册按钮样式 */
.register .register-btn {
  margin-top: 30px;
  margin-bottom: 10px;
  background: linear-gradient(45deg, #f8b195, #f67280, #f8b195) !important;
  border: none !important;
  border-radius: 24px !important;
  padding: 12px !important;
  font-size: 18px !important;
  font-weight: 600 !important;
  color: white !important;
  box-shadow: 0 4px 15px rgba(248, 215, 218, 0.5);
  transition: all 0.5s ease-in-out;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
  position: relative;
  overflow: hidden;
}

.register .register-btn:hover {
  background: linear-gradient(45deg, #f9d5d9, #f67280, #f9d5d9) !important;
  box-shadow: 0 8px 25px rgba(248, 215, 218, 0.7);
  transform: translateY(-3px) scale(1.02);
}

.register .register-btn::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: linear-gradient(
    45deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transform: rotate(45deg);
  animation: buttonShine 3s ease-in-out infinite;
  opacity: 0;
  transition: opacity 0.5s ease;
}

.register .register-btn:hover::before {
  opacity: 1;
}

@keyframes buttonShine {
  0% {
    transform: translateX(-100%) rotate(45deg);
  }
  100% {
    transform: translateX(100%) rotate(45deg);
  }
}

/* 短信验证码按钮样式 */
.register .sms-btn {
  width: 100%;
  height: 48px;
  background: linear-gradient(45deg, #f8b195, #f67280, #f8b195) !important;
  border: none !important;
  border-radius: 24px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  color: white !important;
  box-shadow: 0 2px 10px rgba(248, 215, 218, 0.4);
  transition: all 0.3s ease;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

.register .sms-btn:hover {
  background: linear-gradient(45deg, #f9d5d9, #f67280, #f9d5d9) !important;
  box-shadow: 0 4px 15px rgba(248, 215, 218, 0.6);
  transform: translateY(-1px);
}

.register .sms-btn:disabled {
  background: linear-gradient(45deg, #f9d5d9, #f9d5d9, #f9d5d9) !important;
  color: #f69d96 !important;
  box-shadow: none;
  cursor: not-allowed;
}

/* 图片验证码样式 */
.register .image-code-input {
  border-radius: 24px !important;
  border: 2px solid transparent !important;
  background:
    linear-gradient(rgba(255, 255, 255, 0.9), rgba(255, 255, 255, 0.9))
      padding-box,
    linear-gradient(45deg, #f8b195, #f67280, #f8b195) border-box !important;
  transition: all 0.4s ease;
  height: 48px !important;
}

.register .image-code-img {
  width: 100px;
  height: 36px;
  cursor: pointer;
  border-radius: 18px;
  box-shadow: 0 2px 8px rgba(248, 215, 218, 0.4);
  transition: all 0.2s ease;
  object-fit: contain;
  border: 1px solid rgba(245, 198, 203, 0.3);
  display: block;
  margin: 6px auto;
  background-color: white;
}

.register .image-code-img:hover {
  transform: scale(1.02);
  box-shadow: 0 4px 15px rgba(248, 215, 218, 0.6);
  border-color: rgba(245, 198, 203, 0.5);
}

/* 底部链接样式 */
.register .footer {
  margin-top: 30px;
  margin-bottom: 10px;
  text-align: center;
}

.register .footer-link {
  color: #f8b195 !important;
  text-decoration: none !important;
  font-size: 14px;
  font-weight: 500;
  position: relative;
  transition: all 0.3s ease;
  padding: 5px 10px;
  border-radius: 20px;
  z-index: 20;
  cursor: pointer;
}

.register .footer-link:hover {
  color: #f67280 !important;
  background: rgba(248, 215, 218, 0.3);
  box-shadow: 0 4px 15px rgba(248, 215, 218, 0.4);
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
  background: linear-gradient(90deg, #f8b195, #f67280, #f8b195);
  transition: left 0.5s ease;
}

.ant-input-affix-wrapper:focus-within::after {
  left: 0;
}

/* 输入框聚焦动画 */
.ant-input-affix-wrapper:focus-within {
  animation: focusPulse 0.5s ease-in-out;
}

/* 背景动画效果 */
@keyframes backgroundPulse {
  0%,
  100% {
    background-size: 100% 100%;
  }
  50% {
    background-size: 105% 105%;
  }
}

/* 动画定义 */
@keyframes fadeIn {
  0% {
    opacity: 0;
    transform: scale(0.9);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.02);
  }
  100% {
    opacity: 1;
    transform: scale(1);
  }
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

@keyframes btnPop {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(0.95);
  }
}

@keyframes mainGlow {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes focusPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 179, 128, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(255, 179, 128, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 179, 128, 0);
  }
}

@keyframes decorationPop {
  0%,
  100% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.1) rotate(10deg);
  }
}

@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes livingFloat {
  0% {
    transform: translateY(0px) rotate(0deg);
  }
  25% {
    transform: translateY(-35px) rotate(8deg);
  }
  50% {
    transform: translateY(0px) rotate(15deg);
  }
  75% {
    transform: translateY(35px) rotate(8deg);
  }
  100% {
    transform: translateY(0px) rotate(0deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register {
    padding-top: 15%;
  }

  .register .main {
    width: 90%;
    margin: 0 auto;
    padding: 25px 15px;
  }

  .background-decorations {
    display: none;
  }

  .register .title {
    font-size: 1.5rem;
  }

  .register .title-icon {
    right: -25px;
    width: 25px;
    height: 25px;
  }

  .register .ant-input-affix-wrapper > input.ant-input {
    font-size: 14px !important;
    padding: 8px 11px !important;
  }

  .register .register-btn {
    font-size: 16px !important;
    padding: 10px !important;
  }
}

@media (max-width: 480px) {
  .register {
    padding-top: 20%;
  }

  .register .main {
    width: 95%;
    padding: 20px 12px;
  }

  .register .title {
    font-size: 1.3rem;
  }

  .register .title-icon {
    right: -25px;
    width: 20px;
    height: 20px;
  }

  .register .register-btn {
    font-size: 14px !important;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .register {
    padding-top: 10%;
  }

  .register .main {
    width: 70%;
    margin: 0 auto;
  }
}

/* 横屏模式适配 */
@media (orientation: landscape) and (max-height: 500px) {
  .register {
    padding-top: 5%;
  }

  .register .main {
    padding: 20px;
    margin-top: 0;
  }
}

/* 错误状态样式 */
.ant-form-item-has-error .ant-input-affix-wrapper {
  border-color: #ff6b6b !important;
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.2);
}

.ant-form-item-has-error .ant-input {
  color: #ff6b6b;
}

/* 成功状态样式 */
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

/* 装饰元素悬停动画 */
.decoration:hover {
  animation: decorationHover 0.5s ease-in-out;
}

@keyframes decorationHover {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1) rotate(5deg);
  }
  100% {
    transform: scale(1);
  }
}

/* 音乐符号悬停动画 */
.music-note:hover {
  animation: musicNoteHover 0.5s ease-in-out;
}

@keyframes musicNoteHover {
  0% {
    transform: scale(1) rotate(0deg);
  }
  50% {
    transform: scale(1.2) rotate(15deg);
  }
  100% {
    transform: scale(1) rotate(0deg);
  }
}

/* 音乐符号浮动动画 */
@keyframes musicNoteFloat {
  0% {
    transform: translateY(0px) rotate(0deg);
  }
  25% {
    transform: translateY(-15px) rotate(8deg);
  }
  50% {
    transform: translateY(0px) rotate(15deg);
  }
  75% {
    transform: translateY(15px) rotate(8deg);
  }
  100% {
    transform: translateY(0px) rotate(0deg);
  }
}

/* 页脚链接悬停动画 */
.footer-link:hover {
  animation: linkFloat 0.4s ease-in-out infinite alternate;
}

@keyframes linkFloat {
  from {
    transform: translateY(0px);
  }
  to {
    transform: translateY(-3px);
  }
}
</style>
