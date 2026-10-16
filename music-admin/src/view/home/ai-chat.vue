<template>
  <div class="ai-chat-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">🤖</span>
          AI助手对话管理
        </h2>
        <p class="page-desc">查看用户与AI助手的对话记录</p>
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
            <a-select-option value="0">进行中</a-select-option>
            <a-select-option value="1">已完成</a-select-option>
            <a-select-option value="2">已关闭</a-select-option>
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
      :dataSource="chatSessions"
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
        <template v-else-if="column.dataIndex === 'createdAt'">
          <span class="time-text">{{ record.createdAt }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-button
            type="link"
            size="small"
            @click="viewDetail(record)"
            class="view-btn"
          >
            查看详情
          </a-button>
        </template>
      </template>
    </a-table>

    <!-- 对话详情弹窗 -->
    <a-modal
      v-model:open="detailVisible"
      title="对话详情"
      :footer="null"
      :width="800"
      class="detail-modal"
      :confirmLoading="messageLoading"
    >
      <!-- 会话信息 -->
      <a-descriptions :column="2" size="small" bordered class="session-info">
        <a-descriptions-item label="会话ID" :span="2">{{
          currentSession?.sessionUuid || currentSession?.sessionId
        }}</a-descriptions-item>
        <a-descriptions-item label="会员ID">{{
          currentSession?.userId
        }}</a-descriptions-item>
        <a-descriptions-item label="消息数">{{
          currentSession?.messageCount || messageList.length
        }}</a-descriptions-item>
        <a-descriptions-item label="创建时间">{{
          currentSession?.createdAt
        }}</a-descriptions-item>
        <a-descriptions-item label="状态">
          <a-tag :color="getStatusColor(currentSession?.status)">
            {{ getStatusText(currentSession?.status) }}
          </a-tag>
        </a-descriptions-item>
      </a-descriptions>

      <!-- 消息列表 -->
      <div class="message-list-title">对话内容</div>
      <div class="chat-detail" v-if="!messageLoading">
        <div
          v-for="(msg, index) in messageList"
          :key="index"
          class="message-item"
          :class="{
            'is-user': msg.role === 'USER',
            'is-ai': msg.role === 'ASSISTANT',
          }"
        >
          <div class="message-avatar">
            {{ msg.role === "USER" ? "👤" : "🤖" }}
          </div>
          <div class="message-content">
            <div class="message-role">
              {{ msg.role === "USER" ? "用户" : "AI助手" }}
            </div>
            <div class="message-text">{{ msg.content }}</div>
          </div>
        </div>
        <div v-if="!messageList.length" class="empty-message">暂无对话记录</div>
      </div>
      <a-spin v-else />
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { notification } from "ant-design-vue";
import {
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
} from "@ant-design/icons-vue";
import { getChatSessionList, getChatMessageList } from "../../api/ai-chat";

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
// 消息加载状态
const messageLoading = ref(false);
// 会话列表
const chatSessions = ref([]);
// 消息列表
const messageList = ref([]);

// 详情弹窗
const detailVisible = ref(false);
const currentSession = ref(null);

// 状态映射
const getStatusColor = (status) => {
  const colors = {
    0: "blue",
    1: "green",
    2: "gray",
  };
  return colors[status] || "default";
};

const getStatusText = (status) => {
  const texts = {
    0: "进行中",
    1: "已完成",
    2: "已关闭",
  };
  return texts[status] || status || "-";
};

// 表格列定义
const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "会话UUID", dataIndex: "sessionUuid", width: 220, ellipsis: true },
  { title: "会员ID", dataIndex: "userId", width: 100 },
  { title: "消息数", dataIndex: "messageCount", width: 80 },
  { title: "状态", dataIndex: "status", width: 100 },
  { title: "创建时间", dataIndex: "createdAt", width: 180 },
  { title: "操作", dataIndex: "operation", width: 100, fixed: "right" },
];

// 查询列表
const handleQuery = (param) => {
  if (!param) {
    param = { page: pagination.current, size: pagination.pageSize };
  }
  loading.value = true;
  chatSessions.value = [];

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

  getChatSessionList(params)
    .then((data) => {
      loading.value = false;
      chatSessions.value = data.content?.list || [];
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
const viewDetail = async (record) => {
  currentSession.value = record;
  detailVisible.value = true;
  messageList.value = [];
  messageLoading.value = true;

  try {
    // 获取会话UUID
    const sessionUuid = record.sessionUuid || record.sessionId;
    const data = await getChatMessageList(sessionUuid);
    messageList.value = data.content || [];
  } catch (err) {
    console.error("获取消息列表失败", err);
    notification.error({ description: "获取消息列表失败" });
  } finally {
    messageLoading.value = false;
  }
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.ai-chat-page {
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
  background: linear-gradient(
    90deg,
    var(--primary-color),
    var(--accent-color),
    var(--primary-light)
  );
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
  font-family: "Monaco", "Consolas", monospace;
  font-size: 13px;
  color: var(--text-secondary);
}

/* 会话信息 */
.session-info {
  margin-bottom: 16px;
}

/* 消息列表标题 */
.message-list-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid var(--border-color);
}

/* 对话详情 */
.chat-detail {
  max-height: 400px;
  overflow-y: auto;
  padding: 8px;
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  padding: 12px;
  border-radius: var(--radius-medium);
  background: var(--primary-lighter);
}

.message-item.is-ai {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
  box-shadow: 0 2px 6px var(--shadow-color);
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-role {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.message-text {
  font-size: 14px;
  color: var(--text-primary);
  line-height: 1.6;
  word-break: break-all;
  white-space: pre-wrap;
}

.empty-message {
  text-align: center;
  color: var(--text-light);
  padding: 40px;
}
</style>

<style>
/* 详情弹窗样式 */
.detail-modal .ant-modal-content {
  border-radius: var(--radius-large);
  overflow: hidden;
}

.detail-modal .ant-modal-header {
  background: linear-gradient(
    135deg,
    var(--primary-lighter),
    var(--bg-card)
  ) !important;
  padding: 20px 24px !important;
}

.detail-modal .ant-modal-title {
  font-weight: 600;
  color: var(--text-primary);
}
</style>
