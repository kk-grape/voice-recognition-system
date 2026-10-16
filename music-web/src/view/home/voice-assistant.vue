<template>
  <!-- eslint-disable vue/no-v-model-argument -->
  <div class="voice-assistant-page">
    <!-- 顶部标题与说明 -->
    <div class="va-header">
      <div>
        <h2 class="va-title">语音助手 · 实时同传字幕</h2>
        <p class="va-subtitle">
          一边识别语音、一边翻译并提取字幕。后续可在此基础上接入 AI
          做智能总结与关键词提取。
        </p>
      </div>
    </div>

    <div class="va-main">
      <!-- 左侧控制区 -->
      <div class="va-left">
        <a-card title="控制面板" :bordered="false" class="va-card">
          <div class="va-control-group">
            <span class="va-label">识别语言</span>
            <!-- eslint-disable-next-line vue/no-v-model-argument -->
            <a-select
              v-model:value="sourceLang"
              style="width: 200px"
              :options="sourceLangOptions"
            />
          </div>
          <div class="va-control-group">
            <span class="va-label">翻译目标语言</span>
            <!-- eslint-disable-next-line vue/no-v-model-argument -->
            <a-select
              v-model:value="targetLang"
              style="width: 200px"
              :options="targetLangOptions"
            />
          </div>
          <div class="va-control-group">
            <span class="va-label">模式</span>
            <!-- eslint-disable-next-line vue/no-v-model-argument -->
            <a-segmented
              v-model:value="mode"
              :options="[
                { label: '实时同传', value: 'live' },
                { label: '仅提取字幕', value: 'subtitle' },
              ]"
            />
          </div>
          <a-alert
            class="va-tip"
            type="info"
            show-icon
            message="功能说明"
            description="当前页面仅提供前端展示与交互骨架，后续可接入真实的语音流 WebSocket、翻译接口以及 AI 总结接口。"
          />

          <div class="va-left-actions">
            <a-button
              type="primary"
              shape="round"
              size="large"
              block
              @click="toggleRecording"
            >
              {{ recording ? "停止识别" : "开始识别" }}
            </a-button>
          </div>
        </a-card>

        <!-- AI 对话内容总结 - 真实功能入口 -->
        <a-card
          :bordered="false"
          class="va-card va-summary-card"
        >
          <div class="summary-card-content" @click="openMeetingSummary">
            <div class="summary-card-icon">
              <FileTextOutlined />
            </div>
            <div class="summary-card-info">
              <span class="summary-card-title">AI 对话内容总结</span>
              <span class="summary-card-desc">智能生成会议摘要、要点提炼与行动项</span>
            </div>
            <div class="summary-card-arrow">
              <RightOutlined />
            </div>
          </div>
          <div class="summary-card-hint" v-if="subtitles.length === 0">
            <LockOutlined /> 请先进行语音识别
          </div>
          <div class="summary-card-hint summary-card-hint-active" v-else>
            <CheckCircleOutlined /> 已识别 {{ subtitles.length }} 条字幕
          </div>
        </a-card>
      </div>

      <!-- 中间字幕区 -->
      <div class="va-center">
        <!-- 第一部分：实时字幕 -->
        <a-card :bordered="false" class="va-card va-live-card">
          <template #title>
            <span>实时字幕</span>
          </template>
          <div class="va-live-subtitle">
            <div class="va-line main">
              {{ currentSubtitle.sourceText || liveSubtitlePlaceholder }}
            </div>
          </div>
        </a-card>

        <!-- 第二部分：字幕翻译 -->
        <a-card :bordered="false" class="va-card va-translation-card">
          <template #title>
            <span>字幕翻译</span>
          </template>
          <div class="va-translation-content">
            <div
              class="va-translation-text"
              v-if="mode === 'live' && currentSubtitle.targetText"
            >
              {{ currentSubtitle.targetText }}
            </div>
            <div class="va-translation-text va-translation-empty" v-else>
              {{
                mode === "live"
                  ? translationPlaceholder
                  : translationDisabledText
              }}
            </div>
          </div>
        </a-card>

        <!-- 第三部分：字幕片段（可滚动） -->
        <a-card
          title="字幕片段"
          :bordered="false"
          class="va-card va-history-card"
        >
          <div class="va-history-scroll-container">
            <a-table
              :dataSource="subtitles"
              :columns="columns"
              :pagination="false"
              size="small"
              row-key="id"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.dataIndex === 'time'">
                  {{ record.time }}
                </template>
                <template v-else-if="column.dataIndex === 'sourceText'">
                  {{ record.sourceText }}
                </template>
                <template v-else-if="column.dataIndex === 'targetText'">
                  {{ record.targetText }}
                </template>
              </template>
            </a-table>
            <div v-if="subtitles.length === 0" class="va-empty-text">
              暂无字幕片段，开始识别后，这里会按时间顺序展示所有已识别的句子。
            </div>
          </div>
        </a-card>
      </div>

      <!-- 右侧操作区（后续新增操作也放这里） -->
      <div class="va-ops">
        <a-card title="操作区" :bordered="false" class="va-card va-ops-card">
          <div class="va-ops-actions">
            <a-button
              shape="round"
              size="large"
              block
              @click="clearSubtitles"
              :disabled="subtitles.length === 0"
            >
              清空字幕
            </a-button>
          </div>

          <div class="va-ops-divider"></div>

          <div class="va-ops-export">
            <!-- 合成原文音频 -->
            <div class="va-synthesize-item">
              <a-button
                shape="round"
                size="large"
                block
                @click="synthesizeOriginalAudio"
                :disabled="subtitles.length === 0 || synthesizingOriginal"
                :loading="synthesizingOriginal"
              >
                <template #icon><DownloadOutlined /></template>
                {{ synthesizingOriginal ? '合成中...' : (originalAudioReady ? '下载原文音频' : '合成原文音频') }}
              </a-button>
              <a-button
                v-if="originalAudioReady"
                type="primary"
                shape="round"
                size="large"
                @click="downloadOriginalAudio"
                style="margin-top: 8px;"
              >
                <template #icon><DownloadOutlined /></template>
                下载
              </a-button>
            </div>

            <!-- 合成翻译音频 -->
            <div class="va-synthesize-item" v-if="mode === 'live'">
              <a-button
                shape="round"
                size="large"
                block
                @click="synthesizeTranslatedAudio"
                :disabled="subtitles.length === 0 || synthesizingTranslated"
                :loading="synthesizingTranslated"
              >
                <template #icon><DownloadOutlined /></template>
                {{ synthesizingTranslated ? '合成中...' : (translatedAudioReady ? '下载翻译音频' : '合成翻译音频') }}
              </a-button>
              <a-button
                v-if="translatedAudioReady"
                type="primary"
                shape="round"
                size="large"
                @click="downloadTranslatedAudio"
                style="margin-top: 8px;"
              >
                <template #icon><DownloadOutlined /></template>
                下载
              </a-button>
            </div>

            <a-divider style="margin: 12px 0;" />

            <!-- 导出原文字幕 -->
            <a-button
              shape="round"
              size="large"
              block
              @click="exportOriginalSubtitles"
              :disabled="subtitles.length === 0"
            >
              <template #icon><DownloadOutlined /></template>
              导出原文字幕
            </a-button>

            <!-- 导出翻译字幕 -->
            <a-button
              v-if="mode === 'live'"
              shape="round"
              size="large"
              block
              @click="exportTranslatedSubtitles"
              :disabled="subtitles.length === 0"
              style="margin-top: 8px;"
            >
              <template #icon><DownloadOutlined /></template>
              导出翻译字幕
            </a-button>
          </div>
        </a-card>
      </div>
    </div>

    <!-- 页面底部 Dock：状态 · 灵动岛（未录音小胶囊，录音时大胶囊 + 波浪） -->
    <div class="va-bottom-dock" aria-label="录音状态">
      <div class="va-island" :class="{ recording }">
        <div class="va-island-bg" aria-hidden="true">
          <canvas ref="waveCanvas" class="va-island-wave" />
        </div>
        <div class="va-island-fg">
          <div class="va-island-left">
            <span class="va-island-dot" :class="{ on: recording }"></span>
            <span class="va-island-text">{{
              recording ? "采集中" : "未开始"
            }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- AI 对话内容总结弹窗 -->
    <a-modal
      v-model:open="showSummaryModal"
      :footer="null"
      :width="980"
      :maskClosable="false"
      class="summary-modal"
      @cancel="closeSummaryModal"
    >
      <template #title>
        <div class="summary-modal-title">
          <FileTextOutlined class="summary-modal-icon" />
          <span>AI 对话内容总结</span>
        </div>
      </template>

      <div class="summary-modal-body">
        <!-- 加载状态 -->
        <div v-if="summaryLoading" class="summary-loading">
          <LoadingOutlined class="summary-loading-icon" />
          <span>正在生成对话内容总结，请稍候...</span>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="summaryError" class="summary-error">
          <a-result
            status="error"
            title="生成失败"
            :sub-title="summaryError"
          >
            <template #extra>
              <a-button type="primary" @click="regenerateSummary">
                <ReloadOutlined /> 重新生成
              </a-button>
            </template>
          </a-result>
        </div>

        <!-- 对话内容总结内容 -->
        <div v-else-if="summaryData" class="summary-content">
          <div class="summary-main">
            <!-- 会议摘要 -->
            <div class="summary-section">
              <div class="summary-section-header">
                <span class="summary-section-title">会议摘要</span>
              </div>
              <div class="summary-section-body">
                <p class="summary-text">{{ getSummarySource() }}</p>
                <p v-if="getSummaryTarget()" class="summary-text summary-text-secondary">
                  {{ getSummaryTarget() }}
                </p>
              </div>
            </div>

            <!-- 关键要点 -->
            <div class="summary-section" v-if="getKeyPoints().length > 0">
              <div class="summary-section-header">
                <span class="summary-section-title">关键要点</span>
              </div>
              <div class="summary-section-body">
                <ul class="summary-list">
                  <li v-for="(point, idx) in getKeyPoints()" :key="idx" class="summary-list-item">
                    {{ point }}
                  </li>
                </ul>
              </div>
            </div>

            <!-- 关键词 -->
            <div class="summary-section" v-if="getKeywords()">
              <div class="summary-section-header">
                <span class="summary-section-title">关键词</span>
              </div>
              <div class="summary-section-body">
                <a-tag
                  v-for="(kw, idx) in getKeywords().split(/[,，;；]/)"
                  :key="idx"
                  color="blue"
                  class="summary-tag"
                >
                  {{ kw.trim() }}
                </a-tag>
              </div>
            </div>
          </div>

          <!-- 右侧追问区域 -->
          <div class="summary-sidebar">
            <!-- 顶部操作按钮 -->
            <div class="summary-actions">
              <a-button size="small" @click="regenerateSummary" :loading="summaryLoading">
                <ReloadOutlined /> 重新生成
              </a-button>
              <a-button size="small" @click="exportSummary">
                <ExportOutlined /> 导出
              </a-button>
            </div>

            <!-- 追问对话区 -->
            <div class="followup-area">
              <div class="followup-header">
                <RobotOutlined /> 对话追问
              </div>

              <!-- 对话历史 -->
              <div class="followup-messages" v-if="followupMessages.length > 0">
                <div
                  v-for="(msg, idx) in followupMessages"
                  :key="idx"
                  :class="['followup-message', msg.role === 'user' ? 'followup-message-user' : 'followup-message-ai']"
                >
                  <div class="followup-message-avatar">
                    <UserOutlined v-if="msg.role === 'user'" />
                    <RobotOutlined v-else />
                  </div>
                  <div class="followup-message-content">
                    {{ msg.content }}
                  </div>
                </div>
              </div>
              <div v-else class="followup-empty">
                可以对对话内容总结进行追问或修改
              </div>

              <!-- 输入框 -->
              <div class="followup-input">
                <a-input
                  v-model:value="followupQuestion"
                  placeholder="输入追问内容..."
                  :disabled="followupLoading"
                  @pressEnter="sendFollowup"
                />
                <a-button
                  type="primary"
                  :loading="followupLoading"
                  @click="sendFollowup"
                  :disabled="!followupQuestion.trim()"
                >
                  <SendOutlined />
                </a-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="summary-empty">
          <a-empty description="暂无对话内容总结数据" />
        </div>
      </div>
    </a-modal>
  </div>
  <!-- eslint-enable vue/no-v-model-argument -->
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, ref, watch } from "vue";
import axios from "axios";
import { message } from "ant-design-vue";
import {
  DownloadOutlined,
  FileTextOutlined,
  RightOutlined,
  CheckCircleOutlined,
  LockOutlined,
  CloseOutlined,
  ReloadOutlined,
  ExportOutlined,
  SendOutlined,
  RobotOutlined,
  UserOutlined,
  LoadingOutlined,
} from "@ant-design/icons-vue";
import store from "../../store/index.js";

