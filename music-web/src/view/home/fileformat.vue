<template>
  <div class="container">
    <h3>音频格式转换器</h3>

    <!-- 格式选择区域 -->
    <div class="format-selector">
      <div class="form-group">
        <label>输入格式：</label>
        <select v-model="inputFormat" @change="updateFileInputAccept">
          <option value="flac">FLAC</option>
          <option value="wav">WAV</option>
          <option value="mp3">MP3</option>
          <option value="m4a">M4A</option>
          <option value="ogg">OGG</option>
        </select>
      </div>
      <div class="form-group">
        <label>输出格式：</label>
        <select v-model="outputFormat">
          <option value="mp3">MP3</option>
          <option value="flac">FLAC</option>
          <option value="wav">WAV</option>
          <option value="m4a">M4A</option>
          <option value="ogg">OGG</option>
        </select>
      </div>
    </div>

    <!-- 音频参数调节区域 -->
    <div class="audio-params">
      <h4>音频参数调节</h4>
      <div class="params-grid">
        <div class="form-group">
          <label>比特率：</label>
          <select v-model="audioParams.bitrate">
            <option value="128">128 kbps</option>
            <option value="192">192 kbps</option>
            <option value="256">256 kbps</option>
            <option value="320">320 kbps</option>
          </select>
        </div>
        <div class="form-group">
          <label>采样率：</label>
          <select v-model="audioParams.samplerate">
            <option value="44100">44.1 kHz</option>
            <option value="48000">48 kHz</option>
            <option value="96000">96 kHz</option>
          </select>
        </div>
        <div class="form-group">
          <label>声道：</label>
          <select v-model="audioParams.channels">
            <option value="2">立体声</option>
            <option value="1">单声道</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 文件上传区域 -->
    <div
      class="upload-area"
      @click="openFileDialog"
      @dragover.prevent="highlightDropzone"
      @dragleave="unhighlightDropzone"
      @drop.prevent="handleDrop"
    >
      <p>点击或拖拽{{ inputFormat.toUpperCase() }}文件到此处上传（支持多选）</p>
      <input
        type="file"
        ref="fileInput"
        @change="handleFileSelect"
        :accept="fileInputAccept"
        multiple
      />

      <button class="btn" @click.stop="openFileDialog">选择文件</button>
    </div>

    <!-- 文件列表区域 -->
    <div class="file-list-area" v-if="selectedFiles.length > 0">
      <h4>已选择的文件</h4>
      <div class="file-list">
        <div
          v-for="(file, index) in selectedFiles"
          :key="index"
          class="file-item"
          :class="getTaskStatus(file.name)"
        >
          <div class="file-cover">
            <img
              v-if="file.coverUrl"
              :src="file.coverUrl"
              :alt="file.name + ' 封面'"
              class="cover-image"
            />
            <div v-else class="cover-placeholder">
              <span class="cover-icon">🎵</span>
            </div>
          </div>
          <div class="file-info">
            <div class="file-name">{{ file.name }}</div>
            <div class="file-details">
              <span>大小: {{ formatFileSize(file.size) }}</span>
              <span>类型: {{ file.type || "audio/" + inputFormat }}</span>
            </div>
            <!-- 转换进度条 -->
            <div v-if="getTaskProgress(file.name) >= 0" class="task-progress">
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{ width: getTaskProgress(file.name) + '%' }"
                ></div>
              </div>
              <div class="progress-text">{{ getTaskProgress(file.name) }}%</div>
            </div>
            <!-- 播放器组件（仅在转换完成后显示） -->
            <div
              v-if="
                getTaskStatus(file.name) === 'completed' &&
                getTaskDownloadUrl(file.name)
              "
              class="song-player"
            >
              <div class="player-container">
                <div class="player-controls">
                  <button
                    class="player-btn seek-btn"
                    @click="seekBackward(index)"
                    title="向后跳转15秒"
                  >
                    <svg
                      class="control-icon"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M12 8L8 12L12 16"
                        stroke="#333"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <path
                        d="M16 12H8"
                        stroke="#333"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <text
                        x="18"
                        y="16"
                        font-size="8"
                        font-weight="bold"
                        fill="#333"
                        text-anchor="middle"
                      >
                        15
                      </text>
                    </svg>
                  </button>
                  <button
                    class="player-btn play-btn"
                    @click="togglePlay(index)"
                  >
                    <svg
                      v-if="!isSongPlaying(index)"
                      class="control-icon"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path d="M8 5V19L19 12L8 5Z" fill="#333" />
                    </svg>
                    <svg
                      v-else
                      class="control-icon"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <rect x="6" y="4" width="4" height="16" fill="#333" />
                      <rect x="14" y="4" width="4" height="16" fill="#333" />
                    </svg>
                  </button>
                  <button
                    class="player-btn seek-btn"
                    @click="seekForward(index)"
                    title="向前跳转15秒"
                  >
                    <svg
                      class="control-icon"
                      viewBox="0 0 24 24"
                      fill="none"
                      xmlns="http://www.w3.org/2000/svg"
                    >
                      <path
                        d="M12 16L16 12L12 8"
                        stroke="#333"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <path
                        d="M8 12H16"
                        stroke="#333"
                        stroke-width="2"
                        stroke-linecap="round"
                        stroke-linejoin="round"
                      />
                      <text
                        x="6"
                        y="16"
                        font-size="8"
                        font-weight="bold"
                        fill="#333"
                        text-anchor="middle"
                      >
                        15
                      </text>
                    </svg>
                  </button>
                </div>
                <div class="player-progress">
                  <div class="progress-container">
                    <!-- 进度条背景 -->
                    <div class="progress-background"></div>
                    <!-- 已播放进度 -->
                    <div
                      class="progress-filled"
                      :style="{
                        width:
                          getSongDuration(index) > 0
                            ? (getSongCurrentTime(index) /
                                getSongDuration(index)) *
                                100 +
                              '%'
                            : '0%',
                      }"
                    ></div>
                    <!-- 进度条滑块 -->
                    <input
                      type="range"
                      class="progress-input"
                      :value="getSongCurrentTime(index)"
                      :min="0"
                      :max="getSongDuration(index)"
                      @input="seek(index, $event)"
                    />
                    <!-- 时间显示 -->
                    <div class="time-display">
                      <span class="current-time">{{
                        formatTime(getSongCurrentTime(index))
                      }}</span>
                      <span class="total-time">{{
                        formatTime(getSongDuration(index))
                      }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 错误信息 -->
            <div v-if="getTaskErrorMessage(file.name)" class="error-message">
              {{ getTaskErrorMessage(file.name) }}
            </div>
          </div>
          <div class="file-actions">
            <!-- 下载按钮 -->
            <a
              v-if="
                getTaskStatus(file.name) === 'completed' &&
                getTaskDownloadUrl(file.name)
              "
              :href="getTaskDownloadUrl(file.name)"
              :download="getDownloadFileName(file.name)"
              class="btn btn-success"
            >
              下载
            </a>
            <!-- 删除按钮 -->
            <button
              v-if="
                getTaskStatus(file.name) !== 'processing' &&
                getTaskStatus(file.name) !== 'completed'
              "
              class="btn btn-danger"
              @click="removeFile(index)"
            >
              删除
            </button>
          </div>
        </div>
      </div>

      <!-- 开始转换按钮 -->
      <div class="convert-btn-container">
        <button
          class="btn btn-primary"
          :class="{ loading: status === 'loading' }"
          @click="startConversion"
          :disabled="status === 'loading'"
        >
          <span v-if="status === 'loading'">转换中...</span>
          <span v-else>开始转换</span>
        </button>
      </div>
    </div>

    <!-- 转换结果区域 -->
    <div class="result-area">
      <div v-if="status === 'loading'" class="loading">转换中...</div>
      <div v-else-if="status === 'success'" class="success">
        <p>转换成功，点击下载：</p>
        <a :href="downloadUrl" :download="downloadFilename" target="_blank">
          下载转换后的文件
        </a>
      </div>
      <div v-else-if="status === 'error'" class="error">{{ errorMessage }}</div>
    </div>

    <!-- 批量下载按钮 -->
    <div
      class="batch-download"
      v-if="allTasksCompleted && conversionResults.length > 0"
    >
      <h4>转换完成</h4>
      <button
        class="btn btn-primary"
        @click="downloadAllFiles"
        :disabled="isDownloading"
      >
        <span v-if="!isDownloading">下载所有转换文件 (ZIP)</span>
        <span v-else>下载中...</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import axios from "axios";
import * as mm from "music-metadata";

const fileInput = ref(null);
const status = ref("idle");
const downloadUrl = ref("");
const downloadFilename = ref("converted.mp3");
const errorMessage = ref("");

// 格式选择
const inputFormat = ref("flac");
const outputFormat = ref("mp3");

// 音频参数
const audioParams = ref({
  bitrate: "192",
  samplerate: "44100",
  channels: "2",
});

// 选中的文件列表
const selectedFiles = ref([]);

// WebSocket连接
const ws = ref(null);
const wsConnected = ref(false);

// 任务ID全局变量
const globalTaskId = ref(null);

// 任务总数
const totalTasks = ref(0);

// 转换队列和状态
const conversionQueue = ref([]);
const currentTask = ref(null);
const conversionResults = ref([]);
const allTasksCompleted = ref(false);

// 动态计算文件输入接受类型
const fileInputAccept = computed(() => {
  switch (inputFormat.value) {
    case "flac":
      return ".flac";
    case "wav":
      return ".wav";
    case "mp3":
      return ".mp3";
    case "m4a":
      return ".m4a";
    case "ogg":
      return ".ogg";
    default:
      return ".flac";
  }
});

// 更新文件输入接受类型
const updateFileInputAccept = () => {
  // 触发计算属性更新
  // 如果列表中有文件，需要提示用户确认后才清空
  if (selectedFiles.value.length > 0) {
    if (confirm("切换输入格式将会清空当前已选择的文件，确定要继续吗？")) {
      selectedFiles.value = [];
    }
  }
};

// WebSocket连接管理
const connectWebSocket = () => {
  try {
    // 构建WebSocket URL
    const wsProtocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const wsUrl = `${wsProtocol}//${window.location.host}/ws/conversion`;

    ws.value = new WebSocket(wsUrl);

    ws.value.onopen = () => {
      console.log("WebSocket连接成功");
      wsConnected.value = true;
    };

    ws.value.onmessage = (event) => {
      handleWebSocketMessage(event.data);
    };

    ws.value.onerror = (error) => {
      console.error("WebSocket错误:", error);
    };

    ws.value.onclose = () => {
      console.log("WebSocket连接关闭");
      wsConnected.value = false;
    };
  } catch (error) {
    console.error("WebSocket连接失败:", error);
  }
};

// 发起WebSocket连接并携带用户信息和转换信息
const connectWebSocketWithInfo = async (userInfo, conversionInfo) => {
  return new Promise((resolve, reject) => {
    try {
      // 构建WebSocket URL，连接到后端服务
      // 注意：这里硬编码为后端服务的地址，实际项目中应该通过配置文件管理
      const wsUrl = "ws://localhost:18000/ws/conversion";

      ws.value = new WebSocket(wsUrl);

      ws.value.onopen = () => {
        console.log("WebSocket连接成功");
        wsConnected.value = true;

        // 发送用户信息和转换信息
        const connectionInfo = {
          type: "connect",
          userInfo: userInfo,
          conversionInfo: conversionInfo,
        };
        console.log("发送连接信息:", connectionInfo);
        ws.value.send(JSON.stringify(connectionInfo));
      };

      ws.value.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          console.log("收到WebSocket消息:", data);

          if (data.type === "taskId") {
            // 收到任务ID
            console.log("获取到任务ID:", data.taskId);
            // 保存任务ID到全局变量
            globalTaskId.value = data.taskId;
            resolve(data.taskId);
          } else if (data.type === "error") {
            // 收到错误消息
            console.error("WebSocket错误:", data.message);
            reject(new Error(data.message));
          } else {
            // 其他消息，交给原有的处理函数
            handleWebSocketMessage(event.data);
          }
        } catch (error) {
          console.error("解析WebSocket消息失败:", error);
          handleWebSocketMessage(event.data);
        }
      };

      ws.value.onerror = (error) => {
        console.error("WebSocket错误:", error);
        reject(error);
      };

      ws.value.onclose = (event) => {
        console.log("WebSocket连接关闭:", event);
        wsConnected.value = false;

        // 连接关闭时，如果有任务ID，再次返回给前端
        try {
          const closeData = JSON.parse(event.reason);
          if (closeData.type === "taskId" && closeData.taskId) {
            console.log("连接关闭时收到任务ID:", closeData.taskId);
            // 可以在这里处理连接关闭时的任务ID
          }
        } catch (error) {
          // 解析失败，忽略
        }
      };
    } catch (error) {
      console.error("WebSocket连接失败:", error);
      reject(error);
    }
  });
};

