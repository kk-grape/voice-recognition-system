<template>
  <a-modal
    v-model:open="open"
    title="字幕详情"
    :footer="null"
    :width="800"
    class="subtitle-modal"
  >
    <!-- 操作按钮 -->
    <div class="modal-actions">
      <a-button type="primary" @click="genSubtitleFile" :loading="downloadLoading" class="action-btn">
        <template #icon><DownloadOutlined /></template>
        下载字幕
      </a-button>
      <a-button type="primary" @click="genTextFile" :loading="downloadLoading" class="action-btn action-btn-secondary">
        <template #icon><DownloadOutlined /></template>
        下载纯文本
      </a-button>
    </div>

    <!-- 字幕列表 -->
    <a-table
      :dataSource="subtitles"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
      class="subtitle-table"
      :scroll="{ y: 400 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'time'">
          <span class="time-range">{{ formatMs(record.begin) }} ~ {{ formatMs(record.end) }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'text'">
          <span class="subtitle-text">{{ record.text }}</span>
        </template>
      </template>
    </a-table>
  </a-modal>

  <!-- 下载组件 -->
  <the-download ref="theDownloadCom"></the-download>
</template>

<script setup>
import { ref, reactive } from "vue";
import { message, notification } from "ant-design-vue";
import { DownloadOutlined } from "@ant-design/icons-vue";
import TheDownload from "../../components/the-download.vue";
import { getFiletransSubtitleList, genSubtitle as genSubtitleApi, genText as genTextApi } from "../../api/filetrans";

const open = ref(false);
const filetrans = reactive({ id: null, name: "" });
const downloadLoading = ref(false);

const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 5,
});

const loading = ref(false);
const subtitles = ref([]);

const theDownloadCom = ref();

const showModal = (_filetrans) => {
  filetrans.id = _filetrans.id;
  filetrans.name = _filetrans.name;
  pagination.current = 1;
  open.value = true;
  handleQuery();
};

const columns = [
  {
    title: "时间",
    dataIndex: "time",
    width: 200,
  },
  {
    title: "字幕内容",
    dataIndex: "text",
  },
];

const handleQuery = (param) => {
  if (!param) {
    param = {
      page: pagination.current,
      size: pagination.pageSize,
    };
  }
  loading.value = true;
  subtitles.value = [];
  getFiletransSubtitleList({
    page: param.page,
    size: param.size,
    filetransId: filetrans.id,
  })
    .then((data) => {
      loading.value = false;
      subtitles.value = data.content.list || [];
      pagination.current = param.page;
      pagination.total = data.content.total || 0;
    })
    .catch((err) => {
      loading.value = false;
      console.error("查询字幕失败", err);
      notification.error({ description: "查询字幕失败" });
    });
};

const handleTableChange = (pag) => {
  handleQuery({
    page: pag.current,
    size: pag.pageSize,
  });
};

const formatMs = (ms) => {
  if (!ms) return "00:00:00";
  const totalSeconds = Math.floor(ms / 1000);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  const pad = (n) => String(n).padStart(2, "0");
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)}`;
};

const genSubtitleFile = () => {
  downloadLoading.value = true;
  genSubtitleApi({ filetransId: filetrans.id })
    .then((data) => {
      downloadLoading.value = false;
      theDownloadCom.value.downloadItem(
        data.content,
        filetrans.name + "-" + filetrans.id + ".srt"
      );
      message.success("字幕文件下载成功");
    })
    .catch((err) => {
      downloadLoading.value = false;
      console.error("下载字幕失败", err);
      message.error("下载字幕失败");
    });
};

const genTextFile = () => {
  downloadLoading.value = true;
  genTextApi({ filetransId: filetrans.id })
    .then((data) => {
      downloadLoading.value = false;
      theDownloadCom.value.downloadItem(
        data.content,
        filetrans.name + "-" + filetrans.id + ".txt"
      );
      message.success("文本文件下载成功");
    })
    .catch((err) => {
      downloadLoading.value = false;
      console.error("下载文本失败", err);
      message.error("下载文本失败");
    });
};

defineExpose({
  showModal,
});
</script>

<style scoped>
/* 操作按钮 */
.modal-actions {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px;
  background: var(--primary-lighter);
  border-radius: var(--radius-medium);
}

.action-btn {
  display: inline-flex !important;
  align-items: center;
  gap: 6px;
}

.action-btn-secondary {
  background: linear-gradient(135deg, var(--accent-color), var(--accent-light)) !important;
}

/* 时间显示 */
.time-range {
  font-family: 'Monaco', 'Consolas', monospace;
  color: var(--primary-dark);
  font-weight: 500;
}

/* 字幕文本 */
.subtitle-text {
  color: var(--text-primary);
  line-height: 1.6;
}

/* 字幕表格 */
.subtitle-table :deep(.ant-table-thead > tr > th) {
  background: var(--primary-lighter) !important;
}

.subtitle-table :deep(.ant-table-tbody > tr > td) {
  padding: 12px 16px !important;
}
</style>

<style>
/* 模态框全局样式 */
.subtitle-modal .ant-modal-content {
  border-radius: var(--radius-large);
  overflow: hidden;
}

.subtitle-modal .ant-modal-header {
  background: linear-gradient(135deg, var(--primary-lighter), var(--bg-card)) !important;
  padding: 20px 24px !important;
}

.subtitle-modal .ant-modal-title {
  font-weight: 600;
  font-size: 16px;
  color: var(--text-primary);
}

.subtitle-modal .ant-modal-body {
  padding: 24px !important;
}
</style>
