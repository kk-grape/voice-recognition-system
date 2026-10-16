<template>
  <div class="order-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">💳</span>
          订单管理
        </h2>
        <p class="page-desc">管理系统中的订单信息，查看交易记录</p>
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
        <a-form-item label="订单号">
          <a-input
            v-model:value="searchForm.orderNo"
            placeholder="请输入订单号"
            style="width: 200px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="会员ID">
          <a-input
            v-model:value="searchForm.memberId"
            placeholder="请输入会员ID"
            style="width: 150px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="订单状态">
          <a-select
            v-model:value="searchForm.orderStatus"
            placeholder="请选择状态"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="I">初始</a-select-option>
            <a-select-option value="P">处理中</a-select-option>
            <a-select-option value="S">成功</a-select-option>
            <a-select-option value="F">失败</a-select-option>
            <a-select-option value="C">已取消</a-select-option>
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
      :dataSource="orders"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
      class="custom-table"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'orderStatus'">
          <a-tag :color="getStatusColor(record.orderStatus)">
            {{ record.orderStatusName }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'channel'">
          <span>{{ record.channelName || record.channel || "-" }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'amount'">
          <span class="amount-text">¥{{ record.amount }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'orderAt'">
          <span class="time-text">{{ formatTime(record.orderAt) }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="showDetail(record)">
              查看详情
            </a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 订单详情弹窗 -->
    <a-modal
      v-model:open="detailModalVisible"
      title="订单详情"
      :footer="null"
      width="600px"
    >
      <a-descriptions :column="2" bordered size="small">
        <a-descriptions-item label="订单ID">{{
          currentOrder.id
        }}</a-descriptions-item>
        <a-descriptions-item label="订单号">{{
          currentOrder.orderNo
        }}</a-descriptions-item>
        <a-descriptions-item label="会员ID">{{
          currentOrder.memberId
        }}</a-descriptions-item>
        <a-descriptions-item label="订单状态">
          <a-tag :color="getStatusColor(currentOrder.orderStatus)">
            {{ currentOrder.orderStatusName }}
          </a-tag>
        </a-descriptions-item>
        <a-descriptions-item label="支付渠道">
          {{ currentOrder.channelName || "-" }}
        </a-descriptions-item>
        <a-descriptions-item label="订单金额">
          <span class="amount-text">¥{{ currentOrder.amount }}</span>
        </a-descriptions-item>
        <a-descriptions-item label="创建时间" :span="2">{{
          formatTime(currentOrder.orderAt)
        }}</a-descriptions-item>
        <a-descriptions-item label="订单类型" :span="2">{{
          currentOrder.orderTypeName
        }}</a-descriptions-item>
        <a-descriptions-item label="订单描述" :span="2">{{
          currentOrder.orderDesc
        }}</a-descriptions-item>
      </a-descriptions>
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
import { getOrderList, getOrderDetail } from "../../api/order";

// 搜索表单
const searchForm = reactive({
  orderNo: "",
  memberId: "",
  orderStatus: "",
});

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
});

// 加载状态
const loading = ref(false);
// 订单列表
const orders = ref([]);

// 详情弹窗
const detailModalVisible = ref(false);
const currentOrder = ref({});

// 订单状态映射（API返回的status值）
const STATUS_MAP = {
  I: { text: "初始", color: "default" },
  P: { text: "处理中", color: "orange" },
  S: { text: "支付成功", color: "green" },
  F: { text: "失败", color: "red" },
  C: { text: "已取消", color: "gray" },
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

const getStatusColor = (status) => STATUS_MAP[status]?.color || "default";
const getStatusText = (status) => STATUS_MAP[status]?.text || status;

// 表格列定义（使用API返回的实际字段名）
const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "订单号", dataIndex: "orderNo", width: 200, ellipsis: true },
  { title: "会员ID", dataIndex: "memberId", width: 100 },
  { title: "订单状态", dataIndex: "orderStatus", width: 100 },
  { title: "支付渠道", dataIndex: "channel", width: 100 },
  { title: "订单金额", dataIndex: "amount", width: 100 },
  { title: "创建时间", dataIndex: "orderAt", width: 180 },
  { title: "操作", dataIndex: "operation", width: 100, fixed: "right" },
];

// 查询列表
const handleQuery = (param) => {
  if (!param) {
    param = { page: pagination.current, size: pagination.pageSize };
  }
  loading.value = true;
  orders.value = [];

  // 构建查询参数
  const params = {
    page: param.page,
    size: param.size,
  };

  // 添加搜索条件
  if (searchForm.orderNo) {
    params.orderNo = searchForm.orderNo;
  }
  if (searchForm.memberId) {
    params.memberId = searchForm.memberId;
  }
  if (searchForm.orderStatus) {
    params.orderStatus = searchForm.orderStatus;
  }

  getOrderList(params)
    .then((data) => {
      console.log("订单列表返回:", data);
      loading.value = false;
      // 尝试从不同路径获取列表数据
      orders.value = data.content?.list || data.content || [];
      pagination.current = param.page;
      pagination.total = data.content?.total || data.content?.length || 0;
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
  searchForm.orderNo = "";
  searchForm.memberId = "";
  searchForm.orderStatus = "";
  pagination.current = 1;
  handleQuery({ page: 1, size: pagination.pageSize });
};

// 分页变化
const handleTableChange = (pag) => {
  handleQuery({ page: pag.current, size: pag.pageSize });
};

// 查看详情
const showDetail = (record) => {
  currentOrder.value = record;
  detailModalVisible.value = true;
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.order-page {
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

/* 金额样式 */
.amount-text {
  font-weight: 600;
  color: #f5222d;
  font-size: 14px;
}

/* 时间样式 */
.time-text {
  font-family: "Monaco", "Consolas", monospace;
  font-size: 13px;
  color: var(--text-secondary);
}

/* 表格样式 */
.custom-table {
  margin-top: 16px;
}
</style>
