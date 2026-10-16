import request from "./request";

/**
 * 实时语音识别管理相关 API
 */

/**
 * 查询实时识别会话列表（管理员版本，分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.memberId] - 会员ID（可选）
 * @param {string} [params.status] - 状态（可选）
 */
export const getAsrSessionList = (params) => {
  return request.get("/admin/realtime/session/list", { params });
};

/**
 * 查看实时识别会话详情
 * @param {number} id - 会话ID
 */
export const getAsrSessionDetail = (id) => {
  return request.get(`/admin/realtime/session/detail/${id}`);
};

/**
 * 统计实时识别会话总数
 */
export const getAsrSessionCount = () => {
  return request.get("/admin/realtime/session/count");
};

/**
 * 按状态统计实时识别会话数量
 */
export const getAsrSessionCountByStatus = () => {
  return request.get("/admin/realtime/session/count-by-status");
};

/**
 * 统计实时识别总时长
 */
export const getAsrSessionSumDuration = () => {
  return request.get("/admin/realtime/session/sum-duration");
};
