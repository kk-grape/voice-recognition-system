<template>
  <div class="help-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">❓</span>
          帮助文档
        </h2>
        <p class="page-desc">使用指南和常见问题解答</p>
      </div>
    </div>

    <!-- 装饰线 -->
    <div class="header-decoration"></div>

    <!-- 帮助内容 -->
    <div class="help-content">
      <a-row :gutter="[24, 24]">
        <a-col :xs="24" :lg="12">
          <a-card class="help-card" hoverable>
            <template #title>
              <span class="card-title">📖 使用指南</span>
            </template>
            <a-list size="small" :data-source="guideList">
              <template #renderItem="{ item }">
                <a-list-item>
                  <template #actions>
                    <span class="action-hint">{{ item.hint }}</span>
                  </template>
                  <a-list-item-meta>
                    <template #title>
                      <span class="guide-title">{{ item.title }}</span>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>

        <a-col :xs="24" :lg="12">
          <a-card class="help-card" hoverable>
            <template #title>
              <span class="card-title">💡 常见问题</span>
            </template>
            <a-list size="small" :data-source="faqList">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <span class="faq-q">{{ item.q }}</span>
                    </template>
                    <template #description>
                      <span class="faq-a">{{ item.a }}</span>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </a-card>
        </a-col>
      </a-row>

      <!-- 联系信息 -->
      <a-card class="contact-card" hoverable>
        <template #title>
          <span class="card-title">📞 联系我们</span>
        </template>
        <div class="contact-content">
          <p>如有任何问题，请联系技术支持：</p>
          <div class="contact-info">
            <span class="contact-item">
              <span class="contact-icon">📧</span>
              support@kk-voice.com
            </span>
            <span class="contact-item">
              <span class="contact-icon">📱</span>
              400-888-8888
            </span>
          </div>
        </div>
      </a-card>
    </div>
  </div>
</template>

<script setup>
const guideList = [
  { title: "会员管理", hint: "查看会员信息" },
  { title: "语音识别", hint: "管理识别任务" },
  { title: "AI助手", hint: "查看对话记录" },
  { title: "实时语音", hint: "管理实时会话" },
  { title: "文件格式", hint: "查看存储信息" },
];

const faqList = [
  { q: "如何管理会员？", a: "在会员管理页面可以查看所有会员信息，包括注册时间、会员等级等。" },
  { q: "如何查看语音识别结果？", a: "在语音识别页面可以查看所有识别任务，点击查看字幕可以查看详细结果。" },
  { q: "AI助手对话如何管理？", a: "在AI助手页面可以查看用户的对话记录，点击详情查看完整对话内容。" },
];
</script>

<style scoped>
.help-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  margin-bottom: 20px;
}

.header-content {
  display: flex;
  flex-direction: column;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 6px 0;
}

.title-icon {
  font-size: 28px;
}

.page-desc {
  color: var(--text-light);
  font-size: 14px;
  margin: 0;
}

/* 装饰线 */
.header-decoration {
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color), var(--primary-light));
  border-radius: 2px;
  margin-bottom: 24px;
  opacity: 0.6;
}

/* 帮助内容 */
.help-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.help-card {
  height: 100%;
}

.help-card :deep(.ant-card-head) {
  background: var(--primary-lighter);
}

.card-title {
  font-weight: 600;
  font-size: 16px;
}

.guide-title {
  font-weight: 500;
  color: var(--text-primary);
}

.action-hint {
  color: var(--text-light);
  font-size: 12px;
}

/* FAQ */
.faq-q {
  font-weight: 600;
  color: var(--text-primary);
}

.faq-a {
  color: var(--text-secondary);
  font-size: 13px;
}

/* 联系卡片 */
.contact-card :deep(.ant-card-head) {
  background: var(--primary-lighter);
}

.contact-content p {
  color: var(--text-secondary);
  margin-bottom: 16px;
}

.contact-info {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
  font-weight: 500;
}

.contact-icon {
  font-size: 18px;
}
</style>
