<template>
  <a-modal
    v-model:open="open"
    title=""
    @ok="handleOk"
    footer=""
    style="width: 800px; top: 15%"
  >
    <p>
      <a-space>
        <a-button type="primary" @click="genSubtitle">
          下载字幕<FileTextOutlined />
        </a-button>
        <a-button type="primary" @click="genText">
          下载纯文本<FileTextOutlined />
        </a-button>
      </a-space>
    </p>

    <a-table
      :dataSource="subtitles"
      :columns="columns"
      :pagination="false"
      :loading="loading"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'time'">
          {{ formatMs(record.begin) }} ~ {{ formatMs(record.end) }}
        </template>
        <template v-else-if="column.dataIndex === 'text'">
          <div
            v-if="editingId !== record.id"
            @dblclick="enterEdit(record)"
            style="cursor: text; min-height: 32px; display: flex; align-items: center; padding: 4px 0; width: 100%"
          >
            {{ record.text }}
          </div>
          <a-input
            v-else
            v-model:value="record.text"
            :maxlength="500"
            allow-clear
          />
        </template>
      </template>
    </a-table>
    <div style="margin-top: 16px; display: flex; justify-content: space-between; align-items: center">
      <div style="display: flex; align-items: center">
        <span style="color: #666; font-size: 14px">双击字幕可以编辑</span>
        <a-button
          v-if="isEditing"
          type="primary"
          style="margin-left: 12px"
          @click="saveSubtitles"
        >
          保存修改
        </a-button>
      </div>
      <div>
        <a-pagination
          v-model:current="pagination.current"
          :total="pagination.total"
          :page-size="pagination.pageSize"
          :show-size-changer="false"
          :show-quick-jumper="false"
          @change="handlePaginationChange"
        />
      </div>
    </div>
  </a-modal>

  <the-download ref="theDownloadCom"></the-download>
</template>

<script setup>
import { ref } from "vue";
import { message, notification } from "ant-design-vue";
import axios from "axios";
import TheDownload from "../../components/the-download.vue";

const open = ref(false);
const filetrans = ref();
filetrans.value = {};

const showModal = (_filetrans) => {
  filetrans.value = _filetrans;
  handleQuery();
  open.value = true;
};
const handleOk = () => {
  open.value = false;
};

//----------------- 列表查询 -----------------
// 分页的三个属性名是固定的
const pagination = ref({
  total: 0,
  current: 1,
  pageSize: 5,
});
const loading = ref(false);
const subtitles = ref();
subtitles.value = [];

// 编辑状态控制
const isEditing = ref(false);
const editingId = ref(null);

// 退出编辑状态
const exitEdit = () => {
  isEditing.value = false;
  editingId.value = null;
};

const columns = [
  {
    title: "时间范围",
    dataIndex: "time",
  },
  {
    title: "字幕",
    dataIndex: "text",
  },
];

const enterEdit = (record) => {
  // 如果当前记录已经在编辑状态，再次双击则退出编辑
  if (editingId.value === record.id) {
    exitEdit();
  } else {
    // 否则进入编辑状态
    isEditing.value = true;
    editingId.value = record.id;
  }
};

const handleQuery = (param) => {
  if (!param) {
    param = {
      page: 1,
      size: pagination.value.pageSize,
    };
  }
  loading.value = true;
  // 切换分页或重新查询时，退出编辑状态
  exitEdit();
  subtitles.value = [];
  axios
    .get("/web/filetrans-subtitle/query", {
      params: {
        page: param.page,
        size: param.size,
        filetransId: filetrans.value.id,
      },
    })
    .then((response) => {
      loading.value = false;
      let data = response.data;
      if (data.success) {
        subtitles.value = data.content.list;
        // 设置分页控件的值
        pagination.value.current = param.page;
        pagination.value.total = data.content.total;
      } else {
        notification.error({ description: data.message });
      }
    })
    .catch((error) => {
      loading.value = false;
      console.error("查询字幕失败：", error);
      notification.error({ description: "查询字幕失败，请稍后重试" });
    });
};

const handlePaginationChange = (page) => {
  handleQuery({
    page: page,
    size: pagination.value.pageSize,
  });
};

/**
 * 时间格式化，将"毫秒"格式化成"时:分:秒"
 * @param {number} ms - 毫秒数
 * @returns {string}
 */
const formatMs = (ms) => {
  const second = ms / 1000;
  return Tool.formatSecond(second);
};

//------------------- 下载 ------------------
const theDownloadCom = ref();

// 保存字幕修改
const saveSubtitles = () => {
  if (!subtitles.value || subtitles.value.length === 0) {
    message.warning("没有可保存的字幕");
    return;
  }
  loading.value = true;
  const payload = subtitles.value.map((item) => ({
    id: item.id,
    text: item.text,
  }));
  axios
    .post("/web/filetrans-subtitle/update", payload)
    .then((response) => {
      loading.value = false;
      const data = response.data;
      if (data.success) {
        message.success("字幕保存成功");
        // 保存成功后退出编辑状态并重新加载当前页
        exitEdit();
        handleQuery({
          page: pagination.value.current,
          size: pagination.value.pageSize,
        });
      } else {
        message.error(data.message || "保存失败");
      }
    })
    .catch(() => {
      loading.value = false;
      message.error("保存失败，请稍后重试");
    });
};
const genSubtitle = () => {
  loading.value = true;
  axios
    .get("/web/filetrans-subtitle/gen-subtitle", {
      params: {
        filetransId: filetrans.value.id,
      },
    })
    .then((response) => {
      loading.value = false;
      const data = response.data;
      if (data.success) {
        theDownloadCom.value.downloadItem(
          data.content,
          filetrans.value.name + "-" + filetrans.value.id + ".srt"
        );
      } else {
        message.error(data.message);
      }
    })
    .catch((error) => {
      loading.value = false;
      console.error("生成字幕失败：", error);
      message.error("生成字幕失败，请稍后重试");
    });
};
const genText = () => {
  loading.value = true;
  axios
    .get("/web/filetrans-subtitle/gen-text", {
      params: {
        filetransId: filetrans.value.id,
      },
    })
    .then((response) => {
      loading.value = false;
      const data = response.data;
      if (data.success) {
        theDownloadCom.value.downloadItem(
          data.content,
          filetrans.value.name + "-" + filetrans.value.id + ".txt"
        );
      } else {
        message.error(data.message);
      }
    })
    .catch((error) => {
      loading.value = false;
      console.error("生成文本失败：", error);
      message.error("生成文本失败，请稍后重试");
    });
};

// 使用 defineExpose 向外暴露指定的数据和方法
defineExpose({
  showModal,
});
</script>