// 处理WebSocket消息
const handleWebSocketMessage = (message) => {
  try {
    const data = JSON.parse(message);

    switch (data.type) {
      case "progress":
        updateConversionProgress(data);
        break;
      case "complete":
        handleConversionComplete(data);
        break;
      case "error":
        handleConversionError(data);
        break;
      default:
        console.log("未知消息类型:", data.type);
    }
  } catch (error) {
    console.error("解析WebSocket消息失败:", error);
  }
};

// 断开WebSocket连接
const disconnectWebSocket = () => {
  if (ws.value) {
    ws.value.close();
    ws.value = null;
  }
};

// 更新转换进度
const updateConversionProgress = (data) => {
  // 精确匹配任务ID
  const taskIndex = conversionQueue.value.findIndex(
    (task) => task.id === data.taskId,
  );
  if (taskIndex !== -1) {
    conversionQueue.value[taskIndex].progress = data.progress;
    conversionQueue.value[taskIndex].status = "processing";
  }
};

// 处理转换完成
const handleConversionComplete = (data) => {
  // 精确匹配任务ID
  const taskIndex = conversionQueue.value.findIndex(
    (task) => task.id === data.taskId && task.status === "processing",
  );
  if (taskIndex !== -1) {
    conversionQueue.value[taskIndex].status = "completed";
    conversionQueue.value[taskIndex].progress = 100;
    conversionQueue.value[taskIndex].downloadUrl = data.downloadUrl;

    // 添加到结果列表
    conversionResults.value.push({
      id: data.taskId,
      fileName: data.fileName,
      downloadUrl: data.downloadUrl,
    });

    // 检查是否所有任务都已完成
    checkAllTasksCompleted();

    // 处理下一个任务
    processNextTask();

    // 检查是否所有任务都已完成
    if (allTasksCompleted.value) {
      // 所有任务都已完成，关闭WebSocket连接
      console.log("所有任务都已完成，关闭WebSocket连接");
      disconnectWebSocket();
    }
  }
};

