<template>
  <div class="ai-widget-root">
    <!-- 全局悬浮聊天组件 -->

  <!-- ========== 1. 收起状态：左下角悬浮小乐按钮 ========== -->
  <transition name="float-btn">
    <div
      v-show="!isExpanded"
      class="ai-float-btn"
      @click="openChat"
      title="和小乐聊天"
    >
      <div class="ai-float-btn-avatar">
        <RobotOutlined class="ai-float-btn-icon" />
      </div>
      <div class="ai-float-btn-text">小乐</div>
      <!-- 未读角标 -->
      <div v-if="hasUnread" class="ai-float-btn-badge">1</div>
      <!-- 消息气泡 -->
      <div class="ai-float-btn-tip">点击和我聊聊吧 👋</div>
    </div>
  </transition>

  <!-- ========== 2. 展开状态：聊天窗口 ========== -->
  <transition name="chat-window">
    <div
      v-show="isExpanded"
      class="ai-chat-window"
      :style="windowStyle"
      ref="windowRef"
    >
      <!-- 标题栏 -->
      <div class="ai-chat-titlebar" @mousedown="startDrag">
        <div class="ai-chat-titlebar-left">
          <div class="ai-chat-titlebar-avatar">
            <RobotOutlined />
          </div>
          <div class="ai-chat-titlebar-info">
            <span class="ai-chat-titlebar-name">小乐 AI 助手</span>
            <span class="ai-chat-titlebar-status">
              <span class="ai-status-dot"></span>
              在线
            </span>
          </div>
        </div>
        <div class="ai-chat-titlebar-actions">
          <button class="ai-titlebar-btn" @click="minimizeChat" title="最小化">
            <MinusOutlined />
          </button>
          <button class="ai-titlebar-btn ai-titlebar-close" @click="closeChat" title="关闭">
            <CloseOutlined />
          </button>
        </div>
      </div>

      <!-- 主体内容 -->
      <div class="ai-chat-body">
        <!-- 左侧：会话列表 -->
        <div class="ai-chat-sidebar" :class="{ 'sidebar-collapsed': !sidebarVisible }">
          <div class="ai-sidebar-header">
            <span v-if="sidebarVisible">会话列表</span>
            <button class="ai-sidebar-toggle" @click="toggleSidebar">
              <MenuOutlined />
            </button>
          </div>

          <div v-if="sidebarVisible" class="ai-sidebar-content">
            <!-- 新建会话按钮 -->
            <button class="ai-new-session-btn" @click="createSession">
              <PlusOutlined /> 新建会话
            </button>

            <!-- 会话列表 -->
            <div class="ai-session-list">
              <div
                v-for="session in sessionList"
                :key="session.sessionUuid"
                class="ai-session-item"
                :class="{ active: session.sessionUuid === currentSessionUuid }"
                @click="selectSession(session.sessionUuid)"
              >
                <div class="ai-session-item-content">
                  <div class="ai-session-item-title">{{ session.sessionTitle }}</div>
                  <div class="ai-session-item-preview">{{ session.lastMessage || '新对话' }}</div>
                </div>
                <button
                  class="ai-session-delete-btn"
                  @click.stop="confirmDelete(session.sessionUuid)"
                  title="删除会话"
                >
                  <DeleteOutlined />
                </button>
              </div>

              <div v-if="sessionList.length === 0" class="ai-session-empty">
                暂无会话记录
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧：聊天区域 -->
        <div class="ai-chat-main">
          <!-- 欢迎页（无消息时） -->
          <div v-if="messages.length === 0" class="ai-chat-welcome">
            <div class="ai-welcome-avatar">
              <RobotOutlined />
            </div>
            <h3>你好！我是小乐 👋</h3>
            <p v-if="isLoggedIn">
              我是您的智能语音助手，可以帮您解答问题、介绍系统功能、操作指引等。
            </p>
            <p v-else>
              登录后即可和我聊天，我可以帮您解答问题、介绍系统功能、操作指引等。
            </p>
            <div class="ai-welcome-tips">
              <div class="ai-welcome-tip" @click="sendQuickQuestion('这个系统有哪些功能？')">
                <CompassOutlined /> 这个系统有哪些功能？
              </div>
              <div class="ai-welcome-tip" @click="sendQuickQuestion('如何使用语音识别功能？')">
                <SoundOutlined /> 如何使用语音识别功能？
              </div>
              <div class="ai-welcome-tip" @click="sendQuickQuestion('如何导出字幕文件？')">
                <FileTextOutlined /> 如何导出字幕文件？
              </div>
              <div class="ai-welcome-tip" @click="sendQuickQuestion('什么是实时同传？')">
                <AudioOutlined /> 什么是实时同传？
              </div>
            </div>
          </div>

          <!-- 消息列表 -->
          <div v-else class="ai-message-list" ref="messageListRef">
            <div
              v-for="(msg, index) in messages"
              :key="index"
              class="ai-message-item"
              :class="'ai-message-' + msg.role"
            >
              <!-- 头像 -->
              <div class="ai-message-avatar">
                <UserOutlined v-if="msg.role === 'user'" />
                <RobotOutlined v-else />
              </div>

              <!-- 消息内容 -->
              <div class="ai-message-bubble">
                <!-- 加载中的打字动画 -->
                <div v-if="msg.role === 'assistant' && msg.loading" class="ai-typing">
                  <span class="ai-typing-dot"></span>
                  <span class="ai-typing-dot"></span>
                  <span class="ai-typing-dot"></span>
                </div>
                <div v-else class="ai-message-content" v-html="renderMarkdown(msg.content)"></div>
                <div class="ai-message-time">{{ formatTime(msg.createdAt) }}</div>
              </div>
            </div>
          </div>

          <!-- 输入区域 -->
          <div class="ai-chat-input-area">
            <div class="ai-chat-input-wrapper">
              <textarea
                ref="inputRef"
                v-model="inputText"
                class="ai-chat-input"
                placeholder="输入消息，Shift+Enter 换行，Enter 发送..."
                :disabled="inputDisabled"
                @keydown="handleInputKeydown"
                rows="1"
                @input="autoResizeInput"
              ></textarea>
              <button
                class="ai-chat-send-btn"
                :class="{ disabled: !canSend }"
                :disabled="!canSend"
                @click="sendMessage"
              >
                <SendOutlined />
              </button>
            </div>
            <div class="ai-chat-input-hint">
              <span v-if="isLoggedIn">按 Enter 发送 · Shift+Enter 换行</span>
              <span v-else class="ai-login-hint" @click="goLogin">
                登录后即可和小乐聊天
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </transition>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";
import {
  RobotOutlined,
  UserOutlined,
  PlusOutlined,
  DeleteOutlined,
  SendOutlined,
  CloseOutlined,
  MinusOutlined,
  MenuOutlined,
  CompassOutlined,
  SoundOutlined,
  FileTextOutlined,
  AudioOutlined,
} from "@ant-design/icons-vue";
import { message, Modal } from "ant-design-vue";
import store from "../store/index.js";

