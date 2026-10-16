<template>
  <div class="realtime-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">🎧</span>
          实时语音管理
        </h2>
        <p class="page-desc">管理实时语音识别会话记录</p>
      </div>
      <div class="header-actions">
        <a-button type="primary" @click="handleQuery()" class="refresh-btn">
          <template #icon><ReloadOutlined /></template>
          刷新列表
        </a-button>
      </div>
    </div>

    <!-- 搜索条件 -->
    <a-card class="search-card" :bordered="false">
      <a-form layout="inline" :model="searchForm">
        <a-form-item label="会员ID">
          <a-input
            v-model:value="searchForm.memberId"
            placeholder="请输入会员ID"
            style="width: 120px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            style="width: 140px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="active">进行中</a-select-option>
            <a-select-option value="completed">已完成</a-select-option>
            <a-select-option value="failed">失败</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">
              <template #icon><SearchOutlined /></template>
              查询
            </a-button>
            <a-button @click="handleReset">
              <template #icon><ClearOutlined /></template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 装饰线 -->
    <div class="header-decoration"></div>

    <!-- 数据表格 -->
    <a-table
      :dataSource="sessions"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
      class="custom-table"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'durationSeconds'">
          {{ formatDuration(record.durationSeconds) }}
        </template>
        <template v-else-if="column.dataIndex === 'startTime' || column.dataIndex === 'endTime'">
          <span class="time-text">{{ formatTime(record[column.dataIndex]) }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-button type="link" size="small" @click="showDetail(record)" class="view-btn">
            查看详情
          </a-button>
        </template>
      </template>
    </a-table>

    <!-- 会话详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="会话详情"
      :footer="null"
      width="700px"
    >
      <a-descriptions :column="2" bordered size="small">
        <a-descriptions-item label="会话ID" :span="2">{{ currentSession?.wsSessionId }}</a-descriptions-item>
        <a-descriptions-item label="会员ID">{{ currentSession?.memberId }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(currentSession?.status)">
            {{ getStatusText(currentSession?.status) }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="识别时长">
          {{ formatDuration(currentSession?.durationSeconds) }}
        </a-descriptions-item>
        <a-descriptions-item label="采样率">{{ currentSession?.sampleRate ? currentSession.sampleRate + ' Hz' : '-' }}</a-descriptions-item>
        <a-descriptions-item label="音频格式">{{ currentSession?.format || '-' }}</a-descriptions-item>
        <a-descriptions-item label="源语言">{{ currentSession?.sourceLangDesc || '-' }}</a-descriptions-item>
        <a-descriptions-item label="目标语言">{{ currentSession?.targetLangDesc || '-' }}</a-descriptions-item>
        <a-descriptions-item label="开始时间">{{ formatTime(currentSession?.startTime) }}</a-descriptions-item>
        <a-descriptions-item label="结束时间">{{ formatTime(currentSession?.endTime) }}</a-descriptions-item>
      </a-descriptions>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { notification } from "ant-design-vue";
import { ReloadOutlined, SearchOutlined, ClearOutlined } from "@ant-design/icons-vue";
import { getAsrSessionList, getAsrSessionDetail } from "../../api/asr";

// 搜索表单
const searchForm = reactive({
  memberId: "",
  status: "",
});

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
});

// 加载状态
const loading = ref(false);
// 会话列表
const sessions = ref([]);

// 详情弹窗
const detailVisible = ref(false);
const currentSession = ref(null);

// 状态映射
const STATUS_MAP = {
  "active": { text: "进行中", color: "green" },
  "completed": { text: "已完成", color: "blue" },
  "failed": { text: "失败", color: "red" },
};

const getStatusColor = (status) => STATUS_MAP[status]?.color || "default";
const getStatusText = (status) => STATUS_MAP[status]?.text || status || "-";

// 表格列定义（使用API返回的实际字段名）
const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "会话ID", dataIndex: "wsSessionId", width: 280, ellipsis: true },
  { title: "会员ID", dataIndex: "memberId", width: 100 },
  { title: "状态", dataIndex: "status", width: 100 },
  { title: "识别时长", dataIndex: "durationSeconds", width: 100 },
  { title: "源语言", dataIndex: "sourceLangDesc", width: 80 },
  { title: "目标语言", dataIndex: "targetLangDesc", width: 80 },
  { title: "开始时间", dataIndex: "startTime", width: 180 },
  { title: "结束时间", dataIndex: "endTime", width: 180 },
  { title: "操作", dataIndex: "operation", width: 100, fixed: "right" },
];

// 格式化时长（durationSeconds 单位是秒）
const formatDuration = (duration) => {
  if (!duration) return "-";
  const seconds = parseInt(duration);
  if (seconds < 60) return seconds + "秒";
  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return minutes + "分" + secs + "秒";
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return "-";
  const date = new Date(time);
  return date.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
  });
};

// 查询列表
const handleQuery = (param) => {
  if (!param) {
    param = { page: pagination.current, size: pagination.pageSize };
  }
  loading.value = true;
  sessions.value = [];

  // 构建查询参数
  const params = {
    page: param.page,
    size: param.size,
  };

  if (searchForm.memberId) {
    params.memberId = searchForm.memberId;
  }
  if (searchForm.status) {
    params.status = searchForm.status;
  }

  getAsrSessionList(params)
    .then((data) => {
      loading.value = false;
      sessions.value = data.content?.list || [];
      pagination.current = param.page;
      pagination.total = data.content?.total || 0;
    })
    .catch((err) => {
      loading.value = false;
      console.error("查询失败", err);
      notification.error({ description: "查询失败，请检查网络或后端服务" });
    });
};

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  handleQuery({ page: 1, size: pagination.pageSize });
};

// 重置
const handleReset = () => {
  searchForm.memberId = "";
  searchForm.status = "";
  pagination.current = 1;
  handleQuery({ page: 1, size: pagination.pageSize });
};

// 分页变化
const handleTableChange = (pag) => {
  handleQuery({ page: pag.current, size: pag.pageSize });
};

// 查看详情
const showDetail = async (record) => {
  currentSession.value = record;
  detailVisible.value = true;
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.realtime-page {
  padding: 0;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

/* 刷新按钮 */
.refresh-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 20px !important;
  height: auto !important;
}

/* 搜索卡片 */
.search-card {
  margin-bottom: 20px;
  background: var(--bg-card);
  border-radius: var(--radius-medium);
}

/* 装饰线 */
.header-decoration {
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-color), var(--primary-light));
  border-radius: 2px;
  margin-bottom: 24px;
  opacity: 0.6;
}

/* 表格样式 */
.custom-table {
  margin-top: 16px;
}

/* 查看按钮 */
.view-btn {
  color: var(--primary-color) !important;
  padding: 4px 12px !important;
  border-radius: var(--radius-small) !important;
}

.view-btn:hover {
  background: var(--primary-lighter) !important;
}

/* 时间文本 */
.time-text {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
