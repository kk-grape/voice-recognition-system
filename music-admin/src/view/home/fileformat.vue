<template>
  <div class="fileformat-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">📁</span>
          文件格式
        </h2>
        <p class="page-desc">管理系统中的文件格式转换配置</p>
      </div>
    </div>

    <!-- 装饰线 -->
    <div class="header-decoration"></div>

    <!-- 目录信息卡片 -->
    <a-row :gutter="[20, 20]" class="info-row">
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card class="info-card">
          <div class="info-item">
            <div class="info-icon info-icon-yellow">
              <FolderOutlined />
            </div>
            <div class="info-content">
              <div class="info-label">文件数量</div>
              <div class="info-value">{{ dirInfo.fileCount || 0 }} 个</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card class="info-card">
          <div class="info-item">
            <div class="info-icon info-icon-pink">
              <DatabaseOutlined />
            </div>
            <div class="info-content">
              <div class="info-label">总大小</div>
              <div class="info-value">{{ formatSize(dirInfo.totalSize) }}</div>
            </div>
          </div>
        </a-card>
      </a-col>
      <a-col :xs="24" :sm="12" :lg="8">
        <a-card class="info-card">
          <div class="info-item">
            <div class="info-icon info-icon-purple">
              <FolderOpenOutlined />
            </div>
            <div class="info-content">
              <div class="info-label">存储路径</div>
              <div class="info-value path-value">{{ dirInfo.path || '-' }}</div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 刷新按钮 -->
    <div class="action-row">
      <a-button type="primary" @click="loadDirInfo" class="refresh-btn">
        <template #icon><ReloadOutlined /></template>
        刷新信息
      </a-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { notification } from "ant-design-vue";
import { FolderOutlined, FolderOpenOutlined, DatabaseOutlined, ReloadOutlined } from "@ant-design/icons-vue";
import { getDirInfo } from "../../api/fileformat";

const dirInfo = reactive({
  path: "",
  totalSize: 0,
  fileCount: 0,
  exists: false,
});

const loadDirInfo = () => {
  getDirInfo()
    .then((data) => {
      Object.assign(dirInfo, data.content || {});
    })
    .catch((err) => {
      console.error("获取目录信息失败", err);
      notification.error({ message: "获取目录信息失败" });
    });
};

const formatSize = (bytes) => {
  if (!bytes) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB", "TB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

onMounted(() => {
  loadDirInfo();
});
</script>

<style scoped>
.fileformat-page {
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

/* 信息卡片 */
.info-row {
  margin-bottom: 24px;
}

.info-card {
  height: 100%;
}

.info-card :deep(.ant-card-body) {
  padding: 20px !important;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.info-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
  flex-shrink: 0;
}

.info-icon-yellow {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-light));
}

.info-icon-pink {
  background: linear-gradient(135deg, var(--accent-color), var(--accent-light));
}

.info-icon-purple {
  background: linear-gradient(135deg, #9C27B0, #E1BEE7);
}

.info-content {
  flex: 1;
  min-width: 0;
}

.info-label {
  font-size: 13px;
  color: var(--text-light);
  margin-bottom: 4px;
}

.info-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.path-value {
  font-size: 14px;
  word-break: break-all;
}

/* 操作按钮 */
.action-row {
  display: flex;
  justify-content: flex-start;
}

.refresh-btn {
  display: inline-flex !important;
  align-items: center;
  gap: 6px;
}
</style>