// ============ 状态定义 ============

// 窗口展开/收起状态
const isExpanded = ref(false);
const hasUnread = ref(false);

// 会话列表
const sessionList = ref([]);
const currentSessionUuid = ref(null);
const sidebarVisible = ref(true);

// 消息列表
const messages = ref([]);
const inputText = ref("");
const isLoading = ref(false);

// DOM refs
const windowRef = ref(null);
const messageListRef = ref(null);
const inputRef = ref(null);

// 拖拽相关
const windowStyle = ref({
  left: "auto",
  right: "24px",
  bottom: "24px",
  top: "auto",
});
const isDragging = ref(false);
const dragOffset = ref({ x: 0, y: 0 });

// ============ 计算属性 ============

const canSend = computed(() => {
  return inputText.value.trim().length > 0
    && !isLoading.value
    && currentSessionUuid.value
    && store.state.member?.token;
});

const isLoggedIn = computed(() => !!store.state.member?.token);

const inputDisabled = computed(() => {
  return !currentSessionUuid.value;
});

// ============ 窗口操作 ============
const router = useRouter();

const goLogin = () => {
  router.push("/login");
};

const openChat = () => {
  isExpanded.value = true;
  hasUnread.value = false;
  nextTick(() => {
    scrollToBottom();
    if (inputRef.value) {
      inputRef.value.focus();
    }
  });
  // 如果已登录且还没有加载会话，则加载最近会话
  if (store.state.member?.token && !currentSessionUuid.value) {
    loadRecentSession();
  }
  // 如果未登录，清空消息并提示
  if (!store.state.member?.token) {
    messages.value = [];
    currentSessionUuid.value = null;
  }
};

