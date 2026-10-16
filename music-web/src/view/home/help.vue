<template>
  <div class="help-page">
    <section class="help-hero">
      <div class="hero-title-row">
        <div class="hero-icon">
          <span class="dot dot-1" />
          <span class="dot dot-2" />
          <span class="dot dot-3" />
        </div>
        <div>
          <h1>使用指南</h1>
          <p class="subtitle">
            简要介绍本系统的主要功能与基础操作，帮助你快速完成语音识别与音频处理。
          </p>
        </div>
      </div>
      <p class="subtitle">
        如需更详细的技术实现或系统设计说明，可在论文或答辩中单独展示，无需在此页面完全展开。
      </p>
      <div class="hero-tags">
        <span class="tag">快速上手</span>
        <span class="tag">操作说明</span>
        <span class="tag">常见问题</span>
      </div>
    </section>

    <a-row :gutter="24">
      <!-- 左侧主内容 -->
      <a-col :xs="24" :md="17">
        <section class="help-section" id="overview">
          <h2>主要功能一览</h2>
          <div class="feature-grid">
            <div class="feature-card">
              <div class="feature-icon feature-icon-blue">识</div>
              <div class="feature-content">
                <h3>语音识别</h3>
                <p>
                  上传音频，自动生成字幕和文本，适合课堂录音、访谈、视频配音等场景。
                </p>
              </div>
            </div>
            <div class="feature-card">
              <div class="feature-icon feature-icon-green">转</div>
              <div class="feature-content">
                <h3>音频格式转换</h3>
                <p>
                  在常见音频格式之间一键转换，支持批量处理和基础音质参数调节。
                </p>
              </div>
            </div>
            <div class="feature-card">
              <div class="feature-icon feature-icon-purple">统</div>
              <div class="feature-content">
                <h3>数据统计</h3>
                <p>
                  查看语音识别次数、总时长等基础统计，用于展示系统使用情况。
                </p>
              </div>
            </div>
          </div>
        </section>

        <section class="help-section" id="account">
          <h2>账号与登录</h2>
          <ol class="step-list">
            <li>在登录页输入账号和密码即可进入系统。</li>
            <li>首次使用时，可按页面提示完成快速注册。</li>
          </ol>
        </section>

        <section class="help-section" id="filetrans">
          <h2>语音识别 · 基本使用</h2>
          <ol class="step-list">
            <li>在左侧菜单点击「语音识别」，进入识别页面。</li>
            <li>点击「开始上传音频」，选择本地音频文件并确认。</li>
            <li>
              等待任务状态变为完成后，可查看或下载识别结果（字幕 / 文本）。
            </li>
          </ol>
          <a-alert type="info" show-icon class="tip-alert">
            <template #message>数据安全说明</template>
            <template #description>
              为保障用户数据安全，系统会在
              <strong>一段时间后自动清理</strong>
              上传的音频文件，请在有效期内及时下载和备份识别结果。
            </template>
          </a-alert>
        </section>

        <section class="help-section" id="fileformat">
          <h2>音频格式转换 · 基本使用</h2>
          <ol class="step-list">
            <li>在左侧菜单点击「音频转换」，进入转换页面。</li>
            <li>选择需要转换的音频文件，并在页面中选择目标格式。</li>
            <li>如有需要，可简单调整音质相关参数，然后开始转换。</li>
            <li>转换完成后，可在列表中播放预览或下载保存。</li>
          </ol>
          <a-alert type="warning" show-icon class="tip-alert">
            <template #message>小提示</template>
            <template #description>
              如果只是日常使用，一般保持默认设置即可；如对音质或文件大小有特殊要求，再进行参数微调。
            </template>
          </a-alert>
        </section>

        <section class="help-section" id="faq">
          <h2>常见问题（精简版）</h2>
          <div class="faq-item" v-for="item in faqList" :key="item.q">
            <h3 class="faq-question">{{ item.q }}</h3>
            <p class="faq-answer" v-for="(line, index) in item.a" :key="index">
              {{ line }}
            </p>
          </div>
        </section>
      </a-col>

      <!-- 右侧导航和小贴士 -->
      <a-col :xs="24" :md="7">
        <aside class="help-sidebar">
          <h3>快速导航</h3>
          <ul class="anchor-list">
            <li><a href="#overview">主要功能一览</a></li>
            <li><a href="#account">账号与登录</a></li>
            <li><a href="#filetrans">语音识别</a></li>
            <li><a href="#fileformat">音频转换</a></li>
            <li><a href="#faq">常见问题</a></li>
          </ul>

          <div class="side-card">
            <h4>使用小贴士</h4>
            <ul>
              <li>建议保证音频清晰、背景噪声较少，以获得更高的识别准确率。</li>
              <li>长音频识别前，可先用短片段进行测试，确认识别效果。</li>
              <li>请及时下载重要的识别结果和转换文件，避免超过保留期限。</li>
            </ul>
          </div>
        </aside>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref } from "vue";