// 处理转换错误
const handleConversionError = (data) => {
  // 精确匹配任务ID
  const taskIndex = conversionQueue.value.findIndex(
    (task) => task.id === data.taskId && task.status === "processing",
  );
  if (taskIndex !== -1) {
    conversionQueue.value[taskIndex].status = "error";
    conversionQueue.value[taskIndex].errorMessage = data.errorMessage;

    // 处理下一个任务
    processNextTask();

    // 检查是否所有任务都已完成
    checkAllTasksCompleted();
    if (allTasksCompleted.value) {
      // 所有任务都已完成，关闭WebSocket连接
      console.log("所有任务都已完成，关闭WebSocket连接");
      disconnectWebSocket();
    }
  }
};

// 检查是否所有任务都已完成
const checkAllTasksCompleted = () => {
  const allCompleted = conversionQueue.value.every(
    (task) => task.status === "completed" || task.status === "error",
  );
  allTasksCompleted.value = allCompleted;
};

// 处理下一个任务
const processNextTask = async () => {
  const nextTask = conversionQueue.value.find(
    (task) => task.status === "pending",
  );
  if (nextTask) {
    // 只调用startSingleConversion，不立即调用processNextTask
    // 下一个任务的处理由handleConversionComplete或handleConversionError函数触发
    await startSingleConversion(nextTask);
  } else {
    // 所有任务都已处理完成
    checkAllTasksCompleted();
    status.value = "idle";
  }
};

// 打开文件选择对话框
const openFileDialog = () => {
  fileInput.value.click();
};

// 高亮拖放区域
const highlightDropzone = () => {
  const uploadArea = document.querySelector(".upload-area");
  uploadArea.style.border = "2px dashed #4CAF50";
  uploadArea.style.backgroundColor = "rgba(76, 175, 80, 0.1)";
};

// 取消高亮
const unhighlightDropzone = () => {
  const uploadArea = document.querySelector(".upload-area");
  uploadArea.style.border = "2px dashed #ccc";
  uploadArea.style.backgroundColor = "#fff";
};

