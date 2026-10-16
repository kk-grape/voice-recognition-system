<template>
  <div class="member-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2 class="page-title">
          <span class="title-icon">👥</span>
          会员管理
        </h2>
        <p class="page-desc">管理系统中的会员信息，了解用户注册情况</p>
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
        <a-form-item label="手机号">
          <a-input
            v-model:value="searchForm.mobile"
            placeholder="请输入手机号"
            style="width: 150px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input
            v-model:value="searchForm.nickname"
            placeholder="请输入昵称"
            style="width: 150px"
            allow-clear
            @pressEnter="handleSearch"
          />
        </a-form-item>
        <a-form-item label="会员等级">
          <a-select
            v-model:value="searchForm.memberLevel"
            placeholder="请选择等级"
            style="width: 140px"
            allow-clear
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option :value="0">普通会员</a-select-option>
            <a-select-option :value="1">高级会员</a-select-option>
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
      :dataSource="members"
      :columns="columns"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
      class="custom-table"
      :scroll="{ x: 1200 }"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'memberLevel'">
          <a-tag :color="getLevelColor(record.memberLevel)">
            {{ getLevelText(record.memberLevel) }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'status'">
          <a-tag :color="record.status === 'N' ? 'green' : 'red'">
            {{ record.status === "N" ? "正常" : "禁用" }}
          </a-tag>
        </template>
        <template v-else-if="column.dataIndex === 'createdAt'">
          <span class="time-text">{{ record.createdAt }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'memberExpireAt'">
          <span class="time-text">{{ record.memberExpireAt || "永久" }}</span>
        </template>
        <template v-else-if="column.dataIndex === 'operation'">
          <a-space>
            <a-button type="link" size="small" @click="showEditModal(record)">
              修改等级
            </a-button>
            <!-- <a-button type="link" size="small" @click="showExpireModal(record)">
              调整时间
            </a-button> -->
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 修改会员等级弹窗 -->
    <a-modal
      v-model:open="editModalVisible"
      title="修改会员等级"
      @ok="handleEditSubmit"
      :confirmLoading="editLoading"
    >
      <a-form :form="editForm" layout="vertical">
        <a-form-item label="会员ID">
          <a-input :value="currentMember.id" disabled />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input :value="currentMember.mobile" disabled />
        </a-form-item>
        <a-form-item label="昵称">
          <a-input :value="currentMember.nickname" disabled />
        </a-form-item>
        <a-form-item label="会员等级" name="memberLevel">
          <a-select
            v-model:value="editFormData.memberLevel"
            placeholder="请选择会员等级"
          >
            <a-select-option :value="0">普通会员</a-select-option>
            <a-select-option :value="1">高级会员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 调整到期时间弹窗 -->
    <a-modal
      v-model:open="expireModalVisible"
      title="调整会员到期时间"
      @ok="handleExpireSubmit"
      :confirmLoading="expireLoading"
    >
      <a-form layout="vertical">
        <a-form-item label="会员ID">
          <a-input :value="currentMember.id" disabled />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input :value="currentMember.mobile" disabled />
        </a-form-item>
        <a-form-item label="当前到期时间">
          <a-input :value="currentMember.memberExpireAt || '永久'" disabled />
        </a-form-item>
        <a-form-item label="设置到期时间" name="memberExpireAt">
          <a-date-picker
            v-model:value="editExpireDate"
            show-time
            format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择到期时间"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { notification, message } from "ant-design-vue";
import {
  ReloadOutlined,
  SearchOutlined,
  ClearOutlined,
} from "@ant-design/icons-vue";
import {
  getMemberList,
  updateMemberStatus,
  updateMemberExpire,
} from "../../api/member";

// 搜索表单
const searchForm = reactive({
  mobile: "",
  nickname: "",
  memberLevel: undefined,
});

// 分页配置
const pagination = reactive({
  total: 0,
  current: 1,
  pageSize: 10,
});

// 加载状态
const loading = ref(false);
// 会员列表
const members = ref([]);

// 编辑弹窗
const editModalVisible = ref(false);
const editLoading = ref(false);
const currentMember = ref({});
const editFormData = reactive({
  memberLevel: 1,
});
const editForm = ref(null);

// 到期时间弹窗
const expireModalVisible = ref(false);
const expireLoading = ref(false);
const editExpireDate = ref(null);

// 会员等级映射
const getLevelColor = (level) => {
  const colors = {
    0: "gold",
    1: "purple",
  };
  return colors[level] || "default";
};

const getLevelText = (level) => {
  const texts = {
    0: "普通会员",
    1: "高级会员",
  };
  return texts[level] || level;
};

// 表格列定义
const columns = [
  { title: "ID", dataIndex: "id", width: 80 },
  { title: "手机号", dataIndex: "mobile", width: 130 },
  { title: "昵称", dataIndex: "nickname", width: 150, ellipsis: true },
  { title: "会员等级", dataIndex: "memberLevel", width: 100 },
  { title: "状态", dataIndex: "status", width: 80 },
  { title: "到期时间", dataIndex: "memberExpireAt", width: 180 },
  { title: "注册时间", dataIndex: "createdAt", width: 180 },
  { title: "操作", dataIndex: "operation", width: 180, fixed: "right" },
];

// 查询列表
const handleQuery = (param) => {
  if (!param) {
    param = { page: pagination.current, size: pagination.pageSize };
  }
  loading.value = true;
  members.value = [];

  // 构建查询参数
  const params = {
    page: param.page,
    size: param.size,
  };

  if (searchForm.mobile) {
    params.mobile = searchForm.mobile;
  }
  if (searchForm.nickname) {
    params.nickname = searchForm.nickname;
  }
  if (searchForm.memberLevel !== undefined && searchForm.memberLevel !== "") {
    params.memberLevel = searchForm.memberLevel;
  }

  getMemberList(params)
    .then((data) => {
      loading.value = false;
      members.value = data.content?.list || [];
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
  searchForm.mobile = "";
  searchForm.nickname = "";
  searchForm.memberLevel = undefined;
  pagination.current = 1;
  handleQuery({ page: 1, size: pagination.pageSize });
};

// 分页变化
const handleTableChange = (pag) => {
  handleQuery({ page: pag.current, size: pag.pageSize });
};

// 显示编辑弹窗
const showEditModal = (record) => {
  currentMember.value = record;
  editFormData.memberLevel = record.memberLevel;
  editModalVisible.value = true;
};

// 提交编辑
const handleEditSubmit = async () => {
  if (editFormData.memberLevel === undefined) {
    message.warning("请选择会员等级");
    return;
  }

  editLoading.value = true;
  try {
    await updateMemberStatus(currentMember.value.id, editFormData.memberLevel);
    message.success("修改成功");
    editModalVisible.value = false;
    handleQuery({ page: pagination.current, size: pagination.pageSize });
  } catch (err) {
    console.error("修改失败", err);
    message.error("修改失败，请重试");
  } finally {
    editLoading.value = false;
  }
};

// 显示到期时间弹窗
const showExpireModal = (record) => {
  currentMember.value = record;
  editExpireDate.value = null;
  expireModalVisible.value = true;
};

// 提交到期时间
const handleExpireSubmit = async () => {
  if (!editExpireDate.value) {
    message.warning("请选择到期时间");
    return;
  }

  expireLoading.value = true;
  try {
    // 格式化日期
    const dateStr = editExpireDate.value.format("YYYY-MM-DD HH:mm:ss");
    await updateMemberExpire(currentMember.value.id, dateStr);
    message.success("调整成功");
    expireModalVisible.value = false;
    handleQuery({ page: pagination.current, size: pagination.pageSize });
  } catch (err) {
    console.error("调整失败", err);
    message.error("调整失败，请重试");
  } finally {
    expireLoading.value = false;
  }
};

onMounted(() => {
  handleQuery();
});
</script>

<style scoped>
.member-page {
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

/* 时间样式 */
.time-text {
  font-family: "Monaco", "Consolas", monospace;
  font-size: 13px;
  color: var(--text-secondary);
}
</style>
