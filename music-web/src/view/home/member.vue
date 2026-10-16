<template>
  <div class="member-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-blob bg-blob-1"></div>
      <div class="bg-blob bg-blob-2"></div>
      <div class="bg-blob bg-blob-3"></div>
      <div class="bg-dots"></div>
    </div>

    <div class="member-layout-wrapper">
      <!-- 左侧：个人信息区域 -->
      <div class="left-panel">
        <!-- 用户信息卡片 -->
        <div class="profile-card">
          <div class="profile-header">
            <div class="profile-float-1"></div>
            <div class="profile-float-2"></div>
            <div class="profile-float-3"></div>
            <div class="profile-bg-pattern"></div>
            <div class="avatar-wrapper">
              <img
                :src="memberInfo?.avatar || defaultAvatar"
                alt="头像"
                class="avatar"
              />
              <div class="avatar-upload" @click="handleAvatarUpload">
                <span>编辑</span>
              </div>
            </div>
            <div class="profile-info">
              <h2 class="profile-name">
                {{ memberInfo?.nickname || memberInfo?.name || "未设置昵称" }}
              </h2>
              <div class="profile-meta">
                <span class="meta-item">
                  <span class="meta-icon">📱</span>
                  {{ memberInfo?.mobile || "未绑定手机号" }}
                </span>
              </div>
              <div class="member-status-badge" :class="getStatusClass()">
                <span class="status-dot"></span>
                {{ getStatusText() }}
              </div>
            </div>
          </div>
        </div>

        <!-- 基本信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ff9a9e' stroke-width='2'%3E%3Cpath d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3C/svg%3E"
                  alt=""
                />
              </span>
              基本信息
            </h3>
            <button class="edit-btn" @click="openEditModal">编辑</button>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <div class="info-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Ccircle cx='12' cy='12' r='10'/%3E%3Cpath d='M12 16v-4M12 8h.01'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="info-content">
                <span class="info-label">性别</span>
                <span class="info-value">{{
                  getGenderText(memberInfo?.gender)
                }}</span>
              </div>
            </div>
            <div class="info-item">
              <div class="info-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Crect x='3' y='4' width='18' height='18' rx='2' ry='2'/%3E%3Cline x1='16' y1='2' x2='16' y2='6'/%3E%3Cline x1='8' y1='2' x2='8' y2='6'/%3E%3Cline x1='3' y1='10' x2='21' y2='10'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="info-content">
                <span class="info-label">生日</span>
                <span class="info-value">{{
                  formatDate(memberInfo?.birthday)
                }}</span>
              </div>
            </div>
            <div class="info-item info-full">
              <div class="info-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z'/%3E%3Cpolyline points='22,6 12,13 2,6'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="info-content">
                <span class="info-label">邮箱</span>
                <span class="info-value">{{
                  memberInfo?.email || "未设置"
                }}</span>
              </div>
            </div>
            <div class="info-item info-full">
              <div class="info-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23666' stroke-width='2'%3E%3Cpath d='M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2'/%3E%3Ccircle cx='12' cy='7' r='4'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="info-content">
                <span class="info-label">个人简介</span>
                <span class="info-value bio">{{
                  memberInfo?.bio || "暂未填写个人简介"
                }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 快捷操作卡片 -->
        <div class="quick-actions-card">
          <h4 class="actions-title">
            <span class="title-icon">
              <img
                src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ff9a9e' stroke-width='2'%3E%3Cpolygon points='13 2 3 14 12 14 11 22 21 10 12 10 13 2'/%3E%3C/svg%3E"
                alt=""
              />
            </span>
            快捷操作
          </h4>
          <div class="actions-grid">
            <button class="action-item" @click="openEditModal">
              <div class="action-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ff9a9e' stroke-width='2'%3E%3Cpath d='M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7'/%3E%3Cpath d='M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <span>编辑资料</span>
            </button>
            <button class="action-item" @click="handleResetPassword">
              <div class="action-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ff9a9e' stroke-width='2'%3E%3Crect x='3' y='11' width='18' height='11' rx='2' ry='2'/%3E%3Cpath d='M7 11V7a5 5 0 0 1 10 0v4'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <span>修改密码</span>
            </button>
            <button class="action-item" @click="openMemberPackageModal">
              <div class="action-icon action-icon-vip">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ffd700' stroke='%23ffb800' stroke-width='1'%3E%3Cpolygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <span>升级会员</span>
            </button>
            <button class="action-item" @click="handleLogout">
              <div class="action-icon action-icon-logout">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23ff5252' stroke-width='2'%3E%3Cpath d='M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4'/%3E%3Cpolyline points='16 17 21 12 16 7'/%3E%3Cline x1='21' y1='12' x2='9' y2='12'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <span>退出登录</span>
            </button>
          </div>
        </div>

        <!-- 使用统计卡片 -->
        <div class="stats-card">
          <h4 class="stats-title">
            <span class="title-icon">📊</span>
            使用统计
          </h4>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%231976d2' stroke-width='2'%3E%3Cpath d='M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z'/%3E%3Cpath d='M19 10v2a7 7 0 0 1-14 0v-2'/%3E%3Cline x1='12' y1='19' x2='12' y2='23'/%3E%3Cline x1='8' y1='23' x2='16' y2='23'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="stat-info">
                <span class="stat-value">{{
                  memberInfo?.totalTransCount || 0
                }}</span>
                <span class="stat-label">识别次数</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23388e3c' stroke-width='2'%3E%3Cpolyline points='23 4 23 10 17 10'/%3E%3Cpolyline points='1 20 1 14 7 14'/%3E%3Cpath d='M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="stat-info">
                <span class="stat-value">{{
                  memberInfo?.totalConvertCount || 0
                }}</span>
                <span class="stat-label">转换次数</span>
              </div>
            </div>
            <div class="stat-item">
              <div class="stat-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23f57c00' stroke-width='2'%3E%3Ccircle cx='12' cy='12' r='10'/%3E%3Cpolyline points='12 6 12 12 16 14'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="stat-info">
                <span class="stat-value">{{
                  memberInfo?.totalDuration || 0
                }}</span>
                <span class="stat-label">总时长(分钟)</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：会员区域 -->
      <div class="right-panel">
        <!-- VIP推广卡片（未开通时显示） -->
        <div class="vip-promo-card" v-if="memberInfo?.memberStatus !== 1">
          <div class="vip-promo-float vip-float-1"></div>
          <div class="vip-promo-float vip-float-2"></div>
          <div class="vip-promo-float vip-float-3"></div>
          <div class="promo-bg"></div>
          <div class="promo-decoration">
            <svg viewBox="0 0 200 200" class="deco-svg">
              <circle
                cx="100"
                cy="100"
                r="80"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-width="2"
              />
              <circle
                cx="100"
                cy="100"
                r="60"
                fill="none"
                stroke="rgba(255,255,255,0.15)"
                stroke-width="2"
              />
              <circle
                cx="100"
                cy="100"
                r="40"
                fill="none"
                stroke="rgba(255,255,255,0.1)"
                stroke-width="2"
              />
            </svg>
          </div>
          <div class="promo-particles">
            <span class="particle"></span>
            <span class="particle"></span>
            <span class="particle"></span>
            <span class="particle"></span>
            <span class="particle"></span>
            <span class="particle"></span>
          </div>
          <div class="promo-content">
            <div class="promo-crown">
              <img
                src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23ffd700' stroke='%23ffb800' stroke-width='1'%3E%3Cpath d='M2 20h20v2H2v-2zm2-8l2 3h3l2-5 2 5h3l2-3v8H4v-8zm8 0l2 3h3l2-5 2 5h3l2-3'/%3E%3C/svg%3E"
                alt="crown"
              />
            </div>
            <h3 class="promo-title">升级高级会员</h3>
            <p class="promo-desc">解锁全部高级特权，享受尊贵体验</p>
            <div class="promo-features">
              <span class="feature-tag">无限识别</span>
              <span class="feature-tag">极速转换</span>
              <span class="feature-tag">专属客服</span>
            </div>
            <button class="promo-btn" @click="openMemberPackageModal">
              立即开通
              <svg
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                class="btn-arrow"
              >
                <line x1="5" y1="12" x2="19" y2="12" />
                <polyline points="12 5 19 12 12 19" />
              </svg>
            </button>
          </div>
        </div>

        <!-- 会员状态卡片（已开通时显示） -->
        <div class="member-status-card" v-else>
          <div class="member-card-bg"></div>
          <div class="member-decoration">
            <svg viewBox="0 0 100 100" class="deco-pattern">
              <circle
                cx="50"
                cy="50"
                r="40"
                fill="none"
                stroke="rgba(255,255,255,0.3)"
                stroke-width="1"
              />
              <circle
                cx="50"
                cy="50"
                r="30"
                fill="none"
                stroke="rgba(255,255,255,0.2)"
                stroke-width="1"
              />
            </svg>
          </div>
          <div class="member-card-content">
            <div class="member-level">
              <div class="level-crown">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='%23fff' stroke='%23fff' stroke-width='1'%3E%3Cpath d='M2 20h20v2H2v-2zm2-8l2 3h3l2-5 2 5h3l2-3v8H4v-8zm8 0l2 3h3l2-5 2 5h3l2-3'/%3E%3C/svg%3E"
                  alt="crown"
                />
              </div>
              <span class="level-name">{{
                memberInfo?.memberLevelName || "高级会员"
              }}</span>
            </div>
            <div class="member-expiry">
              <span class="expiry-label">有效期至</span>
              <span class="expiry-date">{{
                formatDate(memberInfo?.memberExpireAt)
              }}</span>
            </div>
            <div class="member-progress">
              <div class="progress-info">
                <span>会员权益进行中</span>
              </div>
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{ width: getMemberProgress() + '%' }"
                ></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 会员特权卡片 -->
        <div class="privileges-card">
          <div class="card-header">
            <h3 class="card-title">
              <span class="title-icon">✨</span>
              会员特权
            </h3>
            <span class="privilege-tag" :class="getMemberBadgeClass()">
              {{ memberInfo?.memberLevelName || "普通用户" }}
            </span>
          </div>
          <div class="privileges-list">
            <div
              class="privilege-item"
              :class="{ active: memberInfo?.memberStatus === 1 }"
            >
              <div class="privilege-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%231976d2' stroke-width='2'%3E%3Cpath d='M12 1a3 3 0 0 0-3 3v8a3 3 0 0 0 6 0V4a3 3 0 0 0-3-3z'/%3E%3Cpath d='M19 10v2a7 7 0 0 1-14 0v-2'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="privilege-content">
                <h4>无限制语音识别</h4>
                <p>无限次使用语音识别服务</p>
              </div>
              <div
                class="privilege-check"
                v-if="memberInfo?.memberStatus === 1"
              >
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='3'%3E%3Cpolyline points='20 6 9 17 4 12'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
            </div>
            <div
              class="privilege-item"
              :class="{ active: memberInfo?.memberStatus === 1 }"
            >
              <div class="privilege-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23388e3c' stroke-width='2'%3E%3Cpolyline points='13 2 3 14 12 14 11 22 21 10 12 10 13 2'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="privilege-content">
                <h4>音频转换加速</h4>
                <p>享受高速转换服务</p>
              </div>
              <div
                class="privilege-check"
                v-if="memberInfo?.memberStatus === 1"
              >
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='3'%3E%3Cpolyline points='20 6 9 17 4 12'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
            </div>
            <div
              class="privilege-item"
              :class="{ active: memberInfo?.memberStatus === 1 }"
            >
              <div class="privilege-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23f57c00' stroke-width='2'%3E%3Cpolygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="privilege-content">
                <h4>优先排队</h4>
                <p>任务优先处理</p>
              </div>
              <div
                class="privilege-check"
                v-if="memberInfo?.memberStatus === 1"
              >
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='3'%3E%3Cpolyline points='20 6 9 17 4 12'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
            </div>
            <div
              class="privilege-item"
              :class="{ active: memberInfo?.memberStatus === 1 }"
            >
              <div class="privilege-icon">
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%237b1fa2' stroke-width='2'%3E%3Crect x='3' y='11' width='18' height='11' rx='2' ry='2'/%3E%3Cpath d='M7 11V7a5 5 0 0 1 10 0v4'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
              <div class="privilege-content">
                <h4>高级功能解锁</h4>
                <p>体验完整语音识别平台</p>
              </div>
              <div
                class="privilege-check"
                v-if="memberInfo?.memberStatus === 1"
              >
                <img
                  src="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23fff' stroke-width='3'%3E%3Cpolyline points='20 6 9 17 4 12'/%3E%3C/svg%3E"
                  alt=""
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑信息弹窗 -->
    <div v-if="editModalVisible" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑个人信息</h3>
          <button class="modal-close" @click="closeEditModal">&times;</button>
        </div>
        <div class="modal-body">
          <div class="edit-form">
            <div class="form-group">
              <label>昵称</label>
              <input
                type="text"
                v-model="editForm.nickname"
                placeholder="请输入昵称"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>邮箱</label>
              <input
                type="email"
                v-model="editForm.email"
                placeholder="请输入邮箱"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>性别</label>
              <div class="radio-group">
                <label
                  class="radio-item"
                  :class="{ active: editForm.gender === 0 }"
                >
                  <input type="radio" v-model="editForm.gender" :value="0" />
                  <span>未知</span>
                </label>
                <label
                  class="radio-item"
                  :class="{ active: editForm.gender === 1 }"
                >
                  <input type="radio" v-model="editForm.gender" :value="1" />
                  <span>男</span>
                </label>
                <label
                  class="radio-item"
                  :class="{ active: editForm.gender === 2 }"
                >
                  <input type="radio" v-model="editForm.gender" :value="2" />
                  <span>女</span>
                </label>
              </div>
            </div>
            <div class="form-group">
              <label>生日</label>
              <input
                type="date"
                v-model="editForm.birthday"
                class="form-input"
              />
            </div>
            <div class="form-group">
              <label>个人简介</label>
              <textarea
                v-model="editForm.bio"
                placeholder="请输入个人简介"
                rows="3"
                class="form-textarea"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeEditModal">
            取消
          </button>
          <button
            class="btn btn-primary"
            @click="handleUpdateInfo"
            :disabled="isUpdating"
          >
            {{ isUpdating ? "保存中..." : "保存" }}
          </button>
        </div>
      </div>
    </div>

    <!-- 会员套餐弹窗 -->
    <div
      v-if="packageModalVisible"
      class="modal-overlay"
      @click="closePackageModal"
    >
      <div class="modal-content package-modal" @click.stop>
        <div class="modal-header">
          <div class="package-modal-title">
            <h3>开通会员</h3>
            <p class="subtitle">解锁更多高级功能，享受尊贵体验</p>
          </div>
          <button class="modal-close" @click="closePackageModal">
            &times;
          </button>
        </div>
        <div class="modal-body">
          <div v-if="isLoadingPackages" class="loading-container">
            <div class="loading-spinner"></div>
            <span>加载中...</span>
          </div>
          <div v-else class="package-grid">
            <div
              v-for="(pkg, index) in memberPackages"
              :key="pkg.id"
              class="package-card"
              :class="{
                'package-selected': selectedPackageId === pkg.id,
                'package-recommended': index === 1,
              }"
              @click="selectPackage(pkg.id)"
            >
              <div v-if="index === 1" class="recommended-tag">推荐</div>
              <div class="package-header">
                <div class="package-icon">
                  <svg viewBox="0 0 24 24" fill="currentColor">
                    <path
                      d="M12 2L15.09 8.26L22 9.27L17 14.14L18.18 21.02L12 17.77L5.82 21.02L7 14.14L2 9.27L8.91 8.26L12 2Z"
                    />
                  </svg>
                </div>
                <h4>{{ pkg.name }}</h4>
              </div>
              <div class="package-price">
                <span class="currency">¥</span>
                <span class="amount">{{ pkg.price }}</span>
                <span class="duration">/{{ pkg.duration }}天</span>
              </div>
              <div class="package-divider"></div>
              <div class="package-features">
                <div
                  v-for="feature in getPackageFeatures(pkg.features)"
                  :key="feature"
                  class="feature-item"
                >
                  <span class="feature-check">
                    <svg
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="3"
                    >
                      <polyline points="20 6 9 17 4 12" />
                    </svg>
                  </span>
                  <span>{{ feature }}</span>
                </div>
              </div>
              <div class="package-desc">{{ pkg.description }}</div>
              <div
                class="package-btn"
                :class="{ active: selectedPackageId === pkg.id }"
              >
                {{ selectedPackageId === pkg.id ? "已选择" : "选择套餐" }}
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closePackageModal">
            取消
          </button>
          <button
            class="btn btn-primary btn-purchase"
            @click="handlePurchasePackage"
            :disabled="!selectedPackageId || isPurchasing"
          >
            <span v-if="isPurchasing" class="btn-spinner"></span>
            {{ isPurchasing ? "处理中..." : "立即开通" }}
          </button>
        </div>
      </div>
    </div>

    <!-- 提示消息 -->
    <div v-if="toast.show" class="toast">
      <div class="toast-content" :class="toast.type">{{ toast.message }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import store from "../../store/index.js";
import { useRouter } from "vue-router";

const memberInfo = ref(null);
const editModalVisible = ref(false);
const packageModalVisible = ref(false);
const isLoading = ref(false);
const isUpdating = ref(false);
const isLoadingPackages = ref(false);
const isPurchasing = ref(false);
const memberPackages = ref([]);
const selectedPackageId = ref(null);
const defaultAvatar =
  "https://api.dicebear.com/7.x/avataaars/svg?seed=avatar&backgroundColor=b6e3f4";

const editForm = ref({
  nickname: "",
  email: "",
  gender: 0,
  birthday: "",
  bio: "",
});

const toast = ref({ show: false, message: "", type: "info" });
const router = useRouter();

onMounted(() => {
  if (!store.state.member || !store.state.member.token) {
    showToast("请先登录", "error");
    setTimeout(() => router.push("/login"), 1500);
    return;
  }
  fetchMemberInfo();
});

const fetchMemberInfo = async () => {
  isLoading.value = true;
  try {
    const response = await axios.get("/web/member/info");
    if (response.data.success) {
      memberInfo.value = response.data.content;
    } else {
      showToast(response.data.message || "获取会员信息失败", "error");
    }
  } catch (error) {
    showToast("获取会员信息失败，请稍后重试", "error");
  } finally {
    isLoading.value = false;
  }
};

const openEditModal = () => {
  if (!memberInfo.value) return;
  editForm.value = {
    nickname: memberInfo.value.nickname || "",
    email: memberInfo.value.email || "",
    gender: memberInfo.value.gender || 0,
    birthday: memberInfo.value.birthday
      ? new Date(memberInfo.value.birthday).toISOString().split("T")[0]
      : "",
    bio: memberInfo.value.bio || "",
  };
  editModalVisible.value = true;
};

const closeEditModal = () => {
  editModalVisible.value = false;
};

const handleUpdateInfo = async () => {
  isUpdating.value = true;
  try {
    const response = await axios.put("/web/member/update", editForm.value);
    if (response.data.success) {
      showToast("更新成功", "success");
      editModalVisible.value = false;
      fetchMemberInfo();
    } else {
      showToast(response.data.message || "更新失败", "error");
    }
  } catch (error) {
    showToast("更新失败，请稍后重试", "error");
  } finally {
    isUpdating.value = false;
  }
};

const handleAvatarUpload = () => {
  const input = document.createElement("input");
  input.type = "file";
  input.accept = "image/*";
  input.onchange = async (e) => {
    const file = e.target.files[0];
    if (file) await uploadAvatar(file);
  };
  input.click();
};

const uploadAvatar = async (file) => {
  try {
    const formData = new FormData();
    formData.append("file", file);
    const response = await axios.post("/web/common/upload", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    if (response.data.success) {
      memberInfo.value.avatar = response.data.content;
      await axios.put("/web/member/update", { avatar: response.data.content });
      showToast("头像上传成功", "success");
    }
  } catch (error) {
    showToast("头像上传失败", "error");
  }
};

const handleResetPassword = () => {
  showToast("密码重置功能开发中", "info");
};

const handleLogout = () => {
  if (confirm("确定要退出登录吗？")) {
    store.commit("logout");
    router.push("/login");
  }
};

const getGenderText = (gender) => {
  switch (gender) {
    case 1:
      return "男";
    case 2:
      return "女";
    default:
      return "未知";
  }
};

const formatDate = (dateString) => {
  if (!dateString) return "未设置";
  return new Date(dateString).toLocaleDateString("zh-CN");
};

const getMemberBadgeClass = () => {
  if (!memberInfo.value) return "";
  return memberInfo.value.memberStatus === 1 ? "badge-premium" : "";
};

const getStatusClass = () => {
  if (!memberInfo.value) return "status-normal";
  return memberInfo.value.memberStatus === 1
    ? "status-active"
    : "status-normal";
};

const getStatusText = () => {
  if (!memberInfo.value) return "普通用户";
  return memberInfo.value.memberStatus === 1 ? "高级会员" : "普通用户";
};

const getMemberProgress = () => {
  if (!memberInfo.value?.memberExpireAt) return 100;
  const now = new Date();
  const expire = new Date(memberInfo.value.memberExpireAt);
  const remaining = expire - now;
  return Math.max(
    0,
    Math.min(100, (remaining / (365 * 24 * 60 * 60 * 1000)) * 100),
  );
};

const showToast = (message, type = "info") => {
  toast.value = { show: true, message, type };
  setTimeout(() => {
    toast.value.show = false;
  }, 3000);
};

const openMemberPackageModal = async () => {
  packageModalVisible.value = true;
  selectedPackageId.value = null;
  await fetchMemberPackages();
};

const closePackageModal = () => {
  packageModalVisible.value = false;
};

const fetchMemberPackages = async () => {
  isLoadingPackages.value = true;
  try {
    const response = await axios.get("/web/member/package/list");
    if (response.data.success) {
      memberPackages.value = response.data.content;
      if (memberPackages.value.length > 0)
        selectedPackageId.value = memberPackages.value[0].id;
    }
  } catch (error) {
    showToast("获取会员套餐失败", "error");
  } finally {
    isLoadingPackages.value = false;
  }
};

const selectPackage = (id) => {
  selectedPackageId.value = id;
};

const getPackageFeatures = (features) => {
  if (!features) return [];
  try {
    const parsed = JSON.parse(features);
    if (parsed?.features) return parsed.features;
    if (Array.isArray(parsed)) return parsed;
  } catch (e) {
    if (features.includes("|")) return features.split("|");
  }
  return [features];
};

const handlePurchasePackage = async () => {
  if (!selectedPackageId.value) {
    showToast("请选择一个套餐", "info");
    return;
  }
  isPurchasing.value = true;
  try {
    const response = await axios.post(`/web/member/package/purchase/${selectedPackageId.value}`);
    if (response.data.success) {
      showToast("会员开通成功！", "success");
      closePackageModal();
      fetchMemberInfo();
    } else {
      showToast(response.data.message || "开通失败", "error");
    }
  } catch (error) {
    showToast("开通失败，请稍后重试", "error");
  } finally {
    isPurchasing.value = false;
  }
};
</script>

<style scoped>
.member-container {
  min-height: calc(100vh - 108px);
  padding: 24px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.6;
}

.bg-blob-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(
    135deg,
    #ff9a9e 0%,
    #ffb8ba 33%,
    #fecfef 66%,
    #ffd6d9 100%
  );
  top: -200px;
  right: -100px;
  animation:
    blobFloat 20s ease-in-out infinite,
    blobColorShift1 15s ease-in-out infinite;
}

.bg-blob-2 {
  width: 400px;
  height: 400px;
  background: linear-gradient(
    135deg,
    #ff758c 0%,
    #ff9a9e 33%,
    #ffb8ba 66%,
    #ffd6d9 100%
  );
  bottom: -150px;
  left: -100px;
  animation:
    blobFloat 25s ease-in-out infinite reverse,
    blobColorShift2 18s ease-in-out infinite;
}

.bg-blob-3 {
  width: 300px;
  height: 300px;
  background: linear-gradient(
    135deg,
    #ffd6d9 0%,
    #ffe8eb 33%,
    #ffb8ba 66%,
    #ff9a9e 100%
  );
  top: 40%;
  left: 30%;
  animation:
    blobFloat 18s ease-in-out infinite,
    blobColorShift3 12s ease-in-out infinite;
}

@keyframes blobFloat {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

@keyframes blobColorShift1 {
  0%,
  100% {
    filter: blur(60px) hue-rotate(0deg);
  }
  50% {
    filter: blur(70px) hue-rotate(15deg);
  }
}

@keyframes blobColorShift2 {
  0%,
  100% {
    filter: blur(60px) hue-rotate(0deg);
  }
  50% {
    filter: blur(65px) hue-rotate(-10deg);
  }
}

@keyframes blobColorShift3 {
  0%,
  100% {
    filter: blur(60px) hue-rotate(0deg);
  }
  50% {
    filter: blur(75px) hue-rotate(20deg);
  }
}

.bg-dots {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: radial-gradient(
    circle,
    rgba(0, 0, 0, 0.03) 1px,
    transparent 1px
  );
  background-size: 30px 30px;
}

/* 布局 */
.member-layout-wrapper {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

@media (max-width: 1100px) {
  .member-layout-wrapper {
    grid-template-columns: 1fr;
  }
}

/* 左侧面板 */
.left-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 右侧面板 */
.right-panel {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 卡片通用样式 */
.profile-card,
.quick-actions-card,
.info-card,
.stats-card,
.vip-promo-card,
.member-status-card,
.privileges-card {
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 24px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.card-title,
.actions-title,
.stats-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.title-icon {
  font-size: 18px;
}

/* 个人信息卡片 */
.profile-card {
  overflow: visible;
}

.profile-header {
  position: relative;
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 28px;
  background: linear-gradient(
    135deg,
    #ff9a9e 0%,
    #ffb8ba 25%,
    #ff758c 50%,
    #ffadc0 75%,
    #ffd6d9 100%
  );
  border-radius: 24px;
  overflow: hidden;
}

.profile-header::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background:
    radial-gradient(
      ellipse at 30% 20%,
      rgba(255, 200, 210, 0.6) 0%,
      transparent 50%
    ),
    radial-gradient(
      ellipse at 70% 80%,
      rgba(255, 117, 140, 0.5) 0%,
      transparent 50%
    ),
    radial-gradient(
      ellipse at 50% 50%,
      rgba(255, 180, 190, 0.4) 0%,
      transparent 60%
    );
  animation: liquidFlow 8s ease-in-out infinite;
  pointer-events: none;
}

@keyframes liquidFlow {
  0%,
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
  25% {
    transform: translate(5%, -3%) rotate(2deg);
  }
  50% {
    transform: translate(-3%, 5%) rotate(-1deg);
  }
  75% {
    transform: translate(-5%, -2%) rotate(1deg);
  }
}

.profile-bg-pattern {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.08'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  border-radius: 24px;
  pointer-events: none;
  z-index: 1;
}

.profile-float-1,
.profile-float-2,
.profile-float-3 {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.profile-float-1 {
  width: 120px;
  height: 120px;
  top: -30px;
  right: 20%;
  background: radial-gradient(
    circle,
    rgba(255, 200, 210, 0.6) 0%,
    transparent 70%
  );
  animation: float1 6s ease-in-out infinite;
}

.profile-float-2 {
  width: 80px;
  height: 80px;
  bottom: -20px;
  left: 30%;
  background: radial-gradient(
    circle,
    rgba(255, 170, 190, 0.5) 0%,
    transparent 70%
  );
  animation: float2 8s ease-in-out infinite;
}

.profile-float-3 {
  width: 60px;
  height: 60px;
  top: 40%;
  left: 10%;
  background: radial-gradient(
    circle,
    rgba(255, 220, 230, 0.4) 0%,
    transparent 70%
  );
  animation: float3 7s ease-in-out infinite;
}

@keyframes float1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translate(-20px, 20px) scale(1.1);
    opacity: 0.8;
  }
}

@keyframes float2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translate(15px, -15px) scale(0.9);
    opacity: 0.7;
  }
}

@keyframes float3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.4;
  }
  50% {
    transform: translate(10px, 10px) scale(1.2);
    opacity: 0.6;
  }
}

