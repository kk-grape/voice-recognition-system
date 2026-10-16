import request from "./request";

/**
 * 语音识别管理相关 API
 */

/**
 * 查询语音识别记录列表（管理员版本，分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.name] - 文件名（可选，模糊查询）
 * @param {string} [params.memberId] - 会员ID（可选）
 * @param {string} [params.lang] - 语种（可选）
 * @param {string} [params.status] - 状态（可选）
 */
export const getFiletransList = (params) => {
  return request.get("/admin/audio/filetrans/query", { params });
};

/**
 * 查看语音识别记录详情
 * @param {number} id - 记录ID
 */
export const getFiletransDetail = (id) => {
  return request.get(`/admin/audio/filetrans/detail/${id}`);
};

/**
 * 删除语音识别记录（管理员操作）
 * @param {number} id - 记录ID
 */
export const deleteFiletrans = (id) => {
  return request.delete(`/admin/audio/filetrans/delete/${id}`);
};

/**
 * 统计语音识别记录总数
 */
export const getFiletransCount = () => {
  return request.get("/admin/audio/filetrans/count");
};

/**
 * 根据状态统计语音识别记录数量
 */
export const getFiletransCountByStatus = () => {
  return request.get("/admin/audio/filetrans/count-by-status");
};

/**
 * 查询字幕记录列表（分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {number} params.filetransId - 语音识别记录ID
 */
export const getFiletransSubtitleList = (params) => {
  return request.get("/admin/audio/filetrans-subtitle/query", { params });
};

/**
 * 生成字幕文件
 * @param {Object} params - 生成参数
 * @param {number} params.filetransId - 语音识别记录ID
 * @param {string} params.format - 字幕格式（srt/lrc）
 */
export const genSubtitle = (params) => {
  return request.get("/admin/audio/filetrans-subtitle/gen-subtitle", { params });
};

/**
 * 生成纯文本文件
 * @param {Object} params - 生成参数
 * @param {number} params.filetransId - 语音识别记录ID
 */
export const genText = (params) => {
  return request.get("/admin/audio/filetrans-subtitle/gen-text", { params });
};