// 处理文件选择
const handleFileSelect = async (e) => {
  const files = e.target.files;
  if (files.length === 0) return;

  const MAX_SIZE_PER_FILE = 500 * 1024 * 1024; // 500MB per file
  const MAX_TOTAL_SIZE = 500 * 1024 * 1024; // 500MB total

  // 计算现有文件的总大小
  const existingTotalSize = selectedFiles.value.reduce(
    (total, file) => total + file.size,
    0,
  );
  let newFilesTotalSize = 0;

  // 验证文件格式并添加到列表
  const validFiles = [];
  for (const file of Array.from(files)) {
    const isValid = file.name.endsWith(`.${inputFormat.value}`);
    if (!isValid) {
      alert(`${file.name} 不是有效的${inputFormat.toUpperCase()}文件`);
      continue;
    }

    // 检查单个文件大小
    if (file.size > MAX_SIZE_PER_FILE) {
      alert(`${file.name} 超过了单个文件大小限制 (500MB)`);
      continue;
    }

    // 检查文件是否重复
    const isDuplicate = selectedFiles.value.some(
      (existingFile) => existingFile.name === file.name,
    );
    if (isDuplicate) {
      alert(`${file.name} 已经在列表中了`);
      continue;
    }

    validFiles.push({
      name: file.name,
      size: file.size,
      type: file.type,
      originalFile: file, // 保存原始File对象用于后续处理
    });
    newFilesTotalSize += file.size;
  }

  // 检查总大小
  if (existingTotalSize + newFilesTotalSize > MAX_TOTAL_SIZE) {
    alert(`文件总大小超过了限制 (500MB)`);
    return;
  }

  // 提取封面图片
  for (const file of validFiles) {
    const coverUrl = await extractCoverImage(file.originalFile);
    file.coverUrl = coverUrl;
    // 保留originalFile属性，用于后续发送请求
  }

  // 添加到已选择文件列表
  if (validFiles.length > 0) {
    selectedFiles.value = [...selectedFiles.value, ...validFiles];
  }

  // 清空文件输入，以便可以重复选择同一个文件
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

// 处理拖放文件
const handleDrop = async (e) => {
  unhighlightDropzone();
  const files = e.dataTransfer.files;
  if (files.length === 0) return;

  const MAX_SIZE_PER_FILE = 500 * 1024 * 1024; // 500MB per file
  const MAX_TOTAL_SIZE = 500 * 1024 * 1024; // 500MB total

  // 计算现有文件的总大小
  const existingTotalSize = selectedFiles.value.reduce(
    (total, file) => total + file.size,
    0,
  );
  let newFilesTotalSize = 0;

  // 验证文件格式并添加到列表
  const validFiles = [];
  for (const file of Array.from(files)) {
    const isValid = file.name.endsWith(`.${inputFormat.value}`);
    if (!isValid) {
      alert(`${file.name} 不是有效的${inputFormat.toUpperCase()}文件`);
      continue;
    }

    // 检查单个文件大小
    if (file.size > MAX_SIZE_PER_FILE) {
      alert(`${file.name} 超过了单个文件大小限制 (500MB)`);
      continue;
    }

    // 检查文件是否重复
    const isDuplicate = selectedFiles.value.some(
      (existingFile) => existingFile.name === file.name,
    );
    if (isDuplicate) {
      alert(`${file.name} 已经在列表中了`);
      continue;
    }

    validFiles.push({
      name: file.name,
      size: file.size,
      type: file.type,
      originalFile: file, // 保存原始File对象用于后续处理
    });
    newFilesTotalSize += file.size;
  }

  // 检查总大小
  if (existingTotalSize + newFilesTotalSize > MAX_TOTAL_SIZE) {
    alert(`文件总大小超过了限制 (500MB)`);
    return;
  }

  // 提取封面图片
  for (const file of validFiles) {
    const coverUrl = await extractCoverImage(file.originalFile);
    file.coverUrl = coverUrl;
    // 保留originalFile属性，用于后续发送请求
  }

  // 添加到已选择文件列表
  if (validFiles.length > 0) {
    selectedFiles.value = [...selectedFiles.value, ...validFiles];
  }
};

// 移除文件
const removeFile = (index) => {
  selectedFiles.value.splice(index, 1);
};

// 开始转换
const startConversion = async () => {
  if (selectedFiles.value.length === 0) {
    alert("请先选择文件");
    return;
  }

  // 重置状态
  status.value = "loading";
  downloadUrl.value = "";
  errorMessage.value = "";
  conversionQueue.value = [];
  conversionResults.value = [];
  allTasksCompleted.value = false;

  try {
    // 构建用户信息和转换信息
    const userInfo = {
      userId: "current_user", // 实际项目中应该从登录状态获取
      userName: "当前用户",
      userEmail: "user@example.com",
    };

    const conversionInfo = {
      inputFormat: inputFormat.value,
      outputFormat: outputFormat.value,
      bitrate: audioParams.value.bitrate,
      samplerate: audioParams.value.samplerate,
      channels: audioParams.value.channels,
      files: selectedFiles.value.map((file) => ({
        fileName: file.name,
        fileSize: file.size,
      })),
    };

    // 发起WebSocket连接，携带用户信息和转换信息
    console.log("发起WebSocket连接，携带用户信息和转换信息");
    const taskId = await connectWebSocketWithInfo(userInfo, conversionInfo);

    if (!taskId) {
      throw new Error("获取任务ID失败");
    }

    console.log("获取到任务ID:", taskId);

    // 记录任务总数
    totalTasks.value = selectedFiles.value.length;

    // 构建转换队列，使用后端生成的任务ID，为每个文件添加唯一索引
    for (let i = 0; i < selectedFiles.value.length; i++) {
      const file = selectedFiles.value[i];
      conversionQueue.value.push({
        id: `${taskId}_${i}`, // 使用后端生成的任务ID加上文件索引
        originalTaskId: taskId, // 保存原始任务ID用于批量下载
        fileName: file.name,
        file: file,
        status: "pending",
        progress: 0,
        errorMessage: "",
      });
    }

    // 开始处理第一个任务，串行处理每个文件
    // 处理完当前任务后，会自动处理下一个任务
    await processNextTask();
  } catch (error) {
    console.error("开始转换失败:", error);
    errorMessage.value = "开始转换失败: " + error.message;
    status.value = "error";
  }
};

// 开始单个文件转换
const startSingleConversion = async (task) => {
  currentTask.value = task;
  task.status = "processing";

  // 任务ID已经在WebSocket连接时绑定，不需要单独调用绑定接口

  const formData = new FormData();

  // 添加文件
  if (task.file.originalFile) {
    formData.append("files", task.file.originalFile);
  }

  // 添加转换参数
  formData.append("inputFormat", inputFormat.value);
  formData.append("outputFormat", outputFormat.value);
  formData.append("bitrate", audioParams.value.bitrate);
  formData.append("samplerate", audioParams.value.samplerate);
  formData.append("channels", audioParams.value.channels);
  formData.append("taskId", task.id); // 使用完整的任务ID

  try {
    const response = await axios.post(
      "/web/fileformat/convert-single",
      formData,
      {
        responseType: "blob",
        headers: {
          "Content-Type": "multipart/form-data",
        },
      },
    );

    if (response.status !== 200) {
      throw new Error(`服务器错误：${response.status}`);
    }

    // 解析响应头获取文件名和类型
    const contentDisposition = response.headers["content-disposition"];
    const contentType = response.headers["Content-Type"];

    let fileName = task.fileName;
    if (contentDisposition) {
      const filenameMatch = contentDisposition.match(/filename=([^;]+)/);
      if (filenameMatch) {
        fileName = decodeURIComponent(filenameMatch[1]);
      }
    }

    // 创建 Blob 并生成下载链接
    const blob = new Blob([response.data], { type: contentType });
    const url = URL.createObjectURL(blob);

    // 更新任务状态
    task.status = "completed";
    task.progress = 100;
    task.downloadUrl = url;
    task.fileName = fileName;

    // 添加到结果列表
    conversionResults.value.push({
      id: task.id,
      fileName: fileName,
      downloadUrl: url,
    });

    // 检查是否所有任务都已完成
    checkAllTasksCompleted();

    // 移除这里的 processNextTask() 调用
    // 只保留 WebSocket 消息处理函数中的调用
  } catch (error) {
    console.error("转换失败:", error);
    task.status = "error";
    task.errorMessage =
      error.response?.data || "转换失败，请检查文件格式或重试";

    // 移除这里的 processNextTask() 调用
    // 只保留 WebSocket 消息处理函数中的调用
  }
};

// 提取音频文件封面图片
const extractCoverImage = async (file) => {
  try {
    // 使用music-metadata库提取元数据
    const metadata = await mm.parseBlob(file);

    // 检查是否有图片
    if (metadata.common.picture && metadata.common.picture.length > 0) {
      const picture = metadata.common.picture[0];
      const blob = new Blob([picture.data], { type: picture.format });
      return URL.createObjectURL(blob);
    }
    return null;
  } catch (error) {
    console.error("提取封面图片失败:", error);
    return null;
  }
};

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return "0 B";
  const k = 1024;
  const sizes = ["B", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: "等待中",
    processing: "处理中",
    completed: "已完成",
    error: "错误",
  };
  return statusMap[status] || status;
};