const minimizeChat = () => {
  isExpanded.value = false;
};

const closeChat = () => {
  isExpanded.value = false;
};

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value;
};

// ============ 拖拽功能 ============

const startDrag = (e) => {
  if (e.target.closest(".ai-chat-titlebar-actions")) return;
  isDragging.value = true;
  const rect = windowRef.value.getBoundingClientRect();
  dragOffset.value = {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top,
  };

  const onMouseMove = (e) => {
    if (!isDragging.value) return;
    const x = e.clientX - dragOffset.value.x;
    const y = e.clientY - dragOffset.value.y;
    const maxX = window.innerWidth - windowRef.value.offsetWidth;
    const maxY = window.innerHeight - windowRef.value.offsetHeight;
    windowStyle.value = {
      left: Math.max(0, Math.min(x, maxX)) + "px",
      right: "auto",
      top: Math.max(0, Math.min(y, maxY)) + "px",
      bottom: "auto",
    };
  };

  const onMouseUp = () => {
    isDragging.value = false;
    document.removeEventListener("mousemove", onMouseMove);
    document.removeEventListener("mouseup", onMouseUp);
  };

  document.addEventListener("mousemove", onMouseMove);
  document.addEventListener("mouseup", onMouseUp);
};

// ============ 会话管理 ============

const loadRecentSession = async () => {
  try {
    const resp = await axios.get("/web/ai/chat/recent");
    if (resp.data.success) {
      currentSessionUuid.value = resp.data.content.sessionUuid;
      messages.value = (resp.data.content.messages || []).map((m) => ({
        role: m.role,
        content: m.content,
        createdAt: m.createdAt,
        aiSkill: m.aiSkill,
      }));
      // 从消息中提取会话标题，构建会话列表
      buildSessionList(resp.data.content);
    }
  } catch (e) {
    console.warn("加载最近会话失败:", e);
  }
};

// 从 recent 接口响应中提取/构建会话列表
const buildSessionList = (data) => {
  if (data.sessionUuid) {
    // 查找是否已存在
    const exists = sessionList.value.find((s) => s.sessionUuid === data.sessionUuid);
    if (!exists) {
      sessionList.value.unshift({
        sessionUuid: data.sessionUuid,
        sessionTitle: data.sessionTitle || "新对话",
        lastMessage: extractLastMessage(data.messages),
      });
    } else {
      // 更新现有会话
      exists.sessionTitle = data.sessionTitle || exists.sessionTitle;
      exists.lastMessage = extractLastMessage(data.messages);
    }
  }
};

const extractLastMessage = (msgs) => {
  if (!msgs || msgs.length === 0) return "新对话";
  const last = msgs[msgs.length - 1];
  if (!last) return "新对话";
  const content = last.content || "";
  return content.length > 18 ? content.substring(0, 18) + "..." : content;
};