// 识别语言与翻译目标语言（使用后端枚举格式）
const sourceLang = ref("LANG0");
const targetLang = ref("LANG1");
const mode = ref("live"); // live: 实时同传；subtitle: 只提取字幕

// 语言选项（从 enums.js 读取，保持前后端统一）
const sourceLangOptions = ASR_LANG_ARRAY;
const targetLangOptions = TRANSLATE_LANG_ARRAY;

// 当前会话ID（后端创建会话后返回）
const currentSessionId = ref(null);

// 合成状态
const synthesizingOriginal = ref(false);
const synthesizingTranslated = ref(false);
const originalAudioReady = ref(false);
const translatedAudioReady = ref(false);

// ====== AI 对话内容总结相关状态 ======
const showSummaryModal = ref(false); // 是否显示对话内容总结弹窗
const summaryLoading = ref(false); // 正在生成纪要
const summaryData = ref(null); // 对话内容总结数据
const summaryError = ref(""); // 错误信息
const followupQuestion = ref(""); // 追问内容
const followupLoading = ref(false); // 追问加载中
const followupMessages = ref([]); // 追问对话历史

// 录音状态
const recording = ref(false);
const wsConnected = ref(false);
let ws = null;
let mediaStreamSource = null;
let processor = null;
let recordingStartAt = 0;