.avatar-wrapper {
  position: relative;
  flex-shrink: 0;
  z-index: 1;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
}

.avatar-upload {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 12px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-upload {
  opacity: 1;
}

.profile-info {
  position: relative;
  z-index: 1;
  flex: 1;
}

.profile-name {
  margin: 0 0 8px;
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.profile-meta {
  margin-bottom: 12px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
}

.member-status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
}

.member-status-badge.status-active .status-dot {
  background: #69f0ae;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%,
  100% {
    opacity: 1;
    box-shadow: 0 0 0 0 rgba(105, 240, 174, 0.4);
  }
  50% {
    opacity: 0.8;
    box-shadow: 0 0 0 6px rgba(105, 240, 174, 0);
  }
}

/* 快捷操作卡片 */
.quick-actions-card {
  padding: 20px 24px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-top: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 20px 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  border-color: rgba(255, 154, 158, 0.3);
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-icon img {
  width: 24px;
  height: 24px;
}

.action-icon-vip {
  background: linear-gradient(135deg, #ffd700 0%, #ffb800 100%);
}

.action-icon-logout {
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
}

.action-item span {
  font-size: 13px;
  color: #666;
  font-weight: 500;
}

/* 基本信息卡片 */
.info-card {
  padding-bottom: 8px;
}

.edit-btn {
  padding: 6px 14px;
  border: none;
  border-radius: 16px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.edit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.3);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px 24px 24px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.info-full {
  grid-column: 1 / -1;
}

.info-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.info-icon img {
  width: 20px;
  height: 20px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.info-value.bio {
  color: #666;
  font-size: 13px;
}

/* 统计卡片 */
.stats-card {
  padding: 20px 24px;
}

.stats-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border-radius: 14px;
  border: 1px solid rgba(0, 0, 0, 0.03);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-icon img {
  width: 24px;
  height: 24px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 12px;
  color: #999;
}

/* VIP推广卡片 */
.vip-promo-card {
  position: relative;
  min-height: 300px;
  padding: 32px;
  background: linear-gradient(
    135deg,
    #ff9a9e 0%,
    #ffb8ba 20%,
    #ff758c 40%,
    #ffadc0 60%,
    #ffd6d9 80%,
    #ffe8eb 100%
  );
  color: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.vip-promo-card::before {
  content: "";
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background:
    radial-gradient(
      ellipse at 20% 30%,
      rgba(255, 220, 230, 0.5) 0%,
      transparent 40%
    ),
    radial-gradient(
      ellipse at 80% 70%,
      rgba(255, 200, 210, 0.4) 0%,
      transparent 40%
    ),
    radial-gradient(
      ellipse at 50% 50%,
      rgba(255, 170, 190, 0.3) 0%,
      transparent 50%
    );
  animation: promoLiquidFlow 10s ease-in-out infinite;
  pointer-events: none;
}

@keyframes promoLiquidFlow {
  0%,
  100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-5%, 3%) rotate(-2deg);
  }
  66% {
    transform: translate(5%, -3%) rotate(2deg);
  }
}

.vip-promo-float {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.vip-float-1 {
  width: 150px;
  height: 150px;
  top: -50px;
  right: -30px;
  background: radial-gradient(
    circle,
    rgba(255, 220, 230, 0.5) 0%,
    transparent 70%
  );
  animation: vipFloat1 7s ease-in-out infinite;
}

.vip-float-2 {
  width: 100px;
  height: 100px;
  bottom: -30px;
  left: 20%;
  background: radial-gradient(
    circle,
    rgba(255, 180, 200, 0.4) 0%,
    transparent 70%
  );
  animation: vipFloat2 9s ease-in-out infinite;
}

.vip-float-3 {
  width: 80px;
  height: 80px;
  top: 50%;
  right: 30%;
  background: radial-gradient(
    circle,
    rgba(255, 200, 220, 0.3) 0%,
    transparent 70%
  );
  animation: vipFloat3 8s ease-in-out infinite;
}

@keyframes vipFloat1 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-15px, 20px) scale(1.1);
  }
}

@keyframes vipFloat2 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(20px, -15px) scale(0.9);
  }
}