// 根据文件名获取任务
const getTaskByFileName = (fileName) => {
  return conversionQueue.value.find((task) => task.fileName === fileName);
};

// 根据文件名获取任务状态
const getTaskStatus = (fileName) => {
  const task = getTaskByFileName(fileName);
  return task ? task.status : null;
};

// 根据文件名获取任务进度
const getTaskProgress = (fileName) => {
  const task = getTaskByFileName(fileName);
  return task ? task.progress : -1;
};

// 根据文件名获取任务错误信息
const getTaskErrorMessage = (fileName) => {
  const task = getTaskByFileName(fileName);
  return task ? task.errorMessage : "";
};

// 根据文件名获取任务下载链接
const getTaskDownloadUrl = (fileName) => {
  const task = getTaskByFileName(fileName);
  return task ? task.downloadUrl : null;
};

// 根据文件名和输出格式获取下载文件名（替换后缀）
const getDownloadFileName = (fileName) => {
  // 获取文件名的基础部分（去掉原始后缀）
  const lastDotIndex = fileName.lastIndexOf(".");
  const baseName =
    lastDotIndex > 0 ? fileName.substring(0, lastDotIndex) : fileName;
  // 返回带输出格式后缀的文件名
  return baseName + "." + outputFormat.value;
};

// 下载状态
const isDownloading = ref(false);

// 每个文件的播放器状态管理
const playerStates = ref(new Map());

// 初始化文件的播放器状态
const initPlayerState = (fileName, url) => {
  if (!playerStates.value.has(fileName)) {
    playerStates.value.set(fileName, {
      audioElement: null,
      url: url,
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      isLoaded: false,
    });
  }
};

// 获取文件的播放器状态
const getPlayerState = (fileName) => {
  return (
    playerStates.value.get(fileName) || {
      audioElement: null,
      url: "",
      isPlaying: false,
      currentTime: 0,
      duration: 0,
      isLoaded: false,
    }
  );
};

// 检查文件是否正在播放
const isSongPlaying = (index) => {
  const fileName = selectedFiles.value[index].name;
  return getPlayerState(fileName).isPlaying;
};

// 获取文件的当前播放时间
const getSongCurrentTime = (index) => {
  const fileName = selectedFiles.value[index].name;
  return getPlayerState(fileName).currentTime;
};

// 获取文件的总时长
const getSongDuration = (index) => {
  const fileName = selectedFiles.value[index].name;
  return getPlayerState(fileName).duration;
};

// 切换文件的播放/暂停状态
const togglePlay = (index) => {
  const file = selectedFiles.value[index];
  const fileName = file.name;
  const url = getTaskDownloadUrl(fileName);

  if (!url) return;

  // 初始化播放器状态
  initPlayerState(fileName, url);
  const state = getPlayerState(fileName);

  // 暂停所有其他正在播放的歌曲
  playerStates.value.forEach((otherState, otherFileName) => {
    if (
      otherFileName !== fileName &&
      otherState.isPlaying &&
      otherState.audioElement
    ) {
      otherState.audioElement.pause();
      otherState.isPlaying = false;
    }
  });

  if (!state.audioElement) {
    // 创建新的音频元素
    state.audioElement = new Audio(url);

    // 监听音频事件
    state.audioElement.addEventListener("timeupdate", () => {
      if (state.audioElement) {
        state.currentTime = state.audioElement.currentTime;
      }
    });

    state.audioElement.addEventListener("loadedmetadata", () => {
      if (state.audioElement) {
        state.duration = state.audioElement.duration || 0;
        state.isLoaded = true;
      }
    });

    state.audioElement.addEventListener("ended", () => {
      state.isPlaying = false;
      state.currentTime = 0;
    });

    // 开始播放
    state.audioElement.play().catch((error) => {
      console.error("播放失败:", error);
      state.isPlaying = false;
    });
    state.isPlaying = true;
  } else {
    // 切换播放/暂停
    if (state.isPlaying) {
      state.audioElement.pause();
      state.isPlaying = false;
    } else {
      state.audioElement.play().catch((error) => {
        console.error("播放失败:", error);
      });
      state.isPlaying = true;
    }
  }
};

