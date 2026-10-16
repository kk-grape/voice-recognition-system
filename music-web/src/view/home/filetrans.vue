<template>
  <filetrans-upload
    ref="filetransUploadCom"
    @afterPay="handleQuery"
  ></filetrans-upload>
  <filetrans-subtitle ref="filetransSubtitleCom"></filetrans-subtitle>

  <!-- 任务详情抽屉 -->
  <a-drawer
    v-model:open="detailOpen"
    title="识别任务详情"
    width="480"
    :body-style="{ paddingBottom: '24px' }"
  >
    <a-descriptions
      v-if="currentRecord"
      :column="1"
      size="small"
      bordered
    >
      <a-descriptions-item label="名称">
        {{ currentRecord.name }}
      </a-descriptions-item>
      <a-descriptions-item label="支付状态">
        <a-tag :color="getPayStatusTagColor(currentRecord.payStatus)">
          {{
            getPayStatusDesc(currentRecord.payStatus) || "未知"
          }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="识别状态">
        <a-tag :color="getStatusTagColor(currentRecord.status)">
          {{
            getStatusDesc(currentRecord.status) || "未知"
          }}
        </a-tag>
      </a-descriptions-item>
      <a-descriptions-item label="语言">
        {{
          getLangDesc(currentRecord.lang)
        }}
      </a-descriptions-item>
      <a-descriptions-item label="时长">
        {{ formatSecond(currentRecord.second) }}
      </a-descriptions-item>
      <a-descriptions-item label="金额">
        {{ currentRecord.amount }} 元
      </a-descriptions-item>
      <a-descriptions-item label="音频地址">
        <a
          v-if="currentRecord.audio"
          :href="currentRecord.audio"
          target="_blank"
        >
          打开音频
        </a>
        <span v-else>-</span>
      </a-descriptions-item>
      <a-descriptions-item label="识别状态描述">
        {{ currentRecord.transStatusText || "-" }}
      </a-descriptions-item>
      <a-descriptions-item label="提交时间">
        {{ formatDateTime(currentRecord.createdAt) }}
      </a-descriptions-item>
      <a-descriptions-item label="识别开始时间">
        {{ formatDateTime(currentRecord.transTime) }}
      </a-descriptions-item>
      <a-descriptions-item label="识别完成时间">
        {{ formatDateTime(currentRecord.solveTime) }}
      </a-descriptions-item>
    </a-descriptions>
  </a-drawer>

  <a-alert :message="null" type="success">
    <template #description>
      1. 上传音频，生成srt字幕文件、txt文本文件，支持多国语言
      <br />
      2. <b>为保障用户数据不泄露，用户上传的音频将在10天后系统自动删除。</b>
    </template>
  </a-alert>
  <br />
  <p>
    <a-space>
      <a-button type="primary" @click="showModal">开始上传音频</a-button>
      <a-button type="primary" @click="handleQuery()">刷新列表</a-button>
    </a-space>
  </p>
  <p style="margin-top: 8px">
    <a-form layout="inline">
      <a-form-item label="名称">
        <a-input
          v-model:value="searchForm.name"
          placeholder="请输入名称关键字"
          allow-clear
          style="width: 200px"
        />
      </a-form-item>
      <a-form-item label="状态">
        <a-select
          v-model:value="searchForm.status"
          placeholder="全部"
          allow-clear
          style="width: 180px"
        >
          <a-select-option
            v-for="item in FILETRANS_STATUS_ARRAY"
            :key="item.code"
            :value="item.code"
          >
            {{ item.desc }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="语言">
        <a-select
          v-model:value="searchForm.lang"
          placeholder="全部"
          allow-clear
          style="width: 160px"
        >
          <a-select-option
            v-for="item in FILETRANS_LANG_ARRAY"
            :key="item.code"
            :value="item.code"
          >
            {{ item.desc }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-space>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button @click="resetSearch">重置</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </p>
  <a-table
    :dataSource="filetranss"
    :columns="columns"
    :pagination="pagination"
    @change="handleTableChange"
    :loading="loading"
  >
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'status'">
        <a-tag :color="getStatusTagColor(record.status)">
          {{ getStatusDesc(record.status) }}
        </a-tag>
      </template>
      <template v-else-if="column.dataIndex === 'payStatus'">
        <a-tag :color="getPayStatusTagColor(record.payStatus)">
          {{ getPayStatusDesc(record.payStatus) }}
        </a-tag>
      </template>
      <template v-else-if="column.dataIndex === 'lang'">
        {{ getLangDesc(record.lang) }}
      </template>
      <template v-else-if="column.dataIndex === 'second'">
        {{ formatSecond(record.second) }}
      </template>
      <template v-else-if="column.dataIndex === 'operation'">
        <a-space>
          <a-button type="link" size="small" @click="showDetail(record)">
            详情
          </a-button>
          <a-popconfirm
            title="确认删除该任务？删除后将从列表中隐藏。"
            ok-text="删除"
            cancel-text="取消"
            @confirm="handleDelete(record)"
          >
            <a-button type="link" danger size="small">删除</a-button>
          </a-popconfirm>
          <a-button
            v-if="record.status === FILETRANS_STATUS.SUBTITLE_SUCCESS.code"
            type="primary"
            @click="showSubtitleModal(record)"
            size="small"
          >
            查看字幕
          </a-button>
        </a-space>
      </template>
    </template>
  </a-table>
</template>
<script setup>
import FiletransUpload from "./filetrans-upload.vue";
import { ref } from "vue";
import axios from "axios";
import { notification } from "ant-design-vue";
import FiletransSubtitle from "./filetrans-subtitle.vue";

const filetransUploadCom = ref();
const filetransSubtitleCom = ref();

const showModal = () => {
  filetransUploadCom.value.showModal();
};
const showSubtitleModal = (filetrans) => {
  filetransSubtitleCom.value.showModal(filetrans);
};

//----------------- 列表查询 -----------------
// 分页的三个属性名是固定的
const pagination = ref({
  total: 0,
  current: 1,
  pageSize: 10,
});
const loading = ref(false);
const filetranss = ref([]);
const FILETRANS_STATUS = window.FILETRANS_STATUS;
const FILETRANS_PAY_STATUS = window.FILETRANS_PAY_STATUS;
const FILETRANS_LANG = window.FILETRANS_LANG;
const FILETRANS_STATUS_ARRAY = window.FILETRANS_STATUS_ARRAY || [];
const FILETRANS_LANG_ARRAY = window.FILETRANS_LANG_ARRAY || [];

// 查询条件
const searchForm = ref({
  name: "",
  lang: undefined,
  status: undefined,
});

// 详情抽屉
const detailOpen = ref(false);
const currentRecord = ref();

const showDetail = (record) => {
  currentRecord.value = record;
  detailOpen.value = true;
};

const columns = [
  {
    title: "名称",
    dataIndex: "name",
  },
  {
    title: "支付状态",
    dataIndex: "payStatus",
  },
  {
    title: "状态",
    dataIndex: "status",
  },
  {
    title: "语言",
    dataIndex: "lang",
  },
  {
    title: "时长",
    dataIndex: "second",
  },
  {
    title: "操作",
    dataIndex: "operation",
  },
];

const handleQuery = (param) => {
  if (!param) {
    param = {
      page: 1,
      size: pagination.value.pageSize,
    };
  }
  loading.value = true;
  axios
    .get("/web/filetrans/query", {
      params: {
        page: param.page,
        size: param.size,
        name: searchForm.value.name,
        lang: searchForm.value.lang,
        status: searchForm.value.status,
      },
    })
    .then((response) => {
      loading.value = false;
      const data = response.data;
      if (data.success) {
        filetranss.value = data.content.list;
        // 设置分页控件的值
        pagination.value.current = param.page;
        pagination.value.total = data.content.total;
      } else {
        notification.error({ description: data.message });
      }
    })
    .catch(() => {
      loading.value = false;
      notification.error({ description: "查询失败，请稍后重试" });
    });
};

const handleSearch = () => {
  handleQuery({
    page: 1,
    size: pagination.value.pageSize,
  });
};

const resetSearch = () => {
  searchForm.value = {
    name: "",
    lang: undefined,
    status: undefined,
  };
  handleQuery({
    page: 1,
    size: pagination.value.pageSize,
  });
};

const handleTableChange = (pagination) => {
  handleQuery({
    page: pagination.current,
    size: pagination.pageSize,
  });
};

/**
 * 时间格式化，将"秒"格式化成"时:分:秒"
 * @param {number} second - 秒数
 * @returns {string}
 */
const formatSecond = (second) => {
  return Tool.formatSecond(second);
};

/**
 * 根据识别状态编码返回标签颜色
 */
const getStatusTagColor = (status) => {
  switch (status) {
    case FILETRANS_STATUS.SUBTITLE_PENDING.code:
    case FILETRANS_STATUS.SUBTITLE_INIT.code:
      return "processing";
    case FILETRANS_STATUS.SUBTITLE_SUCCESS.code:
      return "success";
    case FILETRANS_STATUS.SUBTITLE_FAILURE.code:
      return "error";
    case FILETRANS_STATUS.INIT.code:
      return "default";
    case FILETRANS_STATUS.DELETED.code:
      return "default";
    default:
      return "default";
  }
};

/**
 * 根据支付状态编码返回标签颜色
 */
const getPayStatusTagColor = (payStatus) => {
  switch (payStatus) {
    case FILETRANS_PAY_STATUS.P.code:
      return "processing";
    case FILETRANS_PAY_STATUS.S.code:
      return "success";
    case FILETRANS_PAY_STATUS.F.code:
      return "error";
    case FILETRANS_PAY_STATUS.I.code:
    default:
      return "default";
  }
};

/**
 * 根据识别状态编码返回描述
 * @param {string|number} status - 状态编码
 * @returns {string}
 */
const getStatusDesc = (status) => {
  const item = Object.values(FILETRANS_STATUS).find(
    (item) => item.code === status
  );
  return item?.desc || "";
};

/**
 * 根据支付状态编码返回描述
 * @param {string|number} payStatus - 支付状态编码
 * @returns {string}
 */
const getPayStatusDesc = (payStatus) => {
  const item = Object.values(FILETRANS_PAY_STATUS).find(
    (item) => item.code === payStatus
  );
  return item?.desc || "";
};

/**
 * 根据语言编码返回描述
 * @param {string|number} lang - 语言编码
 * @returns {string}
 */
const getLangDesc = (lang) => {
  const item = Object.values(FILETRANS_LANG).find(
    (item) => item.code === lang
  );
  return item?.desc || "-";
};

const formatDateTime = (value) => {
  if (!value) {
    return "-";
  }
  try {
    return new Date(value).toLocaleString();
  } catch (e) {
    return value;
  }
};

// 删除任务
const handleDelete = (record) => {
  loading.value = true;
  axios
    .delete(`/web/filetrans/delete/${record.id}`)
    .then((response) => {
      loading.value = false;
      const data = response.data;
      if (data.success) {
        notification.success({ description: "删除成功" });
        handleQuery({
          page: pagination.value.current,
          size: pagination.value.pageSize,
        });
      } else {
        notification.error({ description: data.message });
      }
    })
    .catch(() => {
      loading.value = false;
      notification.error({ description: "删除失败，请稍后重试" });
    });
};

handleQuery();
</script>
