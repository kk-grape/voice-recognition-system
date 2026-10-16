import request from "./request";

/**
 * AI助手对话管理相关 API
 */

/**
 * 查询AI对话会话列表（管理员版本，分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.memberId] - 会员ID（可选）
 * @param {string} [params.status] - 状态（可选）
 */
export const getChatSessionList = (params) => {
  return request.get("/admin/ai/chat/session/list", { params });
};

/**
 * 查看AI对话会话详情
 * @param {number} id - 会话ID
 */
export const getChatSessionDetail = (id) => {
  return request.get(`/admin/ai/chat/session/detail/${id}`);
};

/**
 * 查看AI对话会话消息列表
 * @param {string} sessionUuid - 会话UUID
 */
export const getChatMessageList = (sessionUuid) => {
  return request.get(`/admin/ai/chat/message/list/${sessionUuid}`);
};

/**
 * 统计AI对话会话总数
 */
export const getChatSessionCount = () => {
  return request.get("/admin/ai/chat/session/count");
};

/**
 * 按状态统计AI对话会话数量
 */
export const getChatSessionCountByStatus = () => {
  return request.get("/admin/ai/chat/session/count-by-status");
};
