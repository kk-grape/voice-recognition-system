import request from "./request";

/**
 * 会员管理相关 API
 */

/**
 * 查询会员列表（管理员版本，分页）
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页条数
 * @param {string} [params.mobile] - 手机号（可选，模糊查询）
 * @param {string} [params.nickname] - 昵称（可选，模糊查询）
 * @param {number} [params.memberLevel] - 会员等级（可选）
 */
export const getMemberList = (params) => {
  return request.get("/admin/member/list", { params });
};

/**
 * 查看会员详情
 * @param {number} id - 会员ID
 */
export const getMemberDetail = (id) => {
  return request.get(`/admin/member/detail/${id}`);
};

/**
 * 修改会员状态（启用/禁用）
 * @param {number} id - 会员ID
 * @param {number} memberLevel - 会员等级（0=禁用，1=普通会员，2=VIP，3=SVIP）
 */
export const updateMemberStatus = (id, memberLevel) => {
  return request.put(`/admin/member/status/${id}?memberLevel=${memberLevel}`);
};

/**
 * 修改会员到期时间
 * @param {number} id - 会员ID
 * @param {string} memberExpireAt - 到期时间（格式：yyyy-MM-dd HH:mm:ss）
 */
export const updateMemberExpire = (id, memberExpireAt) => {
  return request.put(`/admin/member/expire/${id}?memberExpireAt=${memberExpireAt}`);
};

/**
 * 修改会员信息
 * @param {number} id - 会员ID
 * @param {Object} data - 修改的会员信息
 */
export const updateMember = (id, data) => {
  return request.put(`/admin/member/update/${id}`, data);
};

/**
 * 重置会员密码
 * @param {number} id - 会员ID
 * @param {string} password - 新密码（MD5加密后的）
 */
export const resetMemberPassword = (id, password) => {
  return request.put(`/admin/member/reset-password/${id}?password=${password}`);
};

/**
 * 统计会员总数
 */
export const getMemberCount = () => {
  return request.get("/admin/member/count");
};

/**
 * 按会员等级统计会员数量
 */
export const getMemberCountByLevel = () => {
  return request.get("/admin/member/count-by-level");
};