@keyframes vipFloat3 {
  0%,
  100% {
    transform: translate(0, 0) scale(1);
  }
  50% {
    transform: translate(-10px, -10px) scale(1.15);
  }
}

.promo-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.06'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  pointer-events: none;
  z-index: 1;
}

.promo-particles {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2;
}

.particle {
  position: absolute;
  width: 6px;
  height: 6px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  animation: sparkle 3s ease-in-out infinite;
}

.particle:nth-child(1) {
  top: 15%;
  left: 10%;
  animation-delay: 0s;
}
.particle:nth-child(2) {
  top: 25%;
  left: 85%;
  animation-delay: 0.4s;
}
.particle:nth-child(3) {
  top: 45%;
  left: 75%;
  animation-delay: 0.8s;
}
.particle:nth-child(4) {
  top: 65%;
  left: 20%;
  animation-delay: 1.2s;
}
.particle:nth-child(5) {
  top: 75%;
  left: 50%;
  animation-delay: 1.6s;
}
.particle:nth-child(6) {
  top: 85%;
  left: 65%;
  animation-delay: 2s;
}

@keyframes sparkle {
  0%,
  100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.8);
  }
}

.promo-decoration {
  position: absolute;
  right: -40px;
  bottom: -40px;
  z-index: 2;
}