const faqList = ref([
  {
    q: "1. 支持哪些音频格式进行语音识别？",
    a: [
      "通常支持常见的音频格式（如 WAV、MP3、M4A、FLAC 等），具体支持范围以上传组件提示为准。",
      "如果上传失败，请尝试先通过「音频转换」功能将文件转换为 WAV 或 MP3 再进行识别。",
    ],
  },
  {
    q: "2. 识别结果的准确率如何？",
    a: [
      "识别准确率受音频清晰度、说话人语速、背景噪声等因素影响。",
      "建议使用采样率不低于 16kHz 的清晰录音，并尽量在安静环境中录制。",
    ],
  },
  {
    q: "3. 上传的音频会被长期保存吗？",
    a: [
      "系统默认会在一段时间后自动清理音频文件，目前为 10 天。",
      "请在有效期内及时下载和备份重要的识别结果与音频文件。",
    ],
  },
  {
    q: "4. 为什么任务状态长时间停留在“处理中”？",
    a: [
      "可能是当前排队任务较多，或网络波动导致请求延迟。",
      "如长时间无变化，可尝试点击页面上的“刷新列表”按钮重新获取任务状态。",
    ],
  },
  {
    q: "5. 如何在论文或项目中引用本系统？",
    a: [
      "可以在论文的“系统实现”或“实验平台”部分，说明使用了基于阿里云语音服务的智能语音识别与音频处理平台，并简要描述主要功能模块。",
    ],
  },
]);
</script>

<style scoped>
.help-page {
  padding: 24px;
  background: #f9fafb;
  min-height: calc(100vh - 120px);
}

.help-hero {
  margin-bottom: 24px;
}

.hero-title-row {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 8px;
}

.hero-icon {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  background: radial-gradient(circle at 0 0, #1677ff, #722ed1);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.dot {
  position: absolute;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.85);
}

.dot-1 {
  width: 5px;
  height: 5px;
  top: 9px;
  left: 9px;
}

.dot-2 {
  width: 4px;
  height: 4px;
  top: 18px;
  left: 16px;
}

.dot-3 {
  width: 3px;
  height: 3px;
  top: 12px;
  left: 22px;
}

.help-hero h1 {
  font-size: 24px;
  margin-bottom: 8px;
}

.subtitle {
  color: #666;
  margin-bottom: 12px;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.hero-tags .tag {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: #e6f4ff;
  color: #1677ff;
}

.help-section {
  background: #ffffff;
  border-radius: 8px;
  padding: 18px 20px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}

.help-section h2 {
  font-size: 18px;
  margin-bottom: 10px;
}

.help-section h3 {
  font-size: 15px;
  margin-bottom: 6px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
}

.feature-card {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
  border-radius: 10px;
  background: linear-gradient(135deg, #ffffff, #f5f7ff);
  border: 1px solid #eef0ff;
}

.feature-icon {
  width: 30px;
  height: 30px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 600;
  color: #fff;
}

.feature-icon-blue {
  background: linear-gradient(135deg, #1677ff, #36cfc9);
}

.feature-icon-green {
  background: linear-gradient(135deg, #52c41a, #13c2c2);
}

.feature-icon-purple {
  background: linear-gradient(135deg, #722ed1, #eb2f96);
}

.feature-content p {
  margin: 2px 0 0;
  font-size: 13px;
  color: #666;
}

.feature-list,
.bullet-list,
.step-list {
  margin-left: 18px;
}

.feature-list li,
.bullet-list li {
  margin-bottom: 4px;
}

.step-list li {
  margin-bottom: 6px;
}

.tip-alert {
  margin-top: 12px;
}

.faq-item {
  margin-bottom: 12px;
}

.faq-question {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}

.faq-answer {
  margin: 0 0 2px;
  color: #555;
}

.help-sidebar {
  position: sticky;
  top: 88px;
}

.help-sidebar h3 {
  margin-bottom: 8px;
}

.anchor-list {
  list-style: none;
  margin: 0 0 16px;
  padding: 0;
}

.anchor-list li {
  margin-bottom: 6px;
}

.anchor-list a {
  color: #1677ff;
  text-decoration: none;
  font-size: 14px;
}

.anchor-list a:hover {
  text-decoration: underline;
}

.side-card {
  background: #ffffff;
  border-radius: 8px;
  padding: 12px 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
}

.side-card h4 {
  margin-bottom: 6px;
}

.side-card ul {
  padding-left: 18px;
  margin: 0;
}

.side-card li {
  margin-bottom: 4px;
}

@media (max-width: 768px) {
  .help-page {
    padding: 16px 12px;
  }

  .help-sidebar {
    position: static;
    margin-top: 8px;
  }

  .feature-grid {
    grid-template-columns: repeat(1, minmax(0, 1fr));
  }
}
</style>