const createSession = async () => {
  try {
    const resp = await axios.post("/web/ai/chat/create", {});
    if (resp.data.success) {
      currentSessionUuid.value = resp.data.content.sessionUuid;
      messages.value = [];
      hasUnread.value = false;
      // 添加到会话列表
      sessionList.value.unshift({
        sessionUuid: resp.data.content.sessionUuid,
        sessionTitle: resp.data.content.sessionTitle || "新对话",
        lastMessage: "新对话",
      });
      message.success("新会话已创建");
    }
  } catch (e) {
    message.error("创建会话失败，请重试");
  }
};

const selectSession = async (sessionUuid) => {
  if (sessionUuid === currentSessionUuid.value) return;
  currentSessionUuid.value = sessionUuid;
  await loadSessionHistory(sessionUuid);
};

const loadSessionHistory = async (sessionUuid) => {
  try {
    const resp = await axios.get(`/web/ai/chat/history/${sessionUuid}`);
    if (resp.data.success) {
      messages.value = (resp.data.content.messages || []).map((m) => ({
        role: m.role,
        content: m.content,
        createdAt: m.createdAt,
        aiSkill: m.aiSkill,
      }));
      // 更新会话列表中的标题和末条消息
      const session = sessionList.value.find((s) => s.sessionUuid === sessionUuid);
      if (session) {
        session.sessionTitle = resp.data.content.sessionTitle || session.sessionTitle;
        session.lastMessage = extractLastMessage(resp.data.content.messages);
      }
      nextTick(() => scrollToBottom());
    }
  } catch (e) {
    console.warn("加载会话历史失败:", e);
  }
};

const confirmDelete = (sessionUuid) => {
  Modal.confirm({
    title: "确定删除该会话？",
    okText: "删除",
    cancelText: "取消",
    okType: "danger",
    onOk() {
      deleteSession(sessionUuid);
    },
  });
};

const deleteSession = async (sessionUuid) => {
  try {
    const resp = await axios.delete(`/web/ai/chat/${sessionUuid}`);
    if (resp.data.success) {
      // 从列表中移除
      const idx = sessionList.value.findIndex((s) => s.sessionUuid === sessionUuid);
      if (idx !== -1) sessionList.value.splice(idx, 1);
      // 如果删除的是当前会话，切换到最近一个
      if (sessionUuid === currentSessionUuid.value) {
        if (sessionList.value.length > 0) {
          await selectSession(sessionList.value[0].sessionUuid);
        } else {
          // 没有会话了，创建新会话
          await createSession();
        }
      }
      message.success("会话已删除");
    }
  } catch (e) {
    message.error("删除会话失败");
  }
};

// ============ 消息发送 ============

const sendMessage = async () => {
  const content = inputText.value.trim();
  if (!content || isLoading.value || !currentSessionUuid.value) return;

  inputText.value = "";
  if (inputRef.value) {
    inputRef.value.style.height = "auto";
  }

  // 添加用户消息
  const userMsg = { role: "user", content, createdAt: new Date().toISOString() };
  messages.value.push(userMsg);
  nextTick(() => scrollToBottom());

  // 添加 AI 占位消息（loading 状态）
  const aiMsg = { role: "assistant", content: "", loading: true, createdAt: new Date().toISOString() };
  messages.value.push(aiMsg);
  nextTick(() => scrollToBottom());

  isLoading.value = true;

  try {
    const resp = await axios.post(
      "/web/ai/chat/message",
      { sessionUuid: currentSessionUuid.value, content }
    );

    if (resp.data.success) {
      // 后端返回完整消息列表，直接用服务器数据替换本地列表
      const serverMessages = (resp.data.content.messages || []).map((m) => ({
        role: m.role,
        content: m.content,
        createdAt: m.createdAt,
        aiSkill: m.aiSkill,
      }));
      messages.value = serverMessages;
      // 更新会话列表末条预览：用服务器返回的末条消息内容
      if (currentSessionUuid.value && serverMessages.length > 0) {
        const session = sessionList.value.find(
          (s) => s.sessionUuid === currentSessionUuid.value
        );
        if (session) {
          const last = serverMessages[serverMessages.length - 1];
          const text = last.content || "";
          session.lastMessage = text.length > 18 ? text.substring(0, 18) + "..." : text;
        }
      }
    } else {
      // 失败：移除占位 AI 消息
      const idx = messages.value.indexOf(aiMsg);
      if (idx !== -1) messages.value.splice(idx, 1);
      message.error(resp.data.message || "发送失败，请重试");
    }
  } catch (e) {
    // 异常：移除占位 AI 消息
    const idx = messages.value.indexOf(aiMsg);
    if (idx !== -1) messages.value.splice(idx, 1);
    console.error("发送消息失败:", e);
  } finally {
    isLoading.value = false;
    nextTick(() => scrollToBottom());
  }
};