.deco-svg {
  width: 200px;
  height: 200px;
  animation: rotate 30s linear infinite;
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.promo-content {
  position: relative;
  z-index: 3;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.promo-crown {
  width: 56px;
  height: 56px;
  margin-bottom: 16px;
}

.promo-crown img {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.promo-title {
  margin: 0 0 8px;
  font-size: 24px;
  font-weight: 700;
}

.promo-desc {
  margin: 0 0 20px;
  font-size: 14px;
  opacity: 0.9;
}

.promo-features {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 24px;
}

.feature-tag {
  padding: 8px 14px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  font-size: 13px;
  backdrop-filter: blur(4px);
}

.promo-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 14px 28px;
  border: none;
  border-radius: 30px;
  background: #fff;
  color: #ff9a9e;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  width: fit-content;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.promo-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.btn-arrow {
  width: 18px;
  height: 18px;
  transition: transform 0.3s;
}

.promo-btn:hover .btn-arrow {
  transform: translateX(4px);
}

/* 会员状态卡片 */
.member-status-card {
  position: relative;
  min-height: 200px;
  padding: 28px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb800 50%, #ff9500 100%);
  color: #fff;
  overflow: hidden;
}

.member-card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='40' height='40' viewBox='0 0 40 40' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Ccircle cx='20' cy='20' r='2'/%3E%3C/g%3E%3C/svg%3E");
}

.member-decoration {
  position: absolute;
  right: -20px;
  top: 50%;
  transform: translateY(-50%);
}

.deco-pattern {
  width: 120px;
  height: 120px;
  opacity: 0.5;
}

.member-card-content {
  position: relative;
  z-index: 1;
}

.member-level {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.level-crown {
  width: 40px;
  height: 40px;
}

.level-crown img {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.level-name {
  font-size: 24px;
  font-weight: 700;
}

.member-expiry {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  opacity: 0.9;
}

.expiry-date {
  font-weight: 600;
}

.member-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-info {
  font-size: 12px;
  opacity: 0.9;
}

.progress-bar {
  height: 8px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #fff;
  border-radius: 4px;
  transition: width 0.5s ease;
}

/* 会员特权卡片 */
.privileges-card {
  padding-bottom: 8px;
}

.privilege-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 16px;
  background: #f0f0f0;
  color: #666;
}

.privilege-tag.badge-premium {
  background: linear-gradient(135deg, #ffd700 0%, #ffb800 100%);
  color: #fff;
}

.privileges-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 16px 24px 24px;
}

.privilege-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  background: linear-gradient(135deg, #f8f9fa 0%, #fff 100%);
  border-radius: 16px;
  border: 1px solid rgba(0, 0, 0, 0.03);
  transition: all 0.3s;
}

.privilege-item:hover {
  transform: translateX(4px);
}

.privilege-item.active {
  background: linear-gradient(135deg, #f0fff4 0%, #e8f5e9 100%);
  border-color: rgba(76, 175, 80, 0.2);
}

.privilege-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.privilege-icon img {
  width: 22px;
  height: 22px;
}

.privilege-content {
  flex: 1;
}

.privilege-content h4 {
  margin: 0 0 2px;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.privilege-content p {
  margin: 0;
  font-size: 12px;
  color: #999;
}

.privilege-check {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #69f0ae 0%, #4caf50 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.privilege-check img {
  width: 14px;
  height: 14px;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 24px;
  width: 90%;
  max-width: 500px;
  max-height: 85vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.modal-header {
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 28px;
  color: #999;
  cursor: pointer;
  line-height: 1;
}

.modal-close:hover {
  color: #333;
}

.modal-body {
  padding: 24px;
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

/* 表单样式 */
.edit-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-input,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: #ff9a9e;
  box-shadow: 0 0 0 3px rgba(255, 154, 158, 234, 0.1);
}

.radio-group {
  display: flex;
  gap: 12px;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 18px;
  border: 2px solid #f0f0f0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.radio-item.active {
  border-color: #ff9a9e;
  background: rgba(255, 154, 158, 0.05);
}

.radio-item input {
  display: none;
}

/* 按钮样式 */
.btn {
  padding: 12px 28px;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  color: #fff;
  box-shadow: 0 4px 16px rgba(255, 154, 158, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 154, 158, 0.4);
}

.btn-secondary {
  background: #f5f5f5;
  color: #666;
}

.btn-secondary:hover {
  background: #eee;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 套餐弹窗样式 */
.package-modal {
  max-width: 900px;
}

.package-modal-title {
  text-align: center;
}

.package-modal-title h3 {
  margin: 0 0 4px;
  font-size: 24px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.package-modal-title .subtitle {
  margin: 0;
  font-size: 14px;
  color: #999;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;
  color: #888;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #ff9a9e;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.package-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .package-grid {
    grid-template-columns: 1fr;
  }
}

.package-card {
  position: relative;
  border: 2px solid #f0f0f0;
  border-radius: 20px;
  padding: 24px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.package-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 32px rgba(255, 154, 158, 234, 0.15);
  border-color: #e0e0e0;
}

.package-selected {
  border-color: #ff9a9e;
  background: linear-gradient(180deg, #f8f9ff 0%, #f0f4ff 100%);
  box-shadow: 0 8px 24px rgba(255, 154, 158, 234, 0.2);
}

.package-recommended {
  border-color: #ff758c;
}

.recommended-tag {
  position: absolute;
  top: -12px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #ff758c 0%, #ff9a9e 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(118, 75, 162, 0.3);
}

.package-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.package-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 234, 0.3);
}

.package-icon svg {
  width: 24px;
  height: 24px;
}

.package-header h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  text-align: center;
}

.package-price {
  text-align: center;
  margin-bottom: 16px;
}

.currency {
  font-size: 16px;
  font-weight: 600;
  color: #ff758c;
  vertical-align: top;
}

.amount {
  font-size: 36px;
  font-weight: 700;
  color: #ff758c;
  line-height: 1;
}

.duration {
  font-size: 14px;
  color: #999;
}

.package-divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, #e0e0e0, transparent);
  margin-bottom: 16px;
}

.package-features {
  flex: 1;
  margin-bottom: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #555;
}

.feature-check {
  width: 20px;
  height: 20px;
  background: linear-gradient(135deg, #69f0ae 0%, #4caf50 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feature-check svg {
  width: 12px;
  height: 12px;
  color: #fff;
}

.package-desc {
  font-size: 13px;
  color: #999;
  text-align: center;
  margin-bottom: 16px;
  line-height: 1.4;
}

.package-btn {
  padding: 10px 20px;
  border-radius: 24px;
  text-align: center;
  font-size: 14px;
  font-weight: 500;
  background: #f5f5f5;
  color: #888;
  transition: all 0.3s;
}

.package-btn.active {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
  color: #fff;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 234, 0.3);
}

.btn-purchase {
  min-width: 140px;
}

.btn-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
  margin-right: 8px;
  vertical-align: middle;
}

/* 提示样式 */
.toast {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 2000;
  animation: slideIn 0.3s ease-out;
}

.toast-content {
  padding: 14px 24px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}

.toast-content.info {
  background: linear-gradient(135deg, #ff9a9e 0%, #ff758c 100%);
}
.toast-content.success {
  background: linear-gradient(135deg, #69f0ae 0%, #4caf50 100%);
}
.toast-content.error {
  background: linear-gradient(135deg, #ff758c 0%, #ff5252 100%);
}

@keyframes slideIn {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .member-container {
    padding: 16px;
  }

  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .profile-meta {
    justify-content: center;
  }

  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .info-grid {
    grid-template-columns: 1fr;
  }

  .info-full {
    grid-column: auto;
  }
}
</style>