// 向前跳转15秒
const seekForward = (index) => {
  const fileName = selectedFiles.value[index].name;
  const state = getPlayerState(fileName);

  if (state.audioElement) {
    state.audioElement.currentTime = Math.min(
      state.audioElement.currentTime + 15,
      state.duration,
    );
    state.currentTime = state.audioElement.currentTime;
  }
};

// 向后跳转15秒
const seekBackward = (index) => {
  const fileName = selectedFiles.value[index].name;
  const state = getPlayerState(fileName);

  if (state.audioElement) {
    state.audioElement.currentTime = Math.max(
      state.audioElement.currentTime - 15,
      0,
    );
    state.currentTime = state.audioElement.currentTime;
  }
};

// 拖动进度条调整播放位置
const seek = (index, event) => {
  const fileName = selectedFiles.value[index].name;
  const state = getPlayerState(fileName);
  const newTime = parseFloat(event.target.value);

  if (state.audioElement) {
    state.audioElement.currentTime = newTime;
    state.currentTime = newTime;
  }
};

// 格式化时间显示
const formatTime = (time) => {
  const minutes = Math.floor(time / 60);
  const seconds = Math.floor(time % 60);
  return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
};

// 组件销毁时清理所有音频元素
onUnmounted(() => {
  playerStates.value.forEach((state) => {
    if (state.audioElement) {
      state.audioElement.pause();
      state.audioElement = null;
    }
  });
});

// 下载所有文件
const downloadAllFiles = async () => {
  // 收集所有转换完成的文件
  const completedTasks = conversionQueue.value.filter(
    (task) => task.status === "completed",
  );

  if (completedTasks.length === 0) {
    alert("没有可下载的文件");
    return;
  }

  // 使用全局变量中的任务ID
  let taskId = globalTaskId.value;
  if (!taskId) {
    alert("无法获取任务ID");
    return;
  }

  console.log("使用任务ID下载所有文件:", taskId);

  try {
    // 设置下载状态
    isDownloading.value = true;

    // 构建下载URL
    const downloadUrl = `http://localhost:18000/web/fileformat/download-batch?taskId=${encodeURIComponent(taskId)}`;
    console.log("完整下载URL:", downloadUrl);

    // 创建下载链接并点击，让浏览器直接处理下载
    console.log("创建下载链接并触发下载，让浏览器处理进度");
    const link = document.createElement("a");
    link.href = downloadUrl;
    link.download = `converted-files.zip`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    console.log("下载请求已发送，浏览器应在下载管理器中显示真实进度");

    // 立即重置下载状态，让用户可以再次点击下载按钮
    // 由于浏览器会处理实际的下载过程，我们不需要在这里等待
    setTimeout(() => {
      isDownloading.value = false;
      console.log("下载状态已重置");
    }, 1000); // 1秒后重置，给浏览器足够时间处理下载请求
  } catch (error) {
    console.error("批量下载失败:", error);
    alert("批量下载失败，请重试");
    isDownloading.value = false;
  }
};
</script>

<style scoped>
/* 页面整体样式 */
.container {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
  min-height: calc(100vh - 108px);
  position: relative;
  z-index: 1;
}

/* 页面标题 */
.container h3 {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 40px;
  position: relative;
  display: inline-block;
  width: 100%;
}

.container h3::after {
  content: '';
  position: absolute;
  bottom: -12px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, #ff9a9e, #ff758c);
  border-radius: 2px;
}

/* 格式选择区域 */
.format-selector {
  display: flex;
  gap: 24px;
  margin: 50px 0 30px;
  padding: 28px 32px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.08) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 154, 158, 0.15);
  box-shadow: 0 8px 24px rgba(255, 154, 158, 0.08);
}

.format-selector h3:first-child {
  margin-top: 0;
}

.format-selector .form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.format-selector label {
  font-size: 14px;
  font-weight: 600;
  color: #555;
  display: flex;
  align-items: center;
  gap: 8px;
}

.format-selector label::before {
  content: '';
  width: 4px;
  height: 16px;
  background: linear-gradient(180deg, #ff9a9e, #ff758c);
  border-radius: 2px;
}

.format-selector select {
  padding: 14px 18px;
  border: 2px solid rgba(255, 154, 158, 0.2);
  border-radius: 12px;
  font-size: 15px;
  background: #fff;
  color: #333;
  transition: all 0.3s ease;
  cursor: pointer;
  outline: none;
}

.format-selector select:hover {
  border-color: rgba(255, 154, 158, 0.4);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.15);
}

.format-selector select:focus {
  border-color: #ff758c;
  box-shadow: 0 0 0 3px rgba(255, 117, 140, 0.15);
}

/* 音频参数调节区域 */
.audio-params {
  margin: 30px 0;
  padding: 28px 32px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.06) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 154, 158, 0.12);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.06);
}

.audio-params h4 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 24px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.audio-params h4::before {
  content: '⚙️';
  font-size: 20px;
}

.params-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.audio-params .form-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.audio-params label {
  font-size: 13px;
  font-weight: 500;
  color: #666;
}

.audio-params select {
  padding: 12px 16px;
  border: 1.5px solid rgba(255, 154, 158, 0.2);
  border-radius: 10px;
  font-size: 14px;
  background: #fff;
  color: #333;
  transition: all 0.3s ease;
  cursor: pointer;
  outline: none;
}