// 快捷问题
const sendQuickQuestion = async (question) => {
  if (!currentSessionUuid.value) {
    await createSession();
  }
  inputText.value = question;
  await sendMessage();
};

// ============ 输入框处理 ============

const handleInputKeydown = (e) => {
  if (e.key === "Enter" && !e.shiftKey) {
    e.preventDefault();
    sendMessage();
  }
};

const autoResizeInput = () => {
  nextTick(() => {
    if (inputRef.value) {
      inputRef.value.style.height = "auto";
      inputRef.value.style.height = Math.min(inputRef.value.scrollHeight, 120) + "px";
    }
  });
};

// ============ 工具函数 ============

const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
    }
  });
};

const formatTime = (timeStr) => {
  if (!timeStr) return "";
  const d = new Date(timeStr);
  return d.toLocaleTimeString("zh-CN", { hour: "2-digit", minute: "2-digit" });
};

// 简单的 Markdown 渲染（支持代码块和换行）
const renderMarkdown = (text) => {
  if (!text) return "";
  let html = text
    // 转义 HTML
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    // 换行
    .replace(/\n/g, "<br>")
    // 代码块
    .replace(/```(\w*)\n?([\s\S]*?)```/g, (match, lang, code) => {
      return `<pre class="ai-code-block"><code>${code.trim()}</code></pre>`;
    })
    // 行内代码
    .replace(/`([^`]+)`/g, "<code class=\"ai-inline-code\">$1</code>")
    // 粗体
    .replace(/\*\*([^*]+)\*\*/g, "<strong>$1</strong>")
    // 斜体
    .replace(/\*([^*]+)\*/g, "<em>$1</em>");
  return html;
};

// ============ 生命周期 ============

onMounted(() => {
  // 页面加载时自动尝试加载最近会话（已登录用户）
  if (store.state.member?.token) {
    loadRecentSession();
  }

  // 监听登录状态变化，登录后加载会话
  watch(
    () => store.state.member?.token,
    (newToken) => {
      if (newToken && !currentSessionUuid.value) {
        loadRecentSession();
      }
    }
  );
});

// ============ 暴露方法供外部调用（如登录后）============

defineExpose({
  openChat,
  closeChat,
  isExpanded,
});
</script>

<style scoped>
/* ========================================================
   全局变量与重置
   ======================================================== */
.ai-widget-root {
  all: initial;
  display: contents;
}

.ai-float-btn,
.ai-chat-window {
  --ai-pink: #ff7eb3;
  --ai-pink-light: #ff9a9e;
  --ai-pink-bg: rgba(255, 126, 179, 0.08);
  --ai-border: rgba(255, 154, 158, 0.3);
  --ai-radius: 16px;
  --ai-shadow: 0 8px 32px rgba(255, 117, 140, 0.3);
  --ai-font: "PingFang SC", "Helvetica Neue", Arial, sans-serif;
  font-family: var(--ai-font);
}

/* ========================================================
   1. 悬浮小乐按钮（收起状态）
   ======================================================== */
.ai-float-btn {
  position: fixed;
  left: 24px;
  bottom: 24px;
  z-index: 9999;
  display: flex;
  align-items: center;
  gap: 10px;
  background: linear-gradient(135deg, #ff7eb3 0%, #ff758c 100%);
  color: #fff;
  border: none;
  border-radius: 999px;
  padding: 10px 18px 10px 12px;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(255, 117, 140, 0.5);
  transition: all 0.3s cubic-bezier(0.2, 0.8, 0.2, 1);
  user-select: none;
}

.ai-float-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 28px rgba(255, 117, 140, 0.6);
}

.ai-float-btn:active {
  transform: translateY(0);
}

.ai-float-btn-avatar {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.ai-float-btn-icon {
  font-size: 20px;
  color: #fff;
}

.ai-float-btn-text {
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.ai-float-btn-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  width: 18px;
  height: 18px;
  background: #ff4d4f;
  color: #fff;
  border-radius: 50%;
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #fff;
}

.ai-float-btn-tip {
  position: absolute;
  left: calc(100% + 12px);
  top: 50%;
  transform: translateY(-50%);
  background: #fff;
  color: #555;
  font-size: 12px;
  padding: 6px 12px;
  border-radius: 8px;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.12);
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

.ai-float-btn:hover .ai-float-btn-tip {
  opacity: 1;
}

/* 悬浮按钮动画 */
.float-btn-enter-active,
.float-btn-leave-active {
  transition: all 0.3s ease;
}
.float-btn-enter-from,
.float-btn-leave-to {
  opacity: 0;
  transform: scale(0.6) translateY(20px);
}

/* ========================================================
   2. 聊天窗口
   ======================================================== */
.ai-chat-window {
  position: fixed;
  z-index: 9998;
  width: 860px;
  height: 580px;
  max-width: calc(100vw - 48px);
  max-height: calc(100vh - 48px);
  background: #fff;
  border-radius: var(--ai-radius);
  box-shadow: var(--ai-shadow), 0 0 0 1px var(--ai-border);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  user-select: none;
}

/* 窗口动画 */
.chat-window-enter-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.chat-window-leave-active {
  transition: all 0.25s ease;
}
.chat-window-enter-from,
.chat-window-leave-to {
  opacity: 0;
  transform: scale(0.8) translateY(30px);
}

/* ========== 标题栏 ========== */
.ai-chat-titlebar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff7eb3 50%, #ff758c 100%);
  cursor: move;
  flex-shrink: 0;
}

.ai-chat-titlebar-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-chat-titlebar-avatar {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
}

.ai-chat-titlebar-info {
  display: flex;
  flex-direction: column;
}

.ai-chat-titlebar-name {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.ai-chat-titlebar-status {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.85);
  display: flex;
  align-items: center;
  gap: 4px;
}

.ai-status-dot {
  width: 6px;
  height: 6px;
  background: #69f0ae;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.ai-chat-titlebar-actions {
  display: flex;
  gap: 6px;
}

.ai-titlebar-btn {
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  border-radius: 50%;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  font-size: 12px;
}

.ai-titlebar-btn:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: scale(1.1);
}

.ai-titlebar-btn.ai-titlebar-close:hover {
  background: rgba(255, 77, 79, 0.8);
}

/* ========== 主体区域 ========== */
.ai-chat-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  min-height: 0;
}

/* ========== 左侧会话列表 ========== */
.ai-chat-sidebar {
  width: 200px;
  flex-shrink: 0;
  background: #faf7f8;
  border-right: 1px solid var(--ai-border);
  display: flex;
  flex-direction: column;
  transition: width 0.25s ease;
  overflow: hidden;
}

.ai-chat-sidebar.sidebar-collapsed {
  width: 48px;
}

.ai-sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 10px 8px;
  font-size: 12px;
  font-weight: 600;
  color: #888;
  border-bottom: 1px solid var(--ai-border);
  min-height: 44px;
}

.sidebar-collapsed .ai-sidebar-header {
  justify-content: center;
  padding: 12px 0;
}

.ai-sidebar-toggle {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 14px;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.ai-sidebar-toggle:hover {
  background: var(--ai-pink-bg);
  color: var(--ai-pink);
}

.ai-sidebar-content {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.ai-sidebar-content::-webkit-scrollbar {
  width: 4px;
}
.ai-sidebar-content::-webkit-scrollbar-thumb {
  background: #f0c8d0;
  border-radius: 2px;
}

.ai-new-session-btn {
  width: 100%;
  padding: 8px;
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-bottom: 8px;
  transition: all 0.2s;
}

.ai-new-session-btn:hover {
  box-shadow: 0 4px 12px rgba(255, 117, 140, 0.4);
  transform: translateY(-1px);
}

.ai-session-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.ai-session-item {
  padding: 8px 8px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
  position: relative;
  border: 1px solid transparent;
}

.ai-session-item:hover {
  background: rgba(255, 126, 179, 0.1);
}

.ai-session-item.active {
  background: rgba(255, 126, 179, 0.18);
  border-color: var(--ai-border);
}

.ai-session-item-content {
  flex: 1;
  min-width: 0;
}

.ai-session-item-title {
  font-size: 12px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ai-session-item-preview {
  font-size: 11px;
  color: #aaa;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-top: 1px;
}

.ai-session-delete-btn {
  width: 22px;
  height: 22px;
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  opacity: 0;
  transition: all 0.2s;
  flex-shrink: 0;
}

.ai-session-item:hover .ai-session-delete-btn {
  opacity: 1;
}

.ai-session-delete-btn:hover {
  background: rgba(255, 77, 79, 0.1);
  color: #ff4d4f;
}

.ai-session-empty {
  text-align: center;
  font-size: 12px;
  color: #bbb;
  padding: 20px 0;
}

/* ========== 右侧聊天区域 ========== */
.ai-chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

/* 欢迎页 */
.ai-chat-welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 40px;
  text-align: center;
  gap: 12px;
}

.ai-welcome-avatar {
  width: 72px;
  height: 72px;
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(255, 117, 140, 0.4);
  margin-bottom: 8px;
}

.ai-chat-welcome h3 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.ai-chat-welcome p {
  font-size: 13px;
  color: #888;
  max-width: 400px;
  line-height: 1.6;
  margin: 0;
}

.ai-welcome-tips {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 8px;
  margin-top: 12px;
  width: 100%;
  max-width: 440px;
}

.ai-welcome-tip {
  padding: 10px 14px;
  background: #fff5f7;
  border: 1px solid rgba(255, 154, 158, 0.25);
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  text-align: left;
  transition: all 0.2s;
}

.ai-welcome-tip:hover {
  background: rgba(255, 126, 179, 0.12);
  border-color: var(--ai-pink);
  color: var(--ai-pink);
  transform: translateY(-1px);
}

/* 消息列表 */
.ai-message-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #fdfafb;
}

.ai-message-list::-webkit-scrollbar {
  width: 6px;
}
.ai-message-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}
.ai-message-list::-webkit-scrollbar-thumb {
  background: #f0c8d0;
  border-radius: 3px;
}

/* 消息气泡 */
.ai-message-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.ai-message-user {
  flex-direction: row-reverse;
}

.ai-message-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
  margin-top: 2px;
}

.ai-message-user .ai-message-avatar {
  background: linear-gradient(135deg, #ff9a9e, #ff758c);
  color: #fff;
}

.ai-message-assistant .ai-message-avatar {
  background: #f5f5f5;
  color: #ff7eb3;
  border: 1px solid rgba(255, 154, 158, 0.2);
}

.ai-message-bubble {
  max-width: 72%;
  min-width: 60px;
}

.ai-message-user .ai-message-bubble {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.ai-message-assistant .ai-message-bubble {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.ai-message-content {
  padding: 10px 14px;
  border-radius: 16px;
  font-size: 13.5px;
  line-height: 1.65;
  word-break: break-word;
}

.ai-message-user .ai-message-content {
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-message-assistant .ai-message-content {
  background: #fff;
  color: #333;
  border: 1px solid rgba(255, 154, 158, 0.2);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.ai-message-time {
  font-size: 10px;
  color: #bbb;
  margin-top: 4px;
  padding: 0 4px;
}

/* 打字动画 */
.ai-typing {
  display: flex;
  gap: 4px;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid rgba(255, 154, 158, 0.2);
  border-radius: 16px;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.ai-typing-dot {
  width: 7px;
  height: 7px;
  background: var(--ai-pink);
  border-radius: 50%;
  animation: typingBounce 1.4s ease-in-out infinite;
}

.ai-typing-dot:nth-child(2) { animation-delay: 0.2s; }
.ai-typing-dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes typingBounce {
  0%, 60%, 100% { transform: translateY(0); opacity: 0.5; }
  30% { transform: translateY(-6px); opacity: 1; }
}

/* 代码块 */
:deep(.ai-code-block) {
  background: #2d2d2d;
  color: #f8f8f2;
  padding: 12px 14px;
  border-radius: 8px;
  font-family: "Fira Code", "Consolas", monospace;
  font-size: 12px;
  line-height: 1.6;
  overflow-x: auto;
  margin: 6px 0;
}

:deep(.ai-code-block code) {
  background: none;
  padding: 0;
  font-size: inherit;
  color: inherit;
}

:deep(.ai-inline-code) {
  background: rgba(255, 126, 179, 0.12);
  color: #d63384;
  padding: 1px 5px;
  border-radius: 4px;
  font-family: "Fira Code", "Consolas", monospace;
  font-size: 12px;
}

/* ========== 输入区域 ========== */
.ai-chat-input-area {
  padding: 10px 14px 12px;
  background: #fff;
  border-top: 1px solid rgba(255, 154, 158, 0.15);
  flex-shrink: 0;
}

.ai-chat-input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  background: #faf7f8;
  border: 1px solid rgba(255, 154, 158, 0.3);
  border-radius: 12px;
  padding: 8px 8px 8px 12px;
  transition: border-color 0.2s;
}

.ai-chat-input-wrapper:focus-within {
  border-color: var(--ai-pink);
  box-shadow: 0 0 0 3px rgba(255, 126, 179, 0.1);
}

.ai-chat-input {
  flex: 1;
  border: none;
  background: transparent;
  outline: none;
  font-size: 13.5px;
  color: #333;
  line-height: 1.5;
  resize: none;
  min-height: 22px;
  max-height: 120px;
  font-family: inherit;
  padding: 0;
}

.ai-chat-input::placeholder {
  color: #bbb;
}

.ai-chat-input:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.ai-chat-send-btn {
  width: 34px;
  height: 34px;
  background: linear-gradient(135deg, #ff7eb3, #ff758c);
  color: #fff;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.ai-chat-send-btn:hover:not(.disabled) {
  box-shadow: 0 4px 12px rgba(255, 117, 140, 0.4);
  transform: translateY(-1px);
}

.ai-chat-send-btn.disabled,
.ai-chat-send-btn:disabled {
  opacity: 0.45;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.ai-chat-input-hint {
  margin-top: 4px;
  font-size: 10px;
  color: #ccc;
  text-align: center;
}

.ai-login-hint {
  color: #ff7eb3;
  cursor: pointer;
  text-decoration: none;
  font-weight: 500;
}

.ai-login-hint:hover {
  text-decoration: underline;
}

/* ========================================================
   响应式
   ======================================================== */
@media (max-width: 768px) {
  .ai-chat-window {
    width: calc(100vw - 16px);
    height: calc(100vh - 60px);
    left: 8px !important;
    right: 8px !important;
    bottom: 8px !important;
    top: auto !important;
  }

  .ai-chat-sidebar {
    width: 160px;
  }

  .ai-float-btn {
    left: 12px;
    bottom: 12px;
  }

  .ai-float-btn-text {
    display: none;
  }

  .ai-welcome-tips {
    grid-template-columns: 1fr;
  }
}
</style>