const getWsUrl = () => {
  // 网关路由配置为 /ws/asr（不需要 /web 前缀）
  // WebSocket 使用 ws:// 或 wss:// 协议
  // token 通过 URL query 参数传递
  const base = (import.meta.env.VITE_SERVER || "").replace(/\/$/, "");
  const wsBase = base
    .replace(/^http:\/\//i, "ws://")
    .replace(/^https:\/\//i, "wss://");
  const token = store.state.member?.token || "";
  return `${wsBase}/ws/asr?token=${encodeURIComponent(token)}`;
};

// 麦克风音量波浪
const waveCanvas = ref(null);
let micStream = null;
let audioCtx = null;
let analyser = null;
let freqData = null;
let barLevels = null;
let waveRaf = 0;

// 当前展示的实时字幕
const currentSubtitle = ref({ sourceText: "", targetText: "" });

// 打字机效果用的临时变量
const typingSourceText = ref("");
const typingTargetText = ref("");
let typingTimer = null;

// 打字机效果：逐字显示文本
const startTypewriter = (fullSourceText, fullTargetText) => {
  // 清除之前的打字定时器
  if (typingTimer) {
    clearTimeout(typingTimer);
  }

  // 重置打字内容
  typingSourceText.value = "";
  typingTargetText.value = "";

  // 计算需要显示的总长度
  const sourceLen = fullSourceText.length;
  const targetLen = fullTargetText ? fullTargetText.length : 0;
  const maxLen = Math.max(sourceLen, targetLen);

  if (maxLen === 0) return;

  let i = 0;
  const typeNextChar = () => {
    if (i < maxLen) {
      // 每次多显示几个字符，让效果更流畅
      const chunkSize = Math.min(3, maxLen - i);

      if (i < sourceLen) {
        typingSourceText.value = fullSourceText.substring(0, i + chunkSize);
      }
      if (i < targetLen && fullTargetText) {
        typingTargetText.value = fullTargetText.substring(0, i + chunkSize);
      }

      // 更新显示
      currentSubtitle.value = {
        sourceText: typingSourceText.value,
        targetText: typingTargetText.value,
      };

      i += chunkSize;
      // 打字间隔，可以调整速度
      typingTimer = setTimeout(typeNextChar, 30);
    } else {
      // 打字完成，确保最终内容正确
      currentSubtitle.value = {
        sourceText: fullSourceText,
        targetText: fullTargetText || "",
      };
    }
  };

  typeNextChar();
};

// 历史字幕片段列表
const subtitles = ref([]);

let mockTimer = null;
let mockIndex = 0;

// 表格列定义
const columns = [
  {
    title: "时间",
    dataIndex: "time",
    width: 120,
  },
  {
    title: "原文（识别结果）",
    dataIndex: "sourceText",
  },
  {
    title: "翻译结果",
    dataIndex: "targetText",
  },
];

// 切换识别（真实 WebSocket 推流识别）
const toggleRecording = () => {
  recording.value = !recording.value;

  if (recording.value) {
    startAsrStream();
    startMicWave();
  } else {
    stopAsrStream();
    stopMicWave();
  }
};

const clearSubtitles = () => {
  subtitles.value = [];
  currentSubtitle.value = { sourceText: "", targetText: "" };
  synthesizingOriginal.value = false;
  synthesizingTranslated.value = false;
  originalAudioReady.value = false;
  translatedAudioReady.value = false;
};

// 打开对话内容总结弹窗
const openMeetingSummary = async () => {
  if (subtitles.value.length === 0) {
    message.warning("请先进行语音识别，再生成对话内容总结");
    return;
  }

  if (!currentSessionId.value) {
    message.warning("请先进行语音识别");
    return;
  }

  showSummaryModal.value = true;
  summaryData.value = null;
  summaryError.value = "";
  followupMessages.value = [];
  followupQuestion.value = "";

  // 触发生成对话内容总结
  await generateMeetingSummary();
};

// 生成对话内容总结
const generateMeetingSummary = async () => {
  summaryLoading.value = true;
  summaryError.value = "";

  try {
    // 将前端 LANG0/LANG1 转换为实际的翻译语言代码
    const translateCode = ASR_LANG_TO_TRANSLATE_CODE[targetLang.value] || "en";

    const resp = await axios.post(
      `${getApiBase()}/web/ai/meeting/generate`,
      {
        asrSessionId: String(currentSessionId.value),
        targetLang: translateCode,
      },
      {
        headers: {
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );

    // 后端 CommonResp 使用 success 字段，不是 code
    if (resp.data.success) {
      summaryData.value = resp.data.content;

      // 如果有追问历史，同步到前端
      if (summaryData.value.messages && summaryData.value.messages.length > 1) {
        // messages[0] 是 system 消息，从索引 1 开始是真正的对话
        followupMessages.value = summaryData.value.messages.slice(1);
      } else {
        followupMessages.value = [];
      }

      message.success("对话内容总结生成成功");
    } else {
      summaryError.value = resp.data.message || "生成失败，请重试";
      message.error(summaryError.value);
    }
  } catch (e) {
    console.error("生成对话内容总结失败:", e);
    summaryError.value = e.response?.data?.message || "网络错误，请重试";
    message.error("生成对话内容总结失败，请重试");
  } finally {
    summaryLoading.value = false;
  }
};

// 发送追问
const sendFollowup = async () => {
  const q = followupQuestion.value.trim();
  if (!q) return;

  if (!currentSessionId.value) {
    message.warning("会话信息缺失，请重新识别");
    return;
  }

  followupLoading.value = true;

  try {
    const resp = await axios.post(
      `${getApiBase()}/web/ai/meeting/message`,
      {
        asrSessionId: String(currentSessionId.value),
        content: q,
      },
      {
        headers: {
          Authorization: `Bearer ${getToken()}`,
        },
      }
    );

    // 后端 CommonResp 使用 success 字段，不是 code
    if (resp.data.success) {
      summaryData.value = resp.data.content;

      // 同步追问历史
      if (summaryData.value.messages && summaryData.value.messages.length > 1) {
        followupMessages.value = summaryData.value.messages.slice(1);
      }

      followupQuestion.value = "";
    } else {
      message.error(resp.data.message || "追问失败");
    }
  } catch (e) {
    console.error("追问失败:", e);
    message.error("追问失败，请重试");
  } finally {
    followupLoading.value = false;
  }
};

// 关闭对话内容总结弹窗
const closeSummaryModal = () => {
  showSummaryModal.value = false;
};

// 导出对话内容总结（目前只有按钮，等后端实现）
const exportSummary = () => {
  message.info("导出功能正在开发中，敬请期待");
};

// 重新生成对话内容总结（目前只有按钮）
const regenerateSummary = () => {
  generateMeetingSummary();
};

// 解析 JSON 字符串为数组
const parseJsonArray = (jsonStr) => {
  if (!jsonStr) return [];
  try {
    return JSON.parse(jsonStr);
  } catch {
    return [];
  }
};

// 获取显示用的内容
const getSummarySource = () => summaryData.value?.summarySource || "";
const getSummaryTarget = () => summaryData.value?.summaryTarget || "";
const getKeyPoints = () => parseJsonArray(summaryData.value?.keyPoints);
const getKeywords = () => summaryData.value?.keywords || "";

// ========= 以下是实时字幕流（WebSocket）逻辑 =========
// 语言映射（从 enums.js 读取）
const sourceKey = computed(() => UI_LANG_KEY_MAP[sourceLang.value] || "zh");
const targetKey = computed(() => UI_LANG_KEY_MAP[targetLang.value] || "en");

// 空态/等待态文案（从 enums.js 读取）
const liveSubtitlePlaceholder = computed(
  () =>
    UI_LANG_TEXT.liveSubtitlePlaceholder[sourceKey.value] ||
    UI_LANG_TEXT.liveSubtitlePlaceholder.zh,
);

const translationPlaceholder = computed(
  () =>
    UI_LANG_TEXT.translationPlaceholder[targetKey.value] ||
    UI_LANG_TEXT.translationPlaceholder.en,
);

const translationDisabledText = computed(
  () =>
    UI_LANG_TEXT.translationDisabledText[sourceKey.value] ||
    UI_LANG_TEXT.translationDisabledText.zh,
);

const mockSentences = [
  {
    zh: "欢迎使用语音助手，这是一个实时同传字幕的演示。",
    en: "Welcome to the voice assistant, this is a demo of real-time transcription and translation.",
    ja: "音声アシスタントへようこそ。これはリアルタイム文字起こしと翻訳のデモです。",
  },
  {
    zh: "右侧会显示当前说话的中英文字幕，下方记录完整的字幕片段。",
    en: "On the right, you will see the current bilingual subtitles, and below is the full subtitle history.",
    ja: "右側には現在の字幕が表示され、下には字幕の履歴が時系列で記録されます。",
  },
  {
    zh: "后续你可以在这里接入真正的语音识别、翻译接口和 AI 总结能力。",
    en: "Later you can plug in real speech recognition, translation APIs, and AI summarization here.",
    ja: "今後はここに本物の音声認識、翻訳API、AI要約機能を接続できます。",
  },
];

// Mock：当 WS 不可用时，回退到演示流（避免页面完全不可用）
const startMockStream = () => {
  mockIndex = 0;
  addMockSubtitle();
  mockTimer = setInterval(() => {
    addMockSubtitle();
  }, 4000);
};

const stopMockStream = () => {
  if (mockTimer) {
    clearInterval(mockTimer);
    mockTimer = null;
  }
};

const startAsrStream = async () => {
  try {
    // 重新识别前清空之前的字幕数据
    subtitles.value = [];
    currentSubtitle.value = { sourceText: "", targetText: "" };

    if (!store.state.member?.token) {
      throw new Error("未登录或 token 缺失，请先登录");
    }

    recordingStartAt = Date.now();
    ws = new WebSocket(getWsUrl());
    ws.binaryType = "arraybuffer";

    ws.onopen = async () => {
      wsConnected.value = true;
      // 发送 start 控制消息
      // sourceLang: 直接传 LANG0, LANG1 等格式，后端 AsrLangEnum.getByCode 支持
      // targetLang: 直接传 LANG0, LANG1 等格式，后端 TranslateLangEnum.getByCode 支持
      ws.send(
        JSON.stringify({
          type: "start",
          sourceLang: sourceLang.value,
          sampleRate: 16000,
          format: "pcm",
          mode: mode.value,
          targetLang: targetLang.value,
        }),
      );

      // 开始采集麦克风音频并推送 PCM16
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
        video: false,
      });
      micStream = stream; // 复用 stopMicWave 的清理逻辑

      audioCtx = new (window.AudioContext || window.webkitAudioContext)({
        sampleRate: 48000,
      });
      mediaStreamSource = audioCtx.createMediaStreamSource(stream);

      // ScriptProcessor 简单可用（毕业设计足够）；需要更稳可换 AudioWorklet
      processor = audioCtx.createScriptProcessor(4096, 1, 1);
      mediaStreamSource.connect(processor);
      processor.connect(audioCtx.destination);

      processor.onaudioprocess = (e) => {
        if (!recording.value || !ws || ws.readyState !== 1) return;
        const input = e.inputBuffer.getChannelData(0);

        // downsample 48k -> 16k
        const pcm16 = downsampleTo16kPcm16(input, audioCtx.sampleRate);
        if (pcm16 && pcm16.byteLength > 0) {
          ws.send(pcm16);
        }
      };
    };

    ws.onmessage = (evt) => {
      try {
        const msg = JSON.parse(evt.data);
        console.log("收到 WebSocket 消息:", msg);

        if (msg.type === "started") {
          // 后端返回 started 消息，提取会话ID
          currentSessionId.value = msg.sessionId || null;
          return;
        }

        if (msg.type === "error" || msg.type === "aliyun_error") {
          console.warn("ASR error:", msg.message);
          return;
        }

        // 字幕消息格式：后端返回 { type: "subtitle", text: "...", translatedText: "...", beginTime: 1000, endTime: 3000 }
        if (msg.type === "subtitle") {
          const text = msg.text || "";
          const translatedText = msg.translatedText || "";

          if (!text && !translatedText) return;

          const now = new Date();
          const timeStr = now.toTimeString().slice(0, 8);

          // 如果是 live 模式，显示翻译结果；如果是 subtitle 模式，不显示翻译
          const displayTargetText = mode.value === "live" ? translatedText : "";

          const record = {
            id: `${now.getTime()}-${Math.random()}`,
            time: timeStr,
            sourceText: text,
            targetText: displayTargetText,
            beginTime: msg.beginTime,
            endTime: msg.endTime,
          };

          subtitles.value.push(record);
          // 打字机效果流式显示
          startTypewriter(record.sourceText, record.targetText);
          return;
        }

        // 兼容：后端可能直接透传阿里云事件（event/payload/result/text）
        const payload = msg.payload || msg.Payload || {};
        const text =
          payload.result ||
          payload.text ||
          (payload.result && payload.result.text) ||
          msg.text ||
          "";
        const translatedText = msg.translatedText || "";

        if (!text && !translatedText) return;

        const now = new Date();
        const timeStr = now.toTimeString().slice(0, 8);

        // 如果是 live 模式，显示翻译结果；如果是 subtitle 模式，不显示翻译
        const displayTargetText = mode.value === "live" ? translatedText : "";

        const record = {
          id: `${now.getTime()}-${Math.random()}`,
          time: timeStr,
          sourceText: text || "",
          targetText: displayTargetText,
        };

        subtitles.value.push(record);
        // 打字机效果流式显示
        startTypewriter(record.sourceText, record.targetText);
      } catch (e) {
        // 非 JSON 直接忽略
      }
    };

    ws.onerror = (e) => {
      console.warn("ASR WS error:", e);
    };

    ws.onclose = () => {
      wsConnected.value = false;
    };
  } catch (e) {
    console.warn("startAsrStream failed:", e);
    // 失败时回退到 mock，避免页面“没反应”
    startMockStream();
  }
};

const stopAsrStream = () => {
  try {
    if (processor) {
      processor.disconnect();
      processor.onaudioprocess = null;
      processor = null;
    }
    if (mediaStreamSource) {
      mediaStreamSource.disconnect();
      mediaStreamSource = null;
    }
    if (ws && ws.readyState === 1) {
      ws.send(JSON.stringify({ type: "stop" }));
    }
    ws?.close?.();
    ws = null;
    wsConnected.value = false;
  } catch (e) {
    // ignore
  } finally {
    stopMockStream();
  }
};

// 切换语言/模式时：重置当前展示，并在录音中让 mock 立即应用新语言
watch([sourceLang, targetLang, mode], () => {
  currentSubtitle.value = { sourceText: "", targetText: "" };

  if (recording.value) {
    // 录音中切换语言：重启一次 WS（避免参数不一致）
    stopAsrStream();
    startAsrStream();
  }
});

const startMicWave = async () => {
  try {
    micStream = await navigator.mediaDevices.getUserMedia({
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
      },
      video: false,
    });

    audioCtx = new (window.AudioContext || window.webkitAudioContext)();
    const src = audioCtx.createMediaStreamSource(micStream);
    analyser = audioCtx.createAnalyser();
    analyser.fftSize = 2048;
    analyser.smoothingTimeConstant = 0.86;
    freqData = new Uint8Array(analyser.frequencyBinCount);
    src.connect(analyser);

    await nextTick();
    const canvas = waveCanvas.value;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    const dpr = Math.max(1, Math.min(2, window.devicePixelRatio || 1));
    const ensureSize = () => {
      const cssW = canvas.clientWidth || 240;
      const cssH = canvas.clientHeight || 44;
      const pxW = Math.floor(cssW * dpr);
      const pxH = Math.floor(cssH * dpr);
      if (canvas.width !== pxW || canvas.height !== pxH) {
        canvas.width = pxW;
        canvas.height = pxH;
      }
      return { width: pxW, height: pxH };
    };

    const draw = () => {
      if (!recording.value || !analyser || !ctx) return;
      // 频域：做“音乐播放器那种从底部往上弹”的柱状波浪
      analyser.getByteFrequencyData(freqData);

      const { width, height } = ensureSize();
      ctx.clearRect(0, 0, width, height);

      // 计算整体能量（用于发光强度）
      let sum = 0;
      for (let i = 0; i < freqData.length; i++) sum += freqData[i];
      const energy = Math.min(1, (sum / (freqData.length * 255)) * 2.2); // 0~1

      // 柱状波浪只占药丸底部区域
      const barAreaTop = Math.floor(height * 0.42);
      const barAreaH = height - barAreaTop;

      const bars = 28;
      if (!barLevels || barLevels.length !== bars)
        barLevels = new Array(bars).fill(0);

      const gap = Math.max(2 * dpr, 3 * dpr);
      const barW = Math.max(
        2 * dpr,
        Math.floor((width - gap * (bars - 1)) / bars),
      );
      const step = Math.max(1, Math.floor(freqData.length / bars));

      // 纵向渐变（更像“从底部发光的波浪”）
      const grad = ctx.createLinearGradient(0, barAreaTop, 0, height);
      grad.addColorStop(0, "rgba(255, 255, 255, 0.10)");
      grad.addColorStop(0.35, "rgba(67, 138, 255, 0.35)");
      grad.addColorStop(1, "rgba(255, 64, 84, 0.55)");

      ctx.save();
      // 裁剪到下半部分，避免挡住文字
      ctx.beginPath();
      ctx.rect(0, barAreaTop, width, barAreaH);
      ctx.clip();

      // 发光底雾
      ctx.globalAlpha = 0.55 + energy * 0.25;
      ctx.fillStyle = "rgba(255,255,255,0.10)";
      ctx.shadowColor = "rgba(255,255,255,0.28)";
      ctx.shadowBlur = (10 + energy * 10) * dpr;
      ctx.fillRect(0, height - 2 * dpr, width, 2 * dpr);
      ctx.shadowBlur = 0;

      // 画柱子：带平滑与回落，跳动更舒服
      ctx.fillStyle = grad;
      let x = 0;
      // 以中间为中心，向两侧对称映射频谱：0,1,2,... 由中心向外递增
      const leftCenter = Math.floor((bars - 1) / 2);
      const rightCenter = bars % 2 === 0 ? leftCenter + 1 : leftCenter;
      for (let i = 0; i < bars; i++) {
        const dist = i <= leftCenter ? leftCenter - i : i - rightCenter; // 0,1,2...
        const idx = dist * step;
        // 取一小段平均，避免抖得太碎
        let v = 0;
        const take = Math.min(step, 6);
        for (let k = 0; k < take; k++) v += freqData[idx + k] || 0;
        v = v / take / 255; // 0~1

        const target = Math.pow(v, 0.65); // 低频更明显一点
        const prev = barLevels[i] || 0;
        const rise = 0.45;
        const fall = 0.08;
        const next =
          target > prev
            ? prev + (target - prev) * rise
            : Math.max(0, prev - fall);
        barLevels[i] = next;

        const h = Math.max(2 * dpr, next * (barAreaH * (0.88 + energy * 0.18)));
        const y = height - h;
        const r = Math.min(8 * dpr, barW * 0.6);

        // 圆角柱
        ctx.beginPath();
        const bw = barW;
        ctx.moveTo(x + r, y);
        ctx.arcTo(x + bw, y, x + bw, y + r, r);
        ctx.arcTo(x + bw, height, x + bw - r, height, r);
        ctx.arcTo(x, height, x, height - r, r);
        ctx.arcTo(x, y, x + r, y, r);
        ctx.closePath();
        ctx.globalAlpha = 0.7 + energy * 0.25;
        ctx.fill();

        x += barW + gap;
      }
      ctx.restore();

      waveRaf = requestAnimationFrame(draw);
    };

    waveRaf = requestAnimationFrame(draw);
  } catch (e) {
    // 用户拒绝权限时仍可继续走字幕 Mock
    console.warn("麦克风权限/音量波浪启动失败：", e);
  }
};

const stopMicWave = () => {
  if (waveRaf) {
    cancelAnimationFrame(waveRaf);
    waveRaf = 0;
  }
  if (micStream) {
    micStream.getTracks().forEach((t) => t.stop());
    micStream = null;
  }
  if (audioCtx) {
    audioCtx.close?.();
    audioCtx = null;
  }
  analyser = null;
  freqData = null;
  barLevels = null;
};

onBeforeUnmount(() => {
  // 兜底清理：避免页面切走后仍占用麦克风/raf/定时器
  stopAsrStream();
  stopMicWave();
});

// ====== 工具：把 Float32 音频 downsample 到 16k 并转成 PCM16(ArrayBuffer) ======
const downsampleTo16kPcm16 = (float32Samples, inSampleRate) => {
  const outSampleRate = 16000;
  if (!float32Samples || float32Samples.length === 0) return null;
  if (inSampleRate === outSampleRate) {
    const buf = new ArrayBuffer(float32Samples.length * 2);
    const view = new DataView(buf);
    for (let i = 0; i < float32Samples.length; i++) {
      let s = Math.max(-1, Math.min(1, float32Samples[i]));
      view.setInt16(i * 2, s < 0 ? s * 0x8000 : s * 0x7fff, true);
    }
    return buf;
  }

  const ratio = inSampleRate / outSampleRate;
  const newLen = Math.floor(float32Samples.length / ratio);
  const buf = new ArrayBuffer(newLen * 2);
  const view = new DataView(buf);

  let offset = 0;
  for (let i = 0; i < newLen; i++) {
    const nextOffset = Math.floor((i + 1) * ratio);
    // 简单平均降采样
    let sum = 0;
    let count = 0;
    for (let j = offset; j < nextOffset && j < float32Samples.length; j++) {
      sum += float32Samples[j];
      count++;
    }
    offset = nextOffset;
    let s = sum / (count || 1);
    s = Math.max(-1, Math.min(1, s));
    view.setInt16(i * 2, s < 0 ? s * 0x8000 : s * 0x7fff, true);
  }
  return buf;
};

const addMockSubtitle = () => {
  const item = mockSentences[mockIndex % mockSentences.length];
  mockIndex++;

  const now = new Date();
  const timeStr = now.toTimeString().slice(0, 8);

  const srcText = item[sourceKey.value] || item.zh || "";
  const tgtText = mode.value === "live" ? item[targetKey.value] || "" : "";

  const record = {
    id: `${now.getTime()}-${mockIndex}`,
    time: timeStr,
    sourceText: srcText,
    targetText: tgtText,
  };

  subtitles.value.push(record);
  // 打字机效果流式显示
  startTypewriter(record.sourceText, record.targetText);
};

// 导出功能

// 获取 API 基础路径
const getApiBase = () => {
  const base = (import.meta.env.VITE_SERVER || "").replace(/\/$/, "");
  return base;
};

// 获取 token
const getToken = () => {
  return store.state.member?.token || "";
};

// 合成原文音频
const synthesizeOriginalAudio = async () => {
  if (subtitles.value.length === 0) {
    return;
  }

  if (!currentSessionId.value) {
    message.warning("请先进行语音识别");
    return;
  }

  synthesizingOriginal.value = true;
  originalAudioReady.value = false;

  try {
    await axios.post(`${getApiBase()}/web/tts/synthesize`, {
      sessionId: currentSessionId.value,
      subtitleType: "original",
    });
    originalAudioReady.value = true;
    message.success("原文音频合成成功，点击下载");
  } catch (e) {
    console.error("合成原文音频失败:", e);
    message.error("合成音频失败，请重试");
  } finally {
    synthesizingOriginal.value = false;
  }
};

// 下载原文音频
const downloadOriginalAudio = () => {
  if (!currentSessionId.value) {
    return;
  }

  message.loading("正在下载原文音频...");

  axios.get(`${getApiBase()}/web/tts/download`, {
    params: {
      sessionId: currentSessionId.value,
      type: "original",
    },
    responseType: "blob",
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    // 从响应头获取文件名
    const contentDisposition = response.headers["content-disposition"];
    let fileName = "original_audio.wav";
    if (contentDisposition) {
      const match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (match && match[1]) {
        fileName = match[1].replace(/['"]/g, "");
      }
    }
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success("下载成功");
  }).catch((e) => {
    console.error("下载原文音频失败:", e);
    message.error("下载失败，请重试");
  });
};

// 合成翻译音频
const synthesizeTranslatedAudio = async () => {
  if (subtitles.value.length === 0) {
    return;
  }

  if (!currentSessionId.value) {
    message.warning("请先进行语音识别");
    return;
  }

  synthesizingTranslated.value = true;
  translatedAudioReady.value = false;

  try {
    await axios.post(`${getApiBase()}/web/tts/synthesize`, {
      sessionId: currentSessionId.value,
      subtitleType: "translated",
    });
    translatedAudioReady.value = true;
    message.success("翻译音频合成成功，点击下载");
  } catch (e) {
    console.error("合成翻译音频失败:", e);
    message.error("合成音频失败，请重试");
  } finally {
    synthesizingTranslated.value = false;
  }
};

// 下载翻译音频
const downloadTranslatedAudio = () => {
  if (!currentSessionId.value) {
    return;
  }

  message.loading("正在下载翻译音频...");

  axios.get(`${getApiBase()}/web/tts/download`, {
    params: {
      sessionId: currentSessionId.value,
      type: "translated",
    },
    responseType: "blob",
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    // 从响应头获取文件名
    const contentDisposition = response.headers["content-disposition"];
    let fileName = "translated_audio.wav";
    if (contentDisposition) {
      const match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (match && match[1]) {
        fileName = match[1].replace(/['"]/g, "");
      }
    }
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success("下载成功");
  }).catch((e) => {
    console.error("下载翻译音频失败:", e);
    message.error("下载失败，请重试");
  });
};

// 导出原文字幕（SRT格式）
const exportOriginalSubtitles = () => {
  if (subtitles.value.length === 0) {
    return;
  }

  if (!currentSessionId.value) {
    message.warning("请先进行语音识别");
    return;
  }

  message.loading("正在导出原文字幕...");

  axios.get(`${getApiBase()}/web/asr-subtitle/export`, {
    params: {
      sessionId: currentSessionId.value,
      type: "original",
    },
    responseType: "blob",
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    // 从响应头获取文件名
    const contentDisposition = response.headers["content-disposition"];
    let fileName = "original.srt";
    if (contentDisposition) {
      const match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (match && match[1]) {
        fileName = match[1].replace(/['"]/g, "");
      }
    }
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success("导出成功");
  }).catch((e) => {
    console.error("导出原文字幕失败:", e);
    message.error("导出失败，请重试");
  });
};

// 导出翻译字幕（SRT格式）
const exportTranslatedSubtitles = () => {
  if (subtitles.value.length === 0) {
    return;
  }

  if (!currentSessionId.value) {
    message.warning("请先进行语音识别");
    return;
  }

  message.loading("正在导出翻译字幕...");

  axios.get(`${getApiBase()}/web/asr-subtitle/export`, {
    params: {
      sessionId: currentSessionId.value,
      type: "translated",
    },
    responseType: "blob",
  }).then((response) => {
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    // 从响应头获取文件名
    const contentDisposition = response.headers["content-disposition"];
    let fileName = "translated.srt";
    if (contentDisposition) {
      const match = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
      if (match && match[1]) {
        fileName = match[1].replace(/['"]/g, "");
      }
    }
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success("导出成功");
  }).catch((e) => {
    console.error("导出翻译字幕失败:", e);
    message.error("导出失败，请重试");
  });
};
</script>

<style scoped>
.voice-assistant-page {
  width: 88%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 8px 0 120px; /* 预留底部 Dock 空间，避免遮挡内容 */
}

.va-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.va-title {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}

.va-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #666;
}

.va-main {
  display: grid;
  grid-template-columns: 320px minmax(0, 1fr) 210px;
  gap: 16px;
  height: calc(100vh - 180px);
  min-height: 600px;
}

.va-left {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.va-center {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.va-ops {
  height: 100%;
  overflow: visible;
  display: flex;
  flex-direction: column;
}

.va-card {
  margin-bottom: 16px;
}

.va-control-group {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.va-left-actions {
  margin-top: 12px;
}

.va-label {
  font-size: 13px;
  color: #555;
}

.va-ops-card {
  height: fit-content;
  position: sticky;
  top: 12px;
}

/* 操作栏下方「灵动岛」 */
.va-bottom-dock {
  position: fixed;
  left: 63%;
  /* 以屏幕中线为锚点：先 -50% 居中，再向右偏移；这样药丸变宽会从中间向两侧扩张 */
  transform: translateX(
    calc(-50% + var(--va-dock-shift-x, clamp(140px, 24vw, 520px)))
  );
  bottom: var(--va-dock-bottom, 40px); /* 不要太靠下 */
  z-index: 60;
  display: flex;
  justify-content: center;
  pointer-events: none; /* 不阻挡页面点击 */
}

.va-island {
  position: relative; /* 让 canvas 背景正确落在药丸内 */
  border-radius: 999px;
  overflow: hidden;
  height: 34px; /* 空闲：小药丸 */
  width: 168px;
  max-width: 100%;
  align-self: center;
  /* 半透明“玻璃+微色彩”渐变 */
  background: linear-gradient(
    135deg,
    rgba(255, 64, 84, 0.18) 0%,
    rgba(67, 138, 255, 0.16) 52%,
    rgba(255, 255, 255, 0.1) 100%
  );
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(255, 255, 255, 0.38);
  box-shadow: 0 12px 30px rgba(17, 24, 39, 0.14);
  transition:
    height 260ms cubic-bezier(0.2, 0.8, 0.2, 1),
    width 260ms cubic-bezier(0.2, 0.8, 0.2, 1),
    box-shadow 260ms cubic-bezier(0.2, 0.8, 0.2, 1),
    border-color 260ms cubic-bezier(0.2, 0.8, 0.2, 1);
  pointer-events: auto;
}

.va-island.recording {
  height: 46px; /* 录音：变大一点，但不高 */
  width: 276px; /* 录音：更宽 */
  border-color: rgba(255, 255, 255, 0.7);
  box-shadow:
    0 16px 40px rgba(17, 24, 39, 0.14),
    0 0 0 1px rgba(255, 255, 255, 0.22) inset;
}

.va-island-bg {
  position: absolute;
  inset: 0;
  opacity: 0;
  transition: opacity 220ms ease;
}

.va-island.recording .va-island-bg {
  opacity: 1;
}

.va-island-wave {
  width: 100%;
  height: 100%;
  display: block;
  opacity: 0.95;
}

.va-island-fg {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  color: rgba(17, 24, 39, 0.92);
}

.va-island-left {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  min-width: 96px;
}

.va-island-dot {
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: rgba(17, 24, 39, 0.26);
  box-shadow: 0 0 0 4px rgba(17, 24, 39, 0.06);
  transition:
    background 200ms ease,
    box-shadow 200ms ease;
}

.va-island-dot.on {
  background: rgba(255, 64, 84, 0.95);
  box-shadow:
    0 0 0 4px rgba(255, 64, 84, 0.18),
    0 0 18px rgba(255, 64, 84, 0.35);
}

.va-island-text {
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.2px;
}

.va-island-right {
  display: none;
}

.va-ops-actions,
.va-ops-export {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.va-synthesize-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.va-ops-divider {
  height: 1px;
  background: rgba(0, 0, 0, 0.06);
  margin: 14px 0;
}

.va-tip {
  margin-top: 8px;
}

.va-ai-card {
  margin-top: 8px;
  overflow: hidden;
  position: relative;
}

.va-ai-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  position: relative;
  z-index: 1;
}

.ai-feature-item {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 6px;
  padding: 12px 12px 10px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.65);
  border: 1px solid rgba(255, 154, 158, 0.2);
  cursor: pointer;
  transition: all 0.22s ease;
  position: relative;
}

.ai-feature-item:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 154, 158, 0.55);
  box-shadow: 0 4px 16px rgba(255, 117, 140, 0.15);
  transform: translateY(-1px);
}

.ai-feature-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #fff;
}

.ai-icon-summary {
  background: linear-gradient(135deg, #ff7eb3 0%, #ff758c 100%);
  box-shadow: 0 3px 10px rgba(255, 117, 140, 0.4);
}

.ai-icon-keywords {
  background: linear-gradient(135deg, #6dd5fa 0%, #2980b9 100%);
  box-shadow: 0 3px 10px rgba(41, 128, 185, 0.4);
}

.ai-icon-structure {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  box-shadow: 0 3px 10px rgba(245, 87, 108, 0.4);
}

.ai-icon-multilang {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  box-shadow: 0 3px 10px rgba(79, 172, 254, 0.4);
}

.ai-feature-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.ai-feature-title {
  font-size: 13px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.ai-feature-desc {
  font-size: 11px;
  color: #999;
  line-height: 1.3;
}

.ai-feature-badge {
  position: absolute;
  top: 6px;
  right: 6px;
  font-size: 9px;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(135deg, #ff7eb3 0%, #ff758c 100%);
  padding: 1px 5px;
  border-radius: 6px;
  letter-spacing: 0.3px;
}

/* 装饰性圆形背景 */
.ai-card-decoration {
  position: absolute;
  bottom: -10px;
  right: -10px;
  pointer-events: none;
  z-index: 0;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
}

.deco-1 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  top: 0;
  right: 0;
}

.deco-2 {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #6dd5fa, #2980b9);
  bottom: 20px;
  right: 50px;
}

.va-live-card {
  margin-bottom: 16px;
}

.va-translation-card {
  margin-bottom: 16px;
}

.va-translation-content {
  min-height: 120px;
  padding: 16px;
  border-radius: 8px;
  background: radial-gradient(
    circle at top left,
    #fff7f7 0,
    #ffffff 45%,
    #f7fbff 100%
  );
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.va-translation-text {
  font-size: 18px;
  line-height: 1.8;
  font-weight: 600;
  color: #222;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.va-translation-empty {
  color: #999;
  font-style: italic;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80px;
}

.va-live-subtitle {
  border-radius: 16px;
  padding: 18px 20px;
  background: radial-gradient(
    circle at top left,
    #fff7f7 0,
    #ffffff 45%,
    #f7fbff 100%
  );
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.04);
  min-height: 96px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.va-line {
  white-space: pre-wrap;
}

.va-line.main {
  font-size: 18px;
  font-weight: 600;
  color: #222;
}

.va-history-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* Ant Card 默认 body 不是 flex，会导致内部滚动区无法正确“占满剩余高度”，从而被父级 overflow 裁切 */
.va-history-card :deep(.ant-card-body) {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.va-history-scroll-container {
  flex: 1;
  overflow-y: auto;
  min-height: 0; /* 允许在 flex 容器中收缩，否则容易被父级 overflow:hidden 裁掉 */
  padding-bottom: calc(
    var(--va-dock-bottom, 40px) + 92px
  ); /* Dock(含间距) 的安全区，避免最后几行被遮住 */
}

/* 字幕表格：允许长文本换行，避免“显示不到/挤成一团” */
.va-history-card :deep(.ant-table-cell) {
  white-space: normal;
  word-break: break-word;
  line-height: 1.5;
}

.va-history-scroll-container::-webkit-scrollbar {
  width: 8px;
}

.va-history-scroll-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.va-history-scroll-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.va-history-scroll-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.va-empty-text {
  margin-top: 12px;
  font-size: 13px;
  color: #999;
}

@media (max-width: 1024px) {
  .va-main {
    grid-template-columns: 1fr;
  }
  .voice-assistant-page {
    width: 100%;
    max-width: none;
    padding: 8px 12px 24px;
  }
}

/* ====== AI 对话内容总结卡片样式 ====== */
.va-summary-card {
  margin-top: 12px;
  overflow: hidden;
  position: relative;
  transition: all 0.22s ease;
  border: 1px solid rgba(255, 154, 158, 0.15);
}

.va-summary-card:hover {
  border-color: rgba(255, 117, 140, 0.4);
  box-shadow: 0 4px 20px rgba(255, 117, 140, 0.15);
  transform: translateY(-1px);
}

.summary-card-content {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 4px 0;
}

.summary-card-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  background: linear-gradient(135deg, #ff7eb3 0%, #ff758c 100%);
  box-shadow: 0 3px 12px rgba(255, 117, 140, 0.4);
  flex-shrink: 0;
}

.summary-card-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.summary-card-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.summary-card-desc {
  font-size: 11px;
  color: #999;
  line-height: 1.4;
}

.summary-card-arrow {
  font-size: 14px;
  color: #bbb;
  transition: transform 0.2s, color 0.2s;
}

.summary-card-content:hover .summary-card-arrow {
  transform: translateX(3px);
  color: #ff758c;
}

.summary-card-hint {
  margin-top: 10px;
  font-size: 11px;
  color: #bbb;
  display: flex;
  align-items: center;
  gap: 4px;
  padding-top: 8px;
  border-top: 1px dashed rgba(0, 0, 0, 0.06);
}

.summary-card-hint-active {
  color: #52c41a;
}

/* ====== 对话内容总结弹窗样式 ====== */
.summary-modal :deep(.ant-modal-content) {
  border-radius: 16px;
  overflow: hidden;
}

.summary-modal-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 16px;
}

.summary-modal-icon {
  font-size: 22px;
  color: #ff758c;
}

.summary-modal-body {
  min-height: 400px;
  max-height: calc(70vh - 60px);
  overflow-y: auto;
  padding-right: 4px;
}

.summary-modal-body::-webkit-scrollbar {
  width: 6px;
}

.summary-modal-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.summary-modal-body::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.summary-modal-body::-webkit-scrollbar-thumb:hover {
  background: #aaa;
}

/* 加载状态 */
.summary-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  gap: 16px;
  color: #666;
}

.summary-loading-icon {
  font-size: 36px;
  color: #ff758c;
  animation: summary-pulse 1.5s ease-in-out infinite;
}

@keyframes summary-pulse {
  0%, 100% { opacity: 0.4; transform: scale(0.95); }
  50% { opacity: 1; transform: scale(1.05); }
}

/* 错误状态 */
.summary-error {
  padding: 20px;
}

/* 对话内容总结内容布局 */
.summary-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 20px;
  min-height: 350px;
}

.summary-main {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-sidebar {
  display: flex;
  flex-direction: column;
  gap: 12px;
  border-left: 1px solid #f0f0f0;
  padding-left: 20px;
}

/* 会议摘要区域 */
.summary-section {
  background: #fafafa;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #f0f0f0;
}

.summary-section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.summary-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.summary-section-body {
  color: #555;
  font-size: 13px;
  line-height: 1.7;
}

.summary-text {
  margin: 0 0 8px 0;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.summary-text-secondary {
  color: #888;
  font-size: 12px;
  margin-bottom: 0;
}

.summary-list {
  margin: 0;
  padding-left: 20px;
}

.summary-list-item {
  margin-bottom: 6px;
  line-height: 1.6;
}

.summary-list-item:last-child {
  margin-bottom: 0;
}

.summary-tag {
  margin-right: 6px;
  margin-bottom: 6px;
}

/* 右侧操作按钮 */
.summary-actions {
  display: flex;
  gap: 8px;
}

/* 追问区域 */
.followup-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 0;
}

.followup-header {
  font-size: 13px;
  font-weight: 600;
  color: #555;
  display: flex;
  align-items: center;
  gap: 6px;
}

.followup-messages {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 280px;
  min-height: 80px;
  padding-right: 4px;
}

.followup-messages::-webkit-scrollbar {
  width: 4px;
}

.followup-messages::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.followup-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #bbb;
  font-size: 12px;
  min-height: 80px;
}

.followup-message {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

.followup-message-user {
  flex-direction: row-reverse;
}

.followup-message-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  flex-shrink: 0;
}

.followup-message-user .followup-message-avatar {
  background: #e6f7ff;
  color: #1890ff;
}

.followup-message-ai .followup-message-avatar {
  background: #fff1f0;
  color: #ff4d4f;
}

.followup-message-content {
  max-width: 80%;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.followup-message-user .followup-message-content {
  background: #1890ff;
  color: #fff;
}

.followup-message-ai .followup-message-content {
  background: #f5f5f5;
  color: #333;
}

.followup-input {
  display: flex;
  gap: 8px;
  align-items: center;
}

.followup-input :deep(.ant-input) {
  border-radius: 20px;
}

.followup-input :deep(.ant-btn) {
  border-radius: 50%;
  width: 32px;
  height: 32px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 空状态 */
.summary-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .summary-content {
    grid-template-columns: 1fr;
  }

  .summary-sidebar {
    border-left: none;
    padding-left: 0;
    border-top: 1px solid #f0f0f0;
    padding-top: 16px;
  }

  .summary-modal :deep(.ant-modal) {
    max-width: 95vw;
  }
}
</style>