.audio-params select:hover {
  border-color: rgba(255, 154, 158, 0.4);
}

.audio-params select:focus {
  border-color: #ff758c;
  box-shadow: 0 0 0 3px rgba(255, 117, 140, 0.12);
}

/* 文件上传区域 */
.upload-area {
  margin: 30px 0;
  padding: 60px 40px;
  border: 2px dashed rgba(255, 154, 158, 0.35);
  text-align: center;
  cursor: pointer;
  border-radius: 20px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.05) 0%, rgba(255, 255, 255, 0.9) 100%);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.upload-area::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(255, 154, 158, 0.1) 0%, transparent 70%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-area:hover {
  border-color: #ff758c;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.1) 0%, rgba(255, 255, 255, 0.95) 100%);
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(255, 154, 158, 0.15);
}

.upload-area:hover::before {
  opacity: 1;
}

.upload-area p {
  font-size: 16px;
  color: #666;
  margin: 0 0 20px 0;
  position: relative;
  z-index: 1;
}

input[type="file"] {
  display: none;
}

/* 按钮样式 */
.btn {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  color: white;
  padding: 12px 28px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 6px 18px rgba(255, 117, 140, 0.35);
  position: relative;
  overflow: hidden;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.25), transparent);
  transition: left 0.5s ease;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 117, 140, 0.45);
}

.btn:hover::before {
  left: 100%;
}

.btn:active {
  transform: translateY(0);
}

.btn-danger {
  background: linear-gradient(135deg, #ff8a80 0%, #ff5252 100%);
  box-shadow: 0 6px 18px rgba(255, 82, 82, 0.3);
}

.btn-danger:hover {
  box-shadow: 0 8px 24px rgba(255, 82, 82, 0.4);
}

.btn-primary {
  background: linear-gradient(135deg, #64b5f6 0%, #2196f3 100%);
  box-shadow: 0 6px 18px rgba(33, 150, 243, 0.35);
}

.btn-primary:hover {
  box-shadow: 0 8px 24px rgba(33, 150, 243, 0.45);
}

.btn-success {
  background: linear-gradient(135deg, #81c784 0%, #4caf50 100%);
  box-shadow: 0 6px 18px rgba(76, 175, 80, 0.35);
}

.btn-success:hover {
  box-shadow: 0 8px 24px rgba(76, 175, 80, 0.45);
}

.btn:disabled {
  background: linear-gradient(135deg, #e0e0e0 0%, #bdbdbd 100%);
  box-shadow: none;
  cursor: not-allowed;
  transform: none;
}

.btn:disabled:hover {
  transform: none;
  box-shadow: none;
}

/* 文件列表区域 */
.file-list-area {
  margin: 30px 0;
  padding: 28px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.06) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 20px;
  border: 1px solid rgba(255, 154, 158, 0.12);
  box-shadow: 0 8px 24px rgba(255, 154, 158, 0.06);
}

.file-list-area h4 {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 24px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-list-area h4::before {
  content: '📋';
  font-size: 20px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 28px;
}

/* 文件项样式 */
.file-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  gap: 20px;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 154, 158, 0.08);
}

.file-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 154, 158, 0.12);
  border-color: rgba(255, 154, 158, 0.2);
}

.file-item.pending {
  border-left: 4px solid #ffc107;
}

.file-item.processing {
  border-left: 4px solid #64b5f6;
}

.file-item.completed {
  border-left: 4px solid #81c784;
}

.file-item.error {
  border-left: 4px solid #ff5252;
}

/* 文件封面 */
.file-cover {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
  transition: all 0.3s ease;
}

.cover-placeholder:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 154, 158, 0.4);
}

.cover-icon {
  font-size: 36px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

/* 文件信息 */
.file-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-weight: 600;
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-details {
  font-size: 13px;
  color: #888;
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
}

.file-details span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 进度条样式 */
.task-progress {
  margin: 10px 0;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: linear-gradient(90deg, rgba(255, 154, 158, 0.15) 0%, rgba(255, 154, 158, 0.08) 100%);
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 6px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #ff9a9e 0%, #ff758c 50%, #ffadc0 100%);
  border-radius: 10px;
  transition: width 0.4s ease;
  box-shadow: 0 2px 6px rgba(255, 117, 140, 0.3);
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.progress-text {
  font-size: 13px;
  color: #ff5f7b;
  font-weight: 600;
  text-align: right;
}

/* 错误信息 */
.error-message {
  color: #ff5252;
  font-size: 13px;
  margin-top: 8px;
  padding: 8px 12px;
  background: rgba(255, 82, 82, 0.08);
  border-radius: 8px;
  border-left: 3px solid #ff5252;
}

/* 文件操作按钮 */
.file-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
}

/* 转换按钮容器 */
.convert-btn-container {
  display: flex;
  justify-content: center;
  padding-top: 10px;
}

.convert-btn-container .btn {
  padding: 16px 48px;
  font-size: 16px;
}

/* 结果区域 */
.result-area {
  margin-top: 30px;
  padding: 24px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.08) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 16px;
  text-align: center;
  border: 1px solid rgba(255, 154, 158, 0.15);
}

.loading {
  color: #ff5f7b;
  font-size: 16px;
  font-weight: 500;
  padding: 12px;
}

.loading::before {
  content: '⏳ ';
}

.success {
  color: #4caf50;
  font-size: 15px;
}

.success p {
  margin: 0 0 16px 0;
  font-weight: 500;
}

.success a {
  display: inline-block;
  padding: 12px 32px;
  background: linear-gradient(135deg, #81c784 0%, #4caf50 100%);
  color: white;
  text-decoration: none;
  border-radius: 10px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 6px 18px rgba(76, 175, 80, 0.35);
}

.success a:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(76, 175, 80, 0.45);
}

.error {
  color: #ff5252;
  font-size: 15px;
  padding: 16px;
  background: rgba(255, 82, 82, 0.08);
  border-radius: 12px;
  border-left: 4px solid #ff5252;
}

