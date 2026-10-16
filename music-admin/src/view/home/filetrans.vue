<template>
  <div class="filetrans-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">🎤</span>
          语音识别
        </h2>
        <p class="page-desc">管理语音转字幕任务，查看识别结果</p>
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
        <a-form-item label="文件名">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入文件名"
            style="width: 180px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="会员ID">
          <a-input
            v-model:value="searchForm.memberId"
            placeholder="请输入会员ID"
            style="width: 120px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="语种">
          <a-select
            v-model:value="searchForm.lang"
            placeholder="请选择语种"
            style="width: 140px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="dxksRAMqw2qWxxOG">普通话</a-select-option>
            <a-select-option value="kH1y8ns3kk8lXxr0">英语</a-select-option>
            <a-select-option value="J8hJxw8XQWUzRVkA">中英混合</a-select-option>
            <a-select-option value="yaXsjWbzmAaiHiGD">日语</a-select-option>
            <a-select-option value="e5us7Q2KNRkJihfK">泰语</a-select-option>
            <a-select-option value="fguvKlRZkpVXZY3W">韩语</a-select-option>
            <a-select-option value="v6gsHNemSH95SGGX">粤语</a-select-option>
            <a-select-option value="U8r9efQeREfJkg15">四川话</a-select-option>
            <a-select-option value="zTw21e2aAyJzxrSp">河南话</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="I">初始</a-select-option>
            <a-select-option value="SI">待生成字幕</a-select-option>
            <a-select-option value="SP">生成字幕中</a-select-option>
            <a-select-option value="SS">生成字幕成功</a-select-option>
            <a-select-option value="SF">生成字幕失败</a-select-option>
            <a-select-option value="D">失效</a-select-option>
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
      :dataSource="filetranss"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
      class="custom-table"
      :scroll="{ x: 1400 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'name'">
          <span class="file-name">{{ record.name }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="getStatusColor(record.status)">
            {{ getStatusText(record.status) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'payStatus'">
          <a-tag :color="getPayStatusColor(record.payStatus)">
            {{ getPayStatusText(record.payStatus) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'lang'">
          {{ getLangText(record.lang) }}
        </template>
        <template v-else-if="column.dataIndex === 'second'">
          {{ formatSecond(record.second) }}
        </template>
        <template v-else-if="column.dataIndex === 'createdAt'">
          <span class="time-text">{{ record.createdAt }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button
              v-if="record.status === 'SS'"
              type="link"
              size="small"
              @click="showSubtitleModal(record)"
            >
              查看字幕
            </a-button>
            <a-popconfirm
              title="确定要删除这条记录吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record)"
            >
              <a-button type="link" danger size="small">
                删除
              </a-button>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 字幕详情弹窗 -->
    <filetrans-subtitle ref="filetransSubtitleCom"></filetrans-subtitle>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { notification, message } from "ant-design-vue";
import { ReloadOutlined, SearchOutlined, ClearOutlined } from "@ant-design/icons-vue";
import FiletransSubtitle from "./filetrans-subtitle.vue";
import { getFiletransList, deleteFiletrans } from "../../api/filetrans";

const filetransSubtitleCom = ref();

const showSubtitleModal = (filetrans) => {
  filetransSubtitleCom.value.showModal(filetrans);
};

// 搜索表单
const searchForm = reactive({
  name: "",
  memberId: "",
  lang: "",
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
// 语音识别列表
const filetranss = ref([]);

// 状态映射
const STATUS_MAP = {
  "I": { text: "初始", color: "default" },
  "SI": { text: "待生成字幕", color: "orange" },
  "SP": { text: "生成字幕中", color: "blue" },
  "SS": { text: "生成字幕成功", color: "green" },
  "SF": { text: "生成字幕失败", color: "red" },
  "D": { text: "失效", color: "gray" },
};

const PAY_STATUS_MAP = {
  "I": { text: "未支付", color: "default" },
  "P": { text: "处理中", color: "orange" },
  "S": { text: "支付成功", color: "green" },
  "F": { text: "支付失败", color: "red" },
};

const LANG_MAP = {
  "dxksRAMqw2qWxxOG": "普通话",
  "kH1y8ns3kk8lXxr0": "英语",
  "J8hJxw8XQWUzRVkA": "中英混合",
  "yaXsjWbzmAaiHiGD": "日语",
  "e5us7Q2KNRkJihfK": "泰语",
  "fguvKlRZkpVXZY3W": "韩语",
  "v6gsHNemSH95SGGX": "粤语",
  "U8r9efQeREfJkg15": "四川话",
  "zTw21e2aAyJzxrSp": "河南话",
};

const getStatusColor = (status) => STATUS_MAP[status]?.color || "default";
const getStatusText = (status) => STATUS_MAP[status]?.text || status;
const getPayStatusColor = (status) => PAY_STATUS_MAP[status]?.color || "default";
const getPayStatusText = (status) => PAY_STATUS_MAP[status]?.text || status;
const getLangText = (lang) => LANG_MAP[lang] || lang || "-";

// 表格列定义
const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "文件名", dataIndex: "name", width: 200, ellipsis: true },
  { title: "会员ID", dataIndex: "memberId", width: 80 },
  { title: "支付状态", dataIndex: "payStatus", width: 100 },
  { title: "状态", dataIndex: "status", width: 120 },
  { title: "语言", dataIndex: "lang", width: 100 },
  { title: "时长", dataIndex: "second", width: 100 },
  { title: "创建时间", dataIndex: "createdAt", width: 180 },
  { title: "操作", dataIndex: "operation", width: 180, fixed: "right" },
];

// 查询列表
const handleQuery = (param) => {
  if (!param) {
    param = {
      page: pagination.current,
      size: pagination.pageSize,
    };
  }
  loading.value = true;
  filetranss.value = [];

  // 构建查询参数
  const params = {
    page: param.page,
    size: param.size,
  };

  if (searchForm.name) {
    params.name = searchForm.name;
  }
  if (searchForm.memberId) {
    params.memberId = searchForm.memberId;
  }
  if (searchForm.lang) {
    params.lang = searchForm.lang;
  }
  if (searchForm.status) {
    params.status = searchForm.status;
  }

  getFiletransList(params)
    .then((data) => {
      loading.value = false;
      filetranss.value = data.content?.list || [];
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
  searchForm.name = "";
  searchForm.memberId = "";
  searchForm.lang = "";
  searchForm.status = "";
  pagination.current = 1;
  handleQuery({ page: 1, size: pagination.pageSize });
};

// 分页变化
const handleTableChange = (pag) => {
  handleQuery({
    page: pag.current,
    size: pag.pageSize,
  });
};

// 删除记录
const handleDelete = async (record) => {
  try {
    await deleteFiletrans(record.id);
    message.success("删除成功");
    handleQuery({ page: pagination.current, size: pagination.pageSize });
  } catch (err) {
    console.error("删除失败", err);
    message.error("删除失败，请重试");
  }
};

// 格式化时长
const formatSecond = (second) => {
  if (!second) return "-";
  second = parseInt(second);
  const hours = Math.floor(second / 3600);
  const minutes = Math.floor((second % 3600) / 60);
  const seconds = second % 60;
  let result = "";
  if (hours > 0) result += hours + "时";
  if (minutes > 0) result += minutes + "分";
  if (seconds > 0 || result === "") result += seconds + "秒";
  return result;
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.filetrans-page {
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

/* 文件名样式 */
.file-name {
  font-weight: 500;
  color: var(--text-primary);
}

/* 时间样式 */
.time-text {
  font-family: 'Monaco', 'Consolas', monospace;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