/* 批量下载区域 */
.batch-download {
  margin: 30px 0;
  padding: 28px;
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.08) 0%, rgba(255, 255, 255, 0.95) 100%);
  border-radius: 20px;
  text-align: center;
  border: 1px solid rgba(255, 154, 158, 0.15);
  box-shadow: 0 8px 24px rgba(255, 154, 158, 0.08);
}

.batch-download h4 {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.batch-download h4::before {
  content: '🎉';
  font-size: 24px;
}

.batch-download .btn {
  padding: 16px 40px;
  font-size: 16px;
}

/* 播放器样式 */
.song-player {
  width: 100%;
  margin-top: 14px;
  max-width: 500px;
}

.player-container {
  background: linear-gradient(135deg, rgba(255, 154, 158, 0.12) 0%, rgba(255, 117, 140, 0.08) 100%);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 154, 158, 0.2);
  border-radius: 50px;
  padding: 14px 24px;
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.12);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 64px;
}

.player-container:hover {
  box-shadow: 0 8px 28px rgba(255, 154, 158, 0.18);
  border-color: rgba(255, 154, 158, 0.3);
}

.player-controls {
  display: flex;
  align-items: center;
  gap: 14px;
  height: 44px;
}

.player-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  transition: all 0.3s ease;
  background: rgba(255, 255, 255, 0.95);
  border: 2px solid rgba(255, 154, 158, 0.25);
  color: #333;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.player-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 6px 16px rgba(255, 154, 158, 0.2);
  border-color: #ff9a9e;
  background: #fff;
}

.player-btn:active {
  transform: scale(0.95);
}

.play-btn {
  width: 46px;
  height: 46px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  border: none;
  box-shadow: 0 6px 16px rgba(255, 117, 140, 0.35);
}

.play-btn:hover {
  box-shadow: 0 8px 20px rgba(255, 117, 140, 0.45);
}

.play-btn svg path,
.play-btn svg rect {
  fill: #fff;
}

.control-icon {
  width: 24px;
  height: 24px;
  object-fit: contain;
}

.play-btn .control-icon {
  width: 28px;
  height: 28px;
}

.player-progress {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 44px;
  position: relative;
}

.time-display {
  position: absolute;
  bottom: -22px;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
  font-family: "SF Mono", Monaco, "Courier New", monospace;
  font-weight: 600;
  user-select: none;
  z-index: 100;
}

.current-time,
.total-time {
  background: rgba(255, 255, 255, 0.85);
  padding: 4px 10px;
  border-radius: 10px;
  backdrop-filter: blur(8px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
}

.progress-container {
  position: relative;
  width: 90%;
  max-width: 380px;
  height: 24px;
  border-radius: 12px;
  overflow: visible;
  display: flex;
  align-items: center;
}

.progress-background {
  position: absolute;
  top: 50%;
  left: 0;
  width: 100%;
  height: 6px;
  transform: translateY(-50%);
  background: rgba(255, 154, 158, 0.2);
  border-radius: 6px;
}

.progress-filled {
  position: absolute;
  top: 50%;
  left: 0;
  height: 6px;
  transform: translateY(-50%);
  background: linear-gradient(90deg, #ff9a9e 0%, #ff758c 50%, #ffadc0 100%);
  border-radius: 6px;
  transition: width 0.1s ease;
  z-index: 1;
  box-shadow: 0 2px 6px rgba(255, 117, 140, 0.25);
}

.progress-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  -webkit-appearance: none;
  appearance: none;
  background: transparent;
  border: none;
  outline: none;
  cursor: pointer;
  z-index: 10;
}

.progress-input::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 3px 10px rgba(255, 117, 140, 0.35);
  border: 3px solid rgba(255, 255, 255, 0.95);
  margin-top: -5px;
}

.progress-input::-webkit-slider-thumb:hover {
  width: 20px;
  height: 20px;
  box-shadow: 0 4px 14px rgba(255, 117, 140, 0.45);
  margin-top: -6px;
}

.progress-input::-moz-range-thumb {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  cursor: pointer;
  border: 3px solid rgba(255, 255, 255, 0.95);
  transition: all 0.2s ease;
  box-shadow: 0 3px 10px rgba(255, 117, 140, 0.35);
}

.progress-input::-moz-range-thumb:hover {
  width: 20px;
  height: 20px;
  box-shadow: 0 4px 14px rgba(255, 117, 140, 0.45);
}

.progress-input::-webkit-slider-runnable-track {
  width: 100%;
  height: 6px;
  border-radius: 6px;
  background: transparent;
  border: none;
}

.progress-input::-moz-range-track {
  width: 100%;
  height: 6px;
  border-radius: 6px;
  background: transparent;
  border: none;
}

/* 旋转动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.btn.loading {
  position: relative;
  pointer-events: none;
}

.btn.loading::after {
  content: "";
  position: absolute;
  top: 50%;
  left: 50%;
  width: 18px;
  height: 18px;
  margin: -9px 0 0 -9px;
  border: 2px solid rgba(255, 255, 255, 0.35);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 30px 16px;
  }

  .container h3 {
    font-size: 26px;
  }

  .format-selector {
    flex-direction: column;
    gap: 20px;
    padding: 24px;
  }

  .params-grid {
    grid-template-columns: 1fr;
  }

  .audio-params {
    padding: 24px;
  }

  .upload-area {
    padding: 40px 24px;
  }

  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .file-cover {
    width: 64px;
    height: 64px;
  }

  .file-actions {
    flex-direction: row;
    width: 100%;
    justify-content: flex-end;
  }

  .file-list-area {
    padding: 24px;
  }

  .player-container {
    flex-direction: column;
    gap: 12px;
    padding: 20px;
  }

  .player-controls {
    order: 1;
  }

  .player-progress {
    order: 2;
    width: 100%;
  }

  .time-display {
    position: relative;
    bottom: auto;
    justify-content: center;
    gap: 12px;
  }
}
</style>
